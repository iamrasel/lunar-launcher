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
import android.text.SpannableStringBuilder
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.slider.Slider
import rasel.lunar.launcher.databinding.SettingsMiscBinding
import rasel.lunar.launcher.helpers.Constants.Companion.DEFAULT_ICON_SIZE
import rasel.lunar.launcher.helpers.Constants.Companion.KEY_BACK_HOME
import rasel.lunar.launcher.helpers.Constants.Companion.KEY_ICON_SIZE
import rasel.lunar.launcher.helpers.Constants.Companion.KEY_LOCK_METHOD
import rasel.lunar.launcher.helpers.Constants.Companion.KEY_RSS_URL
import rasel.lunar.launcher.helpers.Constants.Companion.KEY_SHORTCUT_COUNT
import rasel.lunar.launcher.helpers.Constants.Companion.MAX_SHORTCUTS
import rasel.lunar.launcher.helpers.UniUtils.Companion.isRooted
import rasel.lunar.launcher.settings.SettingsActivity.Companion.settingsPrefs
import java.util.*


internal class Misc : BottomSheetDialogFragment() {

    private lateinit var binding : SettingsMiscBinding

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = SettingsMiscBinding.inflate(inflater, container, false)

        /* initialize views according to the saved values */
        when (settingsPrefs!!.getBoolean(KEY_BACK_HOME, false)) {
            true -> binding.backHomePositive.isChecked = true
            false -> binding.backHomeNegative.isChecked = true
        }

        binding.shortcutCount.valueTo = MAX_SHORTCUTS.toFloat()
        binding.shortcutCount.value = settingsPrefs!!.getInt(KEY_SHORTCUT_COUNT, MAX_SHORTCUTS).toFloat()
        binding.iconSize.value = settingsPrefs!!.getInt(KEY_ICON_SIZE, DEFAULT_ICON_SIZE).toFloat()
        binding.inputFeedUrl.text = SpannableStringBuilder(settingsPrefs!!.getString(KEY_RSS_URL, ""))

        when (settingsPrefs!!.getInt(KEY_LOCK_METHOD, 0)) {
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
        if (!isRooted) {
            binding.selectLockRoot.isEnabled = false
        }

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        (requireDialog() as BottomSheetDialog).dismissWithAnimation = true

        binding.backHomeGroup.setOnCheckedStateChangeListener { group, _ ->
            when (group.checkedChipId) {
                binding.backHomePositive.id -> settingsPrefs!!.edit().putBoolean(KEY_BACK_HOME, true).apply()
                binding.backHomeNegative.id -> settingsPrefs!!.edit().putBoolean(KEY_BACK_HOME, false).apply()
            }
        }

        /* change shortcut count value */
        binding.shortcutCount.addOnChangeListener(Slider.OnChangeListener { _: Slider?, value: Float, _: Boolean ->
            settingsPrefs!!.edit().putInt(KEY_SHORTCUT_COUNT, value.toInt()).apply()
        })

        binding.iconSize.addOnChangeListener(Slider.OnChangeListener { _: Slider?, value: Float, _: Boolean ->
            settingsPrefs!!.edit().putInt(KEY_ICON_SIZE, value.toInt()).apply()
        })

        /* change lock method value */
        binding.lockGroup.setOnCheckedStateChangeListener { group, _ ->
            when (group.checkedChipId) {
                binding.selectLockNegative.id -> settingsPrefs!!.edit().putInt(KEY_LOCK_METHOD, 0).apply()
                binding.selectLockAccessibility.id -> settingsPrefs!!.edit().putInt(KEY_LOCK_METHOD, 1).apply()
                binding.selectLockAdmin.id -> settingsPrefs!!.edit().putInt(KEY_LOCK_METHOD, 2).apply()
                binding.selectLockRoot.id -> settingsPrefs!!.edit().putInt(KEY_LOCK_METHOD, 3).apply()
            }
        }
    }

    /* save input field value while closing the dialog */
    override fun onDismiss(dialog: DialogInterface) {
        super.onDismiss(dialog)
        settingsPrefs!!.edit().putString(KEY_RSS_URL,
            Objects.requireNonNull(binding.inputFeedUrl.text).toString().trim { it <= ' ' }).apply()
    }

}
