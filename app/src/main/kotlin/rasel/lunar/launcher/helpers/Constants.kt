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

    /* first launch */
    @JvmField val PREFS_FIRST_LAUNCH = "rasel.lunar.launcher.FIRST_LAUNCH"
    @JvmField val KEY_FIRST_LAUNCH = "first_launch"

    /* settings */
    @JvmField val PREFS_SETTINGS = "rasel.lunar.launcher.SETTINGS"
    @JvmField val KEY_TIME_FORMAT = "time_format"
    @JvmField val KEY_DATE_FORMAT = "date_format"
    @JvmField val KEY_CITY_NAME = "city_name"
    @JvmField val KEY_OWM_API = "owm_api"
    @JvmField val KEY_TEMP_UNIT = "temp_unit"
    @JvmField val KEY_SHOW_CITY = "show_city"
    @JvmField val KEY_TODO_COUNTS = "todo_count"
    @JvmField val KEY_TODO_LOCK = "todo_lock"
    @JvmField val KEY_KEYBOARD_SEARCH = "keyboard_search"
    @JvmField val KEY_QUICK_LAUNCH = "quick_launch"
    @JvmField val KEY_WINDOW_BACKGROUND = "window_background"
    @JvmField val KEY_SHORTCUT_COUNT = "shortcut_count"
    @JvmField val KEY_RSS_URL = "rss_url"
    @JvmField val KEY_LOCK_METHOD = "lock_method"

    /* --- */
    @JvmField val DEFAULT_DATE_FORMAT = "EEE dx MMM, yyyy"
    @JvmField val BOTTOM_SHEET_TAG = "rasel.lunar.launcher.TAG"
    @JvmField val ACCESSIBILITY_SERVICE_LOCK_SCREEN = "rasel.lunar.launcher.LOCK_SCREEN_SERVICE"
    @JvmField val AUTHENTICATOR_TYPE = BIOMETRIC_WEAK or DEVICE_CREDENTIAL

    /* favorite apps */
    @JvmField val PREFS_FAVORITE_APPS = "rasel.lunar.launcher.FAVORITE_APPS"
    @JvmField val KEY_APP_NO_ = "app_no_"

    /* phone and url shortcuts */
    @JvmField val PREFS_SHORTCUTS = "rasel.lunar.launcher.SHORTCUTS"
    @JvmField val KEY_SHORTCUT_NO_ = "shortcut_no_"
    @JvmField val SHORTCUT_TYPE_URL = "shortcut_type_url"
    @JvmField val SHORTCUT_TYPE_PHONE = "shortcut_type_phone"

    /* todo database */
    @JvmField val TODO_DATABASE_NAME = "rasel.lunar.launcher.TODOS"
    @JvmField val TODO_DATABASE_VERSION = 1
    @JvmField val TODO_TABLE_NAME = "todo_table"
    @JvmField val TODO_COLUMN_ID = "todo_column_id"
    @JvmField val TODO_COLUMN_NAME = "todo_column_name"
    @JvmField val TODO_COLUMN_CREATED = "todo_column_created"

    /* rss feed */
    @JvmField val RSS_ITEMS = "rss_items"
    @JvmField val RSS_RECEIVER = "rss_receiver"

}
