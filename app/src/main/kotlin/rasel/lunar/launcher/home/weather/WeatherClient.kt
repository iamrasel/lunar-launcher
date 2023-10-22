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

import java.io.BufferedReader
import java.io.InputStreamReader
import java.lang.Exception
import java.lang.StringBuilder
import java.net.HttpURLConnection
import java.net.URL


internal class WeatherClient {

    fun fetchWeather(wUrl: String?): String? {
        var httpURLConnection: HttpURLConnection? = null
        var bufferedReader: BufferedReader? = null

        try {
            httpURLConnection = URL(wUrl).openConnection() as HttpURLConnection
            httpURLConnection.connect()
            bufferedReader = BufferedReader(InputStreamReader(httpURLConnection.inputStream))

            val stringBuilder = StringBuilder()
            var line: String?
            while (bufferedReader.readLine().also { line = it } != null) {
                stringBuilder.append("$line\n")
            }

            if (stringBuilder.isNotEmpty()) return stringBuilder.toString()
        } catch (exception: Exception) {
            exception.printStackTrace()
        } finally {
            httpURLConnection?.disconnect()
            if (bufferedReader != null) {
                try {
                    bufferedReader.close()
                } catch (exception: Exception) {
                    exception.printStackTrace()
                }
            }
        }

        return null
    }

}
