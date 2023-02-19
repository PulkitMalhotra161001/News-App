package com.androiddevs.mvvmnewsapp.Repository

import com.androiddevs.mvvmnewsapp.API.RetrofitInstance
import com.androiddevs.mvvmnewsapp.db.ArticleDatabase

class NewsRepository(
    val db:ArticleDatabase
) {
    //since network function is a suspend function we also make this
    suspend fun getBreakingNews(countryCode:String,pageNumber: Int) = RetrofitInstance.api.getBreakingNews(countryCode,pageNumber)
}