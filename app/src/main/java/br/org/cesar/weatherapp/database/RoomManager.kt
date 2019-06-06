package br.org.cesar.weatherapp.database

import android.arch.persistence.room.Room
import android.content.Context

/**
 * Classe reponsável por gerenciar a instância do Room
 */
object RoomManager {

    /**
     * Método que retorna uma instância única do banco de dados (WeatherDatabase)
     */
    fun instance(context: Context) = Room.databaseBuilder(
        context,
        WeatherDatabase::class.java, "db")
        .allowMainThreadQueries() // Permite que o room faça execuções na main thread. ESTA NÃO É UMA BOA PRÁTICA
        .build()
}