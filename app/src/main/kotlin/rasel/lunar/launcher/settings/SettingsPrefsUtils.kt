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

    private val constants = Constants()

    /* save settings value for time format */
    fun saveTimeFormat(context: Context, timeFormat: Int) {
        val sharedPreferences = context.getSharedPreferences(constants.PREFS_SETTINGS, 0)
        sharedPreferences.edit().putInt(constants.KEY_TIME_FORMAT, timeFormat).apply()
    }

    /* save settings value for date format */
    fun saveDateFormat(context: Context, dateFormat: String) {
        val sharedPreferences = context.getSharedPreferences(constants.PREFS_SETTINGS, 0)
        sharedPreferences.edit().putString(constants.KEY_DATE_FORMAT, dateFormat).apply()
    }

    /* save city name */
    fun saveCityName(context: Context, cityName: String) {
        val sharedPreferences = context.getSharedPreferences(constants.PREFS_SETTINGS, 0)
        sharedPreferences.edit().putString(constants.KEY_CITY_NAME, cityName).apply()
    }

    /* save open weather map api key */
    fun saveOwmApi(context: Context, owmApi: String) {
        val sharedPreferences = context.getSharedPreferences(constants.PREFS_SETTINGS, 0)
        sharedPreferences.edit().putString(constants.KEY_OWM_API, owmApi).apply()
    }

    /* save settings value for temperature unit */
    fun saveTempUnit(context: Context, tempUnit: Int) {
        val sharedPreferences = context.getSharedPreferences(constants.PREFS_SETTINGS, 0)
        sharedPreferences.edit().putInt(constants.KEY_TEMP_UNIT, tempUnit).apply()
    }

    /* save settings value for show city name with weather */
    fun showCity(context: Context, showCity: Boolean) {
        val sharedPreferences = context.getSharedPreferences(constants.PREFS_SETTINGS, 0)
        sharedPreferences.edit().putBoolean(constants.KEY_SHOW_CITY, showCity).apply()
    }

    /* save settings value for todo count on home screen */
    fun todoCount(context: Context, todoCount: Int) {
        val sharedPreferences = context.getSharedPreferences(constants.PREFS_SETTINGS, 0)
        sharedPreferences.edit().putInt(constants.KEY_TODO_COUNTS, todoCount).apply()
    }

    /* save settings value todo manager lock */
    fun todoLock(context: Context, todoLock: Boolean) {
        val sharedPreferences = context.getSharedPreferences(constants.PREFS_SETTINGS, 0)
        sharedPreferences.edit().putBoolean(constants.KEY_TODO_LOCK, todoLock).apply()
    }

    /* save settings value for search with keyboard */
    fun keyboardSearch(context: Context, keyboardSearch: Boolean) {
        val sharedPreferences = context.getSharedPreferences(constants.PREFS_SETTINGS, 0)
        sharedPreferences.edit().putBoolean(constants.KEY_KEYBOARD_SEARCH, keyboardSearch).apply()
    }

    /* save rss feed url */
    fun saveRssUrl(context: Context, rssUrl: String) {
        val sharedPreferences = context.getSharedPreferences(constants.PREFS_SETTINGS, 0)
        sharedPreferences.edit().putString(constants.KEY_RSS_URL, rssUrl).apply()
    }

    /* save settings value for double tap lock method */
    fun saveLockMethod(context: Context, lockMethod: Int) {
        val sharedPreferences = context.getSharedPreferences(constants.PREFS_SETTINGS, 0)
        sharedPreferences.edit().putInt(constants.KEY_LOCK_METHOD, lockMethod).apply()
    }

}
