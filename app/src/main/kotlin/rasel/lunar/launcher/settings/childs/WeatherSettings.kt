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
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.button.MaterialButtonToggleGroup
import rasel.lunar.launcher.databinding.SettingsWeatherBinding
import rasel.lunar.launcher.helpers.Constants
import rasel.lunar.launcher.settings.SettingsPrefsUtils
import java.util.*


internal class WeatherSettings : BottomSheetDialogFragment() {

    private lateinit var binding : SettingsWeatherBinding
    private val constants = Constants()
    private val settingsPrefsUtils = SettingsPrefsUtils()
    private lateinit var cityName: String
    private lateinit var owmApi: String
    private var tempUnit = 0
    private var showCity : Boolean = false

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = SettingsWeatherBinding.inflate(inflater, container, false)

        /* get saved values */
        val sharedPreferences = requireContext().getSharedPreferences(constants.PREFS_SETTINGS, MODE_PRIVATE)
        cityName = sharedPreferences.getString(constants.KEY_CITY_NAME, "").toString()
        owmApi = sharedPreferences.getString(constants.KEY_OWM_API, "").toString()
        tempUnit = sharedPreferences.getInt(constants.KEY_TEMP_UNIT, 0)
        showCity = sharedPreferences.getBoolean(constants.KEY_SHOW_CITY, false)

        /* initialize views according to the saved values */
        binding.inputCity.setText(cityName)
        binding.inputOwm.setText(owmApi)

        when (tempUnit) {
            0 -> binding.selectCelsius.isChecked = true
            1 -> binding.selectFahrenheit.isChecked = true
        }

        when (showCity) {
            false -> binding.showCityNegative.isChecked = true
            true -> binding.showCityPositive.isChecked = true
        }

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        (requireDialog() as BottomSheetDialog).dismissWithAnimation = true

        /* change temperature unit value */
        binding.tempGroup.addOnButtonCheckedListener { _: MaterialButtonToggleGroup?, checkedId: Int, isChecked: Boolean ->
            if (isChecked) {
                when (checkedId) {
                    binding.selectCelsius.id -> settingsPrefsUtils.saveTempUnit(requireContext(), 0)
                    binding.selectFahrenheit.id -> settingsPrefsUtils.saveTempUnit(requireContext(), 1)
                }
            }
        }

        /* change show city value */
        binding.cityGroup.addOnButtonCheckedListener { _: MaterialButtonToggleGroup?, checkedId: Int, isChecked: Boolean ->
            if (isChecked) {
                when (checkedId) {
                    binding.showCityNegative.id -> settingsPrefsUtils.showCity(requireContext(), false)
                    binding.showCityPositive.id -> settingsPrefsUtils.showCity(requireContext(), true)
                }
            }
        }
    }

    /* get city name string from it's input field */
    private fun getCityName(): String {
        return Objects.requireNonNull(binding.inputCity.text).toString().trim { it <= ' ' }
    }

    /* get open weather map api key from it's input field */
    private fun getOwmApi(): String {
        return Objects.requireNonNull(binding.inputOwm.text).toString().trim { it <= ' ' }
    }

    /* save above two values while closing the dialog */
    override fun onDismiss(dialog: DialogInterface) {
        super.onDismiss(dialog)
        settingsPrefsUtils.saveCityName(requireContext(), getCityName())
        settingsPrefsUtils.saveOwmApi(requireContext(), getOwmApi())
    }

}
