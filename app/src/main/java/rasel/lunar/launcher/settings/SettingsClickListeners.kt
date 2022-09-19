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
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.button.MaterialButton
import com.google.android.material.button.MaterialButtonToggleGroup
import com.google.android.material.slider.Slider
import rasel.lunar.launcher.databinding.AboutBinding

internal class SettingsClickListeners(private val appCompatActivity: AppCompatActivity) {
    private val context: Context = appCompatActivity.applicationContext

    fun showTodos(slider: Slider) {
        slider.addOnChangeListener(Slider.OnChangeListener { _: Slider?, value: Float, _: Boolean ->
            SettingsPrefsUtils().showTodos(context, value.toInt())
        })
    }

    fun screenLock(buttonToggleGroup: MaterialButtonToggleGroup, button0: MaterialButton,
        button1: MaterialButton, button2: MaterialButton, button3: MaterialButton) {
        buttonToggleGroup.addOnButtonCheckedListener { _: MaterialButtonToggleGroup?, checkedId: Int, isChecked: Boolean ->
            if (isChecked) {
                when (checkedId) {
                    button0.id -> SettingsPrefsUtils().saveLockMode(context, 0)
                    button1.id -> SettingsPrefsUtils().saveLockMode(context, 1)
                    button2.id -> SettingsPrefsUtils().saveLockMode(context, 2)
                    button3.id -> SettingsPrefsUtils().saveLockMode(context, 3)
                }
            }
        }
    }

    fun theme(buttonToggleGroup: MaterialButtonToggleGroup,
        button0: MaterialButton, button1: MaterialButton, button2: MaterialButton) {
        buttonToggleGroup.addOnButtonCheckedListener { _: MaterialButtonToggleGroup?, checkedId: Int, isChecked: Boolean ->
            if (isChecked) {
                when (checkedId) {
                    button0.id -> {
                        SettingsPrefsUtils().saveTheme(context, 0)
                        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM)
                    }
                    button1.id -> {
                        SettingsPrefsUtils().saveTheme(context, 1)
                        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
                    }
                    button2.id -> {
                        SettingsPrefsUtils().saveTheme(context, 2)
                        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
                    }
                }
            }
        }
    }

    fun openAbout(view: View) {
        view.setOnClickListener {
            val bottomSheetDialog = BottomSheetDialog(appCompatActivity)
            val aboutBinding = AboutBinding.inflate(appCompatActivity.layoutInflater)
            bottomSheetDialog.setContentView(aboutBinding.root)
            bottomSheetDialog.show()
        }
    }
}