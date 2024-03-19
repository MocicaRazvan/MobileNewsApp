package com.example.news.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.news.databinding.ItemCardBinding
import com.example.news.dto.Article
import kotlin.reflect.KProperty0

class NewsAdapter(
    private val onItemClick: (Article) -> Unit
) : ListAdapter<Article, NewsViewHolder>(ArticleDiff()) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NewsViewHolder =
        NewsViewHolder(
            ItemCardBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )

    override fun onBindViewHolder(holder: NewsViewHolder, position: Int) {
        holder.bind(getItem(position), onItemClick)
    }
}

class NewsViewHolder(
    private val binding: ItemCardBinding
) : RecyclerView.ViewHolder(binding.root) {
    fun bind(article: Article, onItemClick: (Article) -> Unit) {
        binding.apply {
            Glide.with(binding.root)
                .load(article.urlToImage)
                .into(imgThumb)

            tvTitle.text = article.title

            binding.root.setOnClickListener {
                onItemClick(article)
            }
        }
    }
}

class ArticleDiff : DiffUtil.ItemCallback<Article>() {
    override fun areItemsTheSame(oldItem: Article, newItem: Article): Boolean {
        return oldItem.url == newItem.url
    }

    override fun areContentsTheSame(oldItem: Article, newItem: Article): Boolean {
        return oldItem == newItem
    }
}
