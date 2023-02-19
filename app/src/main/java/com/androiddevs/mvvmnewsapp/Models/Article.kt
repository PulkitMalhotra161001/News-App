package com.androiddevs.mvvmnewsapp.Models

import androidx.room.Entity
import androidx.room.PrimaryKey

//to save our article in our database we need annotate entity
//we tell android studio that this class is a table in database
@Entity(
    tableName = "articles"
)

data class Article(

    //we create id object and declare it as primary key in database
    @PrimaryKey(autoGenerate = true)
    var id:Int?=null,
    val author: String,
    val content: String,
    val description: String,
    val publishedAt: String,
    val source: Source,
    val title: String,
    val url: String,
    val urlToImage: String
)