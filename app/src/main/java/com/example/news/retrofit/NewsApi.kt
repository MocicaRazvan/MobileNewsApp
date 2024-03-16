package com.example.news.retrofit

import com.example.news.dto.AllNewsResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface NewsApi {
    @GET("everything")
    suspend fun getAllNews(
        @Query("q") query: String,
        @Query("page") page: Int = 1,
        @Query("pageSize") pageSize: Int = 20,
        @Query("sortBy") sortBy: String = "publishedAt",
        @Query("searchIn") searchIn: String = "title",
        @Query("apiKey")
        // put the string manually its not working not like this
        apiKey: String = ApiKeyHandler.getNewsApiKey()
    ): Response<AllNewsResponse>

    @GET("top-headlines")
    suspend fun getTopHeadlines(
        @Query("q") query: String,
        @Query("category") category: CategoryApiQuery = CategoryApiQuery.GENERAL,
        @Query("country") country: CountriesApiQuery = CountriesApiQuery.US,
        @Query("page") page: Int = 1,
        @Query("pageSize") pageSize: Int = 20,
        @Query("apiKey")
        apiKey: String = ApiKeyHandler.getNewsApiKey()
    ): Response<AllNewsResponse>

}