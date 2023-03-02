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
) : java.io.Serializable
//we declared it as a Serializable so that we can share this class between fragments
//to declare this class as a argument for our Article Fragment (add argument in nav graph)