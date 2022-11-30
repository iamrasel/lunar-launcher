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

import android.Manifest
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.graphics.*
import android.media.AudioManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.text.InputType
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
import com.google.android.material.button.MaterialButtonToggleGroup
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.slider.Slider
import com.google.android.material.textview.MaterialTextView
import rasel.lunar.launcher.LauncherActivity
import rasel.lunar.launcher.R
import rasel.lunar.launcher.databinding.QuickAccessBinding
import rasel.lunar.launcher.databinding.ShortcutMakerBinding
import rasel.lunar.launcher.helpers.ColorPicker
import rasel.lunar.launcher.helpers.Constants.Companion.KEY_APP_NO_
import rasel.lunar.launcher.helpers.Constants.Companion.KEY_SHORTCUT_COUNT
import rasel.lunar.launcher.helpers.Constants.Companion.KEY_SHORTCUT_NO_
import rasel.lunar.launcher.helpers.Constants.Companion.PREFS_FAVORITE_APPS
import rasel.lunar.launcher.helpers.Constants.Companion.PREFS_SETTINGS
import rasel.lunar.launcher.helpers.Constants.Companion.PREFS_SHORTCUTS
import rasel.lunar.launcher.helpers.Constants.Companion.SHORTCUT_TYPE_PHONE
import rasel.lunar.launcher.helpers.Constants.Companion.SHORTCUT_TYPE_URL
import rasel.lunar.launcher.settings.PrefsUtil.Companion.removeFavApps
import java.util.*


internal class QuickAccess : BottomSheetDialogFragment() {

    private lateinit var binding: QuickAccessBinding
    private lateinit var fragmentActivity: FragmentActivity
    private lateinit var sharedPreferences: SharedPreferences

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = QuickAccessBinding.inflate(inflater, container, false)

        fragmentActivity = if (isAdded) {
            requireActivity()
        } else {
            LauncherActivity()
        }

