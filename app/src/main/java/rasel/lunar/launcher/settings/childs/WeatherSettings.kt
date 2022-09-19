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
import android.content.DialogInterface
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.button.MaterialButtonToggleGroup
import rasel.lunar.launcher.databinding.SettingsWeatherBinding
import rasel.lunar.launcher.helpers.Constants
import rasel.lunar.launcher.settings.SettingsPrefsUtils
import java.util.*

internal class WeatherSettings : BottomSheetDialogFragment() {
    private lateinit var binding : SettingsWeatherBinding
    private lateinit var cityName: String
    private lateinit var owmKey: String
    private var tempUnit = 0
    private var showCity = 0

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = SettingsWeatherBinding.inflate(inflater, container, false)

        val sharedPreferences = requireContext().getSharedPreferences(Constants().SHARED_PREFS_SETTINGS, MODE_PRIVATE)
        cityName = sharedPreferences.getString(Constants().SHARED_PREF_CITY_NAME, "").toString()
        owmKey = sharedPreferences.getString(Constants().SHARED_PREF_OWM_KEY, "").toString()
        tempUnit = sharedPreferences.getInt(Constants().SHARED_PREF_TEMP_UNIT, 0)
        showCity = sharedPreferences.getInt(Constants().SHARED_PREF_SHOW_CITY, 0)

        binding.inputCity.setText(cityName)
        binding.inputOwm.setText(owmKey)

        when (tempUnit) {
            0 -> binding.selectCelsius.isChecked = true
            1 -> binding.selectFahrenheit.isChecked = true
        }
        when (showCity) {
            0 -> binding.showCityNegative.isChecked = true
            1 -> binding.showCityPositive.isChecked = true
        }

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.tempGroup.addOnButtonCheckedListener { _: MaterialButtonToggleGroup?, checkedId: Int, isChecked: Boolean ->
            if (isChecked) {
                when (checkedId) {
                    binding.selectCelsius.id -> SettingsPrefsUtils().saveTempUnit(requireContext(), 0)
                    binding.selectFahrenheit.id -> SettingsPrefsUtils().saveTempUnit(requireContext(), 1)
                }
            }
        }

        binding.cityGroup.addOnButtonCheckedListener { _: MaterialButtonToggleGroup?, checkedId: Int, isChecked: Boolean ->
            if (isChecked) {
                when (checkedId) {
                    binding.showCityNegative.id -> SettingsPrefsUtils().showCity(requireContext(), 0)
                    binding.showCityPositive.id -> SettingsPrefsUtils().showCity(requireContext(), 1)
                }
            }
        }
    }

    private fun getCityName(): String {
        return Objects.requireNonNull(binding.inputCity.text).toString().trim { it <= ' ' }
    }

    private fun getOwmKey(): String {
        return Objects.requireNonNull(binding.inputOwm.text).toString().trim { it <= ' ' }
    }

    override fun onDismiss(dialog: DialogInterface) {
        super.onDismiss(dialog)
        SettingsPrefsUtils().saveCityName(requireContext(), getCityName())
        SettingsPrefsUtils().saveOwmKey(requireContext(), getOwmKey())
    }
}