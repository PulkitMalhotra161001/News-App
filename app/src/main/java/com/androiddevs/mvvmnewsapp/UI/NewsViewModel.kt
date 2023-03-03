package com.androiddevs.mvvmnewsapp.UI

import android.app.Application
import android.content.Context
import android.net.ConnectivityManager
import android.net.ConnectivityManager.TYPE_ETHERNET
import android.net.ConnectivityManager.TYPE_WIFI
import android.net.ConnectivityManager.TYPE_MOBILE
import android.net.NetworkCapabilities.*
import android.os.Build
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.androiddevs.mvvmnewsapp.Models.Article
import com.androiddevs.mvvmnewsapp.Models.NewsResponse
import com.androiddevs.mvvmnewsapp.NewsApplication
import com.androiddevs.mvvmnewsapp.Repository.NewsRepository
import com.androiddevs.mvvmnewsapp.util.Resource
import kotlinx.coroutines.launch
import retrofit2.Response
import java.io.IOException
import java.net.ContentHandler

//we can't take parameter in ViewModel for that we need ViewModelProviderFactory
//need to modify ViewModelFactory as we added Application as parameter
class NewsViewModel(
    app:Application,
    val newsRepository: NewsRepository
) : AndroidViewModel(app){
    //never paas context as a parameter (bad practice) -> we are separating activity data from UI
    //AndroidViewMode() same as ViewModel() but we can also use the application context

    //LiveData notify to our fragments about changes regarding corresponding requests
    //Resource handle our network responses
    val breakingNews:MutableLiveData<Resource<NewsResponse>> = MutableLiveData()
    //if we wrote in our fragment the page number will get reset whenever we rotate our device and viewModel doesn't get destroy
    var breakingNewsPage = 1
    var breakingNewsResponse :NewsResponse?=null

    val searchNews:MutableLiveData<Resource<NewsResponse>> = MutableLiveData()
    //if we wrote in our fragment the page number will get reset whenever we rotate our device and viewModel doesn't get destroy
    var searchNewsPage = 1
    var searchNewsResponse:NewsResponse?=null



    init{
        //call the function to getBreakingNews
        getBreakingNews("in")
    }

    //by the help of viewModelScope we can make coroutine and only alive as long as our viewModel alive
    //we can't make this function suspend because then we have to initialize in our fragment and we don't want that
    fun getBreakingNews(countryCode:String) = viewModelScope.launch {
        safeBreakingNewsCall(countryCode)
    }

    fun searchNews(searchQuery:String) = viewModelScope.launch {
safeSearchNewsCall(searchQuery)
    }

    private fun handleBreakingNewsResponse(response:Response<NewsResponse>) :Resource<NewsResponse>{
        if(response.isSuccessful){
            //check if body is not null
            response.body()?.let{resultResponse->



                //handling more than one page(20 like google) news
                breakingNewsPage++

                //first response
                if(breakingNewsResponse == null){
                    breakingNewsResponse = resultResponse
                }else{
                    //not the first page
                    //add newResponse(resultResponse) to our oldResponse

                    val oldArticles= breakingNewsResponse?.articles
                    val newArticles = resultResponse.articles
                    oldArticles?.addAll(newArticles)
                }
                //breakingNewsResponse is null (first response then) resultResponse instead
                return Resource.Success(breakingNewsResponse?:resultResponse)
            }
        }

        return Resource.Error(response.message())
    }

    private fun handleSearchNewsResponse(response:Response<NewsResponse>) :Resource<NewsResponse>{
        if(response.isSuccessful){
            //check if body is not null
            response.body()?.let{resultResponse->

                //handling more than one page(20 like google) news
                searchNewsPage++

                //first response
                if(searchNewsResponse == null){
                    searchNewsResponse = resultResponse
                }else{
                    //not the first page
                    //add newResponse(resultResponse) to our oldResponse

                    val oldArticles= searchNewsResponse?.articles
                    val newArticles = resultResponse.articles
                    oldArticles?.addAll(newArticles)
                }
                //breakingNewsResponse is null (first response then) resultResponse instead
                return Resource.Success(searchNewsResponse?:resultResponse)
            }
        }

        return Resource.Error(response.message())
    }

    //so fragments can call these (function from NewsRepository)

    //since this is a suspend function we need to start a coroutine
    fun saveArticle(article: Article) = viewModelScope.launch {
        newsRepository.upsert(article)
    }

    fun getSavedNews() = newsRepository.getSavedNews()

    fun deleteArticle(article: Article) = viewModelScope.launch {
        newsRepository.deleteArticle(article)
    }

    private suspend fun safeBreakingNewsCall(countryCode: String){
        breakingNews.postValue(Resource.Loading())
        try{
            if(hasInternetConnection()) {
                var response = newsRepository.getBreakingNews(countryCode, breakingNewsPage)
//        Log.d("NewsViewModel",response.toString())
                if (response.body()?.articles?.get(0)?.urlToImage == null) {
                    response = newsRepository.searchNews("india", searchNewsPage)
                }
//        Log.d("NewsViewModel", response.body()?.articles?.get(0)?.urlToImage.toString())
                breakingNews.postValue(handleBreakingNewsResponse(response))
            }else{
                breakingNews.postValue(Resource.Error("No internet connection"))
            }
        }catch(t:Throwable){
            when(t){
                is IOException->breakingNews.postValue(Resource.Error("Network Failure"))
                else -> breakingNews.postValue(Resource.Error("Conversion Error"))
            }
        }
    }
    private suspend fun safeSearchNewsCall(searchQuery: String){
        searchNews.postValue(Resource.Loading())
        try{
            if(hasInternetConnection()) {
                val response = newsRepository.searchNews(searchQuery,searchNewsPage)
                searchNews.postValue(handleSearchNewsResponse(response))
            }else{
                searchNews.postValue(Resource.Error("No internet connection"))
            }
        }catch(t:Throwable){
            when(t){
                is IOException->searchNews.postValue(Resource.Error("Network Failure"))
                else -> searchNews.postValue(Resource.Error("Conversion Error"))
            }
        }
    }

    //check if user is connected to the internet or not
    private fun hasInternetConnection():Boolean{
        //we need connectivityManager(System service, requires context)
        val connectivityManager = getApplication<NewsApplication>().getSystemService(
            Context.CONNECTIVITY_SERVICE
        ) as ConnectivityManager

        if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.M){
            val activityNetwork = connectivityManager.activeNetwork?:return false
            //just for checking network state
            val capabilities = connectivityManager.getNetworkCapabilities(activityNetwork)?:return false
            return when{
//                checkout network
                capabilities.hasTransport(TRANSPORT_WIFI) -> true
                capabilities.hasTransport(TRANSPORT_CELLULAR) -> true
                capabilities.hasTransport(TRANSPORT_ETHERNET) -> true
                else -> false
            }
        }else{
            connectivityManager.activeNetworkInfo?.run{
                return when(type){
                    TYPE_WIFI->true
                    TYPE_MOBILE->true
                    TYPE_ETHERNET->true
                    else->false
                }
            }
        }
        return false
    }
}