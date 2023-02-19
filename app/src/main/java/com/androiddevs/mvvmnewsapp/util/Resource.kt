package com.androiddevs.mvvmnewsapp.util

//this is used to group different related objects together
//in sealed we can define individual behaviour whether in enum we can't
//wrap around our network responses -> Recommended by Google (Generic class)
//sealed class kind of a abstract class but we can define which classes are allowed inherited from Resource class
sealed class Resource<T>(
    val data:T?=null,
    val message:String?=null
) {
    //resource class (inherit from Resource class)
    class Success<T>(data:T) : Resource<T>(data)
    class Error<T>(message:String,data:T?=null) : Resource<T>(data,message)
    class Loading<T>:Resource<T>()
}