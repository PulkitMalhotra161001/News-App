package com.androiddevs.mvvmnewsapp.UI.fragments

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.androiddevs.mvvmnewsapp.R
import com.androiddevs.mvvmnewsapp.UI.NewsActivity
import com.androiddevs.mvvmnewsapp.UI.NewsViewModel
import com.androiddevs.mvvmnewsapp.adapters.NewsAdapter
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.fragment_saved_news.*
import kotlinx.android.synthetic.main.fragment_search_news.*

class SavedNewsFragment: Fragment(R.layout.fragment_saved_news) {

    lateinit var viewModel: NewsViewModel
    lateinit var newsAdapter:NewsAdapter
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {


        super.onViewCreated(view, savedInstanceState)
        viewModel = (activity as NewsActivity).viewModel
        setupRecyclerView()

        //to share data(in this case we have article) between fragments
        newsAdapter.setOnItemClickListener {

            //put it in a bundle
            val bundle = Bundle().apply{
                //Serializable bcz this is a complex data type
                putSerializable("article",it)
            }

            //perform navigation and take action
            findNavController().navigate(R.id.action_savedNewsFragment_to_articleFragment,bundle)
        }


        //for left swipe and delete
        //anonymous
        val itemTouchHelperCallback = object:ItemTouchHelper.SimpleCallback(

            //directions we drag our recyclerView
            ItemTouchHelper.UP or ItemTouchHelper.DOWN,

            //directions we want to swipe items
            ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT
        ){
            //ctrl+i
            override fun onMove(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder
            ): Boolean {
                return true
            }

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val position  = viewHolder.adapterPosition
                val article = newsAdapter.differ.currentList[position]
                viewModel.deleteArticle(article)

                //undo
                Snackbar.make(view,"Successfully deleted article",Snackbar.LENGTH_LONG).apply {
                    setAction("Undo"){
                        viewModel.saveArticle(article)
                    }

                    //show snackbar
                    show()
                }
            }
        }

        //since itemTouchHelperCallback is just a Callback not a real itemTouchHelper
        ItemTouchHelper(itemTouchHelperCallback).apply {
            attachToRecyclerView(rvSavedNews)
        }


        //observe changes in database
        viewModel.getSavedNews().observe(viewLifecycleOwner, Observer {articles->
            //update recyclerView ( .differ calculate difference and update it)
            newsAdapter.differ.submitList(articles)
        })
    }

    private fun setupRecyclerView(){
        //set adapter and layoutManager
        //adapter is a intermediary between viewHolder and recyclerView
        //layoutManager will tell how to define views (Grid, vertical etc.)
        newsAdapter= NewsAdapter()
        rvSavedNews.apply{
            adapter=newsAdapter
            layoutManager= LinearLayoutManager(activity)
        }
    }

}