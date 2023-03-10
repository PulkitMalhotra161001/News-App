package com.androiddevs.mvvmnewsapp.Repository

import com.androiddevs.mvvmnewsapp.API.RetrofitInstance
import com.androiddevs.mvvmnewsapp.Models.Article
import com.androiddevs.mvvmnewsapp.db.ArticleDatabase

//newsRepository work is to get data from database and api
//we don't need to pass api we can directly from RetrofitInstance companion object
class NewsRepository(
    val db:ArticleDatabase
) {
    //since network function is a suspend function we also need to make this suspend function
    suspend fun getBreakingNews(countryCode:String,pageNumber: Int) = RetrofitInstance.api.getBreakingNews(countryCode,pageNumber)

    suspend fun searchNews(searchQuery:String,pageNumber: Int) = RetrofitInstance.api.searchForNews(searchQuery, pageNumber)

    //all function from (ArticleDao) for save and delete
    suspend fun upsert(article: Article) = db.getArticleDao().upsert(article)

    fun getSavedNews() = db.getArticleDao().getAllArticles()

    suspend fun deleteArticle(article: Article) = db.getArticleDao().deleteArticle(article)

    //next layer is newsViewModel
}