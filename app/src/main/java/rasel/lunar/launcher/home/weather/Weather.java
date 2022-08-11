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

public class Weather {

    private String weatherCondition, weatherDescription, weatherIconStr;
    private float temperature;

    protected String getWeatherCondition() {
        return weatherCondition;
    }

    protected void setWeatherCondition(String weatherCondition) {
        this.weatherCondition = weatherCondition;
    }

    protected String getWeatherDescription() {
        return weatherDescription;
    }

    protected void setWeatherDescription(String weatherDescription) {
        this.weatherDescription = weatherDescription;
    }

    protected String getWeatherIconStr() {
        return weatherIconStr;
    }

    protected void setWeatherIconStr(String weatherIconStr) {
        this.weatherIconStr = weatherIconStr;
    }

    public float getTemperature() {
        return temperature;
    }

    protected void setTemperature(float temperature) {
        this.temperature = temperature;
    }
}