        sharedPreferences = requireContext().getSharedPreferences(PREFS_SHORTCUTS, 0)
        /* set up volume sliders, brightness slider and favorite apps */
        volumeControllers()
        controlBrightness()

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        /* enable dismiss animation */
        (requireDialog() as BottomSheetDialog).dismissWithAnimation = true
    }

    override fun onResume() {
        super.onResume()
        /* repopulate shortcuts and apps */
        binding.shortcutsGroup.removeAllViews()
        shortcuts()
        binding.favAppsGroup.removeAllViews()
        favApps()
    }

    /* control the volumes */
    private fun volumeControllers() {
        val audioManager = fragmentActivity.getSystemService(Context.AUDIO_SERVICE) as AudioManager
        /* max value */
        binding.notification.valueTo = audioManager.getStreamMaxVolume(AudioManager.STREAM_NOTIFICATION).toFloat()
        binding.alarm.valueTo = audioManager.getStreamMaxVolume(AudioManager.STREAM_ALARM).toFloat()
        binding.media.valueTo = audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC).toFloat()
        binding.voice.valueTo = audioManager.getStreamMaxVolume(AudioManager.STREAM_VOICE_CALL).toFloat()
        binding.ring.valueTo = audioManager.getStreamMaxVolume(AudioManager.STREAM_RING).toFloat()
        /* current value */
        binding.notification.value = audioManager.getStreamVolume(AudioManager.STREAM_NOTIFICATION).toFloat()
        binding.alarm.value = audioManager.getStreamVolume(AudioManager.STREAM_ALARM).toFloat()
        binding.media.value = audioManager.getStreamVolume(AudioManager.STREAM_MUSIC).toFloat()
        binding.voice.value = audioManager.getStreamVolume(AudioManager.STREAM_VOICE_CALL).toFloat()
        binding.ring.value = audioManager.getStreamVolume(AudioManager.STREAM_RING).toFloat()

        /* slider change listener for alarm volume */
        binding.alarm.addOnChangeListener(Slider.OnChangeListener { _: Slider?, value: Float, _: Boolean ->
            audioManager.setStreamVolume(AudioManager.STREAM_ALARM, value.toInt(), 0)
        })

        /* slider change listener for media volume */
        binding.media.addOnChangeListener(Slider.OnChangeListener { _: Slider?, value: Float, _: Boolean ->
            audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, value.toInt(), 0)
        })

        /* slider change listener for voice call volume */
        binding.voice.addOnChangeListener(Slider.OnChangeListener { _: Slider?, value: Float, _: Boolean ->
            audioManager.setStreamVolume(AudioManager.STREAM_VOICE_CALL, value.toInt(), 0)
        })

        /*  notify and ring volume sliders will work only if
            the device isn't in dnd or silent mode */
        if (Settings.Global.getInt(fragmentActivity.contentResolver, "zen_mode") == 0 &&
            audioManager.ringerMode != AudioManager.RINGER_MODE_SILENT) {
            /* slider change listener for notify volume */
            binding.notification.addOnChangeListener(Slider.OnChangeListener { _: Slider?, value: Float, _: Boolean ->
                audioManager.setStreamVolume(AudioManager.STREAM_NOTIFICATION, value.toInt(), 0)
            })
            /* slider change listener for ring volume */
            binding.ring.addOnChangeListener(Slider.OnChangeListener { _: Slider?, value: Float, _: Boolean ->
                audioManager.setStreamVolume(AudioManager.STREAM_RING, value.toInt(), 0)
            })
        } else {
            binding.notification.isEnabled = false
            binding.ring.isEnabled = false
        }
    }

    /* set up contact and url shortcuts */
    private fun shortcuts() {
        val shortcutCount =
            requireContext().getSharedPreferences(PREFS_SETTINGS, 0).getInt(KEY_SHORTCUT_COUNT, 6)
        if (shortcutCount == 0) binding.shortcutsGroup.visibility = View.GONE

        for (position in 1..shortcutCount) {
            val shortcutValue = sharedPreferences.getString(KEY_SHORTCUT_NO_ + position.toString(), "").toString()
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

            shortcutsUtil(textView, shortcutType, intentString, thumbLetter, color, position)
        }
    }

    /* control the brightness */
    private fun controlBrightness() {
        val resolver = fragmentActivity.contentResolver
        /* set max value */
        binding.brightness.valueTo = 255f

        /* set slider value to current brightness value */
        try {
            val brightness = Settings.System.getInt(resolver, Settings.System.SCREEN_BRIGHTNESS)
            binding.brightness.value = brightness.toFloat()
        } catch (settingNotFoundException: Settings.SettingNotFoundException) {
            settingNotFoundException.printStackTrace()
        }

        /* listen slider value changes */
        binding.brightness.addOnChangeListener(Slider.OnChangeListener { _: Slider?, value: Float, _: Boolean ->
            /*  if write settings permission is not allowed already,
                again ask for it to be granted */
            if (!Settings.System.canWrite(fragmentActivity)) {
                fragmentActivity.startActivity(
                    Intent(Settings.ACTION_MANAGE_WRITE_SETTINGS)
                        .setData(Uri.parse("package:" + fragmentActivity.packageName))
                        .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                )
                /* set the brightness according to the slider value */
            } else {
                Settings.System.putInt(
                    resolver,
                    Settings.System.SCREEN_BRIGHTNESS_MODE,
                    Settings.System.SCREEN_BRIGHTNESS_MODE_MANUAL
                )
                Settings.System.putInt(resolver, Settings.System.SCREEN_BRIGHTNESS, value.toInt())
            }
        })
    }

    /* set up favorite apps */
    private fun favApps() {
        val prefsFavApps = requireContext().getSharedPreferences(PREFS_FAVORITE_APPS, 0)
        if (prefsFavApps.all.toString().length < 3) {
            binding.favAppsGroup.visibility = View.GONE
        } else {
            binding.favAppsGroup.visibility = View.VISIBLE
            for (position in 1..6) {
                val packageValue = prefsFavApps.getString(KEY_APP_NO_ + position.toString(), "").toString()
                favApp(packageValue, imageView, position)
            }
        }
    }

    /* contact/url shortcuts */
    private fun shortcutsUtil(textView: MaterialTextView, shortcutType: String, intentString: String,
                              thumbLetter: String, color: String, position: Int) {
        /* show plus sign for empty positions and set click listener */
        if (intentString.isEmpty()) {
            textView.text = "+"
            textView.setOnClickListener { shortcutsSaverDialog(position) }
        } else {
            /* show thumbnail letter */
            textView.text = thumbLetter
            /* set background color */
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                textView.background.colorFilter =
                    BlendModeColorFilter(Color.parseColor("#$color"), BlendMode.MULTIPLY)
            } else {
                @Suppress("DEPRECATION")
                textView.background.setColorFilter(Color.parseColor("#$color"), PorterDuff.Mode.MULTIPLY)
            }

            /* on normal click */
            textView.setOnClickListener {
                /* type is url */
                if (shortcutType == SHORTCUT_TYPE_URL) {
                    var url = intentString
                    /* add http before the url if it doesn't have http/https prefix */
                    if (!url.startsWith("http://") && !url.startsWith("https://")) {
                        url = "http://$intentString"
                    }
                    /* open the url */
                    fragmentActivity.startActivity(
                        Intent(Intent.ACTION_VIEW, Uri.parse(url)).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                    )
                    /* type is contact */
                } else if (shortcutType == SHORTCUT_TYPE_PHONE) {
                    /*  if the necessary permission is not granted already,
                        ask for it again */
                    if (fragmentActivity.checkSelfPermission(Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                        fragmentActivity.requestPermissions(arrayOf(Manifest.permission.CALL_PHONE), 1)
                    } else {
                        /* make phone call */
                        fragmentActivity.startActivity(
                            Intent(Intent.ACTION_CALL, Uri.parse("tel:$intentString"))
                        )
                    }
                }
                this.dismiss()
            }

            /* reset the shortcut on long click */
            textView.setOnLongClickListener {
                sharedPreferences.edit().remove(KEY_SHORTCUT_NO_ + position).apply()
                this.onResume()
                true
            }
        }
    }

    /* dialog for creating shortcuts */
    private fun shortcutsSaverDialog(position: Int) {
        val dialogBinding = ShortcutMakerBinding.inflate(fragmentActivity.layoutInflater)
        val dialogBuilder = MaterialAlertDialogBuilder(fragmentActivity)
            .setView(dialogBinding.root)
            .setNegativeButton(android.R.string.cancel, null)
            .setPositiveButton(android.R.string.ok, null)
            .show()

        /* set up color picker section */
        ColorPicker("00000000", dialogBinding.colorPicker.colorInput, dialogBinding.colorPicker.colorA,
            dialogBinding.colorPicker.colorR, dialogBinding.colorPicker.colorG,
            dialogBinding.colorPicker.colorB, dialogBinding.root).pickColor()

        /* shortcut type chooser - contact/url */
        var shortcutType = ""
        dialogBinding.shortcutType.addOnButtonCheckedListener { _: MaterialButtonToggleGroup?, checkedId: Int, isChecked: Boolean ->
            if (isChecked) {
                when (checkedId) {
                    dialogBinding.contact.id -> {
                        shortcutType = SHORTCUT_TYPE_PHONE
                        dialogBinding.inputField.inputType = InputType.TYPE_CLASS_PHONE
                    }
                    dialogBinding.url.id -> {
                        shortcutType = SHORTCUT_TYPE_URL
                        dialogBinding.inputField.inputType = InputType.TYPE_TEXT_VARIATION_URI
                    }
                }
            }
        }

        /* save the shortcut values */
        dialogBuilder.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener {
            /* get shortcut value */
            val intentString =
                Objects.requireNonNull(dialogBinding.inputField.text).toString().trim { it <= ' ' }
            /* get thumbnail letter */
            val thumbLetter =
                Objects.requireNonNull(dialogBinding.thumbField.text).toString().trim { it <= ' ' }.uppercase()
            /* get color value */
            val color =
                Objects.requireNonNull(dialogBinding.colorPicker.colorInput.text).toString().trim { it <= ' ' }

            /* save the values if every field is filled */
            if (shortcutType.isNotEmpty() && intentString.isNotEmpty() && thumbLetter.isNotEmpty() && color.isNotEmpty()) {
                sharedPreferences.edit().putString(KEY_SHORTCUT_NO_ + position,
                    "$shortcutType||$intentString||$thumbLetter||$color").apply()
                dialogBuilder.dismiss()
                this.onResume()
            }
        }
    }

    /* favorite apps */
    private fun favApp(packageName: String, imageView: AppCompatImageView, position: Int) {
        val packageManager = requireContext().packageManager
        /* package name is not empty for a specific position */
        if (packageName.isNotEmpty()) {
            try {
                /* show app icon */
                imageView.setImageDrawable(packageManager.getApplicationIcon(packageName))
                /* on click - open app */
                imageView.setOnClickListener {
                    requireContext().startActivity(packageManager.getLaunchIntentForPackage(packageName))
                    this.dismiss()
                }
                /* on long click - remove from favorite apps */
                imageView.setOnLongClickListener {
                    removeFavApps(requireContext(), position)
                    this.onResume()
                    true
                }
            } catch (nameNotFoundException: PackageManager.NameNotFoundException) {
                removeFavApps(requireContext(), position)
                imageView.visibility = View.GONE
                nameNotFoundException.printStackTrace()
            }
        } else {
            imageView.visibility = View.GONE
        }
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
            (48 * resources.displayMetrics.density).toInt(),
            (48 * resources.displayMetrics.density).toInt())
        textView.gravity = Gravity.CENTER
        textView.textSize = 20 * resources.displayMetrics.density
        textView.setTypeface(null, Typeface.BOLD)
        textView.background = ContextCompat.getDrawable(requireContext(), R.drawable.rounded_bg)
        relativeLayout.addView(textView)
        return textView
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

}
