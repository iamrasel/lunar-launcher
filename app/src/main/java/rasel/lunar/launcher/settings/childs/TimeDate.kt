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
    private var timeFormatValue = 0
    private lateinit var dateFormatValue: String

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = SettingsTimeDateBinding.inflate(inflater, container, false)

        val sharedPreferences = requireContext().getSharedPreferences(Constants().SHARED_PREFS_SETTINGS, MODE_PRIVATE)
        timeFormatValue = sharedPreferences.getInt(Constants().SHARED_PREF_TIME_FORMAT, 0)
        dateFormatValue =
            sharedPreferences.getString(Constants().SHARED_PREF_DATE_FORMAT, Constants().DEFAULT_DATE_FORMAT).toString()

        when (timeFormatValue) {
            0 -> binding.followSystemTime.isChecked = true
            1 -> binding.selectTwelve.isChecked = true
            2 -> binding.selectTwentyFour.isChecked = true
        }

        binding.dateFormat.setText(dateFormatValue)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.timeGroup.addOnButtonCheckedListener { _: MaterialButtonToggleGroup?, checkedId: Int, isChecked: Boolean ->
            if (isChecked) {
                when (checkedId) {
                    binding.followSystemTime.id -> SettingsPrefsUtils().saveTimeFormat(requireContext(), 0)
                    binding.selectTwelve.id -> SettingsPrefsUtils().saveTimeFormat(requireContext(), 1)
                    binding.selectTwentyFour.id -> SettingsPrefsUtils().saveTimeFormat(requireContext(), 2)
                }
            }
        }
    }

    private val dateFormat: String
        get() = Objects.requireNonNull(binding.dateFormat.text).toString().trim { it <= ' ' }

    override fun onDismiss(dialog: DialogInterface) {
        super.onDismiss(dialog)
        if (dateFormat.isEmpty()) {
            SettingsPrefsUtils().saveDateFormat(requireContext(), Constants().DEFAULT_DATE_FORMAT)
        } else {
            SettingsPrefsUtils().saveDateFormat(requireContext(), dateFormat)
        }
    }
}