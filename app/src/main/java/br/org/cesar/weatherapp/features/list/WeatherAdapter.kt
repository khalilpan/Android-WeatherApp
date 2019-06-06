package br.org.cesar.weatherapp.features.list

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.text.BoringLayout
import android.text.TextUtils
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import br.org.cesar.weatherapp.Constants
import br.org.cesar.weatherapp.R
import br.org.cesar.weatherapp.database.RoomManager
import br.org.cesar.weatherapp.entity.City
import br.org.cesar.weatherapp.entity.FavoriteCity
import com.bumptech.glide.Glide
import kotlinx.android.synthetic.main.row_city_layout.view.*

/**
 *
 * Classe que cria um Adapter para o nosso RecyclerView
 * @sample https://medium.com/android-dev-br/listas-com-recyclerview-d3f41e0d653c
 *
 */
class WeatherAdapter(private val context: Context,private val callback: (City) -> Unit) : RecyclerView.Adapter<WeatherAdapter.MyViewHolder>() {
//    private val context: Context,
    private var list: List<City>? = null



    /**
     * Método responsável por inflar a view e retornar um ViewHolder
     */
    override fun onCreateViewHolder(
        viewGroup: ViewGroup,
        position: Int): MyViewHolder {

        val view = LayoutInflater.from(viewGroup.context)
            .inflate(R.layout.row_city_layout, viewGroup, false)



        return MyViewHolder(view)
    }

    /**
     * Método que retorna a quantidade de itens da lista
     *
     * Aqui utilizamos o operador Elvis Operator ?:
     * https://www.concrete.com.br/2017/06/21/kotlin-no-tratamento-de-erros/
     */
    override fun getItemCount() = list?.size ?: 0

    /**
     * Método responsável por realizar o bind da View com o item
     *
     * @param vh Nosso viewholder criado para reciclar as views
     * @param position posição do item que será inflado no recyclerview
     */
    override fun onBindViewHolder(vh: MyViewHolder, position: Int) {
        list?.let {
            vh.bind(it[position], callback)
        }
    }

    /**
     * Classe responsável por fazer o bind da View com o objeto City
     */
    class MyViewHolder(view: View) : RecyclerView.ViewHolder(view) {

        /**
         * Método que faz o bind
         *
         * @param city objeto a ser exibido
         * @param callback expressão lambda que será invokada quando a view for clicada/tocada
         */


        fun bind(city: City, callback: (City) -> Unit) {

            itemView.tvCityCountry.text = "${city.name}, ${city.sys.country}"
            itemView.tvTemp.text="${city.main.temp.toInt()}"
            itemView.tvClouds.text="${city.weathers[0].description}"
            itemView.tvDetails.text="wind ${city.wind.speed} m/s | clouds ${city.clouds.all}% | ${city.main.pressure.toInt()} hpa"
            itemView.tvTempType.text=getTempType()
            itemView.tvFavorita.setBackgroundResource(getFavoriteIcon(city))
            /**
             * Glide é uma lib opensource para facilitar o carregamento de imagens de forma eficiente
             * @sample https://github.com/bumptech/glide
              */
            Glide.with(itemView.context)
                .load("http://openweathermap.org/img/w/${city.weathers[0].icon}.png")
                .placeholder(R.drawable.w_01d)
                .into(itemView.imgIcon)

            itemView.setOnClickListener {
//                callback(city)
            }
            itemView.tvFavorita.setOnClickListener {

                if (getFavoritaStatus(city).equals(false)){
                    //insert
                    itemView.tvFavorita.setBackgroundResource(R.drawable.favorite)
                    callback(city)
                }else{
                    //delete
                    callback(city)
                    itemView.tvFavorita.setBackgroundResource(R.drawable.notfavorite)
                }

            }
        }

        fun getFavoriteIcon(city: City):Int{
            if (getFavoritaStatus(city)) {
             return R.drawable.favorite
            }else{
                return R.drawable.notfavorite
            }
        }

        fun getFavoritaStatus(city: City):Boolean{

            ListActivity.favoriteCities?.forEach {
                if (it.id===city.id) {
                    return true
                }
            }
            return false
        }


        fun getTempType():String?{

            if (ListActivity.isTempC2!!) {
                return "°C"
            }else{
                return "°F"
            }
        }
    }

    /**
     * Método responsável por atualizar os itens do recyclerview
     */
    fun updataData(list: List<City>) {
        this.list = list
        notifyDataSetChanged()
    }



}