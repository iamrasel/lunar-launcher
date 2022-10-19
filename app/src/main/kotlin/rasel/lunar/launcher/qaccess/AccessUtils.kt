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
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.BlendMode
import android.graphics.BlendModeColorFilter
import android.graphics.Color
import android.graphics.PorterDuff
import android.media.AudioManager
import android.net.Uri
import android.os.Build
import android.provider.Settings
import android.provider.Settings.SettingNotFoundException
import android.text.InputType
import android.view.View
import androidx.appcompat.widget.AppCompatImageView
import androidx.fragment.app.FragmentActivity
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.button.MaterialButtonToggleGroup
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.slider.Slider
import com.google.android.material.textview.MaterialTextView
import rasel.lunar.launcher.apps.FavouriteUtils
import rasel.lunar.launcher.databinding.ShortcutMakerBinding
import rasel.lunar.launcher.helpers.ColorPicker
import rasel.lunar.launcher.helpers.Constants
import java.util.*


internal class AccessUtils(
    private val context: Context,
    private val bottomSheetDialogFragment: BottomSheetDialogFragment,
    private val fragmentActivity: FragmentActivity) {
    
    private val constants = Constants()
    private val sharedPreferences = context.getSharedPreferences(constants.PREFS_SHORTCUTS, Context.MODE_PRIVATE)

    /* control the volumes */
    fun volumeControllers(notifyBar: Slider, alarmBar: Slider, mediaBar: Slider, voiceBar: Slider, ringerBar: Slider) {
        val audioManager = fragmentActivity.getSystemService(Context.AUDIO_SERVICE) as AudioManager
        notifyBar.valueTo = audioManager.getStreamMaxVolume(AudioManager.STREAM_NOTIFICATION).toFloat()
        alarmBar.valueTo = audioManager.getStreamMaxVolume(AudioManager.STREAM_ALARM).toFloat()
        mediaBar.valueTo = audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC).toFloat()
        voiceBar.valueTo = audioManager.getStreamMaxVolume(AudioManager.STREAM_VOICE_CALL).toFloat()
        ringerBar.valueTo = audioManager.getStreamMaxVolume(AudioManager.STREAM_RING).toFloat()
        notifyBar.value = audioManager.getStreamVolume(AudioManager.STREAM_NOTIFICATION).toFloat()
        alarmBar.value = audioManager.getStreamVolume(AudioManager.STREAM_ALARM).toFloat()
        mediaBar.value = audioManager.getStreamVolume(AudioManager.STREAM_MUSIC).toFloat()
        voiceBar.value = audioManager.getStreamVolume(AudioManager.STREAM_VOICE_CALL).toFloat()
        ringerBar.value = audioManager.getStreamVolume(AudioManager.STREAM_RING).toFloat()

        alarmBar.addOnChangeListener(Slider.OnChangeListener { _: Slider?, value: Float, _: Boolean ->
            audioManager.setStreamVolume(AudioManager.STREAM_ALARM, value.toInt(), 0)
        })

        mediaBar.addOnChangeListener(Slider.OnChangeListener { _: Slider?, value: Float, _: Boolean ->
            audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, value.toInt(), 0)
        })

        voiceBar.addOnChangeListener(Slider.OnChangeListener { _: Slider?, value: Float, _: Boolean ->
            audioManager.setStreamVolume(AudioManager.STREAM_VOICE_CALL, value.toInt(), 0)
        })

        if (Settings.Global.getInt(fragmentActivity.contentResolver, "zen_mode") == 0 &&
            audioManager.ringerMode != AudioManager.RINGER_MODE_SILENT) {
            notifyBar.addOnChangeListener(Slider.OnChangeListener { _: Slider?, value: Float, _: Boolean ->
                audioManager.setStreamVolume(AudioManager.STREAM_NOTIFICATION, value.toInt(), 0)
            })
            ringerBar.addOnChangeListener(Slider.OnChangeListener { _: Slider?, value: Float, _: Boolean ->
                audioManager.setStreamVolume(AudioManager.STREAM_RING, value.toInt(), 0)
            })
        } else {
            notifyBar.isEnabled = false
            ringerBar.isEnabled = false
        }
    }

    /* contact/url shortcuts */
    fun shortcutsUtil(textView: MaterialTextView, shortcutType: String, intentString: String,
                      thumbLetter: String, color: String, position: Int) {
        if (intentString.isEmpty()) {
            textView.text = "+"
            textView.setOnClickListener { shortcutsSaverDialog(position) }
        } else {
            textView.text = thumbLetter
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                textView.background.colorFilter =
                    BlendModeColorFilter(Color.parseColor("#$color"), BlendMode.MULTIPLY)
            } else {
                @Suppress("DEPRECATION")
                textView.background.setColorFilter(Color.parseColor("#$color"), PorterDuff.Mode.MULTIPLY)
            }
            textView.setOnClickListener {
                if (shortcutType == constants.SHORTCUT_TYPE_URL) {
                    var url = intentString
                    if (!url.startsWith("http://") && !url.startsWith("https://")) {
                        url = "http://$intentString"
                    }
                    fragmentActivity.startActivity(
                        Intent(Intent.ACTION_VIEW, Uri.parse(url)).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                    )
                } else if (shortcutType == constants.SHORTCUT_TYPE_PHONE) {
                    if (fragmentActivity.checkSelfPermission(Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                        fragmentActivity.requestPermissions(arrayOf(Manifest.permission.CALL_PHONE), 1)
                    } else {
                        fragmentActivity.startActivity(
                            Intent(Intent.ACTION_CALL, Uri.parse("tel:$intentString"))
                        )
                    }
                }
                bottomSheetDialogFragment.dismiss()
            }
            textView.setOnLongClickListener {
                sharedPreferences.edit().putString(constants.KEY_SHORTCUT_NO_ + position, "").apply()
                textView.text = "+"
                textView.background.colorFilter = null
                bottomSheetDialogFragment.onResume()
                true
            }
        }
    }

    /* control the brightness */
    fun controlBrightness(slider: Slider) {
        val resolver = fragmentActivity.contentResolver
        /* set max value */
        slider.valueTo = 255f

        /* set slider value to current brightness value */
        try {
            val brightness = Settings.System.getInt(resolver, Settings.System.SCREEN_BRIGHTNESS)
            slider.value = brightness.toFloat()
        } catch (settingNotFoundException: SettingNotFoundException) {
            settingNotFoundException.printStackTrace()
        }

        /* listen slider value changes */
        slider.addOnChangeListener(Slider.OnChangeListener { _: Slider?, value: Float, _: Boolean ->
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

    /* favorite apps */
    fun favApps(packageName: String, imageView: AppCompatImageView, position: Int) {
        val packageManager = context.packageManager
        /* package name is not empty for a specific position */
        if (packageName.isNotEmpty()) {
            try {
                /* show app icon */
                imageView.setImageDrawable(packageManager.getApplicationIcon(packageName))
                /* on click - open app */
                imageView.setOnClickListener {
                    context.startActivity(packageManager.getLaunchIntentForPackage(packageName))
                    bottomSheetDialogFragment.dismiss()
                }
                /* on long click - remove from favorite apps */
                imageView.setOnLongClickListener {
                    FavouriteUtils().saveFavApps(context, position, "")
                    imageView.visibility = View.GONE
                    true
                }
            } catch (nameNotFoundException: PackageManager.NameNotFoundException) {
                imageView.visibility = View.GONE
                nameNotFoundException.printStackTrace()
            }
        } else {
            imageView.visibility = View.GONE
        }
    }

    /* dialog for creating shortcuts */
    private fun shortcutsSaverDialog(position: Int) {
        val dialogBuilder = MaterialAlertDialogBuilder(fragmentActivity)
        val dialogBinding = ShortcutMakerBinding.inflate(fragmentActivity.layoutInflater)
        dialogBuilder.setView(dialogBinding.root)
        val dialog = dialogBuilder.create()
        dialog.show()

        /* set up color picker section */
        ColorPicker(dialogBinding.colorPicker.colorInput, dialogBinding.colorPicker.colorA,
            dialogBinding.colorPicker.colorR, dialogBinding.colorPicker.colorG,
            dialogBinding.colorPicker.colorB, dialogBinding.colorPicker.colorPicker).pickColor()

        /* shortcut type chooser - contact/url */
        var shortcutType = ""
        dialogBinding.shortcutType.addOnButtonCheckedListener { _: MaterialButtonToggleGroup?, checkedId: Int, isChecked: Boolean ->
            if (isChecked) {
                when (checkedId) {
                    dialogBinding.contact.id -> {
                        shortcutType = constants.SHORTCUT_TYPE_PHONE
                        dialogBinding.inputField.inputType = InputType.TYPE_CLASS_PHONE
                    }
                    dialogBinding.url.id -> {
                        shortcutType = constants.SHORTCUT_TYPE_URL
                        dialogBinding.inputField.inputType = InputType.TYPE_TEXT_VARIATION_URI
                    }
                }
            }
        }

        /* close the dialog on cancel */
        dialogBinding.cancel.setOnClickListener { dialog.dismiss() }
        /* save the shortcut value */
        dialogBinding.ok.setOnClickListener {
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
                sharedPreferences.edit().putString(constants.KEY_SHORTCUT_NO_ + position,
                    "$shortcutType||$intentString||$thumbLetter||$color").apply()
                dialog.dismiss()
                bottomSheetDialogFragment.onResume()
            }
        }
    }

}
