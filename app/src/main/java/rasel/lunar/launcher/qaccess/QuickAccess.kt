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

package rasel.lunar.launcher.qaccess

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import rasel.lunar.launcher.databinding.QuickAccessBinding
import rasel.lunar.launcher.helpers.Constants

internal class QuickAccess : BottomSheetDialogFragment() {
    private lateinit var binding: QuickAccessBinding
    private lateinit var accessUtils: AccessUtils

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = QuickAccessBinding.inflate(inflater, container, false)
        accessUtils = AccessUtils(requireContext(), this, requireActivity())

        favApps()
        accessUtils.controlBrightness(binding.brightness)
        accessUtils.volumeControllers(binding.notification, binding.alarm, binding.media, binding.voice, binding.ring)

        return binding.root
    }

    private fun favApps() {
        val prefsFavApps = requireContext().getSharedPreferences(Constants().SHARED_PREFS_FAV_APPS, Context.MODE_PRIVATE)
        for (position in 1..6) {
            val packageValue = prefsFavApps.getString(Constants().FAV_APP_ + position.toString(), "").toString()
            when (position) {
                1 -> accessUtils.favApps(packageValue, binding.appOne, position)
                2 -> accessUtils.favApps(packageValue, binding.appTwo, position)
                3 -> accessUtils.favApps(packageValue, binding.appThree, position)
                4 -> accessUtils.favApps(packageValue, binding.appFour, position)
                5 -> accessUtils.favApps(packageValue, binding.appFive, position)
                6 -> accessUtils.favApps(packageValue, binding.appSix, position)
            }
        }
    }

    private fun shortcuts() {
        val prefsShortcuts = requireContext().getSharedPreferences(Constants().SHARED_PREFS_SHORTCUTS, Context.MODE_PRIVATE)
        for (position in 1..6) {
            val shortcutValue = prefsShortcuts.getString(Constants().SHORTCUT_NO_ + position.toString(), "").toString()
            val splitShortcutValue = shortcutValue.split("||").toTypedArray()

            var shortcutType = ""
            var intentString = ""
            var thumbLetter = ""
            var color = ""
            try {
                shortcutType = splitShortcutValue[0]
                intentString = splitShortcutValue[1]
                thumbLetter = splitShortcutValue[2]
                color = splitShortcutValue[3]
            } catch (exception : Exception) {
                exception.printStackTrace()
            }

            when (position) {
                1 -> accessUtils.shortcutsUtil(binding.shortcutOne, shortcutType, intentString, thumbLetter, color, position)
                2 -> accessUtils.shortcutsUtil(binding.shortcutTwo, shortcutType, intentString, thumbLetter, color, position)
                3 -> accessUtils.shortcutsUtil(binding.shortcutThree, shortcutType, intentString, thumbLetter, color, position)
                4 -> accessUtils.shortcutsUtil(binding.shortcutFour, shortcutType, intentString, thumbLetter, color, position)
                5 -> accessUtils.shortcutsUtil(binding.shortcutFive, shortcutType, intentString, thumbLetter, color, position)
                6 -> accessUtils.shortcutsUtil(binding.shortcutSix, shortcutType, intentString, thumbLetter, color, position)
            }
        }
    }

    override fun onResume() {
        super.onResume()
        shortcuts()
    }
}