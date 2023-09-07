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
import kotlin.math.roundToInt


internal class WeatherExecutor(sharedPreferences: SharedPreferences) {

    private val cityName: String
    private val owmApi: String
    private val weatherUrl: String
    private val tempUnit: Int
    private val showCity: Boolean

    fun generateWeatherString(materialTextView: MaterialTextView) {
        materialTextView.visibility = View.GONE

        /*  run the executor if network is available,
            and city name and owm api values are not empty */
        if (isNetworkAvailable && cityName.isNotEmpty() && owmApi.isNotEmpty()) {
            try {
                val executor = Executors.newSingleThreadExecutor()
                val handler = Handler(Looper.getMainLooper())

                executor.execute {
                    var weather: Weather? = null
                    val jsonStr = WeatherClient().fetchWeather(weatherUrl)
                    if (!jsonStr.isNullOrEmpty()) {
                        weather = JsonParser().getMyWeather(jsonStr)
                    }
                    val finalWeather = weather

                    handler.post {
                        if (finalWeather != null) {
                            /* set temperature unit */
                            val temp = when (tempUnit) {
                                0 -> (finalWeather.temperature - 273.15f).roundToInt().toString() + "ºC"
                                1 -> ((finalWeather.temperature - 273.15f) * 1.8 + 32).roundToInt().toString() + "ºF"
                                else -> throw AssertionError()
                            }
                            /* show/hide the city name */
                            val tempStr = when (showCity) {
                                false -> temp
                                true -> "$temp at $cityName"
                            }

                            materialTextView.visibility = View.VISIBLE
                            materialTextView.text = tempStr
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
        weatherUrl = "https://api.openweathermap.org/data/2.5/weather?q=$cityName&APPID=$owmApi"
    }

}
