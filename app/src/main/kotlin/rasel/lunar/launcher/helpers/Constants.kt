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


internal class Constants {

    companion object {

        /* first launch */
        const val PREFS_FIRST_LAUNCH = "rasel.lunar.launcher.FIRST_LAUNCH"
        const val KEY_FIRST_LAUNCH = "first_launch"

        /* widgets */
        const val PREFS_WIDGETS = "rasel.lunar.launcher.WIDGETS"
        const val KEY_WIDGET_IDS = "widget_ids"
        const val KEY_WIDGET_HEIGHTS = "widget_heights"

        /* settings */
        const val PREFS_SETTINGS = "rasel.lunar.launcher.SETTINGS"
        const val KEY_TIME_FORMAT = "time_format"
        const val KEY_DATE_FORMAT = "date_format"
        const val KEY_CITY_NAME = "city_name"
        const val KEY_OWM_API = "owm_api"
        const val KEY_TEMP_UNIT = "temp_unit"
        const val KEY_SHOW_CITY = "show_city"
        const val KEY_TODO_COUNTS = "todo_count"
        const val KEY_TODO_LOCK = "todo_lock"
        const val KEY_KEYBOARD_SEARCH = "keyboard_search"
        const val KEY_QUICK_LAUNCH = "quick_launch"
        const val KEY_APPS_LAYOUT = "apps_layout"
        const val KEY_DRAW_ALIGN = "drawer_alignment"
        const val KEY_ICON_PACK = "icon_pack"
        const val KEY_GRID_COLUMNS = "grid_columns"
        const val KEY_SCROLLBAR_HEIGHT = "scrollbar_height"
        const val KEY_WINDOW_BACKGROUND = "window_background"
        const val KEY_APPLICATION_THEME = "application_theme"
        const val KEY_STATUS_BAR = "status_bar"
        const val KEY_BACK_HOME = "back_home"
        const val KEY_SHORTCUT_COUNT = "shortcut_count"
        const val KEY_ICON_SIZE = "icon_size"
        const val KEY_RSS_URL = "rss_url"
        const val KEY_LOCK_METHOD = "lock_method"

        /* --- */
        const val DEFAULT_DATE_FORMAT = "EEE dx MMM, yyyy"
        const val DEFAULT_ICON_SIZE = 44
        const val DEFAULT_ICON_PACK = "default_icon_pack"
        const val DEFAULT_GRID_COLUMNS = 4
        const val DEFAULT_SCROLLBAR_HEIGHT = 400
        const val MAX_SHORTCUTS = 6
        const val MAX_FAVORITE_APPS = 6

        const val BOTTOM_SHEET_TAG = "rasel.lunar.launcher.TAG"
        const val SEPARATOR = "||"
        const val ACCESSIBILITY_SERVICE_LOCK_SCREEN = "rasel.lunar.launcher.LOCK_SCREEN_SERVICE"
        const val AUTHENTICATOR_TYPE = BIOMETRIC_WEAK or DEVICE_CREDENTIAL

        const val rssJobId = 101
        const val widgetHostId = 102
        const val requestPickWidget = 103
        const val requestCreateWidget = 104

        /* favorite apps */
        const val PREFS_FAVORITE_APPS = "rasel.lunar.launcher.FAVORITE_APPS"
        const val KEY_APP_NO_ = "app_no_"

        /* phone and url shortcuts */
        const val PREFS_SHORTCUTS = "rasel.lunar.launcher.SHORTCUTS"
        const val KEY_SHORTCUT_NO_ = "shortcut_no_"
        const val SHORTCUT_TYPE_URL = "shortcut_type_url"
        const val SHORTCUT_TYPE_PHONE = "shortcut_type_phone"

        /* to-do database */
        const val TODO_DATABASE_NAME = "rasel.lunar.launcher.TODOS"
        const val TODO_DATABASE_VERSION = 1
        const val TODO_TABLE_NAME = "todo_table"
        const val TODO_COLUMN_ID = "todo_column_id"
        const val TODO_COLUMN_NAME = "todo_column_name"
        const val TODO_COLUMN_CREATED = "todo_column_created"

        /* rss feed */
        const val RSS_ITEMS = "rss_items"
        const val RSS_RECEIVER = "rss_receiver"
    }

}
