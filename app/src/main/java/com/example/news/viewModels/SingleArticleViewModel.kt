package com.example.news.viewModels

import android.icu.text.CaseMap.Title
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.news.db.ArticleDao
import com.example.news.dto.ParcelableArticle
import com.example.news.utils.ArticleMapper
import kotlinx.coroutines.launch

class SingleArticleViewModel(
    private val articleDao: ArticleDao
) : ViewModel() {

    var existsArticleLiveData = MutableLiveData<Boolean>()
        private set

    fun existsArticle(title: String) {
        viewModelScope.launch {
            existsArticleLiveData.value = articleDao.existsByTitle(title)
        }
    }

    fun insertArticle(parcelableArticle: ParcelableArticle) {
        viewModelScope.launch {
            articleDao.upsert(ArticleMapper.fromParcelableToModel(parcelableArticle))
            existsArticleLiveData.value = true
        }
    }

    fun deleteArticleByTitle(title: String) {
        viewModelScope.launch {
            articleDao.deleteByTitle(title)
            existsArticleLiveData.value = false
        }
    }
}