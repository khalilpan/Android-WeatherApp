package br.org.cesar.weatherapp.api

import br.org.cesar.weatherapp.entity.FindResult
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

/**
 * Interface que descreve os serviços, verbos HTTP (GET, POST, PUT, DELETE) e parâmetros (Query, Path) dos end points
 */
interface WeatherServices {

    @GET("find")
    fun find(
        @Query("q")
        cityName: String,

        @Query("lang")
        lang:String,

        @Query("units")
        units: String,

        @Query("appid")
        appKey: String): Call<FindResult>

    //====================================

        @GET("group")
        fun findGroup(
            @Query("id")
            id: String,

            @Query("lang")
            lang:String,

            @Query("units")
            units: String,

            @Query("appid")
            appKey: String): Call<FindResult>
}