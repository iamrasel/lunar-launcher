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
import android.os.PowerManager
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
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.button.MaterialButtonToggleGroup
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.slider.Slider
import com.google.android.material.textview.MaterialTextView
import rasel.lunar.launcher.LauncherActivity.Companion.lActivity
import rasel.lunar.launcher.R
import rasel.lunar.launcher.databinding.QuickAccessBinding
import rasel.lunar.launcher.databinding.ShortcutMakerBinding
import rasel.lunar.launcher.helpers.ColorPicker
import rasel.lunar.launcher.helpers.Constants.Companion.DEFAULT_ICON_SIZE
import rasel.lunar.launcher.helpers.Constants.Companion.KEY_APP_NO_
import rasel.lunar.launcher.helpers.Constants.Companion.KEY_ICON_SIZE
import rasel.lunar.launcher.helpers.Constants.Companion.KEY_SHORTCUT_COUNT
import rasel.lunar.launcher.helpers.Constants.Companion.KEY_SHORTCUT_NO_
import rasel.lunar.launcher.helpers.Constants.Companion.MAX_FAVORITE_APPS
import rasel.lunar.launcher.helpers.Constants.Companion.MAX_SHORTCUTS
import rasel.lunar.launcher.helpers.Constants.Companion.PREFS_FAVORITE_APPS
import rasel.lunar.launcher.helpers.Constants.Companion.PREFS_SETTINGS
import rasel.lunar.launcher.helpers.Constants.Companion.PREFS_SHORTCUTS
import rasel.lunar.launcher.helpers.Constants.Companion.SEPARATOR
import rasel.lunar.launcher.helpers.Constants.Companion.SHORTCUT_TYPE_PHONE
import rasel.lunar.launcher.helpers.Constants.Companion.SHORTCUT_TYPE_URL
import rasel.lunar.launcher.helpers.PrefsUtil.Companion.removeFavApps
import java.util.*
import kotlin.properties.Delegates


internal class QuickAccess : BottomSheetDialogFragment() {

    private lateinit var binding: QuickAccessBinding
    private lateinit var sharedPreferences: SharedPreferences
    private var iconSize by Delegates.notNull<Int>()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = QuickAccessBinding.inflate(inflater, container, false)

