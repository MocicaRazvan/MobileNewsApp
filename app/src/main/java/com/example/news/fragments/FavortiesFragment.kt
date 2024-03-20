package com.example.news.fragments

import android.os.Bundle
import android.text.Editable
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.doAfterTextChanged
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.example.news.R
import com.example.news.adapters.NewsAdapter
import com.example.news.databinding.FragmentFavortiesBinding
import com.example.news.databinding.FragmentSingleArticleBinding
import com.example.news.db.ArticleModel
import com.example.news.db.DataBaseManager
import com.example.news.dto.Article
import com.example.news.utils.ArticleMapper
import com.example.news.viewModels.FavoritesViewModel
import com.example.news.viewModels.FavoritesViewModelFactory
import com.example.news.viewModels.SingleArticleViewModel
import com.example.news.viewModels.SingleViewModelFactory


class FavortiesFragment : Fragment() {

    private lateinit var binding: FragmentFavortiesBinding
    private lateinit var favoritesViewModel: FavoritesViewModel
    private val articleDao by lazy {
        DataBaseManager.getInstance(requireContext()).articleDao()
    }
    private lateinit var newsAdapter: NewsAdapter

    private var isLoading = false
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentFavortiesBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        favoritesViewModel = ViewModelProvider(
            this,
            FavoritesViewModelFactory(articleDao)
        )[FavoritesViewModel::class.java]

        initRv()
        getArticles()

        observeArticles()

        binding.searchET.apply {
            doAfterTextChanged(this@FavortiesFragment::handleSearchTextChange)
        }

    }

    private fun observeArticles() {
        favoritesViewModel.favoritesArticles.observe(viewLifecycleOwner) {
            isLoading = false
            binding.apply {
                progressBar.visibility = View.GONE
            }
            if (it.isEmpty()) {
                binding.tvEmpty.visibility = View.VISIBLE
            } else {
                binding.tvEmpty.visibility = View.GONE
                newsAdapter.submitList(it)
            }
        }
    }

    private fun initRv() {
        newsAdapter = NewsAdapter(this::onItemClick)
        binding.rvNews.apply {
            layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
            adapter = newsAdapter
            setHasFixedSize(true)
        }

    }

    private fun onItemClick(article: Article) {
        val action = FavortiesFragmentDirections.actionFavortiesFragmentToSingleArticleFragment(
            ArticleMapper.fromArticleToParcelable(article)
        )
        findNavController().navigate(action)
    }

    private fun handleSearchTextChange(ed: Editable?) {
        isLoading = true
        binding.apply {
            progressBar.visibility = View.VISIBLE
            tvEmpty.visibility = View.GONE
        }
        newsAdapter.submitList(emptyList())
        favoritesViewModel.searchQuery.value = ed.toString()
    }


    private fun getArticles() {
        isLoading = true
        favoritesViewModel.getArticles()
    }


}