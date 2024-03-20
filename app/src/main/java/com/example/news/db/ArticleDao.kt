package com.example.news.db

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.google.firebase.auth.FirebaseAuth

@Dao
interface ArticleDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(articleModel: ArticleModel)

    @Delete
    suspend fun delete(articleModel: ArticleModel)

    @Query("delete from article where title = :title and userId = :userId")
    suspend fun deleteByTitle(
        title: String,
        userId: String = FirebaseAuth.getInstance().currentUser?.uid ?: ""
    )


    @Query("select exists (select 1 from article where title = :title and userId = :userId)")
    suspend fun existsByTitle(
        title: String,
        userId: String = FirebaseAuth.getInstance().currentUser?.uid ?: ""
    ): Boolean

    @Query("SELECT * FROM article WHERE LOWER(title) LIKE '%' || LOWER(:title) || '%' and userId = :userId")
    suspend fun getArticlesByTitleLike(
        title: String,
        userId: String = FirebaseAuth.getInstance().currentUser?.uid ?: ""
    ): List<ArticleModel>

}