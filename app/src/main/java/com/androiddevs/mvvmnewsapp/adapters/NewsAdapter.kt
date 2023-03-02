package com.androiddevs.mvvmnewsapp.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.androiddevs.mvvmnewsapp.R
import com.androiddevs.mvvmnewsapp.Models.Article
import com.bumptech.glide.Glide
import kotlinx.android.synthetic.main.item_article_preview.view.*

//adapter - it adapt data in a format that can be consumed by recycler view
//recycle view require data from adapter and adapter provides it

//recycler view needs to 2 things to work -> adapter and viewHolder
//adapter is a intermediary between viewHolder and recyclerView
// we can define parameters or properties inside constructor
class NewsAdapter : RecyclerView.Adapter<NewsAdapter.ArticleViewHolder>() {

    //ViewHolder-> store view
    //viewHolder can't work solely it needs adapter so we make inner class
    //as there no need to create a separate class (although we can do it)
    //we can store all references view inside viewHolder before hand and use anywhere in adapter class
    inner class ArticleViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)

    //diffUtil -> utility class that calculate difference between two lists
    //& output a list of update operations that converts the first list into second
    //there are two ways to implement this -> manual, ListAdapter

    //performance issue -> livedata provide a list and recycler view render full list
    //we can optimize it by rendering only new list item

    private val differCallback = object : DiffUtil.ItemCallback<Article>() {
        override fun areItemsTheSame(oldItem: Article, newItem: Article): Boolean {
            //usually we check id in this case we getting Article from id which doesn't have id by default
            return oldItem.url == newItem.url
        }

        override fun areContentsTheSame(oldItem: Article, newItem: Article): Boolean {
            return oldItem == newItem
        }
    }

    //asynchronous runs on background take two list and find difference
    val differ = AsyncListDiffer(this, differCallback)

    //create holder to store view/object (that visible on screen)
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ArticleViewHolder {
        //create view and store it
        return ArticleViewHolder(
            //layout inflater convert xml file to java object
            LayoutInflater.from(parent.context).inflate(
                R.layout.item_article_preview,
                parent,
                false
            )
        )

        /*
        mostly we do like this

        create a inflater
        val inflater = LayoutInflater.from(parent.context)

        create a view
        val view = inflater.inflate(R.layout.item_article_preview, parent, false)

        store view
        return ArticleViewHolder(view)
        */

    }

    override fun getItemCount(): Int {
        return differ.currentList.size
    }

    private var onItemClickListener: ((Article) -> Unit)? = null

    //based on position it bind the data into view that visible on screen
    //now we created view and want to set data in it based on position
    override fun onBindViewHolder(holder: ArticleViewHolder, position: Int) {
        val article = differ.currentList[position]

        //set holder at one shot
        holder.itemView.apply {
            //we can't directly bind image
            //we require libraries for that (Glide is one of them)
            Glide.with(this).load(article.urlToImage).into(ivArticleImage)
            tvSource.text = article.source.name
            tvTitle.text = article.title
            tvDescription.text = article.description
            tvPublishedAt.text = article.publishedAt

            //set clickListener
            setOnClickListener {
                //it refers to onItemClickListener lambda function
                onItemClickListener?.let { it(article) }
            }
        }
    }

    fun setOnItemClickListener(listener: (Article) -> Unit) {
        onItemClickListener = listener
    }
}