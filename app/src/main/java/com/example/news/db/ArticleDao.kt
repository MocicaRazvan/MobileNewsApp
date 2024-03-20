package com.example.news.db

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface ArticleDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(articleModel: ArticleModel)

    @Delete
    suspend fun delete(articleModel: ArticleModel)

    @Query("delete from article where title = :title")
    suspend fun deleteByTitle(title: String)


    @Query("select exists (select 1 from article where title = :title)")
    suspend fun existsByTitle(title: String): Boolean
}