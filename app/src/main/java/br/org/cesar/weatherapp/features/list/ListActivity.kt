package br.org.cesar.weatherapp.features.list

import android.content.Context
import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import br.org.cesar.weatherapp.features.setting.SettingActivity
import android.net.ConnectivityManager
import android.os.AsyncTask
import android.support.v7.widget.LinearLayoutManager
import android.text.TextUtils
import android.util.Log
import android.view.KeyEvent
import android.view.View
import android.widget.Toast
import br.org.cesar.weatherapp.Constants
import br.org.cesar.weatherapp.R
import br.org.cesar.weatherapp.api.RetrofitManager
import br.org.cesar.weatherapp.database.RoomManager
import br.org.cesar.weatherapp.entity.City
import br.org.cesar.weatherapp.entity.FavoriteCity
import br.org.cesar.weatherapp.entity.FindResult
import kotlinx.android.synthetic.main.activity_main.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ListActivity : AppCompatActivity() {

    val adapter = WeatherAdapter(this){ saveFavorite(it) }

    private val prefs by lazy {
        getSharedPreferences(Constants.PREF_NAME, Context.MODE_PRIVATE)
    }

    var isTempC:Boolean? = null
    var isLangEn:Boolean? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        readPrefs()
        updateCompanionObjects()

//        favoriteCities?.forEach {
//            Log.i("test",it.name+" , = "+it.id)
//        }

        initUI()
    }

    override fun onResume() {
        super.onResume()
        readPrefs()
        updateCompanionObjects()
        initUI()
    }

    /**
     * Salva uma cidade favorita no bando de dados de forma SÍNCRONA
     */
    private fun saveFavorite(city: City) {
        RoomManager.instance(this).favoriteDao().apply {
            val (id,name) = city
            if (getCityStatusInDB(city)){
                delete(FavoriteCity(id,name))
                Toast.makeText(applicationContext,"${city.name} Removed from Favorite list",Toast.LENGTH_SHORT).show()
                refreshList()
            }else {
                insert(FavoriteCity(id, name))
                Toast.makeText(applicationContext,"${city.name} added to Favorite List",Toast.LENGTH_SHORT).show()
                refreshList()
            }

            ListActivity.favoriteCities=getFavoriteCitiesList()
            selectAll().forEach {
                Log.d("w", it.name)
            }
        }
    }

    /**
     * Salva uma cidade favorita no banco de dados de forma ASSÍNCRONA
     */
    private fun saveFavoriteAsync(city: City) {
        FavoriteAsync(this).execute(city)
    }

    /**
     * Classe que herda de AsyncTask para salvar a cidade favorita de forma assíncrona
     */
    class FavoriteAsync(val context: Context) : AsyncTask<City, Void, List<FavoriteCity>>() {

        /**
         * Método executado em segundo plano
         */
        override fun doInBackground(vararg params: City?): List<FavoriteCity> {
            RoomManager.instance(context).favoriteDao().apply {
                params[0]?.let {
                    val (id,name) = it
                    insert(FavoriteCity(id, name))
                }
                return selectAll()
            }
        }

        /**
         * Método que será executado após o doInBackground.
         * Aqui, o processamento será realizado na Main Thread
         */
        override fun onPostExecute(result: List<FavoriteCity>?) {
            result?.apply {
                forEach {
                    Log.d("w", it.name) }
            }
        }

    }

    /**
     * Método paque faz a requisição a Weather API e atualiza o recycler view
     */
    private fun refreshList() {

        var units:String=getUnits()
        var lang:String=getLang()

        val rm = RetrofitManager()

        if (!edtSearch.text.isNullOrEmpty()){
            progressBar.visibility = View.VISIBLE

            val call = rm.weatherService().find(
                edtSearch.text.toString(),
                lang,
                units,
                "5fde54966e3e1c8a80e436245bdf9672")

            call.enqueue(object : Callback<FindResult> {

                override fun onFailure(call: Call<FindResult>, t: Throwable) {
                    progressBar.visibility = View.GONE
                }

                override fun onResponse(call: Call<FindResult>, response: Response<FindResult>) {
                    if (response.isSuccessful) {
                        response.body()?.let { findResult ->
                            adapter.updataData(findResult.list)
                        }
                    }
                    progressBar.visibility = View.GONE
                }

            })
        }else{
            //load favorite cities
            progressBar.visibility = View.VISIBLE
            var id:String=getListOfCities()

            if (!id.isNullOrEmpty()) {
                val call = rm.weatherService().findGroup(
                    id,
                    lang,
                    units,
                    "5fde54966e3e1c8a80e436245bdf9672"
                )

                call.enqueue(object : Callback<FindResult> {

                    override fun onFailure(call: Call<FindResult>, t: Throwable) {
                        progressBar.visibility = View.GONE
                    }

                    override fun onResponse(call: Call<FindResult>, response: Response<FindResult>) {
                        if (response.isSuccessful) {
                            response.body()?.let { findResult ->
                                adapter.updataData(findResult.list)
                            }
                        }
                        progressBar.visibility = View.GONE
                    }

                })
            }else{
                progressBar.visibility = View.GONE
                adapter.updataData(emptyList())
            }

        }
    }

    fun getListOfCities():String{
        var id:String=""

//        id=TextUtils.join(",",ListActivity.favoriteCities)

        ListActivity.favoriteCities?.forEach {
            id=id+it.id.toString()+","
        }
        id=id?.dropLast(1)

        return id


    }


    fun getLang():String{
        if (ListActivity.isLangEn2!!){
            return "en"
        }else{
            return "pt"
        }
    }

    fun getUnits():String{
        if (ListActivity.isTempC2!!.equals(true)){
            return "metric"
        }else{
            return "imperial"
        }
    }


    /**
     * Inicializa os componentes da UI
     */
    private fun initUI() {
        refreshList()
        btnSearch.setOnClickListener {
            if (isDeviceConnected()) {
                refreshList()
            } else {
                Toast.makeText(this, "Desconectado", Toast.LENGTH_SHORT).show()
            }
        }

        // RecyclerView
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = adapter
    }

    /**
     * Verifica se o device está conectado a internet
     */
    private fun isDeviceConnected(): Boolean {
        val cm = getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val netInfo = cm.activeNetworkInfo
        return netInfo != null && netInfo.isConnected
    }

    /**
     * Método que infla o menu na Actionbar/Toolbar
     */
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.weather_menu, menu)
        return true
    }

    /**
     * Método que será invocado quando o item do menu for selecionado
     */
    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        if (item?.itemId == R.id.settings_action) {
            startActivity(Intent(this, SettingActivity::class.java))
        }
        return true
    }

    private fun readPrefs() {
        val isTempC = (prefs.getBoolean(Constants.PREF_TEMP_C, true))
        val isLangEn = (prefs.getBoolean(Constants.PREF_LANG_EN, true))

        this.isTempC=isTempC
        this.isLangEn=isLangEn
    }


    fun getCityStatusInDB(city:City):Boolean{
        RoomManager.instance(this).favoriteDao().apply {

            if (selectById(city.id)?.id===city.id){
                return true
            }else{
                return false
            }
        }

    }


    companion object {
        var favoriteCities: List<FavoriteCity>? = null
        var isTempC2:Boolean? = null
        var isLangEn2:Boolean? = null
    }

    fun getFavoriteCitiesList():List<FavoriteCity>?{
        RoomManager.instance(this).favoriteDao().apply {
            return selectAll()
        }
    }

    fun updateCompanionObjects(){
        ListActivity.isTempC2=isTempC
        ListActivity.isLangEn2=isLangEn
        ListActivity.favoriteCities=getFavoriteCitiesList()
    }

}
