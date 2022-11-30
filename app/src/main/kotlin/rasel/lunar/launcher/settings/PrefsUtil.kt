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
import rasel.lunar.launcher.helpers.Constants.Companion.KEY_APP_NO_
import rasel.lunar.launcher.helpers.Constants.Companion.KEY_BACK_HOME
import rasel.lunar.launcher.helpers.Constants.Companion.KEY_CITY_NAME
import rasel.lunar.launcher.helpers.Constants.Companion.KEY_DATE_FORMAT
import rasel.lunar.launcher.helpers.Constants.Companion.KEY_KEYBOARD_SEARCH
import rasel.lunar.launcher.helpers.Constants.Companion.KEY_LOCK_METHOD
import rasel.lunar.launcher.helpers.Constants.Companion.KEY_OWM_API
import rasel.lunar.launcher.helpers.Constants.Companion.KEY_QUICK_LAUNCH
import rasel.lunar.launcher.helpers.Constants.Companion.KEY_RSS_URL
import rasel.lunar.launcher.helpers.Constants.Companion.KEY_SHORTCUT_COUNT
import rasel.lunar.launcher.helpers.Constants.Companion.KEY_SHOW_CITY
import rasel.lunar.launcher.helpers.Constants.Companion.KEY_TEMP_UNIT
import rasel.lunar.launcher.helpers.Constants.Companion.KEY_TIME_FORMAT
import rasel.lunar.launcher.helpers.Constants.Companion.KEY_TODO_COUNTS
import rasel.lunar.launcher.helpers.Constants.Companion.KEY_TODO_LOCK
import rasel.lunar.launcher.helpers.Constants.Companion.KEY_WINDOW_BACKGROUND
import rasel.lunar.launcher.helpers.Constants.Companion.PREFS_FAVORITE_APPS
import rasel.lunar.launcher.helpers.Constants.Companion.PREFS_SETTINGS


internal class PrefsUtil {

    companion object {

        /* save favorite package names */
        fun saveFavApps(context: Context, position: Int, packageName: String?) {
            val sharedPreferences = context.getSharedPreferences(PREFS_FAVORITE_APPS, 0)
            sharedPreferences.edit().putString(KEY_APP_NO_ + position, packageName).apply()
        }

        /* remove an entry saved by above method */
        fun removeFavApps(context: Context, position: Int) {
            val sharedPreferences = context.getSharedPreferences(PREFS_FAVORITE_APPS, 0)
            sharedPreferences.edit().remove(KEY_APP_NO_ + position).apply()
        }

        /* save settings value for time format */
        fun saveTimeFormat(context: Context, timeFormat: Int) {
            val sharedPreferences = context.getSharedPreferences(PREFS_SETTINGS, 0)
            sharedPreferences.edit().putInt(KEY_TIME_FORMAT, timeFormat).apply()
        }

        /* save settings value for date format */
        fun saveDateFormat(context: Context, dateFormat: String) {
            val sharedPreferences = context.getSharedPreferences(PREFS_SETTINGS, 0)
            sharedPreferences.edit().putString(KEY_DATE_FORMAT, dateFormat).apply()
        }

        /* save city name */
        fun saveCityName(context: Context, cityName: String) {
            val sharedPreferences = context.getSharedPreferences(PREFS_SETTINGS, 0)
            sharedPreferences.edit().putString(KEY_CITY_NAME, cityName).apply()
        }

        /* save open weather map api key */
        fun saveOwmApi(context: Context, owmApi: String) {
            val sharedPreferences = context.getSharedPreferences(PREFS_SETTINGS, 0)
            sharedPreferences.edit().putString(KEY_OWM_API, owmApi).apply()
        }

        /* save settings value for temperature unit */
        fun saveTempUnit(context: Context, tempUnit: Int) {
            val sharedPreferences = context.getSharedPreferences(PREFS_SETTINGS, 0)
            sharedPreferences.edit().putInt(KEY_TEMP_UNIT, tempUnit).apply()
        }

        /* save settings value for show city name with weather */
        fun showCity(context: Context, showCity: Boolean) {
            val sharedPreferences = context.getSharedPreferences(PREFS_SETTINGS, 0)
            sharedPreferences.edit().putBoolean(KEY_SHOW_CITY, showCity).apply()
        }

        /* save settings value for todo count on home screen */
        fun todoCount(context: Context, todoCount: Int) {
            val sharedPreferences = context.getSharedPreferences(PREFS_SETTINGS, 0)
            sharedPreferences.edit().putInt(KEY_TODO_COUNTS, todoCount).apply()
        }

        /* save settings value todo manager lock */
        fun todoLock(context: Context, todoLock: Boolean) {
            val sharedPreferences = context.getSharedPreferences(PREFS_SETTINGS, 0)
            sharedPreferences.edit().putBoolean(KEY_TODO_LOCK, todoLock).apply()
        }

        /* save settings value for search with keyboard */
        fun keyboardSearch(context: Context, keyboardSearch: Boolean) {
            val sharedPreferences = context.getSharedPreferences(PREFS_SETTINGS, 0)
            sharedPreferences.edit().putBoolean(KEY_KEYBOARD_SEARCH, keyboardSearch).apply()
        }

        /* settings for quick launch */
        fun quickLaunch(context: Context, quickLaunch: Boolean) {
            val sharedPreferences = context.getSharedPreferences(PREFS_SETTINGS, 0)
            sharedPreferences.edit().putBoolean(KEY_QUICK_LAUNCH, quickLaunch).apply()
        }

        /* save window background color value*/
        fun windowBackground(context: Context, windowBackground: String) {
            val sharedPreferences = context.getSharedPreferences(PREFS_SETTINGS, 0)
            sharedPreferences.edit().putString(KEY_WINDOW_BACKGROUND, windowBackground).apply()
        }

        /* back to home on resume */
        fun backHome(context: Context, backHome: Boolean) {
            val sharedPreferences = context.getSharedPreferences(PREFS_SETTINGS, 0)
            sharedPreferences.edit().putBoolean(KEY_BACK_HOME, backHome).apply()
        }

        /* save shortcuts count value */
        fun shortcutCount(context: Context, shortcutCount: Int) {
            val sharedPreferences = context.getSharedPreferences(PREFS_SETTINGS, 0)
            sharedPreferences.edit().putInt(KEY_SHORTCUT_COUNT, shortcutCount).apply()
        }

        /* save rss feed url */
        fun saveRssUrl(context: Context, rssUrl: String) {
            val sharedPreferences = context.getSharedPreferences(PREFS_SETTINGS, 0)
            sharedPreferences.edit().putString(KEY_RSS_URL, rssUrl).apply()
        }

        /* save settings value for double tap lock method */
        fun saveLockMethod(context: Context, lockMethod: Int) {
            val sharedPreferences = context.getSharedPreferences(PREFS_SETTINGS, 0)
            sharedPreferences.edit().putInt(KEY_LOCK_METHOD, lockMethod).apply()
        }

    }

}
