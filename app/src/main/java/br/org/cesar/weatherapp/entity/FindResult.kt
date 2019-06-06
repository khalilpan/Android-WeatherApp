package br.org.cesar.weatherapp.entity

import android.arch.persistence.room.ColumnInfo
import android.arch.persistence.room.Entity
import android.arch.persistence.room.PrimaryKey
import com.google.gson.annotations.SerializedName

data class FindResult(var list: List<City>)

data class City(
    var id: Int,
    var name: String,
    @SerializedName("weather") var weathers: List<Weather>,
    var main: Main,
    var clouds: Clouds,
    var wind: Wind,
    var sys: sys)

data class Weather(var main: String,var description:String, var icon: String)

@Entity(tableName = "tb_favorite_city")
data class FavoriteCity(
    @PrimaryKey
    var id: Int,
    @ColumnInfo(name = "city_name")
    var name: String)

data class Main(var temp:Float,
                var pressure:Float)

data class Clouds(var all:Number)

data class Wind(var speed:Float)

data class sys(var country:String)

