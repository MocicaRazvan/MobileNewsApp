package com.example.news.utils

import com.example.news.db.ArticleModel
import com.example.news.dto.Article
import com.example.news.dto.ParcelableArticle
import com.google.firebase.auth.FirebaseAuth

object ArticleMapper {
    fun fromArticleToParcelable(article: Article) = ParcelableArticle(
        article.author,
        article.content,
        article.description,
        article.publishedAt,
        article.title,
        article.url,
        article.urlToImage
    )

    fun fromModelToParcelable(articleModel: ArticleModel) = ParcelableArticle(
        articleModel.author,
        articleModel.content,
        articleModel.description,
        articleModel.publishedAt,
        articleModel.title,
        articleModel.url,
        articleModel.urlToImage
    )

    fun fromParcelableToModel(parcelableArticle: ParcelableArticle) = ArticleModel(
        FirebaseAuth.getInstance().currentUser?.uid,
        parcelableArticle.author,
        parcelableArticle.content,
        parcelableArticle.description,
        parcelableArticle.publishedAt,
        parcelableArticle.title,
        parcelableArticle.url,
        parcelableArticle.urlToImage
    )

    fun fromModelToArticle(articleModel: ArticleModel) = Article(
        author = articleModel.author ?: "",
        content = articleModel.content ?: "",
        description = articleModel.description ?: "",
        publishedAt = articleModel.publishedAt ?: "",
        source = null,
        title = articleModel.title ?: "",
        url = articleModel.url ?: "",
        urlToImage = articleModel.urlToImage ?: ""

    )
}