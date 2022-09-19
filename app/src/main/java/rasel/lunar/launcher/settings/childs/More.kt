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
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.button.MaterialButtonToggleGroup
import rasel.lunar.launcher.databinding.SettingsMoreBinding
import rasel.lunar.launcher.helpers.Constants
import rasel.lunar.launcher.helpers.UniUtils
import rasel.lunar.launcher.settings.SettingsPrefsUtils
import java.util.*

internal class More : BottomSheetDialogFragment() {
    private lateinit var binding : SettingsMoreBinding
    private var lockMode = 0
    private lateinit var feedUrl: String

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = SettingsMoreBinding.inflate(inflater, container, false)

        val sharedPreferences = requireContext().getSharedPreferences(Constants().SHARED_PREFS_SETTINGS, MODE_PRIVATE)
        feedUrl = sharedPreferences.getString(Constants().SHARED_PREF_FEED_URL, "").toString()
        lockMode = sharedPreferences.getInt(Constants().SHARED_PREF_LOCK, 0)

        binding.inputFeedUrl.setText(feedUrl)

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.P) {
            binding.selectLockAccessibility.isEnabled = false
        }
        if (!UniUtils().isRooted) {
            binding.selectLockRoot.isEnabled = false
        }

        when (lockMode) {
            0 -> binding.selectLockNegative.isChecked = true
            1 -> binding.selectLockAccessibility.isChecked = true
            2 -> binding.selectLockAdmin.isChecked = true
            3 -> binding.selectLockRoot.isChecked = true
        }

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.lockGroup.addOnButtonCheckedListener { _: MaterialButtonToggleGroup?, checkedId: Int, isChecked: Boolean ->
            if (isChecked) {
                when (checkedId) {
                    binding.selectLockNegative.id -> SettingsPrefsUtils().saveLockMode(requireContext(), 0)
                    binding.selectLockAccessibility.id -> SettingsPrefsUtils().saveLockMode(requireContext(), 1)
                    binding.selectLockAdmin.id -> SettingsPrefsUtils().saveLockMode(requireContext(), 2)
                    binding.selectLockRoot.id -> SettingsPrefsUtils().saveLockMode(requireContext(), 3)
                }
            }
        }
    }

    private fun getFeedUrl(): String {
        return Objects.requireNonNull(binding.inputFeedUrl.text).toString().trim { it <= ' ' }
    }

    override fun onDismiss(dialog: DialogInterface) {
        super.onDismiss(dialog)
        SettingsPrefsUtils().saveFeedUrl(requireContext(), getFeedUrl())
    }
}