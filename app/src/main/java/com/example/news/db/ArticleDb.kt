package com.example.news.db

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(
    entities = [ArticleModel::class],
    version = 2
)
abstract class ArticleDb : RoomDatabase() {

    abstract fun articleDao(): ArticleDao


}