package com.androiddevs.mvvmnewsapp.API

import com.androiddevs.mvvmnewsapp.util.Constants.Companion.BASE_URL
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

//if we used dependency injection(dagger hilt) then we don't need this
//we use instance to tell retrofit we want this, this methods
//HTTPS API requests gave json object

//second step to create instance and we create singleton bcz we want only one object throughout the project
class RetrofitInstance {

    //if we want to access functions without making objects
    //class have only one companion object
    companion object {

        //lazy means object will be created only when it is called
        private val retrofit by lazy {

            //log.d responses of retrofit
            val logging = HttpLoggingInterceptor()
            logging.setLevel(HttpLoggingInterceptor.Level.BODY)

            //use logging Interceptor to create client
            val client = OkHttpClient.Builder()
                .addInterceptor(logging)
                .build()

            //use our client to pass it our retrofit instance
            //mostly we wrote this (without client)
            // .create(TodoApi::class.java) if this is not a api yet
            Retrofit.Builder()
                .baseUrl(BASE_URL)
                    //it used to determine how our result is interpreted and determined in kotlin object
                    //create json to objects (supported by retrofit)
                .addConverterFactory(GsonConverterFactory.create())
                .client(client)
                .build()
        }

        //api object instance from retrofit builder
        //we will use this to make network requests
        val api by lazy {
            retrofit.create(NewsAPI::class.java)
        }
    }
}