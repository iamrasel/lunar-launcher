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
import android.content.Context.MODE_PRIVATE
import android.content.res.ColorStateList
import androidx.appcompat.widget.LinearLayoutCompat
import com.google.android.material.button.MaterialButton
import com.google.android.material.button.MaterialButtonToggleGroup
import rasel.lunar.launcher.R
import rasel.lunar.launcher.helpers.Constants


internal class FavouriteUtils {

    private val constants = Constants()

    /* save favorite package names to shared preferences */
    fun saveFavApps(context: Context, position: Int, packageName: String?) {
        val sharedPreferences = context.getSharedPreferences(constants.PREFS_FAVORITE_APPS, MODE_PRIVATE)
        sharedPreferences.edit().putString(constants.KEY_APP_NO_ + position, packageName).apply()
    }

    /* manage initial preview and on clicks */
    fun previewAndClicks(context: Context, packageName: String, buttonToggleGroup: MaterialButtonToggleGroup) {
        val sharedPreferences = context.getSharedPreferences(constants.PREFS_FAVORITE_APPS, MODE_PRIVATE)
        val almostTransparent = ColorStateList.valueOf(context.getColor(R.color.almost_transparent))

        for (position in 1..6) {
            val button = outlinedButton(context, buttonToggleGroup)
            val savedPackageName = sharedPreferences.getString(constants.KEY_APP_NO_ + position, "")

            /* set previews */
            if (packageName == savedPackageName) button.isChecked = true
            if (savedPackageName?.isNotEmpty() == true) button.strokeColor = almostTransparent

            /* listen on clicks */
            buttonToggleGroup.addOnButtonCheckedListener { _: MaterialButtonToggleGroup?,
                                                           checkedId: Int, isChecked: Boolean ->
                if (checkedId == button.id) {
                    if (isChecked) {
                        saveFavApps(context, position, packageName)
                        button.strokeColor = almostTransparent
                    } else {
                        saveFavApps(context, position, "")
                        button.strokeColor = ColorStateList.valueOf(context.getColor(android.R.color.darker_gray))
                    }
                }
            }
        }
    }

    /* create and add an outlined button to the toggle group */
    private fun outlinedButton(context: Context, buttonToggleGroup: MaterialButtonToggleGroup): MaterialButton {
        val style = com.google.android.material.R.attr.materialButtonOutlinedStyle
        val button = MaterialButton(context, null, style)
        button.layoutParams = LinearLayoutCompat.LayoutParams(
            LinearLayoutCompat.LayoutParams.WRAP_CONTENT,
            LinearLayoutCompat.LayoutParams.WRAP_CONTENT, 1F
        )
        buttonToggleGroup.addView(button)
        return button
    }

}
