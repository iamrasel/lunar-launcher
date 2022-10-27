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
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.button.MaterialButtonToggleGroup
import rasel.lunar.launcher.databinding.SettingsMoreBinding
import rasel.lunar.launcher.helpers.Constants
import rasel.lunar.launcher.helpers.UniUtils
import rasel.lunar.launcher.settings.PrefsUtil
import java.util.*


internal class More : BottomSheetDialogFragment() {

    private lateinit var binding : SettingsMoreBinding
    private val prefsUtil = PrefsUtil()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = SettingsMoreBinding.inflate(inflater, container, false)

        val constants = Constants()
        val sharedPreferences = requireContext().getSharedPreferences(constants.PREFS_SETTINGS, 0)

        /* initialize views according to the saved values */
        binding.inputFeedUrl.setText(sharedPreferences.getString(constants.KEY_RSS_URL, "").toString())

        when (sharedPreferences.getInt(constants.KEY_LOCK_METHOD, 0)) {
            0 -> binding.selectLockNegative.isChecked = true
            1 -> binding.selectLockAccessibility.isChecked = true
            2 -> binding.selectLockAdmin.isChecked = true
            3 -> binding.selectLockRoot.isChecked = true
        }

        /* disable accessibility button for devices below android 9 */
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.P) {
            binding.selectLockAccessibility.isEnabled = false
        }

        /* disable root button for non-rooted devices */
        if (!UniUtils().isRooted) {
            binding.selectLockRoot.isEnabled = false
        }

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        (requireDialog() as BottomSheetDialog).dismissWithAnimation = true

        /* change lock method value */
        binding.lockGroup.addOnButtonCheckedListener { _: MaterialButtonToggleGroup?, checkedId: Int, isChecked: Boolean ->
            if (isChecked) {
                when (checkedId) {
                    binding.selectLockNegative.id -> prefsUtil.saveLockMethod(requireContext(), 0)
                    binding.selectLockAccessibility.id -> prefsUtil.saveLockMethod(requireContext(), 1)
                    binding.selectLockAdmin.id -> prefsUtil.saveLockMethod(requireContext(), 2)
                    binding.selectLockRoot.id -> prefsUtil.saveLockMethod(requireContext(), 3)
                }
            }
        }
    }

    /* save input field value while closing the dialog */
    override fun onDismiss(dialog: DialogInterface) {
        super.onDismiss(dialog)
        prefsUtil.saveRssUrl(requireContext(),
            Objects.requireNonNull(binding.inputFeedUrl.text).toString().trim { it <= ' ' })
    }

}
