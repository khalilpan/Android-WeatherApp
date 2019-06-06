package br.org.cesar.weatherapp.features.setting

import android.content.Context
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import br.org.cesar.weatherapp.Constants
import br.org.cesar.weatherapp.R
import kotlinx.android.synthetic.main.activity_preferences.*

class SettingActivity : AppCompatActivity() {

    private val prefs by lazy {
        getSharedPreferences(Constants.PREF_NAME, Context.MODE_PRIVATE)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_preferences)
        readPrefs()
        btnSave.setOnClickListener {
            writePrefs()
            Toast.makeText(applicationContext,"Saved.", Toast.LENGTH_SHORT).show()
        }
    }

    private fun readPrefs() {
        val isTempC = (prefs.getBoolean(Constants.PREF_TEMP_C, true))
        val isLangEn = (prefs.getBoolean(Constants.PREF_LANG_EN, true))

        rgTemp.check(if (isTempC) R.id.rdTempC else R.id.rdTempF)
        rgLang.check(if (isLangEn) R.id.rdLangEN else R.id.rdLangPT)
    }

    private fun writePrefs() {
        prefs.edit().apply {
            putBoolean(Constants.PREF_TEMP_C, rdTempC.isChecked)
            putBoolean(Constants.PREF_LANG_EN, rdLangEN.isChecked)
            apply()
        }
        finish()
    }
}
