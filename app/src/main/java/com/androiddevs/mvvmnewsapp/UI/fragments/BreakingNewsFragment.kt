package com.androiddevs.mvvmnewsapp.UI.fragments

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.androiddevs.mvvmnewsapp.R
import com.androiddevs.mvvmnewsapp.UI.NewsActivity
import com.androiddevs.mvvmnewsapp.UI.NewsViewModel
import com.androiddevs.mvvmnewsapp.adapters.NewsAdapter
import com.androiddevs.mvvmnewsapp.util.Resource
import kotlinx.android.synthetic.main.fragment_breaking_news.*

class BreakingNewsFragment: Fragment(R.layout.fragment_breaking_news) {


    lateinit var viewModel:NewsViewModel
    lateinit var newsAdapter: NewsAdapter
    val TAG = "BreakingNewsFragment"
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel = (activity as NewsActivity).viewModel
        setupRecyclerView()


        //breakingNews contains liveData and contains all NewsResponse
        //we set a observer on viewModel
        viewModel.breakingNews.observe(viewLifecycleOwner, Observer{response->
            when(response){

                //when response is successful
                is Resource.Success->{
                    //we set ProgressBar in NewsViewModel getBreakingNews
                    hideProgressBar()
                    //check data is not null
                    response.data?.let{newsResponse ->
                        //set newsResponse in the adapter
                        //differ is used to check the difference between old and new list
                        newsAdapter.differ.submitList(newsResponse.articles)
                    }
                }

                is Resource.Error->{
                    hideProgressBar()
                    response.message?.let{message->
                        Log.e(TAG,"An error occures: $message")
                    }
                }

                is Resource.Loading->{
                    showProgressBar()
                }
            }

        })
    }

    private fun hideProgressBar() {
        paginationProgressBar.visibility=View.INVISIBLE
    }
    private fun showProgressBar() {
        paginationProgressBar.visibility=View.VISIBLE
    }

    private fun setupRecyclerView(){
        //set adapter and layoutManager
        //adapter is a intermediary between viewHolder and recyclerView
        //layoutManager will tell how to define views (Grid, vertical etc.)
        newsAdapter= NewsAdapter()
        rvBreakingNews.apply{
            adapter=newsAdapter
            layoutManager=LinearLayoutManager(activity)
        }
    }

}