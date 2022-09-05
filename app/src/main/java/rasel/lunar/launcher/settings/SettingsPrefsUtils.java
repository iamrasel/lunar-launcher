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

package rasel.lunar.launcher.settings;

import android.content.Context;
import android.content.SharedPreferences;

import rasel.lunar.launcher.helpers.Constants;

public class SettingsPrefsUtils {

    private final Constants constants = new Constants();

    protected void saveTimeFormat(Context context, int timeFormatValue) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(constants.SHARED_PREFS_SETTINGS, Context.MODE_PRIVATE);
        sharedPreferences.edit().putInt(constants.SHARED_PREF_TIME_FORMAT, timeFormatValue).apply();
    }

    protected void saveDateFormat(Context context, String dateFormatValue) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(constants.SHARED_PREFS_SETTINGS, Context.MODE_PRIVATE);
        sharedPreferences.edit().putString(constants.SHARED_PREF_DATE_FORMAT, dateFormatValue).apply();
    }

    protected void saveCityName(Context context, String cityName) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(constants.SHARED_PREFS_SETTINGS, Context.MODE_PRIVATE);
        sharedPreferences.edit().putString(constants.SHARED_PREF_CITY_NAME, cityName).apply();
    }

    protected void saveOwmKey(Context context, String owmKey) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(constants.SHARED_PREFS_SETTINGS, Context.MODE_PRIVATE);
        sharedPreferences.edit().putString(constants.SHARED_PREF_OWM_KEY, owmKey).apply();
    }

    protected void saveTempUnit(Context context, int tempUnit) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(constants.SHARED_PREFS_SETTINGS, Context.MODE_PRIVATE);
        sharedPreferences.edit().putInt(constants.SHARED_PREF_TEMP_UNIT, tempUnit).apply();
    }

    protected void showCity(Context context, int showCity) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(constants.SHARED_PREFS_SETTINGS, Context.MODE_PRIVATE);
        sharedPreferences.edit().putInt(constants.SHARED_PREF_SHOW_CITY, showCity).apply();
    }

    protected void showTodos(Context context, int showTodos) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(constants.SHARED_PREFS_SETTINGS, Context.MODE_PRIVATE);
        sharedPreferences.edit().putInt(constants.SHARED_PREF_SHOW_TODOS, showTodos).apply();
    }

    protected void saveFeedUrl(Context context, String feedUrl) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(constants.SHARED_PREFS_SETTINGS, Context.MODE_PRIVATE);
        sharedPreferences.edit().putString(constants.SHARED_PREF_FEED_URL, feedUrl).apply();
    }

    protected void saveLockMode(Context context, int lockMode) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(constants.SHARED_PREFS_SETTINGS, Context.MODE_PRIVATE);
        sharedPreferences.edit().putInt(constants.SHARED_PREF_LOCK, lockMode).apply();
    }

    protected void saveTheme(Context context, int themeValue) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(constants.SHARED_PREFS_SETTINGS, Context.MODE_PRIVATE);
        sharedPreferences.edit().putInt(constants.SHARED_PREF_THEME, themeValue).apply();
    }
}
