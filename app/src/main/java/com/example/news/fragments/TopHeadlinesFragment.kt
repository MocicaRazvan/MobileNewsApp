package com.example.news.fragments

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.text.Editable
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.core.view.children
import androidx.core.widget.doAfterTextChanged
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.example.news.R
import com.example.news.adapters.NewsAdapter
import com.example.news.databinding.FragmentTopHeadlinesBinding
import com.example.news.dto.Article
import com.example.news.dto.ParcelableArticle
import com.example.news.retrofit.CategoryApiQuery
import com.example.news.retrofit.CountriesApiQuery
import com.example.news.utils.ArticleMapper
import com.example.news.utils.ThemePreferences
import com.example.news.viewModels.TopHeadlinesViewModel
import com.google.android.material.button.MaterialButton
import com.google.android.material.color.MaterialColors
import kotlin.math.log


class TopHeadlinesFragment : Fragment(), SwipeRefreshLayout.OnRefreshListener {

    private lateinit var binding: FragmentTopHeadlinesBinding
    private lateinit var topHeadlinesViewModel: TopHeadlinesViewModel
    private lateinit var newsAdapter: NewsAdapter
    private val themePreferences: ThemePreferences by lazy {
        ThemePreferences(requireContext())
    }

    private var maxResults = 80
    private var totalResults: Int? = null
    private var page = 1
    private var isLoading = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentTopHeadlinesBinding.inflate(layoutInflater, container, false)
        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        topHeadlinesViewModel = ViewModelProvider(this)[TopHeadlinesViewModel::class.java]

        binding.reusableRv.swipeRefreshLayout.setOnRefreshListener(this)

        CategoryApiQuery.entries.forEach {
            val button = createButton(it.name).apply {
                setOnClickListener { btn ->

                    onCategoryClicked(it, btn)
                }
            }
            binding.categoriesLL.addView(button)
        }

        CountriesApiQuery.entries.forEach {
            val button = createButton(it.name).apply {
                setOnClickListener { btn ->

                    onCountryClicked(it, btn)

                }
            }
            binding.countriesLL.addView(button)
        }


        initRv()

        getTopHeadlines(false)

        observerHeadlines()

        observeSelections()

        binding.reusableRv.rvNews.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                val layoutManager = recyclerView.layoutManager as LinearLayoutManager
                val lastVisibleItem = layoutManager.findLastVisibleItemPosition()
                val totalItems = layoutManager.itemCount
                if (totalItems == maxResults) {
                    Log.e("HomeFragment", "Max results reached")
                    binding.reusableRv.progressBar.visibility = View.GONE
                } else if (lastVisibleItem == totalItems - 1 && !isLoading) {
                    if (totalResults != null && totalResults!! > newsAdapter.currentList.size) {
                        page++
                        getTopHeadlines(false)
                    }
                }

                super.onScrolled(recyclerView, dx, dy)
            }
        })

        binding.searchET.doAfterTextChanged(this::handleSearchTextChange)


    }

    private fun observeSelections() {
        topHeadlinesViewModel.apply {
            selectedCategory.observe(viewLifecycleOwner) { c ->
                binding.categoriesLL.children.forEach {
                    it.isPressed = false
                    it.isSelected = it.tag as String == c?.name
                }
            }

            selectedCountry.observe(viewLifecycleOwner) { c ->
                binding.countriesLL.children.forEach {
                    it.isPressed = false
                    it.isSelected = it.tag as String == c?.name
                }
            }
        }
    }


    private fun initRv() {
        newsAdapter = NewsAdapter(this::onItemClick)
        binding.reusableRv.rvNews.apply {
            layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
            adapter = newsAdapter
            setHasFixedSize(true)
        }

    }

    private fun onItemClick(article: Article) {

        val navigation =
            TopHeadlinesFragmentDirections.actionTopHeadlinesFragmentToSingleArticleFragment(
                ArticleMapper.fromArticleToParcelable(article)
            )

        findNavController().navigate(navigation)

        Log.e("HomeFragment", "Article: ${article.title}")
    }


    private fun createButton(name: String) = Button(requireContext()).apply {
        text = name
        tag = name
        isClickable = true
        isFocusable = true
        layoutParams = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.WRAP_CONTENT,
            LinearLayout.LayoutParams.MATCH_PARENT
        ).apply {
            marginEnd = 8
        }
        Log.e("THF", textColors.toString());
        setBackgroundResource(R.drawable.options_btn)

        setTextColor(
            ContextCompat.getColor(
                context, when (themePreferences.isDarkTheme()) {
                    true -> R.color.black
                    false -> R.color.md_theme_light_onTertiary
                }
            )
        )
        Log.e("THF", textColors.toString());

    }


    private fun onCountryClicked(country: CountriesApiQuery, btn: View) {
        newsAdapter.submitList(emptyList())
        page = 1
        topHeadlinesViewModel.countryQuery.value = country


        val button = btn as? Button ?: return
        button.isPressed = true
    }


    private fun onCategoryClicked(category: CategoryApiQuery, btn: View) {
        newsAdapter.submitList(emptyList())
        page = 1
        topHeadlinesViewModel.categoryQuery.value = category

        val button = btn as? Button ?: return
        button.isPressed = true
    }

    private fun handleSearchTextChange(ed: Editable?) {
        isLoading = true
        binding.reusableRv.progressBar.visibility = View.VISIBLE
        val query = ed.toString()

        newsAdapter.submitList(emptyList())
        page = 1
        topHeadlinesViewModel.searchQuery.value = ed.toString()
    }

    private fun observerHeadlines() {
        topHeadlinesViewModel.topHeadlinesLiveData.observe(
            viewLifecycleOwner

        ) {
            isLoading = false
            binding.reusableRv.apply {
                progressBar.visibility = View.GONE
                swipeRefreshLayout.isRefreshing = false
            }
            newsAdapter.submitList(newsAdapter.currentList + it.articles)
            totalResults = it.totalResults
            if (totalResults == 0) {
                binding.reusableRv.apply {
                    tvEmpty.visibility = View.VISIBLE
                    rvNews.visibility = View.GONE
                }
            } else {
                binding.reusableRv.apply {
                    tvEmpty.visibility = View.GONE
                    rvNews.visibility = View.VISIBLE
                }
            }
        }
    }

    private fun getTopHeadlines(isOnRefresh: Boolean) {
        isLoading = true
        if (!isOnRefresh) binding.reusableRv.progressBar.visibility = View.VISIBLE
        topHeadlinesViewModel.getTopHeadlines(page)
    }

    override fun onRefresh() {
        newsAdapter.submitList(emptyList())
        page = 1
        getTopHeadlines(true)
    }

}