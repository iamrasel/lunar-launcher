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

package rasel.lunar.launcher.home.weather;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class JsonParser {

    protected Weather getMyWeather(String jsonStr) {
        Weather weather = new Weather();

        try {
            JSONObject jsonObject = new JSONObject(jsonStr);

            // Get weather condition
            JSONArray weatherJsonArray = jsonObject.getJSONArray("weather");
            JSONObject jsonObject1 = weatherJsonArray.getJSONObject(0);
            weather.setWeatherCondition(jsonObject1.getString("main"));
            weather.setWeatherDescription(jsonObject1.getString("description"));
            weather.setWeatherIconStr(jsonObject1.getString("icon"));

            // Get temperature
            JSONObject jsonObject2 = jsonObject.getJSONObject("main");
            weather.setTemperature((float) jsonObject2.getDouble("temp"));

        } catch (JSONException jsonException) {
            jsonException.printStackTrace();
        }
        return weather;
    }
}
