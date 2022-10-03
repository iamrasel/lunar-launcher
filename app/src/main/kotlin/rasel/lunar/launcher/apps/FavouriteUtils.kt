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

package rasel.lunar.launcher.apps

import android.content.Context
import com.google.android.material.button.MaterialButtonToggleGroup
import com.google.android.material.button.MaterialButton
import rasel.lunar.launcher.helpers.Constants

internal class FavouriteUtils {

    fun saveFavApps(context: Context, position: Int, packageName: String?) {
        val sharedPreferences = context.getSharedPreferences(Constants().SHARED_PREFS_FAV_APPS, Context.MODE_PRIVATE)
        sharedPreferences.edit().putString(Constants().FAV_APP_ + position, packageName).apply()
    }

    fun saveFavPosition(
        buttonToggleGroup: MaterialButtonToggleGroup, button1: MaterialButton, button2: MaterialButton,
        button3: MaterialButton, button4: MaterialButton, button5: MaterialButton, button6: MaterialButton,
        context: Context, packageName: String?) {
        buttonToggleGroup.addOnButtonCheckedListener { _: MaterialButtonToggleGroup?, checkedId: Int, isChecked: Boolean ->
            if (isChecked) {
                when (checkedId) {
                    button1.id -> saveFavApps(context, 1, packageName)
                    button2.id -> saveFavApps(context, 2, packageName)
                    button3.id -> saveFavApps(context, 3, packageName)
                    button4.id -> saveFavApps(context, 4, packageName)
                    button5.id -> saveFavApps(context, 5, packageName)
                    button6.id -> saveFavApps(context, 6, packageName)
                }
            } else {
                when (checkedId) {
                    button1.id -> saveFavApps(context, 1, "")
                    button2.id -> saveFavApps(context, 2, "")
                    button3.id -> saveFavApps(context, 3, "")
                    button4.id -> saveFavApps(context, 4, "")
                    button5.id -> saveFavApps(context, 5, "")
                    button6.id -> saveFavApps(context, 6, "")
                }
            }
        }
    }

    fun setPreview(
        context: Context, packageName: String, button1: MaterialButton, button2: MaterialButton,
        button3: MaterialButton, button4: MaterialButton, button5: MaterialButton, button6: MaterialButton) {
        val sharedPreferences = context.getSharedPreferences(Constants().SHARED_PREFS_FAV_APPS, Context.MODE_PRIVATE)
        when (packageName) {
            sharedPreferences.getString(Constants().FAV_APP_ + 1, "") -> button1.isChecked = true
            sharedPreferences.getString(Constants().FAV_APP_ + 2, "") -> button2.isChecked = true
            sharedPreferences.getString(Constants().FAV_APP_ + 3, "") -> button3.isChecked = true
            sharedPreferences.getString(Constants().FAV_APP_ + 4, "") -> button4.isChecked = true
            sharedPreferences.getString(Constants().FAV_APP_ + 5, "") -> button5.isChecked = true
            sharedPreferences.getString(Constants().FAV_APP_ + 6, "") -> button6.isChecked = true
        }
    }
}