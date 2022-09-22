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
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.media.AudioManager
import android.net.Uri
import android.provider.Settings
import android.provider.Settings.SettingNotFoundException
import android.text.InputType
import android.view.View
import android.widget.NumberPicker
import androidx.appcompat.widget.AppCompatImageView
import androidx.fragment.app.FragmentActivity
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.slider.Slider
import com.google.android.material.textview.MaterialTextView
import rasel.lunar.launcher.R
import rasel.lunar.launcher.apps.FavouriteUtils
import rasel.lunar.launcher.databinding.SaverDialogBinding
import rasel.lunar.launcher.helpers.Constants
import java.util.*
import java.util.concurrent.atomic.AtomicBoolean

internal class AccessUtils(
    private val context: Context,
    private val bottomSheetDialogFragment: BottomSheetDialogFragment,
    private val fragmentActivity: FragmentActivity) {
    private val sharedPreferences: SharedPreferences =
        context.getSharedPreferences(Constants().SHARED_PREFS_PHONES_URLS, Context.MODE_PRIVATE)

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

        try {
            if (Settings.Global.getInt(fragmentActivity.contentResolver, "zen_mode") == 0) {
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
        } catch (exception: Exception) {
            exception.printStackTrace()
        }
    }

    fun phonesAndUrls(root: String, intentString: String, thumbLetter: String, thumbHolder: MaterialTextView, position: Int) {
        if (intentString.isEmpty()) {
            thumbHolder.text = "+"
            thumbHolder.setOnClickListener { saverDialog(position, root) }
        } else {
            thumbHolder.text = thumbLetter
            thumbHolder.setOnClickListener {
                if (root == Constants().PHONE_NO) {
                    if (fragmentActivity.checkSelfPermission(Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                        fragmentActivity.requestPermissions(arrayOf(Manifest.permission.CALL_PHONE), 1)
                    } else {
                        fragmentActivity.startActivity(
                            Intent(Intent.ACTION_CALL, Uri.parse("tel:$intentString"))
                        )
                    }
                } else if (root == Constants().URL_ADDRESS) {
                    var url = intentString
                    if (!url.startsWith("http://") && !url.startsWith("https://")) {
                        url = "http://$intentString"
                    }
                    fragmentActivity.startActivity(
                        Intent(Intent.ACTION_VIEW, Uri.parse(url)).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                    )
                }
                bottomSheetDialogFragment.dismiss()
            }
            thumbHolder.setOnLongClickListener {
                if (root == Constants().PHONE_NO) {
                    sharedPreferences.edit().putString(Constants().PHONE_NO_ + position, "").apply()
                    sharedPreferences.edit().putString(Constants().PHONE_THUMB_LETTER_ + position, "")
                        .apply()
                } else if (root == Constants().URL_ADDRESS) {
                    sharedPreferences.edit().putString(Constants().URL_NO_ + position, "").apply()
                    sharedPreferences.edit().putString(Constants().URL_THUMB_LETTER_ + position, "")
                        .apply()
                }
                thumbHolder.text = "+"
                bottomSheetDialogFragment.onResume()
                true
            }
        }
    }

    fun controlBrightness(seekBar: Slider) {
        val resolver = fragmentActivity.contentResolver
        seekBar.valueTo = 255f
        try {
            val brightness = Settings.System.getInt(resolver, Settings.System.SCREEN_BRIGHTNESS)
            seekBar.value = brightness.toFloat()
        } catch (settingNotFoundException: SettingNotFoundException) {
            settingNotFoundException.printStackTrace()
        }
        seekBar.addOnChangeListener(Slider.OnChangeListener { _: Slider?, value: Float, _: Boolean ->
            if (!Settings.System.canWrite(fragmentActivity)) {
                fragmentActivity.startActivity(
                    Intent(Settings.ACTION_MANAGE_WRITE_SETTINGS)
                        .setData(Uri.parse("package:" + fragmentActivity.packageName))
                        .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                )
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

    fun favApps(packageName: String, imageView: AppCompatImageView, position: Int) {
        val packageManager = context.packageManager
        if (packageName.isNotEmpty()) {
            try {
                val appIcon = packageManager.getApplicationIcon(packageName)
                imageView.setImageDrawable(appIcon)
                imageView.setOnClickListener {
                    context.startActivity(packageManager.getLaunchIntentForPackage(packageName))
                    bottomSheetDialogFragment.dismiss()
                }
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

    private fun saverDialog(position: Int, hintText: String) {
        val dialogBuilder = MaterialAlertDialogBuilder(fragmentActivity)
        val dialogBinding = SaverDialogBinding.inflate(fragmentActivity.layoutInflater)
        dialogBuilder.setView(dialogBinding.root)
        val dialog = dialogBuilder.create()
        dialog.show()

        dialogBinding.inputLayout.hint = hintText
        if (hintText == Constants().PHONE_NO) {
            dialogBinding.urlPhone.inputType = InputType.TYPE_CLASS_PHONE
        } else if (hintText == Constants().URL_ADDRESS) {
            dialogBinding.urlPhone.inputType = InputType.TYPE_TEXT_VARIATION_URI
        }

        val alphabets = arrayOf(
            "A", "B", "C", "D", "E", "F", "G", "H", "I", "J", "K", "L", "M",
            "N", "O", "P", "Q", "R", "S", "T", "U", "V", "W", "X", "Y", "Z"
        )

        val isAlphabetPicked = AtomicBoolean(false)
        dialogBinding.alphabetPicker.minValue = 0
        dialogBinding.alphabetPicker.maxValue = alphabets.size - 1
        dialogBinding.alphabetPicker.displayedValues = alphabets
        dialogBinding.alphabetPicker.setOnValueChangedListener { _: NumberPicker?, _: Int, newVal: Int ->
            isAlphabetPicked.set(true)
            if (hintText == Constants().PHONE_NO) {
                sharedPreferences.edit()
                    .putString(Constants().PHONE_THUMB_LETTER_ + position, alphabets[newVal]).apply()
            } else if (hintText == Constants().URL_ADDRESS) {
                sharedPreferences.edit()
                    .putString(Constants().URL_THUMB_LETTER_ + position, alphabets[newVal]).apply()
            }
        }

        dialogBinding.cancel.setOnClickListener { dialog.dismiss() }
        dialogBinding.ok.setOnClickListener {
            val urlPhone =
                Objects.requireNonNull(dialogBinding.urlPhone.text).toString().trim { it <= ' ' }
            if (urlPhone.isNotEmpty() && isAlphabetPicked.get()) {
                if (hintText == Constants().PHONE_NO) {
                    sharedPreferences.edit().putString(Constants().PHONE_NO_ + position, urlPhone)
                        .apply()
                } else if (hintText == Constants().URL_ADDRESS) {
                    sharedPreferences.edit().putString(Constants().URL_NO_ + position, urlPhone)
                        .apply()
                }
                dialog.dismiss()
                bottomSheetDialogFragment.onResume()
            } else {
                dialogBinding.urlPhone.error =
                    context.getString(R.string.empty_text_field) + " or alphabet field is unchanged"
            }
        }
    }

}