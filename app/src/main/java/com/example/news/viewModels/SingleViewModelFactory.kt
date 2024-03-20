package com.example.news.viewModels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.news.db.ArticleDao

class SingleViewModelFactory(
    private val articleDao: ArticleDao
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return SingleArticleViewModel(articleDao) as T
    }
}