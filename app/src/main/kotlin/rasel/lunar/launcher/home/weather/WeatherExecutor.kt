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
import androidx.fragment.app.FragmentActivity
import com.google.android.material.textview.MaterialTextView
import rasel.lunar.launcher.helpers.Constants
import rasel.lunar.launcher.helpers.UniUtils
import java.util.concurrent.Executors
import kotlin.math.roundToInt

internal class WeatherExecutor(sharedPreferences: SharedPreferences) {
    private val cityName: String
    private val owmKey: String
    private val weatherUrl: String
    private val tempUnitValue: Int
    private val showCityValue: Boolean

    fun generateTempString(materialTextView: MaterialTextView, fragmentActivity: FragmentActivity) {
        materialTextView.visibility = View.GONE
        if (UniUtils().isNetworkAvailable(fragmentActivity) && cityName.isNotEmpty() && owmKey.isNotEmpty()) {
            try {
                val executor = Executors.newSingleThreadExecutor()
                val handler = Handler(Looper.getMainLooper())
                executor.execute {
                    var weather: Weather? = null
                    val jsonStr = WeatherClient().fetchWeather(weatherUrl)
                    if (jsonStr != null && jsonStr.isNotEmpty()) {
                        weather = JsonParser().getMyWeather(jsonStr)
                    }
                    val finalWeather = weather
                    handler.post {
                        if (finalWeather != null) {
                            val temp = when (tempUnitValue) {
                                0 -> (finalWeather.temperature - 273.15f).roundToInt().toString() + "ºC"
                                1 -> ((finalWeather.temperature - 273.15f) * 1.8 + 32).roundToInt().toString() + "ºF"
                                else -> throw AssertionError()
                            }

                            val tempStr = when (showCityValue) {
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
        cityName = sharedPreferences.getString(Constants().KEY_CITY_NAME, "").toString()
        owmKey = sharedPreferences.getString(Constants().KEY_OWM_API, "").toString()
        tempUnitValue = sharedPreferences.getInt(Constants().KEY_TEMP_UNIT, 0)
        showCityValue = sharedPreferences.getBoolean(Constants().KEY_SHOW_CITY, false)
        weatherUrl = "https://api.openweathermap.org/data/2.5/weather?q=$cityName&APPID=$owmKey"
    }
}