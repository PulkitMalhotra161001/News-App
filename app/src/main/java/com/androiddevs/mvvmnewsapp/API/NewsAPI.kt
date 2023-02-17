package com.androiddevs.mvvmnewsapp.API

import com.androiddevs.mvvmnewsapp.NewsResponse
import com.androiddevs.mvvmnewsapp.util.Constants.Companion.API_KEY
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface NewsAPI {

//    retrofit requests => get, put, post, delete

    //this is a network call func
    //we need to execute this func asynchronously
    //best way is coroutines
    //to use coroutines we create suspend func
    @GET("v2/top-headlines")
    //we want to access our request in a coroutine that's why we added "suspend" keyword
    //network request should always be asynchronously
    suspend fun getBreakingNews(

        //request/query parameter
        @Query("country")
        countryCode: String = "in",

// page is the name of the query parameter that api accepts
        @Query("page")
        pageNumber: Int=1,

//        api takes apikey (depends on api docs), authorize that u are allowed to access the api, we need to pass apikey to each requests
        @Query("apiKey")
        apiKey:String=API_KEY


    ): Response<NewsResponse>

    @GET("v2/everything")
    suspend fun searchForNews(

        //request parameter
        @Query("q")
        searchQuery: String,

        @Query("page")
        pageNumber: Int=1,

        @Query("apiKey")
        apiKey:String=API_KEY


    ): Response<NewsResponse>

}