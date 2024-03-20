package com.example.news.fragments

import android.os.Bundle
import android.text.Editable
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.doAfterTextChanged
import androidx.core.widget.doOnTextChanged
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.example.news.adapters.NewsAdapter
import com.example.news.databinding.FragmentHomeBinding
import com.example.news.dto.Article
import com.example.news.dto.ParcelableArticle
import com.example.news.utils.ArticleMapper
import com.example.news.viewModels.HomeViewModel
import com.google.firebase.auth.FirebaseAuth

class HomeFragment : Fragment(), SwipeRefreshLayout.OnRefreshListener {

    private lateinit var binding: FragmentHomeBinding
    private lateinit var fAuth: FirebaseAuth
    private lateinit var homeViewModel: HomeViewModel
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
        binding = FragmentHomeBinding.inflate(layoutInflater, container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        homeViewModel = ViewModelProvider(this)[HomeViewModel::class.java]
        binding.reusableRv.swipeRefreshLayout.setOnRefreshListener(this)

        initRv()

        fAuth = FirebaseAuth.getInstance()
        if (fAuth.currentUser == null) {
            navigateToLogin()
        }


        getAllNews(false)

        observerNews()



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
                        getAllNews(false)
                    }
                }

                super.onScrolled(recyclerView, dx, dy)
            }
        })


        binding.searchET.apply {
            setText("electric")
            doAfterTextChanged(this@HomeFragment::handleSearchTextChange)
            doOnTextChanged { _, _, _, _ ->
                if (error != null) error = null
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

    private fun handleSearchTextChange(ed: Editable?) {
        isLoading = true
        binding.reusableRv.progressBar.visibility = View.VISIBLE
        val query = ed.toString()

        if (query.length < 3) {
            binding.searchET.apply {
                error = "Search query must be at least 3 characters"
                return
            }
        } else {
            newsAdapter.submitList(emptyList())
            page = 1
            homeViewModel.searchQuery.value = ed.toString()
        }
    }


    private fun navigateToLogin() {
        val action = HomeFragmentDirections.actionHomeFragmentToLoginFragment()
        findNavController().navigate(action)
    }

    private fun observerNews() {
        homeViewModel.allNewsLiveData.observe(
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


    private fun onItemClick(article: Article) {
        val action = HomeFragmentDirections.actionHomeFragmentToSingleArticleFragment(
            ArticleMapper.fromArticleToParcelable(article)
        )
        findNavController().navigate(action)
        Log.e("HomeFragment", "Article: ${article.title}")
    }

    private fun getAllNews(isOnRefresh: Boolean) {
        isLoading = true
        if (!isOnRefresh) binding.reusableRv.progressBar.visibility = View.VISIBLE
        homeViewModel.getAllNews(page)
    }

    override fun onRefresh() {
        newsAdapter.submitList(emptyList())
        page = 1
        getAllNews(true)
    }


}