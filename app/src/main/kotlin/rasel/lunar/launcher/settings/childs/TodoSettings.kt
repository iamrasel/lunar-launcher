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
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.button.MaterialButtonToggleGroup
import com.google.android.material.slider.Slider
import rasel.lunar.launcher.databinding.SettingsTodoBinding
import rasel.lunar.launcher.helpers.Constants
import rasel.lunar.launcher.settings.SettingsPrefsUtils

internal class TodoSettings : BottomSheetDialogFragment() {
    private lateinit var binding : SettingsTodoBinding
    private var showTodos = 0
    private var todoLock : Boolean = false

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = SettingsTodoBinding.inflate(inflater, container, false)

        val sharedPreferences = requireContext().getSharedPreferences(Constants().PREFS_SETTINGS, MODE_PRIVATE)
        showTodos = sharedPreferences.getInt(Constants().KEY_TODO_COUNTS, 3)
        todoLock = sharedPreferences.getBoolean(Constants().KEY_TODO_LOCK, false)
        binding.showTodos.value = showTodos.toFloat()

        when (todoLock) {
            false -> binding.todoLockNegative.isChecked = true
            true -> binding.todoLockPositive.isChecked = true
        }

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.showTodos.addOnChangeListener(Slider.OnChangeListener { _: Slider?, value: Float, _: Boolean ->
            SettingsPrefsUtils().todoCount(requireContext(), value.toInt())
        })

        binding.todoLockGroup.addOnButtonCheckedListener { _: MaterialButtonToggleGroup?, checkedId: Int, isChecked: Boolean ->
            if (isChecked) {
                when (checkedId) {
                    binding.todoLockPositive.id -> SettingsPrefsUtils().todoLock(requireContext(), true)
                    binding.todoLockNegative.id -> SettingsPrefsUtils().todoLock(requireContext(), false)
                }
            }
        }
    }
}