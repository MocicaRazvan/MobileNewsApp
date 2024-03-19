package com.example.news.fragments

import android.os.Bundle
import android.text.Editable
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.LinearLayout
import android.widget.Toast
import androidx.core.widget.doAfterTextChanged
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.example.news.R
import com.example.news.adapters.NewsAdapter
import com.example.news.databinding.FragmentTopHeadlinesBinding
import com.example.news.dto.Article
import com.example.news.retrofit.CategoryApiQuery
import com.example.news.retrofit.CountriesApiQuery
import com.example.news.viewModels.TopHeadlinesViewModel
import com.google.android.material.button.MaterialButton


class TopHeadlinesFragment : Fragment(), SwipeRefreshLayout.OnRefreshListener {

    private lateinit var binding: FragmentTopHeadlinesBinding
    private lateinit var topHeadlinesViewModel: TopHeadlinesViewModel
    private lateinit var newsAdapter: NewsAdapter

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
                setOnClickListener { _ ->
                    onCategoryClicked(it)
                }
            }
            binding.categoriesLL.addView(button)
        }

        CountriesApiQuery.entries.forEach {
            val button = createButton(it.name).apply {
                setOnClickListener { _ ->
                    onCountryClicked(it)

                }
            }
            binding.countriesLL.addView(button)
        }


        initRv()

        getTopHeadlines(false)

        observerHeadlines()


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


    private fun initRv() {
        newsAdapter = NewsAdapter(this::onItemClick)
        binding.reusableRv.rvNews.apply {
            layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
            adapter = newsAdapter
            setHasFixedSize(true)
        }

    }

    private fun onItemClick(article: Article) {
        Log.e("HomeFragment", "Article: ${article.title}")
    }


    private fun createButton(name: String) = MaterialButton(requireContext()).apply {
        text = name
        layoutParams = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.WRAP_CONTENT,
            LinearLayout.LayoutParams.MATCH_PARENT
        ).apply {
            marginEnd = 8
            cornerRadius = 8
        }

    }


    private fun onCountryClicked(country: CountriesApiQuery) {
        newsAdapter.submitList(emptyList())
        page = 1
        topHeadlinesViewModel.countryQuery.value = country
    }


    private fun onCategoryClicked(category: CategoryApiQuery) {
        newsAdapter.submitList(emptyList())
        page = 1
        topHeadlinesViewModel.categoryQuery.value = category
    }

    private fun handleSearchTextChange(ed: Editable?) {
        isLoading = true
        binding.reusableRv.progressBar.visibility = View.VISIBLE
        val query = ed.toString()

//        if (query.length < 3) {
//            binding.searchET.apply {
//                error = "Search query must be at least 3 characters"
//                return
//            }
//        } else {
//            newsAdapter.submitList(emptyList())
//            page = 1
//            topHeadlinesViewModel.searchQuery.value = ed.toString()
//        }

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