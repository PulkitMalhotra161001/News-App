package com.androiddevs.mvvmnewsapp.Models

import com.androiddevs.mvvmnewsapp.Models.Article

data class NewsResponse(
    val articles: List<Article>,
    val status: String,
    val totalResults: Int
)