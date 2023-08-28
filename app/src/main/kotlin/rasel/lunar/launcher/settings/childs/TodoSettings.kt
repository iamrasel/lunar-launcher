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

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.slider.Slider
import rasel.lunar.launcher.databinding.SettingsTodoBinding
import rasel.lunar.launcher.helpers.Constants.Companion.KEY_TODO_COUNTS
import rasel.lunar.launcher.helpers.Constants.Companion.KEY_TODO_LOCK
import rasel.lunar.launcher.settings.SettingsActivity.Companion.settingsPrefs


internal class TodoSettings : BottomSheetDialogFragment() {

    private lateinit var binding : SettingsTodoBinding

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = SettingsTodoBinding.inflate(inflater, container, false)

        /* initialize views according to the saved values */
        binding.showTodos.value = settingsPrefs!!.getInt(KEY_TODO_COUNTS, 3).toFloat()

        when (settingsPrefs!!.getBoolean(KEY_TODO_LOCK, false)) {
            false -> binding.todoLockNegative.isChecked = true
            true -> binding.todoLockPositive.isChecked = true
        }

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        (requireDialog() as BottomSheetDialog).dismissWithAnimation = true

        /* change to-do count value */
        binding.showTodos.addOnChangeListener(Slider.OnChangeListener { _: Slider?, value: Float, _: Boolean ->
            settingsPrefs!!.edit().putInt(KEY_TODO_COUNTS, value.toInt()).apply()
        })

        /* change to-do lock state value */
        binding.todoLockGroup.setOnCheckedStateChangeListener { group, _ ->
            when (group.checkedChipId) {
                binding.todoLockPositive.id -> settingsPrefs!!.edit().putBoolean(KEY_TODO_LOCK, true).apply()
                binding.todoLockNegative.id -> settingsPrefs!!.edit().putBoolean(KEY_TODO_LOCK, false).apply()
            }
        }
    }

}
