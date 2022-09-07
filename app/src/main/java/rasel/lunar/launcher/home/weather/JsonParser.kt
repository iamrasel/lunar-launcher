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

import org.json.JSONException
import org.json.JSONObject

internal class JsonParser {
    fun getMyWeather(jsonStr: String): Weather {
        val weather = Weather()
        try {
            val jsonObject = JSONObject(jsonStr)

            // Get weather condition
            val weatherJsonArray = jsonObject.getJSONArray("weather")
            val jsonObject1 = weatherJsonArray.getJSONObject(0)
            weather.weatherCondition = jsonObject1.getString("main")
            weather.weatherDescription = jsonObject1.getString("description")
            weather.weatherIconStr = jsonObject1.getString("icon")

            // Get temperature
            val jsonObject2 = jsonObject.getJSONObject("main")
            weather.temperature = jsonObject2.getDouble("temp").toFloat()
        } catch (jsonException: JSONException) {
            jsonException.printStackTrace()
        }
        return weather
    }
}