package com.androiddevs.mvvmnewsapp.UI

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.androiddevs.mvvmnewsapp.Models.Article
import com.androiddevs.mvvmnewsapp.Models.NewsResponse
import com.androiddevs.mvvmnewsapp.Repository.NewsRepository
import com.androiddevs.mvvmnewsapp.util.Resource
import kotlinx.coroutines.launch
import retrofit2.Response

//we can't take parameter in ViewModel for that we need ViewModelProviderFactory
class NewsViewModel(
    val newsRepository: NewsRepository
) : ViewModel(){

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
        breakingNews.postValue(Resource.Loading())
        val response = newsRepository.getBreakingNews(countryCode,breakingNewsPage)
        breakingNews.postValue(handleBreakingNewsResponse(response))
    }

    fun searchNews(searchQuery:String) = viewModelScope.launch {
        searchNews.postValue(Resource.Loading())
        val response = newsRepository.searchNews(searchQuery,searchNewsPage)
        searchNews.postValue(handleSearchNewsResponse(response))
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
}