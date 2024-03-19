package com.example.news.viewModels

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.news.dto.AllNewsResponse
import com.example.news.retrofit.CategoryApiQuery
import com.example.news.retrofit.CountriesApiQuery
import com.example.news.retrofit.RetrofitInstance
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.launch

@OptIn(FlowPreview::class)
class TopHeadlinesViewModel : ViewModel() {

    private val newsApi = RetrofitInstance.newsRetrofitInstance
    var topHeadlinesLiveData = MutableLiveData<AllNewsResponse>()
        private set
    var searchQuery = MutableStateFlow("")
    var countryQuery = MutableStateFlow(CountriesApiQuery.US)
    var categoryQuery = MutableStateFlow(CategoryApiQuery.GENERAL)


    init {
        viewModelScope.launch {
            searchQuery.debounce(500)
                .collect {
                    getTopHeadlines(it, categoryQuery.value, countryQuery.value)
                }
        }
        viewModelScope.launch {
            countryQuery
                .collect {
                    getTopHeadlines(searchQuery.value, categoryQuery.value, it)
                }
        }
        viewModelScope.launch {
            categoryQuery
                .collect {
                    getTopHeadlines(searchQuery.value, it, countryQuery.value)
                }
        }
    }

    var topHeadlinesErrorResult = MutableLiveData<String?>()
        private set

    private fun getTopHeadlines(
        q: String,
        category: CategoryApiQuery,
        country: CountriesApiQuery,
        page: Int = 1
    ) {
        viewModelScope.launch {
            try {
//                delay(4000)
                val response = newsApi.getTopHeadlines(q, category, country, page)
                response.body()?.let {
                    topHeadlinesLiveData.value = it
                } ?: return@launch
                Log.e("HomeFragment", "Response: ${response}")
            } catch (e: Exception) {
                topHeadlinesErrorResult.value = e.message
                Log.e("HomeFragment", "Error: ${e.message}")
            }
        }
    }

    fun getTopHeadlines(page: Int = 1) {
        getTopHeadlines(searchQuery.value, categoryQuery.value, countryQuery.value, page)
    }
}