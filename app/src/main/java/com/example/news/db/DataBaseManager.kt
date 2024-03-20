package com.example.news.db

import android.content.Context
import androidx.room.Room

object DataBaseManager {
    @Volatile
    private var INSTANCE: ArticleDb? = null

    @Synchronized
    fun getInstance(context: Context): ArticleDb {
        return INSTANCE ?: synchronized(this) {
            INSTANCE ?: buildDatabase(context).also { INSTANCE = it }
        }
    }

    private fun buildDatabase(context: Context): ArticleDb {
        return Room.databaseBuilder(
            context.applicationContext,
            ArticleDb::class.java,
            "article_db"
        ).fallbackToDestructiveMigration()
            .build()
    }
}