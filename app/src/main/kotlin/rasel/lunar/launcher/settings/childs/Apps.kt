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
import com.google.android.material.button.MaterialButtonToggleGroup
import rasel.lunar.launcher.databinding.SettingsAppsBinding
import rasel.lunar.launcher.helpers.Constants
import rasel.lunar.launcher.settings.PrefsUtil


internal class Apps : BottomSheetDialogFragment() {

    private lateinit var binding : SettingsAppsBinding
    private val prefsUtil = PrefsUtil()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = SettingsAppsBinding.inflate(inflater, container, false)

        val constants = Constants()
        val sharedPreferences = requireContext().getSharedPreferences(constants.PREFS_SETTINGS, 0)

        /* initialize views according to the saved values */
        when (sharedPreferences.getBoolean(constants.KEY_KEYBOARD_SEARCH, false)) {
            false -> binding.keyboardAutoNegative.isChecked = true
            true -> binding.keyboardAutoPositive.isChecked = true
        }

        when (sharedPreferences.getBoolean(constants.KEY_QUICK_LAUNCH, true)) {
            true -> binding.quickLaunchPositive.isChecked = true
            false -> binding.quickLaunchNegative.isChecked = true
        }

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        (requireDialog() as BottomSheetDialog).dismissWithAnimation = true

        /* change search with keyboard value */
        binding.keyboardAutoGroup.addOnButtonCheckedListener { _: MaterialButtonToggleGroup?, checkedId: Int, isChecked: Boolean ->
            if (isChecked) {
                when (checkedId) {
                    binding.keyboardAutoPositive.id -> prefsUtil.keyboardSearch(requireContext(), true)
                    binding.keyboardAutoNegative.id -> prefsUtil.keyboardSearch(requireContext(), false)
                }
            }
        }

        /* change settings for quick launch */
        binding.quickLaunchGroup.addOnButtonCheckedListener { _: MaterialButtonToggleGroup?, checkedId: Int, isChecked: Boolean ->
            if (isChecked) {
                when (checkedId) {
                    binding.quickLaunchPositive.id -> prefsUtil.quickLaunch(requireContext(), true)
                    binding.quickLaunchNegative.id -> prefsUtil.quickLaunch(requireContext(), false)
                }
            }
        }
    }

}
