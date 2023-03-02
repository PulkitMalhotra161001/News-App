package com.androiddevs.mvvmnewsapp.UI.fragments

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.AbsListView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.androiddevs.mvvmnewsapp.R
import com.androiddevs.mvvmnewsapp.UI.NewsActivity
import com.androiddevs.mvvmnewsapp.UI.NewsViewModel
import com.androiddevs.mvvmnewsapp.adapters.NewsAdapter
import com.androiddevs.mvvmnewsapp.util.Constants.Companion.QUERY_PAGE_SIZE
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


        //to share data(in this case we have article) between fragments
        newsAdapter.setOnItemClickListener {

            //put it in a budle
            val bundle = Bundle().apply{
                //Serializable bcz this is a complex data type
                putSerializable("article",it)
            }

            //perform navigation and take action
            findNavController().navigate(R.id.action_breakingNewsFragment_to_articleFragment,bundle)
        }



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
                        //toList() convert mutable list to normal list
                        newsAdapter.differ.submitList(newsResponse.articles.toList())

                        //1 is for int division and 1 for page is always empty and we don't want to consider that
                        val totalPages = newsResponse.totalResults / QUERY_PAGE_SIZE + 2
                        isLastPage=viewModel.breakingNewsPage==totalPages
                        //if we are at our last page then reset the padding
                        if(isLastPage){
                            rvBreakingNews.setPadding(0,0,0,0)
                        }
                    }
                }

                is Resource.Error->{
                    hideProgressBar()
                    response.message?.let{message->
                        Log.e(TAG,"An error occurs: $message")
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
        isLoading=false
    }
    private fun showProgressBar() {
        paginationProgressBar.visibility=View.VISIBLE
        isLoading=true
    }

    //detect when we completely scroll down
    var isLoading = false
    var isLastPage = false
    var isScrolling = false

    //scroll listener for recycler view
    val scrollListener = object  : RecyclerView.OnScrollListener(){

        override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
            super.onScrolled(recyclerView, dx, dy)

            //check if we are on the bottom of our recycler view

            val layoutManager = recyclerView.layoutManager as LinearLayoutManager
            val firstVisibleItemPosition = layoutManager.findFirstVisibleItemPosition()
            val visibleItemCount = layoutManager.childCount
            val totalItemCount = layoutManager.itemCount

            val isNotLoadingAndNotLastPage = !isLoading && !isLastPage
            val isAtLastItem = firstVisibleItemPosition + visibleItemCount >= totalItemCount
            val isNotAtBeginning  = firstVisibleItemPosition>=0
            val isTotalMoreThanVisible = totalItemCount>=QUERY_PAGE_SIZE
            val shouldPaginate = isNotLoadingAndNotLastPage && isAtLastItem && isNotAtBeginning && isTotalMoreThanVisible && isScrolling

            if(shouldPaginate){
                viewModel.getBreakingNews("in")
                isScrolling=false
            }

        }
        override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
            super.onScrollStateChanged(recyclerView, newState)

            //check if we are currently scrolling
            if(newState == AbsListView.OnScrollListener.SCROLL_STATE_TOUCH_SCROLL){
                isScrolling=true
            }
        }
    }

    private fun setupRecyclerView(){
        //set adapter and layoutManager
        //adapter is a intermediary between viewHolder and recyclerView
        //layoutManager will tell how to define views (Grid, vertical etc.)
        newsAdapter= NewsAdapter()
        rvBreakingNews.apply{
            adapter=newsAdapter
            layoutManager=LinearLayoutManager(activity)
            addOnScrollListener(this@BreakingNewsFragment.scrollListener)
        }
    }

}