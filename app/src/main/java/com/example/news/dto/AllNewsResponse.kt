package com.example.news.dto

data class AllNewsResponse(
    val articles: List<Article>,
    val status: String,
    val totalResults: Int
)