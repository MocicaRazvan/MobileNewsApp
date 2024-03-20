package com.example.news.fragments

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.navArgs
import com.bumptech.glide.Glide
import com.example.news.R
import com.example.news.databinding.FragmentSingleArticleBinding
import com.example.news.db.ArticleDao
import com.example.news.db.DataBaseManager
import com.example.news.dto.Article
import com.example.news.dto.ParcelableArticle
import com.example.news.viewModels.RegisterViewModel
import com.example.news.viewModels.SingleArticleViewModel
import com.example.news.viewModels.SingleViewModelFactory
import java.time.LocalDateTime
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter

class SingleArticleFragment : Fragment() {

    private lateinit var binding: FragmentSingleArticleBinding
    private lateinit var singleArticleViewModel: SingleArticleViewModel
    private val articleDao by lazy {
        DataBaseManager.getInstance(requireContext()).articleDao()
    }

    private val args: SingleArticleFragmentArgs by navArgs()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentSingleArticleBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        singleArticleViewModel = ViewModelProvider(
            this,
            SingleViewModelFactory(articleDao)
        )[SingleArticleViewModel::class.java]
        args.article.let {
            binding.apply {

                Glide.with(this@SingleArticleFragment)
                    .load(it.urlToImage)
                    .placeholder(R.drawable.no_image)
                    .into(articleImg)
                articleAuthor.text = it.author
                articleDate.text =
                    ZonedDateTime.parse(it.publishedAt)
                        .format(DateTimeFormatter.ofPattern("dd-MM-yy"))
                articleTitle.text = it.title
                articleBody.text = it.content
                btnFavourite.setOnClickListener { _ ->
                    favouriteClick(it)
                }
                btnDelete.setOnClickListener { _ ->
                    it.title?.let { it1 -> deleteClick(it1) }
                }

            }

            it.title?.let { t ->
                singleArticleViewModel.existsArticle(t)
            }
        }

        observeExistsArticle()


    }

    private fun observeExistsArticle() {
        singleArticleViewModel.existsArticleLiveData.observe(viewLifecycleOwner) {
            if (!it) {
                binding.apply {
                    btnFavourite.visibility = View.VISIBLE
                    btnDelete.visibility = View.GONE
                }
            } else {
                binding.btnFavourite.visibility = View.GONE
                binding.btnDelete.visibility = View.VISIBLE
            }
        }
    }


    private fun deleteClick(title: String) {
        singleArticleViewModel.deleteArticleByTitle(title)
    }

    private fun favouriteClick(parcelableArticle: ParcelableArticle) {
        singleArticleViewModel.insertArticle(parcelableArticle)
    }

}