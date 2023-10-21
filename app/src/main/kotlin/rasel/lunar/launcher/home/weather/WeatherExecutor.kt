/*
 * Lunar Launcher
 * Copyright (C) 2022 Md Rasel Hossain
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package rasel.lunar.launcher.home.weather

import android.annotation.SuppressLint
import android.content.SharedPreferences
import android.os.Handler
import android.os.Looper
import android.view.View
import com.google.android.material.textview.MaterialTextView
import rasel.lunar.launcher.helpers.Constants.Companion.KEY_CITY_NAME
import rasel.lunar.launcher.helpers.Constants.Companion.KEY_OWM_API
import rasel.lunar.launcher.helpers.Constants.Companion.KEY_SHOW_CITY
import rasel.lunar.launcher.helpers.Constants.Companion.KEY_TEMP_UNIT
import rasel.lunar.launcher.helpers.UniUtils.Companion.isNetworkAvailable
import java.util.concurrent.Executors


internal class WeatherExecutor(sharedPreferences: SharedPreferences) {

    private val cityName: String
    private val owmApi: String
    private val weatherUrl: String
    private val tempUnit: Int
    private val showCity: Boolean

    @SuppressLint("SetTextI18n")
    fun generateWeatherString(materialTextView: MaterialTextView) {
        materialTextView.visibility = View.GONE

        /*  run the executor if network is available,
            and city name and owm api values are not empty */
        if (isNetworkAvailable && cityName.isNotEmpty() && owmApi.isNotEmpty()) {
            try {
                Executors.newSingleThreadExecutor().execute {
                    var weather: Weather? = null
                    WeatherClient().fetchWeather(weatherUrl).let {
                        if (!it.isNullOrEmpty()) weather = JsonParser().getMyWeather(it)
                    }

                    Handler(Looper.getMainLooper()).post {
                        if (weather != null) {
                            materialTextView.apply {
                                visibility = View.VISIBLE
                                text = weather!!.temperature.toString() +
                                        (if (tempUnit == 0) "ºC" else "ºF") +
                                        (if (showCity) " at $cityName" else "")
                            }
                        }
                    }
                }
            } catch (exception: Exception) {
                exception.printStackTrace()
            }
        }
    }

    init {
        cityName = sharedPreferences.getString(KEY_CITY_NAME, "").toString()
        owmApi = sharedPreferences.getString(KEY_OWM_API, "").toString()
        tempUnit = sharedPreferences.getInt(KEY_TEMP_UNIT, 0)
        showCity = sharedPreferences.getBoolean(KEY_SHOW_CITY, false)
        weatherUrl =
            if (tempUnit == 0) "https://api.openweathermap.org/data/2.5/weather?q=$cityName&APPID=$owmApi&units=metric"
            else "https://api.openweathermap.org/data/2.5/weather?q=$cityName&APPID=$owmApi&units=imperial"
    }

}
