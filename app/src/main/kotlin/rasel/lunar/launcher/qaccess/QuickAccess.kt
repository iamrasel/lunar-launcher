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

import android.graphics.Typeface
import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RelativeLayout
import androidx.appcompat.widget.AppCompatImageView
import androidx.appcompat.widget.LinearLayoutCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentActivity
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.textview.MaterialTextView
import rasel.lunar.launcher.LauncherActivity
import rasel.lunar.launcher.R
import rasel.lunar.launcher.databinding.QuickAccessBinding
import rasel.lunar.launcher.helpers.Constants


internal class QuickAccess : BottomSheetDialogFragment() {

    private lateinit var binding: QuickAccessBinding
    private lateinit var fragmentActivity: FragmentActivity
    private lateinit var accessUtils: AccessUtils
    private val constants = Constants()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = QuickAccessBinding.inflate(inflater, container, false)

        fragmentActivity = if (isAdded) {
            requireActivity()
        } else {
            LauncherActivity()
        }

        accessUtils = AccessUtils(requireContext(), this, fragmentActivity)
        /* favorite apps */
        favApps()
        /* set up brightness slider */
        accessUtils.controlBrightness(binding.brightness)
        /* set up volume sliders */
        accessUtils.volumeControllers(binding.notification, binding.alarm, binding.media, binding.voice, binding.ring)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        /* enable dismiss animation */
        (requireDialog() as BottomSheetDialog).dismissWithAnimation = true
    }

    /* set up favorite apps */
    private fun favApps() {
        val prefsFavApps = requireContext().getSharedPreferences(constants.PREFS_FAVORITE_APPS, 0)
        for (position in 1..6) {
            val packageValue = prefsFavApps.getString(constants.KEY_APP_NO_ + position.toString(), "").toString()
            accessUtils.favApps(packageValue, imageView, position)
        }
    }

    /* set up contact and url shortcuts */
    private fun shortcuts() {
        val prefsShortcuts = requireContext().getSharedPreferences(constants.PREFS_SHORTCUTS, 0)
        for (position in 1..6) {
            val shortcutValue = prefsShortcuts.getString(constants.KEY_SHORTCUT_NO_ + position.toString(), "").toString()
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

            accessUtils.shortcutsUtil(textView, shortcutType, intentString, thumbLetter, color, position)
        }
    }

    /* create image view for favorite app icons */
    private val imageView: AppCompatImageView get() {
        val imageView = AppCompatImageView(fragmentActivity)
        imageView.layoutParams = LinearLayoutCompat.LayoutParams(
            LinearLayoutCompat.LayoutParams.WRAP_CONTENT,
            LinearLayoutCompat.LayoutParams.MATCH_PARENT, 1F)
        binding.favAppsGroup.addView(imageView)
        return imageView
    }

    /* create text view for shortcut thumbnails */
    private val textView: MaterialTextView get() {
        val relativeLayout = RelativeLayout(fragmentActivity)
        relativeLayout.layoutParams = LinearLayoutCompat.LayoutParams(
            LinearLayoutCompat.LayoutParams.WRAP_CONTENT,
            LinearLayoutCompat.LayoutParams.WRAP_CONTENT, 1F)
        relativeLayout.gravity = Gravity.CENTER
        binding.shortcutsGroup.addView(relativeLayout)

        val textView = MaterialTextView(fragmentActivity)
        textView.layoutParams = LinearLayoutCompat.LayoutParams(
            (54 * resources.displayMetrics.density).toInt(),
            (54 * resources.displayMetrics.density).toInt())
        textView.gravity = Gravity.CENTER
        textView.textSize = 20 * resources.displayMetrics.density
        textView.setTypeface(null, Typeface.BOLD)
        textView.background = ContextCompat.getDrawable(requireContext(), R.drawable.rounded_bg)
        relativeLayout.addView(textView)
        return textView
    }

    override fun onResume() {
        super.onResume()
        /* repopulate shortcuts */
        binding.shortcutsGroup.removeAllViews()
        shortcuts()
    }

}
