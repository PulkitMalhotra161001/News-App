package com.androiddevs.mvvmnewsapp.UI

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.setupWithNavController
import com.androiddevs.mvvmnewsapp.R
import com.androiddevs.mvvmnewsapp.Repository.NewsRepository
import com.androiddevs.mvvmnewsapp.db.ArticleDatabase
import kotlinx.android.synthetic.main.activity_news.*
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class NewsActivity : AppCompatActivity() {

    //for viewModel parameter we require ViewModelFactory
    //for ViewModelFactory we require Repository
    lateinit var viewModel: NewsViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_news)

        val newsrepository=NewsRepository(ArticleDatabase(this))
        val viewModelProviderFactory = NewsViewModelProviderFactory(application,newsrepository)

        viewModel = ViewModelProvider(this,viewModelProviderFactory).get(NewsViewModel::class.java)


        bottomNavigationView.setupWithNavController(newsNavHostFragment.findNavController())

        //set the adapter to list
        //we need to set layoutManager(tell how to display views -> grid, linear) to list

    }
}
