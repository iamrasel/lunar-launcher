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

import android.Manifest.permission.READ_EXTERNAL_STORAGE
import android.app.Activity.RESULT_OK
import android.app.AlertDialog
import android.app.WallpaperManager
import android.content.Intent
import android.content.pm.PackageManager
import android.content.res.ColorStateList
import android.graphics.BitmapFactory
import android.graphics.Color
import android.graphics.Matrix
import android.os.Bundle
import android.provider.MediaStore
import android.text.SpannableStringBuilder
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatDelegate
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.button.MaterialButtonToggleGroup
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import rasel.lunar.launcher.R
import rasel.lunar.launcher.databinding.ColorPickerBinding
import rasel.lunar.launcher.databinding.SettingsAppearancesBinding
import rasel.lunar.launcher.helpers.ColorPicker
import rasel.lunar.launcher.helpers.Constants
import rasel.lunar.launcher.helpers.UniUtils
import rasel.lunar.launcher.settings.PrefsUtil
import java.io.IOException
import java.util.*


internal class Appearances : BottomSheetDialogFragment() {

    private lateinit var binding : SettingsAppearancesBinding
    private lateinit var windowBackground : String

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = SettingsAppearancesBinding.inflate(inflater, container, false)

        /* initialize views according to the saved values */
        when (AppCompatDelegate.getDefaultNightMode()) {
            AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM -> binding.followSystemTheme.isChecked = true
            AppCompatDelegate.MODE_NIGHT_YES -> binding.selectDarkTheme.isChecked = true
            AppCompatDelegate.MODE_NIGHT_NO -> binding.selectLightTheme.isChecked = true
            else -> binding.followSystemTheme.isChecked = true
        }

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        (requireDialog() as BottomSheetDialog).dismissWithAnimation = true

        /* change theme */
        binding.themeGroup.addOnButtonCheckedListener { _: MaterialButtonToggleGroup?, checkedId: Int, isChecked: Boolean ->
            if (isChecked) {
                when (checkedId) {
                    binding.followSystemTheme.id ->
                        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM)
                    binding.selectDarkTheme.id ->
                        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
                    binding.selectLightTheme.id ->
                        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
                }
            }
        }

        binding.background.setOnClickListener { selectBackground() }
        binding.changeWallpaper.setOnClickListener { selectWallpaper() }
    }

    override fun onResume() {
        super.onResume()
        windowBackground = requireContext().getSharedPreferences(Constants().PREFS_SETTINGS, 0)
            .getString(Constants().KEY_WINDOW_BACKGROUND, defaultColorString()).toString()
        binding.background.iconTint = ColorStateList.valueOf(Color.parseColor("#${windowBackground}"))
    }

    private fun selectBackground() {
        val prefsUtil = PrefsUtil()
        val colorPickerBinding = ColorPickerBinding.inflate(requireActivity().layoutInflater)
        val dialogBuilder = MaterialAlertDialogBuilder(requireActivity())
            .setView(colorPickerBinding.root)
            .setNeutralButton(R.string.default_, null)
            .setNegativeButton(android.R.string.cancel, null)
            .setPositiveButton(android.R.string.ok) { _, _ ->
                prefsUtil.windowBackground(requireContext(),
                    Objects.requireNonNull(colorPickerBinding.colorInput.text).toString().trim { it <= ' ' })
                this.onResume()
            }
            .show()

        /* set up color picker section */
        ColorPicker(windowBackground, colorPickerBinding.colorInput, colorPickerBinding.colorA,
            colorPickerBinding.colorR, colorPickerBinding.colorG,
            colorPickerBinding.colorB, colorPickerBinding.root).pickColor()

        dialogBuilder.getButton(AlertDialog.BUTTON_NEUTRAL).setOnClickListener {
            colorPickerBinding.colorInput.text =
                SpannableStringBuilder(defaultColorString())
        }
    }

    private fun selectWallpaper() {
        if (requireActivity().checkSelfPermission(READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            requireActivity().requestPermissions(arrayOf(READ_EXTERNAL_STORAGE), 1)
        } else {
            wallpaperChangeLauncher.launch(Intent(Intent.ACTION_PICK).setType("image/*"))
        }
    }

    private fun defaultColorString() =
        requireActivity().getString(UniUtils().getColorResId(
            requireContext(), android.R.attr.colorBackground)).replace("#", "")

    private var wallpaperChangeLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == RESULT_OK) {
            try {
                val uri = result.data?.data
                val projection = arrayOf(MediaStore.Images.Media.DATA)
                val cursor = requireContext().contentResolver.query(
                    uri!!, projection, null, null, null
                )
                cursor?.moveToFirst()
                val index = cursor!!.getColumnIndex(projection[0])
                val filePath = cursor.getString(index)
                cursor.close()
                val bitmap = BitmapFactory.decodeFile(filePath)
                val matrix = Matrix()
                matrix.postRotate(0F)
                try {
                    if (bitmap != null) {
                        WallpaperManager.getInstance(requireContext()).setBitmap(bitmap)
                        Toast.makeText(requireContext(),
                            requireActivity().getString(R.string.wallpaper_change_success), Toast.LENGTH_SHORT).show()
                    } else {
                        Toast.makeText(requireContext(),
                            requireActivity().getString(R.string.image_pick_failed), Toast.LENGTH_SHORT).show()
                    }
                } catch (e: IOException) {
                    e.printStackTrace()
                    Toast.makeText(requireContext(),
                        requireActivity().getString(R.string.something_went_wrong), Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

}
