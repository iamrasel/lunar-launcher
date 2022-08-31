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

import android.content.SharedPreferences;
import android.os.Handler;
import android.os.Looper;
import android.view.View;

import androidx.fragment.app.FragmentActivity;

import com.google.android.material.textview.MaterialTextView;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import rasel.lunar.launcher.helpers.Constants;
import rasel.lunar.launcher.helpers.UniUtils;

public class WeatherExecutor {

    private final String cityName;
    private final String owmKey;
    private final String weatherUrl;
    private final int tempUnitValue;
    private final int showCityValue;
    private final UniUtils uniUtils = new UniUtils();

    public WeatherExecutor(SharedPreferences sharedPreferences) {
        Constants constants = new Constants();
        this.cityName = sharedPreferences.getString(constants.SHARED_PREF_CITY_NAME, null);
        this.owmKey = sharedPreferences.getString(constants.SHARED_PREF_OWM_KEY, null);
        this.tempUnitValue = sharedPreferences.getInt(constants.SHARED_PREF_TEMP_UNIT, 0);
        this.showCityValue = sharedPreferences.getInt(constants.SHARED_PREF_SHOW_CITY, 0);
        this.weatherUrl = "https://api.openweathermap.org/data/2.5/weather?q=" + cityName + "&APPID=" + owmKey;
    }

    public void generateTempString(MaterialTextView materialTextView, FragmentActivity fragmentActivity) {
        materialTextView.setVisibility(View.GONE);
        try{
            if(uniUtils.isNetworkAvailable(fragmentActivity) && cityName != null && owmKey != null) {
                ExecutorService executor = Executors.newSingleThreadExecutor();
                Handler handler = new Handler(Looper.getMainLooper());

                executor.execute(() -> {
                    Weather weather = null;
                    String jsonStr = (new WeatherClient()).fetchWeather(weatherUrl);
                    if (jsonStr != null) {
                        weather = (new JsonParser()).getMyWeather(jsonStr);
                    }
                    Weather finalWeather = weather;

                    handler.post(() -> {
                        if(finalWeather != null) {
                            String temp = null, tempStr = null;

                            switch(tempUnitValue) {
                                case 0: temp = Math.round(finalWeather.getTemperature() - 273.15f) + "ºC";
                                    break;
                                case 1: temp = Math.round(((finalWeather.getTemperature() - 273.15f) * 1.8) + 32) + "ºF";
                                    break;
                            }

                            switch(showCityValue) {
                                case 0: tempStr = temp; break;
                                case 1: tempStr = temp + " at " + cityName; break;
                            }

                            materialTextView.setVisibility(View.VISIBLE);
                            materialTextView.setText(tempStr);
                        }
                    });
                });
            }
        } catch(Exception exception) {
            uniUtils.exceptionViewer(fragmentActivity, exception.getMessage());
            exception.printStackTrace();
        }
    }
}
