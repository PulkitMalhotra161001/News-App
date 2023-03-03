package com.androiddevs.mvvmnewsapp.adapters

import android.util.Log
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


//    Article(id=null, author=Moneycontrol, content=null, description=null, publishedAt=2023-03-01T04:32:49Z, source=Source(id=google-news, name=Google News), title=Market LIVE Updates: Sensex up 300 pts, Nifty around 17,400; metal, auto, banks in focus - Moneycontrol, url=https://news.google.com/rss/articles/CBMiiAJodHRwczovL3d3dy5tb25leWNvbnRyb2wuY29tL25ld3MvYnVzaW5lc3MvbWFya2V0cy9zaGFyZS1tYXJrZXQtbGl2ZS11cGRhdGVzLXN0b2NrLW1hcmtldC10b2RheS1tYXJjaC0wMS1sYXRlc3QtbmV3cy1ic2UtbnNlLXNlbnNleC1uaWZ0eS1jb3ZpZC1jb3JvbmF2aXJ1cy1wb3dlci1ncmlkLXp5ZHVzLWxpZmVzY2llbmNlcy10YXRhLXBvd2VyLXZvZGFmb25lLWlkZWEtYmhhcmF0LWVsZWN0cm9uaWNzLWFkaXR5YS1iaXJsYS1jYXBpdGFsLTEwMTc3OTcxLmh0bWzSAYwCaHR0cHM6Ly93d3cubW9uZXljb250cm9sLmNvbS9uZXdzL2J1c2luZXNzL21hcmtldHMvc2hhcmUtbWFya2V0LWxpdmUtdXBkYXRlcy1zdG9jay1tYXJrZXQtdG9kYXktbWFyY2gtMDEtbGF0ZXN0LW5ld3MtYnNlLW5zZS1zZW5zZXgtbmlmdHktY292aWQtY29yb25hdmlydXMtcG93ZXItZ3JpZC16eWR1cy1saWZlc2NpZW5jZXMtdGF0YS1wb3dlci12b2RhZm9uZS1pZGVhLWJoYXJhdC1lbGVjdHJvbmljcy1hZGl0eWEtYmlybGEtY2FwaXRhbC0xMDE3Nzk3MS5odG1sL2FtcA?oc=5, urlToImage=null)
//    Article(id=null, author=Jake Peterson, content=Two-factor authentication (2FA) is essential for securing your accounts these days. It isnt enough to have a password anymore. Between password leaks, and weak and reused passwords, its too easy for … [+6092 chars], description=Two-factor authentication (2FA) is essential for securing your accounts these days. It isn’t enough to have a password anymore. Between password leaks, and weak and reused passwords, it’s too easy for hackers to figure out your secrets and break into your acc…, publishedAt=2023-02-21T22:30:00Z, source=Source(id=null, name=Lifehacker.com), title=The Best Authenticator Apps for iPhone and Android, url=https://lifehacker.com/the-best-authenticator-apps-for-iphone-and-android-1850140802, urlToImage=https://i.kinja-img.com/gawker-media/image/upload/c_fill,f_auto,fl_progressive,g_center,h_675,pg_1,q_80,w_1200/4a197ec6dd6f16873227408161c3b9c0.jpg)
    override fun onBindViewHolder(holder: ArticleViewHolder, position: Int) {
        val article = differ.currentList[position]

        //set holder at one shot
        holder.itemView.apply {
            //we can't directly bind image
            //we require libraries for that (Glide is one of them)
//            Log.d("NewsAdapter",article.toString())
//          for Google News => description,urlToImage is null
            if(article.urlToImage==null){
                Glide.with(this).load(R.drawable.no_image).into(ivArticleImage)
//                Log.d("NewsAdapter","Google: "+ article.url.split("=")[1])
//                Log.d("NewsAdapter","Google: "+ article+" "+ article.url?.split("=")!![1])
            }else{
                Glide.with(this).load(article.urlToImage).into(ivArticleImage)
//                Log.d("NewsAdapter", article.toString())
            }
            tvSource.text = article.source?.name
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