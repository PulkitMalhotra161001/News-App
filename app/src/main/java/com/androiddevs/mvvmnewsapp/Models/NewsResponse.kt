package com.androiddevs.mvvmnewsapp.Models

import com.androiddevs.mvvmnewsapp.Models.Article

data class NewsResponse(
    //MutableList so we can add articles to that
    //new articles to old list
    val articles: MutableList<Article>,
    val status: String,
    val totalResults: Int
)