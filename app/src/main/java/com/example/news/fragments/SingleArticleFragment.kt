package com.example.news.fragments

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.bumptech.glide.Glide
import com.example.news.MainActivity
import com.example.news.R
import com.example.news.databinding.FragmentSingleArticleBinding
import com.example.news.db.ArticleDao
import com.example.news.db.DataBaseManager
import com.example.news.dto.Article
import com.example.news.dto.ParcelableArticle
import com.example.news.viewModels.RegisterViewModel
import com.example.news.viewModels.SingleArticleViewModel
import com.example.news.viewModels.SingleViewModelFactory
import com.google.firebase.auth.FirebaseAuth
import java.time.LocalDateTime
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import kotlin.random.Random

class SingleArticleFragment : Fragment() {

    private val channelId = "article_channel"
    private lateinit var binding: FragmentSingleArticleBinding
    private lateinit var singleArticleViewModel: SingleArticleViewModel
    private val articleDao by lazy {
        DataBaseManager.getInstance(requireContext()).articleDao()
    }
    private val fAuth: FirebaseAuth by lazy {
        FirebaseAuth.getInstance()
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

        if (fAuth.currentUser == null) {
            navigateToLogin()
        }


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

        createNotificationChannel()

    }

    private fun navigateToLogin() {
        val action = SingleArticleFragmentDirections.actionSingleArticleFragmentToLoginFragment()
        findNavController().navigate(action)
    }

    private fun scheduleNotification(article: ParcelableArticle) {
        val notificationId =
            Random(69).nextInt()

        val intent = Intent(requireContext(), MainActivity::class.java).apply {
            flags =
                Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
            putExtra("navigateTo", "SingleArticleFragment")
            putExtra("article", article)
        }

        val pendingIntent = PendingIntent.getActivity(
            requireContext(),
            notificationId,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )


        val builder = Notification.Builder(requireContext(), channelId)
            .setSmallIcon(R.drawable.ic_favorites)
            .setContentTitle("Favorite Added")
            .setContentText("Tap to view your favorite article.")
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)


        val notificationManager =
            requireContext().getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.notify(notificationId, builder.build())
    }

    private fun createNotificationChannel() {
        val name = "Article channel"
        val desc = "You just saved an article"
        val importance = NotificationManager.IMPORTANCE_DEFAULT
        val channel = NotificationChannel(channelId, name, importance)
        channel.description = desc
        val notificationManager =
            requireActivity().getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.createNotificationChannel(channel)
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
                scheduleNotification(args.article)

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