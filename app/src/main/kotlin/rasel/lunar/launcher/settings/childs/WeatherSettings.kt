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

import android.content.DialogInterface
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import rasel.lunar.launcher.databinding.SettingsWeatherBinding
import rasel.lunar.launcher.helpers.Constants.Companion.KEY_CITY_NAME
import rasel.lunar.launcher.helpers.Constants.Companion.KEY_OWM_API
import rasel.lunar.launcher.helpers.Constants.Companion.KEY_SHOW_CITY
import rasel.lunar.launcher.helpers.Constants.Companion.KEY_TEMP_UNIT
import rasel.lunar.launcher.settings.SettingsActivity.Companion.settingsPrefs
import java.util.*


internal class WeatherSettings : BottomSheetDialogFragment() {

    private lateinit var binding : SettingsWeatherBinding

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = SettingsWeatherBinding.inflate(inflater, container, false)

        /* initialize views according to the saved values */
        binding.inputCity.setText(settingsPrefs!!.getString(KEY_CITY_NAME, "").toString())
        binding.inputOwm.setText(settingsPrefs!!.getString(KEY_OWM_API, "").toString())

        when (settingsPrefs!!.getInt(KEY_TEMP_UNIT, 0)) {
            0 -> binding.selectCelsius.isChecked = true
            1 -> binding.selectFahrenheit.isChecked = true
        }

        when (settingsPrefs!!.getBoolean(KEY_SHOW_CITY, false)) {
            false -> binding.showCityNegative.isChecked = true
            true -> binding.showCityPositive.isChecked = true
        }

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        (requireDialog() as BottomSheetDialog).dismissWithAnimation = true

        /* change temperature unit value */
        binding.tempGroup.setOnCheckedStateChangeListener { group, _ ->
            when (group.checkedChipId) {
                binding.selectCelsius.id -> settingsPrefs!!.edit().putInt(KEY_TEMP_UNIT, 0).apply()
                binding.selectFahrenheit.id -> settingsPrefs!!.edit().putInt(KEY_TEMP_UNIT, 1).apply()
            }
        }

        /* change show city value */
        binding.cityGroup.setOnCheckedStateChangeListener { group, _ ->
            when (group.checkedChipId) {
                binding.showCityNegative.id -> settingsPrefs!!.edit().putBoolean(KEY_SHOW_CITY, false).apply()
                binding.showCityPositive.id -> settingsPrefs!!.edit().putBoolean(KEY_SHOW_CITY, true).apply()
            }
        }
    }

    /* save input field values while closing the dialog */
    override fun onDismiss(dialog: DialogInterface) {
        super.onDismiss(dialog)
        settingsPrefs!!.edit().putString(KEY_CITY_NAME,
            Objects.requireNonNull(binding.inputCity.text).toString().trim { it <= ' ' }).apply()
        settingsPrefs!!.edit().putString(KEY_OWM_API,
            Objects.requireNonNull(binding.inputOwm.text).toString().trim { it <= ' ' }).apply()
    }

}
