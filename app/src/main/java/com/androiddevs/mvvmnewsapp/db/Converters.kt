package com.androiddevs.mvvmnewsapp.db

import androidx.room.TypeConverter
import com.androiddevs.mvvmnewsapp.Models.Source


//in the type converter class we need to define fromHereToWhere methods
class Converters {

    //from Source to string
    @TypeConverter
    fun fromSource(source: Source):String{

        //in the source class we care about name only
        return source.name
    }

    //from string to source
    @TypeConverter
    fun toSource(name:String): Source {
        return Source(name,name)
    }
}