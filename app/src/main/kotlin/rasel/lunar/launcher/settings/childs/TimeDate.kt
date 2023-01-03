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
import rasel.lunar.launcher.databinding.SettingsTimeDateBinding
import rasel.lunar.launcher.helpers.Constants.Companion.DEFAULT_DATE_FORMAT
import rasel.lunar.launcher.helpers.Constants.Companion.KEY_DATE_FORMAT
import rasel.lunar.launcher.helpers.Constants.Companion.KEY_TIME_FORMAT
import rasel.lunar.launcher.settings.SettingsActivity.Companion.settingsPrefs
import java.util.*


internal class TimeDate : BottomSheetDialogFragment() {

    private lateinit var binding : SettingsTimeDateBinding

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = SettingsTimeDateBinding.inflate(inflater, container, false)

        /* initialize views according to the saved values */
        when (settingsPrefs!!.getInt(KEY_TIME_FORMAT, 0)) {
            0 -> binding.followSystemTime.isChecked = true
            1 -> binding.selectTwelve.isChecked = true
            2 -> binding.selectTwentyFour.isChecked = true
        }

        binding.dateFormat
            .setText(settingsPrefs!!.getString(KEY_DATE_FORMAT, DEFAULT_DATE_FORMAT).toString())

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        (requireDialog() as BottomSheetDialog).dismissWithAnimation = true

        /* change time format value */
        binding.timeGroup.setOnCheckedStateChangeListener { group, _ ->
            when (group.checkedChipId) {
                binding.followSystemTime.id -> settingsPrefs!!.edit().putInt(KEY_TIME_FORMAT, 0).apply()
                binding.selectTwelve.id -> settingsPrefs!!.edit().putInt(KEY_TIME_FORMAT, 1).apply()
                binding.selectTwentyFour.id -> settingsPrefs!!.edit().putInt(KEY_TIME_FORMAT, 2).apply()
            }
        }
    }

    /*  if the input field is empty, then save the default value.
        else save the value from input field while closing the dialog */
    override fun onDismiss(dialog: DialogInterface) {
        super.onDismiss(dialog)
        val dateFormat = Objects.requireNonNull(binding.dateFormat.text).toString().trim { it <= ' ' }
        if (dateFormat.isEmpty()) settingsPrefs!!.edit().putString(KEY_DATE_FORMAT, DEFAULT_DATE_FORMAT).apply()
        else settingsPrefs!!.edit().putString(KEY_DATE_FORMAT, dateFormat).apply()
    }

}