        sharedPreferences = requireContext().getSharedPreferences(PREFS_SHORTCUTS, 0)
        iconSize = requireContext().getSharedPreferences(PREFS_SETTINGS, 0).getInt(KEY_ICON_SIZE, DEFAULT_ICON_SIZE)

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
        shortcuts()
        favApps()
    }

    /* control the volumes */
    private fun volumeControllers() {
        val audioManager = lActivity!!.getSystemService(Context.AUDIO_SERVICE) as AudioManager
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
        if (Settings.Global.getInt(lActivity!!.contentResolver, "zen_mode") == 0 &&
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
        binding.shortcutsGroup.removeAllViews()
        val shortcutCount =
            requireContext().getSharedPreferences(PREFS_SETTINGS, 0).getInt(KEY_SHORTCUT_COUNT, MAX_SHORTCUTS)
        if (shortcutCount == 0) binding.shortcutsGroup.visibility = View.GONE

        for (position in 1..shortcutCount) {
            val shortcutValue = sharedPreferences.getString(KEY_SHORTCUT_NO_ + position.toString(), "").toString()
            val splitShortcutValue = shortcutValue.split(SEPARATOR).toTypedArray()

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
        val resolver = lActivity!!.contentResolver
        /* set max value */
        binding.brightness.valueTo = maxBrightness

        /* set slider value to current brightness value */
        try {
            binding.brightness.value = Settings.System.getInt(resolver, Settings.System.SCREEN_BRIGHTNESS).toFloat()
        } catch (settingNotFoundException: Settings.SettingNotFoundException) {
            settingNotFoundException.printStackTrace()
        }

        /* listen slider value changes */
        binding.brightness.addOnChangeListener(Slider.OnChangeListener { _: Slider?, value: Float, _: Boolean ->
            /*  if write settings permission is not allowed already,
                again ask for it to be granted */
            if (!Settings.System.canWrite(lActivity!!)) {
                lActivity!!.startActivity(
                    Intent(Settings.ACTION_MANAGE_WRITE_SETTINGS)
                        .setData(Uri.parse("package:" + lActivity!!.packageName))
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
        binding.favAppsGroup.removeAllViews()
        val prefsFavApps = requireContext().getSharedPreferences(PREFS_FAVORITE_APPS, 0)
        if (prefsFavApps.all.toString().length < 3) {
            binding.favAppsGroup.visibility = View.GONE
        } else {
            binding.favAppsGroup.visibility = View.VISIBLE
            for (position in 1..MAX_FAVORITE_APPS) {
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
            textView.setOnClickListener {
                shortcutsSaverDialog(position, "00000000", "", "", "")
            }
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
                    lActivity!!.startActivity(
                        Intent(Intent.ACTION_VIEW, Uri.parse(url)).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                    )
                    /* type is contact */
                } else if (shortcutType == SHORTCUT_TYPE_PHONE) {
                    /*  if the necessary permission is not granted already,
                        ask for it again */
                    if (lActivity!!.checkSelfPermission(Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                        lActivity!!.requestPermissions(arrayOf(Manifest.permission.CALL_PHONE), 1)
                    } else {
                        /* make phone call */
                        lActivity!!.startActivity(
                            Intent(Intent.ACTION_CALL, Uri.parse("tel:$intentString"))
                        )
                    }
                }
                this.dismiss()
            }

            /* reset the shortcut on long click */
            textView.setOnLongClickListener {
                shortcutsSaverDialog(position, color, thumbLetter, shortcutType, intentString)
                true
            }
        }
    }

    /* dialog for creating shortcuts */
    private fun shortcutsSaverDialog(
        position: Int, color: String, thumbLetter: String, shortcutType: String, intentString: String) {
        val dialogBinding = ShortcutMakerBinding.inflate(lActivity!!.layoutInflater)
        val dialogBuilder = MaterialAlertDialogBuilder(lActivity!!)
            .setView(dialogBinding.root)
            .setNeutralButton(R.string.delete, null)
            .setNegativeButton(android.R.string.cancel, null)
            .setPositiveButton(android.R.string.ok, null)
            .show()

        dialogBinding.thumbField.setText(thumbLetter)
        dialogBinding.inputField.setText(intentString)
        when (shortcutType) {
            SHORTCUT_TYPE_PHONE -> dialogBinding.shortcutType.check(dialogBinding.contact.id)
            SHORTCUT_TYPE_URL -> dialogBinding.shortcutType.check(dialogBinding.url.id)
        }

        /* set up color picker section */
        ColorPicker(color, dialogBinding.colorPicker.colorInput, dialogBinding.colorPicker.colorA,
            dialogBinding.colorPicker.colorR, dialogBinding.colorPicker.colorG,
            dialogBinding.colorPicker.colorB, dialogBinding.root).pickColor()

        /* shortcut type chooser - contact/url */
        var updatedShortcutType = shortcutType
        dialogBinding.shortcutType.addOnButtonCheckedListener {
                _: MaterialButtonToggleGroup?, checkedId: Int, isChecked: Boolean ->
            if (isChecked) {
                when (checkedId) {
                    dialogBinding.contact.id -> {
                        updatedShortcutType = SHORTCUT_TYPE_PHONE
                        dialogBinding.inputField.inputType = InputType.TYPE_CLASS_PHONE
                    }
                    dialogBinding.url.id -> {
                        updatedShortcutType = SHORTCUT_TYPE_URL
                        dialogBinding.inputField.inputType = InputType.TYPE_TEXT_VARIATION_URI
                    }
                }
            }
        }

        dialogBuilder.getButton(AlertDialog.BUTTON_NEUTRAL).setOnClickListener {
            sharedPreferences.edit().remove(KEY_SHORTCUT_NO_ + position).apply()
            dialogBuilder.dismiss()
            this.onResume()
        }

        /* save the shortcut values */
        dialogBuilder.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener {
            /* get shortcut value */
            val updatedIntentString =
                Objects.requireNonNull(dialogBinding.inputField.text).toString().trim { it <= ' ' }
            /* get thumbnail letter */
            val updatedThumbLetter =
                Objects.requireNonNull(dialogBinding.thumbField.text).toString().trim { it <= ' ' }.uppercase()
            /* get color value */
            val updatedColor =
                Objects.requireNonNull(dialogBinding.colorPicker.colorInput.text).toString().trim { it <= ' ' }

            /* save the values if every field is filled */
            if (updatedShortcutType.isNotEmpty() && updatedIntentString.isNotEmpty() &&
                updatedThumbLetter.isNotEmpty() && updatedColor.isNotEmpty()) {
                sharedPreferences.edit().putString(KEY_SHORTCUT_NO_ + position,
                    "$updatedShortcutType$SEPARATOR$updatedIntentString$SEPARATOR" +
                            "$updatedThumbLetter$SEPARATOR$updatedColor").apply()
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
                    removeFavApps(position)
                    this.onResume()
                    true
                }
            } catch (nameNotFoundException: PackageManager.NameNotFoundException) {
                removeFavApps(position)
                imageView.visibility = View.GONE
            }
        } else {
            imageView.visibility = View.GONE
        }
    }

    /* create text view for shortcut thumbnails */
    private val textView: MaterialTextView get() {
        val relativeLayout = RelativeLayout(lActivity!!)
        relativeLayout.apply {
            layoutParams = LinearLayoutCompat.LayoutParams(
                LinearLayoutCompat.LayoutParams.WRAP_CONTENT,
                LinearLayoutCompat.LayoutParams.WRAP_CONTENT, 1F)
            gravity = Gravity.CENTER
        }
        binding.shortcutsGroup.addView(relativeLayout)

        MaterialTextView(requireContext()).apply {
            layoutParams = LinearLayoutCompat.LayoutParams(
                (iconSize * resources.displayMetrics.density).toInt(),
                (iconSize * resources.displayMetrics.density).toInt())
            gravity = Gravity.CENTER
            textSize = 10 * resources.displayMetrics.density
            setTypeface(null, Typeface.BOLD)
            background = ContextCompat.getDrawable(requireContext(), R.drawable.rounded_bg)
        }.let {
            relativeLayout.addView(it)
            return it
        }
    }

    /* create image view for favorite app icons */
    private val imageView: AppCompatImageView get() {
        AppCompatImageView(requireContext()).apply {
            layoutParams = LinearLayoutCompat.LayoutParams(
                (iconSize * resources.displayMetrics.density).toInt(),
                (iconSize * resources.displayMetrics.density).toInt(), 1F)
        }.let {
            binding.favAppsGroup.addView(it)
            return it
        }
    }

    /* returns maximum brightness value of the device */
    private val maxBrightness: Float get() {
        val powerManager = requireContext().getSystemService(Context.POWER_SERVICE) as PowerManager
        var value = 255f
        for (f in powerManager.javaClass.declaredFields) {
            if (f.name.equals("BRIGHTNESS_ON")) {
                f.isAccessible = true
                value = try {
                    f.getInt(powerManager).toFloat()
                } catch (e: IllegalAccessException) {
                    255f
                }
            }
        }
        return value
    }

}
