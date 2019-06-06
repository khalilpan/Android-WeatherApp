package br.org.cesar.weatherapp.database

import android.arch.persistence.room.Database
import android.arch.persistence.room.RoomDatabase
import br.org.cesar.weatherapp.entity.FavoriteCity

@Database(entities = [FavoriteCity::class], version = 1)
abstract class WeatherDatabase : RoomDatabase() {

    abstract fun favoriteDao(): FavoriteDao

}