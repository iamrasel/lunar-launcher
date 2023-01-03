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

import android.content.SharedPreferences
import rasel.lunar.launcher.LauncherActivity.Companion.lActivity
import rasel.lunar.launcher.helpers.Constants.Companion.KEY_APP_NO_
import rasel.lunar.launcher.helpers.Constants.Companion.PREFS_FAVORITE_APPS


internal class PrefsUtil {

    companion object {

        private val favAppsPrefs: SharedPreferences.Editor get() =
            lActivity!!.getSharedPreferences(PREFS_FAVORITE_APPS, 0).edit()

        /* save favorite package names */
        fun saveFavApps(position: Int, packageName: String?) {
            favAppsPrefs.putString(KEY_APP_NO_ + position, packageName).apply()
        }

        /* remove an entry saved by above method */
        fun removeFavApps(position: Int) {
            favAppsPrefs.remove(KEY_APP_NO_ + position).apply()
        }

    }

}
