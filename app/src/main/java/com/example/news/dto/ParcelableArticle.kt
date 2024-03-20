package com.example.news.dto

import android.os.Parcelable
import com.example.news.db.ArticleModel
import kotlinx.parcelize.Parcelize


@Parcelize
class ParcelableArticle(
    val author: String?,
    val content: String?,
    val description: String?,
    val publishedAt: String?,
    val title: String?,
    val url: String?,
    val urlToImage: String?
) : Parcelable {

}