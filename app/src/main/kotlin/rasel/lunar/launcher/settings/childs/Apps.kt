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

import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.ApplicationInfo
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewGroup.LayoutParams.MATCH_PARENT
import android.view.ViewGroup.LayoutParams.WRAP_CONTENT
import android.widget.Toast
import androidx.appcompat.widget.LinearLayoutCompat
import androidx.core.view.children
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipDrawable
import com.google.android.material.chip.ChipGroup
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.slider.Slider
import rasel.lunar.launcher.R
import rasel.lunar.launcher.databinding.SettingsAppsBinding
import rasel.lunar.launcher.helpers.Constants.Companion.DEFAULT_GRID_COLUMNS
import rasel.lunar.launcher.helpers.Constants.Companion.DEFAULT_ICON_PACK
import rasel.lunar.launcher.helpers.Constants.Companion.DEFAULT_SCROLLBAR_HEIGHT
import rasel.lunar.launcher.helpers.Constants.Companion.KEY_APPS_LAYOUT
import rasel.lunar.launcher.helpers.Constants.Companion.KEY_DRAW_ALIGN
import rasel.lunar.launcher.helpers.Constants.Companion.KEY_GRID_COLUMNS
import rasel.lunar.launcher.helpers.Constants.Companion.KEY_ICON_PACK
import rasel.lunar.launcher.helpers.Constants.Companion.KEY_KEYBOARD_SEARCH
import rasel.lunar.launcher.helpers.Constants.Companion.KEY_QUICK_LAUNCH
import rasel.lunar.launcher.helpers.Constants.Companion.KEY_SCROLLBAR_HEIGHT
import rasel.lunar.launcher.settings.SettingsActivity.Companion.settingsPrefs
import kotlin.system.exitProcess


internal class Apps : BottomSheetDialogFragment() {

    private lateinit var binding: SettingsAppsBinding
    private var settingsChanged: Boolean = false
    private var packageManager: PackageManager? = null

    @SuppressLint("RtlHardcoded")
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = SettingsAppsBinding.inflate(inflater, container, false)
        packageManager = requireActivity().packageManager

        /* initialize views according to the saved values */
        when (settingsPrefs!!.getBoolean(KEY_KEYBOARD_SEARCH, false)) {
            false -> binding.keyboardAutoNegative.isChecked = true
            true -> binding.keyboardAutoPositive.isChecked = true
        }

        when (settingsPrefs!!.getBoolean(KEY_QUICK_LAUNCH, true)) {
            true -> binding.quickLaunchPositive.isChecked = true
            false -> binding.quickLaunchNegative.isChecked = true
        }

        when (settingsPrefs!!.getBoolean(KEY_APPS_LAYOUT, true)) {
            true -> {
                binding.drawerLayoutList.isChecked = true
                binding.appAlignmentGroup.children.forEach { it.isEnabled = true }
                binding.iconPackChooser.isEnabled = false
                binding.columnsCount.isEnabled = false
            }
            false -> {
                binding.drawerLayoutGrid.isChecked = true
                binding.appAlignmentGroup.children.forEach { it.isEnabled = false }
                binding.iconPackChooser.isEnabled = true
                binding.columnsCount.isEnabled = true
            }
        }

        when (settingsPrefs!!.getInt(KEY_DRAW_ALIGN, Gravity.CENTER)) {
            Gravity.CENTER -> binding.appAlignmentCenter.isChecked = true
            Gravity.LEFT -> binding.appAlignmentLeft.isChecked = true
            Gravity.RIGHT -> binding.appAlignmentRight.isChecked = true
        }

        binding.columnsCount.value = settingsPrefs!!.getInt(KEY_GRID_COLUMNS, DEFAULT_GRID_COLUMNS).toFloat()
        binding.scrollbarHeight.value = settingsPrefs!!.getInt(KEY_SCROLLBAR_HEIGHT, DEFAULT_SCROLLBAR_HEIGHT).toFloat()

