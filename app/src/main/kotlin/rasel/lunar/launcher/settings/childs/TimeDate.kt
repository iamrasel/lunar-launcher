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
import rasel.lunar.launcher.databinding.SettingsTimeDateBinding
import rasel.lunar.launcher.helpers.Constants
import rasel.lunar.launcher.settings.SettingsPrefsUtils
import java.util.*


internal class TimeDate : BottomSheetDialogFragment() {

    private lateinit var binding : SettingsTimeDateBinding
    private val constants = Constants()
    private val settingsPrefsUtils = SettingsPrefsUtils()
    private var timeFormat = 0
    private lateinit var dateFormat: String

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = SettingsTimeDateBinding.inflate(inflater, container, false)

        /* get saved values */
        val sharedPreferences = requireContext().getSharedPreferences(constants.PREFS_SETTINGS, MODE_PRIVATE)
        timeFormat = sharedPreferences.getInt(constants.KEY_TIME_FORMAT, 0)
        dateFormat =
            sharedPreferences.getString(constants.KEY_DATE_FORMAT, constants.DEFAULT_DATE_FORMAT).toString()

        /* initialize views according to the saved values */
        when (timeFormat) {
            0 -> binding.followSystemTime.isChecked = true
            1 -> binding.selectTwelve.isChecked = true
            2 -> binding.selectTwentyFour.isChecked = true
        }

        binding.dateFormat.setText(dateFormat)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        /* change time format value */
        binding.timeGroup.addOnButtonCheckedListener { _: MaterialButtonToggleGroup?, checkedId: Int, isChecked: Boolean ->
            if (isChecked) {
                when (checkedId) {
                    binding.followSystemTime.id -> settingsPrefsUtils.saveTimeFormat(requireContext(), 0)
                    binding.selectTwelve.id -> settingsPrefsUtils.saveTimeFormat(requireContext(), 1)
                    binding.selectTwentyFour.id -> settingsPrefsUtils.saveTimeFormat(requireContext(), 2)
                }
            }
        }
    }

    /* get date format value from it's input field */
    private val getDateFormat: String
        get() = Objects.requireNonNull(binding.dateFormat.text).toString().trim { it <= ' ' }

    /*  if the input field is empty, then save the default value,
        else save the above value */
    override fun onDismiss(dialog: DialogInterface) {
        super.onDismiss(dialog)
        if (getDateFormat.isEmpty()) {
            settingsPrefsUtils.saveDateFormat(requireContext(), constants.DEFAULT_DATE_FORMAT)
        } else {
            settingsPrefsUtils.saveDateFormat(requireContext(), getDateFormat)
        }
    }

}
