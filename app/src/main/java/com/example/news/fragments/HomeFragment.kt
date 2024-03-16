package com.example.news.fragments

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.NavigationUI
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.news.R
import com.example.news.adapters.NewsAdapter
import com.example.news.databinding.FragmentHomeBinding
import com.example.news.dto.Article
import com.example.news.viewModels.HomeViewModel
import com.google.firebase.auth.FirebaseAuth

class HomeFragment : Fragment() {

    private lateinit var binding: FragmentHomeBinding
    private lateinit var fAuth: FirebaseAuth
    private lateinit var homeViewModel: HomeViewModel
    private lateinit var newsAdapter: NewsAdapter

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

        binding.apply {
            btnLogout.setOnClickListener {
                logout()
            }


        }

        fAuth = FirebaseAuth.getInstance()
        if (fAuth.currentUser == null) {
            navigateToLogin()
        }

        initRv()

        // TODO: infite scrolling
        homeViewModel.getAllNews("electric")

        observerNews()

    }

    private fun logout() {
        FirebaseAuth.getInstance().signOut()
        navigateToLogin()
    }

    private fun navigateToLogin() {
        val action = HomeFragmentDirections.actionHomeFragmentToLoginFragment()
        findNavController().navigate(action)
    }

    private fun observerNews() {
        homeViewModel.allNewsLiveData.observe(viewLifecycleOwner) {
            newsAdapter.submitList(it.articles)
        }
    }

    private fun initRv() {
        newsAdapter = NewsAdapter(this::onItemClick)
        binding.rvNews.apply {
            layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
            adapter = newsAdapter
        }

    }

    private fun onItemClick(article: Article) {
        Log.e("HomeFragment", "Article: ${article.title}")
    }

}