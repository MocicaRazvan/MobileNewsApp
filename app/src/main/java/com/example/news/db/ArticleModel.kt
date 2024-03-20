package com.example.news.db

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "article",
    indices = [Index(value = ["title"], unique = false), Index(value = ["userId"], unique = false)]
)
class ArticleModel(
    @PrimaryKey(autoGenerate = true)
    var id: Int? = null,
    var userId: String?,
    val author: String?,
    val content: String?,
    val description: String?,
    val publishedAt: String?,
    val title: String?,
    val url: String?,
    val urlToImage: String?
) {
    constructor(
        userId: String?,
        author: String?,
        content: String?,
        description: String?,
        publishedAt: String?,
        title: String?,
        url: String?,
        urlToImage: String?
    ) : this(null, userId, author, content, description, publishedAt, title, url, urlToImage)
}


