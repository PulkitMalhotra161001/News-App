package com.androiddevs.mvvmnewsapp.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.androiddevs.mvvmnewsapp.Models.Article


//this is our database class
@Database(
    entities = [Article::class],
    version = 1
)

//room is extended version of SQLlite
//when we look into our data/entity(article class) we have source field but database doesn't support custom field
//so to use it we made typeConverter class
@TypeConverters(Converters::class)

//database class for room always need to be abstract class (data hiding)
abstract class ArticleDatabase : RoomDatabase(){

    //created an object to access functions (implementation is handled by room)
    abstract fun getArticleDao(): ArticleDao

    //it can be called without having the instance
    //you can access the members of the class by class name only
    companion object{

        //volatile is a keyword to inform all threads
        @Volatile

        //created an instance
        private var instance:ArticleDatabase?=null

        //used to synchronization
        private val LOCK = Any()

        //this funtions will be called when object created
        //synchronization used because we want that only one thread can access database at a time
        operator fun invoke(context: Context)=instance?: synchronized(LOCK){

            //created database and put/include instance
            instance?:createDatabase(context).also{ instance=it}

        }

        //this is the way to create database
        private fun createDatabase(context: Context)=
            Room.databaseBuilder(
                    context.applicationContext,
                ArticleDatabase::class.java,
                "article_db.db"
            ).build()
    }

}