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
import com.google.android.material.button.MaterialButtonToggleGroup
import rasel.lunar.launcher.databinding.SettingsWeatherBinding
import rasel.lunar.launcher.helpers.Constants
import rasel.lunar.launcher.settings.PrefsUtil
import java.util.*


internal class WeatherSettings : BottomSheetDialogFragment() {

    private lateinit var binding : SettingsWeatherBinding
    private val prefsUtil = PrefsUtil()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = SettingsWeatherBinding.inflate(inflater, container, false)

        val constants = Constants()
        val sharedPreferences = requireContext().getSharedPreferences(constants.PREFS_SETTINGS, 0)

        /* initialize views according to the saved values */
        binding.inputCity.setText(sharedPreferences.getString(constants.KEY_CITY_NAME, "").toString())
        binding.inputOwm.setText(sharedPreferences.getString(constants.KEY_OWM_API, "").toString())

        when (sharedPreferences.getInt(constants.KEY_TEMP_UNIT, 0)) {
            0 -> binding.selectCelsius.isChecked = true
            1 -> binding.selectFahrenheit.isChecked = true
        }

        when (sharedPreferences.getBoolean(constants.KEY_SHOW_CITY, false)) {
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
                    binding.selectCelsius.id -> prefsUtil.saveTempUnit(requireContext(), 0)
                    binding.selectFahrenheit.id -> prefsUtil.saveTempUnit(requireContext(), 1)
                }
            }
        }

        /* change show city value */
        binding.cityGroup.addOnButtonCheckedListener { _: MaterialButtonToggleGroup?, checkedId: Int, isChecked: Boolean ->
            if (isChecked) {
                when (checkedId) {
                    binding.showCityNegative.id -> prefsUtil.showCity(requireContext(), false)
                    binding.showCityPositive.id -> prefsUtil.showCity(requireContext(), true)
                }
            }
        }
    }

    /* save input field values while closing the dialog */
    override fun onDismiss(dialog: DialogInterface) {
        super.onDismiss(dialog)
        prefsUtil.saveCityName(requireContext(),
            Objects.requireNonNull(binding.inputCity.text).toString().trim { it <= ' ' })
        prefsUtil.saveOwmApi(requireContext(),
            Objects.requireNonNull(binding.inputOwm.text).toString().trim { it <= ' ' })
    }

}
