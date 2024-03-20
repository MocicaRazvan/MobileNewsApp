package com.example.news.viewModels

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.news.db.ArticleDao
import com.example.news.db.ArticleModel
import com.example.news.dto.Article
import com.example.news.utils.ArticleMapper
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.launch

@OptIn(FlowPreview::class)
class FavoritesViewModel(
    private val articleDao: ArticleDao
) : ViewModel() {
    var favoritesArticles = MutableLiveData<List<Article>>()
        private set

    var searchQuery = MutableStateFlow("")

    init {
        viewModelScope.launch {
            searchQuery.debounce(500)
                .collect {
                    getArticles(it)
                }
        }
    }

    private fun getArticles(q: String) {
        viewModelScope.launch {
            favoritesArticles.value = articleDao.getArticlesByTitleLike(q)
                .map(ArticleMapper::fromModelToArticle)
        }
    }


    fun getArticles() {
        getArticles(searchQuery.value)
    }

}