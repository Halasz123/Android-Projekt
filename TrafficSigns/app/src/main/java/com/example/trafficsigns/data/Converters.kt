package com.example.trafficsigns.data

import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.time.Instant
import javax.xml.transform.Source

class Convertes {
    @TypeConverter
    fun fromTrafficSignsCollectionToString(source: TrafficSignsCollection) :String{
        return Gson().toJson(source)
    }

    @TypeConverter
    fun  fromStringToTrafficSignsCollection(string: String): TrafficSignsCollection {
        return Gson().fromJson(string, TrafficSignsCollection::class.java)
    }

    @TypeConverter
    fun fromTrafficSignToString(source: TrafficSign) :String{
        return Gson().toJson(source)
    }

    @TypeConverter
    fun  fromStringToTrafficSign(string: String): TrafficSign {
        return Gson().fromJson(string, TrafficSign::class.java)
    }

    @TypeConverter
    fun fromTrafficSignsToString(source: List<TrafficSign>) :String{
        return Gson().toJson(source)
    }

    @TypeConverter
    fun  fromStringToTrafficSigns(string: String): List<TrafficSign> {
        val type = object : TypeToken<List<TrafficSign>>() {}.type
        return Gson().fromJson(string, type)
    }

    @TypeConverter
    fun fromIntsToString(source: ArrayList<Int>): String{
        return Gson().toJson(source)
    }

    @TypeConverter
    fun  fromStringToInts(string: String): ArrayList<Int> {
        val type = object : TypeToken<ArrayList<Int>>() {}.type
        return Gson().fromJson(string, type)
    }
    @TypeConverter
    fun fromTrafficSignsToString2(source: ArrayList<TrafficSign>) :String{
        return Gson().toJson(source)
    }

    @TypeConverter
    fun  fromStringToTrafficSigns2(string: String): ArrayList<TrafficSign> {
        val type = object : TypeToken<ArrayList<TrafficSign>>() {}.type
        return Gson().fromJson(string, type)
    }

}