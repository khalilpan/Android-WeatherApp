package br.org.cesar.weatherapp.database

import android.arch.persistence.room.*
import br.org.cesar.weatherapp.entity.FavoriteCity
import retrofit2.http.DELETE

/**
 * Interface que descreve os métodos de acesso aos dados do Room
 */
@Dao
interface FavoriteDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(favoriteCity: FavoriteCity)

    @Delete
    fun delete(favoriteCity: FavoriteCity)

    @Query("SELECT * FROM tb_favorite_city")
    fun selectAll(): List<FavoriteCity>

    @Query("SELECT * FROM tb_favorite_city WHERE id = :id")
    fun selectById(id: Int): FavoriteCity?

}