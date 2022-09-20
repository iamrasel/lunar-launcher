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

package rasel.lunar.launcher.settings

import android.content.Context
import rasel.lunar.launcher.helpers.Constants

internal class SettingsPrefsUtils {
    
    fun saveTimeFormat(context: Context, timeFormatValue: Int) {
        val sharedPreferences = context.getSharedPreferences(Constants().SHARED_PREFS_SETTINGS, Context.MODE_PRIVATE)
        sharedPreferences.edit().putInt(Constants().SHARED_PREF_TIME_FORMAT, timeFormatValue).apply()
    }

    fun saveDateFormat(context: Context, dateFormatValue: String) {
        val sharedPreferences = context.getSharedPreferences(Constants().SHARED_PREFS_SETTINGS, Context.MODE_PRIVATE)
        sharedPreferences.edit().putString(Constants().SHARED_PREF_DATE_FORMAT, dateFormatValue).apply()
    }

    fun saveCityName(context: Context, cityName: String) {
        val sharedPreferences = context.getSharedPreferences(Constants().SHARED_PREFS_SETTINGS, Context.MODE_PRIVATE)
        sharedPreferences.edit().putString(Constants().SHARED_PREF_CITY_NAME, cityName).apply()
    }

    fun saveOwmKey(context: Context, owmKey: String) {
        val sharedPreferences = context.getSharedPreferences(Constants().SHARED_PREFS_SETTINGS, Context.MODE_PRIVATE)
        sharedPreferences.edit().putString(Constants().SHARED_PREF_OWM_KEY, owmKey).apply()
    }

    fun saveTempUnit(context: Context, tempUnit: Int) {
        val sharedPreferences = context.getSharedPreferences(Constants().SHARED_PREFS_SETTINGS, Context.MODE_PRIVATE)
        sharedPreferences.edit().putInt(Constants().SHARED_PREF_TEMP_UNIT, tempUnit).apply()
    }

    fun showCity(context: Context, showCity: Boolean) {
        val sharedPreferences = context.getSharedPreferences(Constants().SHARED_PREFS_SETTINGS, Context.MODE_PRIVATE)
        sharedPreferences.edit().putBoolean(Constants().SHARED_PREF_SHOW_CITY, showCity).apply()
    }

    fun showTodos(context: Context, showTodos: Int) {
        val sharedPreferences = context.getSharedPreferences(Constants().SHARED_PREFS_SETTINGS, Context.MODE_PRIVATE)
        sharedPreferences.edit().putInt(Constants().SHARED_PREF_SHOW_TODOS, showTodos).apply()
    }

    fun todoLock(context: Context, todoLock: Boolean) {
        val sharedPreferences = context.getSharedPreferences(Constants().SHARED_PREFS_SETTINGS, Context.MODE_PRIVATE)
        sharedPreferences.edit().putBoolean(Constants().SHARED_PREF_TODO_LOCK, todoLock).apply()
    }

    fun saveFeedUrl(context: Context, feedUrl: String) {
        val sharedPreferences = context.getSharedPreferences(Constants().SHARED_PREFS_SETTINGS, Context.MODE_PRIVATE)
        sharedPreferences.edit().putString(Constants().SHARED_PREF_FEED_URL, feedUrl).apply()
    }

    fun saveLockMode(context: Context, lockMode: Int) {
        val sharedPreferences = context.getSharedPreferences(Constants().SHARED_PREFS_SETTINGS, Context.MODE_PRIVATE)
        sharedPreferences.edit().putInt(Constants().SHARED_PREF_LOCK, lockMode).apply()
    }

    fun saveTheme(context: Context, themeValue: Int) {
        val sharedPreferences = context.getSharedPreferences(Constants().SHARED_PREFS_SETTINGS, Context.MODE_PRIVATE)
        sharedPreferences.edit().putInt(Constants().SHARED_PREF_THEME, themeValue).apply()
    }
}