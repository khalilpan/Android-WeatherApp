package br.org.cesar.weatherapp.api

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

/**
 * Classe responsável por gerenciar a instância do Retrofit
 */
class RetrofitManager {

    // Cria uma instância do Retrofit
    private val retrofit = Retrofit.Builder()
        .baseUrl("http://api.openweathermap.org/data/2.5/")
        .addConverterFactory(GsonConverterFactory.create())
        .build()


    // Cria a implementação da interface WeateherServices
    fun weatherService() = retrofit.create(WeatherServices::class.java)

}