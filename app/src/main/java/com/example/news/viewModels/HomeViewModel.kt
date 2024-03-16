package com.example.news.viewModels

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.news.dto.AllNewsResponse
import com.example.news.dto.Article
import com.example.news.retrofit.RetrofitInstance
import kotlinx.coroutines.launch

class HomeViewModel : ViewModel() {
    private val newsApi = RetrofitInstance.newsRetrofitInstance
    var allNewsLiveData = MutableLiveData<AllNewsResponse>()
        private set
    var allNewsErrorResult = MutableLiveData<String?>()
        private set

    fun getAllNews(q: String, page: Int = 1) {
        viewModelScope.launch {
            try {
                val response = newsApi.getAllNews(q, page)
                response.body()?.let {
                    allNewsLiveData.value = it
                } ?: return@launch
                Log.e("HomeFragment", "Response: ${response}")
            } catch (e: Exception) {
                allNewsErrorResult.value = e.message
                Log.e("HomeFragment", "Error: ${e.message}")
            }
        }
    }
}




