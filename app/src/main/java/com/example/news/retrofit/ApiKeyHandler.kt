package com.example.news.retrofit

import android.content.Context
import com.example.news.ApplicationController
import com.example.news.R

object ApiKeyHandler {
    fun getNewsApiKey(): String {
        return ApplicationController.INSTANCE?.getString(R.string.news_api_key) ?: ""
    }

    fun getBaseUrl(): String {
        return ApplicationController.INSTANCE?.getString(R.string.news_api_base_url) ?: ""
    }
}