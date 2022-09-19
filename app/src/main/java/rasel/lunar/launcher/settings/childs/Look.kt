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

package rasel.lunar.launcher.settings.childs

import android.content.Context.MODE_PRIVATE
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatDelegate
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.button.MaterialButtonToggleGroup
import rasel.lunar.launcher.databinding.SettingsLookBinding
import rasel.lunar.launcher.helpers.Constants
import rasel.lunar.launcher.settings.SettingsPrefsUtils

internal class Look : BottomSheetDialogFragment() {
    private lateinit var binding : SettingsLookBinding
    private var themeValue = 0

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = SettingsLookBinding.inflate(inflater, container, false)

        val sharedPreferences = requireContext().getSharedPreferences(Constants().SHARED_PREFS_SETTINGS, MODE_PRIVATE)
        themeValue = sharedPreferences.getInt(Constants().SHARED_PREF_THEME, 0)
        when (themeValue) {
            0 -> binding.followSystemTheme.isChecked = true
            1 -> binding.selectDarkTheme.isChecked = true
            2 -> binding.selectLightTheme.isChecked = true
        }

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.themeGroup.addOnButtonCheckedListener { _: MaterialButtonToggleGroup?, checkedId: Int, isChecked: Boolean ->
            if (isChecked) {
                when (checkedId) {
                    binding.followSystemTheme.id -> {
                        SettingsPrefsUtils().saveTheme(requireContext(), 0)
                        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM)
                    }
                    binding.selectDarkTheme.id -> {
                        SettingsPrefsUtils().saveTheme(requireContext(), 1)
                        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
                    }
                    binding.selectLightTheme.id -> {
                        SettingsPrefsUtils().saveTheme(requireContext(), 2)
                        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
                    }
                }
            }
        }
    }
}