        return binding.root
    }

    @SuppressLint("RtlHardcoded")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        (requireDialog() as BottomSheetDialog).dismissWithAnimation = true

        /* change search with keyboard value */
        binding.keyboardAutoGroup.setOnCheckedStateChangeListener { group, _ ->
            when (group.checkedChipId) {
                binding.keyboardAutoPositive.id -> settingsPrefs!!.edit().putBoolean(KEY_KEYBOARD_SEARCH, true).apply()
                binding.keyboardAutoNegative.id -> settingsPrefs!!.edit().putBoolean(KEY_KEYBOARD_SEARCH, false).apply()
            }
        }

        /* change settings for quick launch */
        binding.quickLaunchGroup.setOnCheckedStateChangeListener { group, _ ->
            when (group.checkedChipId) {
                binding.quickLaunchPositive.id -> settingsPrefs!!.edit().putBoolean(KEY_QUICK_LAUNCH, true).apply()
                binding.quickLaunchNegative.id -> settingsPrefs!!.edit().putBoolean(KEY_QUICK_LAUNCH, false).apply()
            }
        }

        binding.drawerLayoutGroup.setOnCheckedStateChangeListener { group, _ ->
            settingsChanged = true
            when (group.checkedChipId) {
                binding.drawerLayoutList.id -> {
                    settingsPrefs!!.edit().putBoolean(KEY_APPS_LAYOUT, true).apply()
                    binding.appAlignmentGroup.children.forEach { if (!it.isEnabled) it.isEnabled = true }
                    binding.iconPackChooser.let { if (it.isEnabled) it.isEnabled = false }
                    binding.columnsCount.let { if (it.isEnabled) it.isEnabled = false }
                }
                binding.drawerLayoutGrid.id -> {
                    settingsPrefs!!.edit().putBoolean(KEY_APPS_LAYOUT, false).apply()
                    binding.appAlignmentGroup.children.forEach { if (it.isEnabled) it.isEnabled = false }
                    binding.iconPackChooser.let { if (!it.isEnabled) it.isEnabled = true }
                    binding.columnsCount.let { if (!it.isEnabled) it.isEnabled = true }
                }
            }
        }

        binding.appAlignmentGroup.setOnCheckedStateChangeListener { group, _ ->
            when (group.checkedChipId) {
                binding.appAlignmentLeft.id -> settingsPrefs!!.edit().putInt(KEY_DRAW_ALIGN, Gravity.LEFT).apply()
                binding.appAlignmentCenter.id -> settingsPrefs!!.edit().putInt(KEY_DRAW_ALIGN, Gravity.CENTER).apply()
                binding.appAlignmentRight.id -> settingsPrefs!!.edit().putInt(KEY_DRAW_ALIGN, Gravity.RIGHT).apply()
            }
        }

        binding.iconPackChooser.setOnClickListener { iconPackChooser() }

        binding.columnsCount.addOnChangeListener(Slider.OnChangeListener { _, value, _ ->
            settingsChanged = true
            settingsPrefs!!.edit().putInt(KEY_GRID_COLUMNS, value.toInt()).apply()
        })

        binding.scrollbarHeight.addOnChangeListener(Slider.OnChangeListener { _, value, _ ->
            settingsPrefs!!.edit().putInt(KEY_SCROLLBAR_HEIGHT, value.toInt()).apply()
        })
    }

    override fun onDestroyView() {
        super.onDestroyView()
        if (settingsChanged) {
            MaterialAlertDialogBuilder(requireActivity())
                .setTitle(R.string.restart_now)
                .setMessage(R.string.restart_message)
                .setPositiveButton(R.string.restart) { _, _ ->
                    exitProcess(0)
                }
                .setNeutralButton(R.string.later, null)
                .show()
        }
    }

    private fun iconPackChooser() {
        if (installedIconPacks.isNotEmpty()) {
            var selectedIconPack: String? = null

            val chipGroup = ChipGroup(requireContext()).apply {
                layoutParams = LinearLayoutCompat.LayoutParams(WRAP_CONTENT, WRAP_CONTENT)
                isSingleSelection = true
                isSelectionRequired = true
                setOnCheckedStateChangeListener { group, _ ->
                    selectedIconPack = group.findViewById<Chip>(group.checkedChipId).tag as String
                }
            }

            installedIconPacks.indices.forEach { i ->
                Chip(requireContext()).apply {
                    layoutParams = LinearLayoutCompat.LayoutParams(WRAP_CONTENT, WRAP_CONTENT)
                    setChipDrawable(ChipDrawable.createFromAttributes(requireContext(), null, 0,
                        com.google.android.material.R.style.Widget_Material3_Chip_Filter_Elevated))

                    text = packageManager?.getApplicationLabel(appInfo(installedIconPacks[i])!!)
                    tag = installedIconPacks[i]

                    if (settingsPrefs!!.getString(KEY_ICON_PACK, DEFAULT_ICON_PACK).equals(tag as String)) {
                        isChecked = true
                    }
                }.let { chipGroup.addView(it) }
            }

            val linearLayoutCompat = LinearLayoutCompat(requireContext()).apply {
                layoutParams = LinearLayoutCompat.LayoutParams(MATCH_PARENT, WRAP_CONTENT)
                gravity = Gravity.CENTER
                setPadding(8, 8, 8, 8)
                addView(chipGroup)
            }

            MaterialAlertDialogBuilder(requireActivity()).apply {
                setTitle("Choose Icon Pack")
                setView(linearLayoutCompat)
                setPositiveButton(android.R.string.ok) { dialog, _ ->
                    when (selectedIconPack) {
                        null -> dialog.dismiss()
                        else -> {
                            if (!selectedIconPack.equals(settingsPrefs!!.getString(KEY_ICON_PACK, DEFAULT_ICON_PACK))) {
                                settingsChanged = true
                                settingsPrefs!!.edit().putString(KEY_ICON_PACK, selectedIconPack).apply()
                            } else { dialog.dismiss() }

                        }
                    }
                }
                setNeutralButton(R.string.default_) { dialog, _ ->
                    if (DEFAULT_ICON_PACK != settingsPrefs!!.getString(KEY_ICON_PACK, DEFAULT_ICON_PACK)) {
                        settingsChanged = true
                        settingsPrefs!!.edit().putString(KEY_ICON_PACK, DEFAULT_ICON_PACK).apply()
                    } else { dialog.dismiss() }
                }
                show()
            }
        } else {
            Toast.makeText(requireContext(), R.string.icon_pack_not_found, Toast.LENGTH_SHORT).show()
        }
    }

    private val installedIconPacks: ArrayList<String> get() {
        val iconPacks = ArrayList<String>()

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            packageManager?.queryIntentActivities(
                Intent("org.adw.launcher.THEMES"),
                PackageManager.ResolveInfoFlags.of(PackageManager.GET_META_DATA.toLong())
            )
        } else {
            @Suppress("DEPRECATION")
            (packageManager?.queryIntentActivities(
                Intent("org.adw.launcher.THEMES"), PackageManager.GET_META_DATA))
        }.let {
            it?.indices?.forEach { i ->
                it[i].activityInfo.packageName.let { packageName: String? ->
                    iconPacks.add(packageName!!)
                }
            }
        }

        return iconPacks
    }

    private fun appInfo(packageName: String) : ApplicationInfo? {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            packageManager?.getApplicationInfo(packageName,
                PackageManager.ApplicationInfoFlags.of(PackageManager.GET_META_DATA.toLong()))
        } else {
            @Suppress("DEPRECATION")
            packageManager?.getApplicationInfo(packageName, PackageManager.GET_META_DATA)
        }
    }

}
