package com.androiddevs.mvvmnewsapp.db

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.androiddevs.mvvmnewsapp.Models.Article

//dao means data access object
//it is same as NewsAPI
//there we define function how we access api
//here we define function how we access local database
@Dao
interface ArticleDao {

    //onConflict means what if data is already present in our database
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    //insert or update
    //suspend function because we want to accees inside our coroutine
    suspend fun upsert(article: Article): Long

    //select all from article table
    //we declared tablename in article class
    @Query("SELECT * FROM articles")
    //this will not be the suspend fun because it will return livedata and this doesn't work with suspend func
    fun getAllArticles(): LiveData<List<Article>>

    @Delete
    suspend fun deleteArticle(article: Article)

}