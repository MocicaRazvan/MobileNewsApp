package com.example.news.viewModels

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.news.dto.AllNewsResponse
import com.example.news.dto.Article
import com.example.news.retrofit.RetrofitInstance
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch

@OptIn(FlowPreview::class)
class HomeViewModel : ViewModel() {
    private val newsApi = RetrofitInstance.newsRetrofitInstance
    var allNewsLiveData = MutableLiveData<AllNewsResponse>()
        private set
    var searchQuery = MutableStateFlow("electric")

    init {
        viewModelScope.launch {
            searchQuery.debounce(500)
                .collect {
                    getAllNews(it)
                }
        }
    }

    var allNewsErrorResult = MutableLiveData<String?>()
        private set

    private fun getAllNews(q: String, page: Int = 1) {
        viewModelScope.launch {
            try {
//                delay(4000)
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

    fun getAllNews(page: Int = 1) {
        getAllNews(searchQuery.value, page)
    }
}




