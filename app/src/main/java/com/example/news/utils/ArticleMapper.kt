package com.example.news.utils

import com.example.news.db.ArticleModel
import com.example.news.dto.Article
import com.example.news.dto.ParcelableArticle

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
        parcelableArticle.author,
        parcelableArticle.content,
        parcelableArticle.description,
        parcelableArticle.publishedAt,
        parcelableArticle.title,
        parcelableArticle.url,
        parcelableArticle.urlToImage
    )
}