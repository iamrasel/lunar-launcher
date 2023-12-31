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

            /* Get weather condition */
            val weatherArray = jsonObject.getJSONArray("weather")
            val weatherObject = weatherArray.getJSONObject(0)
            weather.weatherCondition = weatherObject.getString("main")
            weather.weatherDescription = weatherObject.getString("description")
            weather.weatherIconId = weatherObject.getString("icon")

            /* Get temperature */
            val mainObject = jsonObject.getJSONObject("main")
            weather.temperature = mainObject.getDouble("temp").toFloat()

            weather.cityName = jsonObject.getString("name")
        } catch (jsonException: JSONException) {
            jsonException.printStackTrace()
        }

        return weather
    }

}
