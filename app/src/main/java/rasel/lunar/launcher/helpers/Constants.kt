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

package rasel.lunar.launcher.helpers

import androidx.biometric.BiometricManager.Authenticators.BIOMETRIC_WEAK
import androidx.biometric.BiometricManager.Authenticators.DEVICE_CREDENTIAL

@Suppress("PropertyName")
internal class Constants {
    @JvmField val SHARED_PREFS_FIRST_LAUNCH = "rasel.lunar.launcher.FIRST_LAUNCH"
    @JvmField val FIRST_LAUNCH = "first_launch"

    @JvmField val SHARED_PREFS_SETTINGS = "rasel.lunar.launcher.SETTINGS"
    @JvmField val SHARED_PREF_TIME_FORMAT = "time_format"
    @JvmField val SHARED_PREF_DATE_FORMAT = "date_format"
    @JvmField val SHARED_PREF_CITY_NAME = "city_name"
    @JvmField val SHARED_PREF_OWM_KEY = "owm_key"
    @JvmField val SHARED_PREF_TEMP_UNIT = "temp_unit"
    @JvmField val SHARED_PREF_SHOW_CITY = "show_city"
    @JvmField val SHARED_PREF_SHOW_TODOS = "show_todos"
    @JvmField val SHARED_PREF_TODO_LOCK = "todo_lock"
    @JvmField val SHARED_PREF_AUTO_KEYBOARD = "automatic_keyboard"
    @JvmField val SHARED_PREF_FEED_URL = "feed_url"
    @JvmField val SHARED_PREF_LOCK = "lock"
    @JvmField val SHARED_PREF_THEME = "app_theme"

    @JvmField val DEFAULT_DATE_FORMAT = "EEE dx MMM, yyyy"
    @JvmField val MODAL_BOTTOM_SHEET_TAG = "rasel.lunar.launcher.TAG"
    @JvmField val ACCESSIBILITY_SERVICE_LOCK_SCREEN = "rasel.lunar.launcher.SERVICE_LOCK_SCREEN"
    @JvmField val AUTHENTICATOR_TYPE = BIOMETRIC_WEAK or DEVICE_CREDENTIAL

    @JvmField val SHARED_PREFS_SHORTCUTS = "rasel.lunar.launcher.SHORTCUTS"
    @JvmField val SHORTCUT_NO_ = "shortcut_no_"
    @JvmField val TYPE_URL = "shortcut_type_url"
    @JvmField val TYPE_PHONE = "shortcut_type_phone"

    @JvmField val SHARED_PREFS_FAV_APPS = "rasel.lunar.launcher.FAVOURITES"
    @JvmField val FAV_APP_ = "fav_app_"

    @JvmField val TODO_DATABASE_NAME = "rasel.lunar.launcher.TODOS"
    @JvmField val TODO_DATABASE_VERSION = 1
    @JvmField val TODO_TABLE = "todo_table"
    @JvmField val TODO_COLUMN_ID = "todo_column_id"
    @JvmField val TODO_COL_CREATED = "todo_column_created"
    @JvmField val TODO_COLUMN_NAME = "todo_column_name"

    @JvmField val RSS_ITEMS = "rss_items"
    @JvmField val RSS_RECEIVER = "rss_receiver"
}