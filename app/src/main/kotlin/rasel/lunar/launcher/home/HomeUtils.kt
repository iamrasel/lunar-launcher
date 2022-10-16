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

package rasel.lunar.launcher.home

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.text.format.DateFormat
import android.view.View
import android.widget.Toast
import androidx.biometric.BiometricPrompt
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.FragmentManager
import rasel.lunar.launcher.R
import rasel.lunar.launcher.helpers.Constants
import rasel.lunar.launcher.helpers.SwipeTouchListener
import rasel.lunar.launcher.helpers.UniUtils
import rasel.lunar.launcher.qaccess.QuickAccess
import rasel.lunar.launcher.settings.SettingsActivity
import rasel.lunar.launcher.todos.TodoManager
import java.util.*

internal class HomeUtils(
    private val fragmentActivity: FragmentActivity,
    private val sharedPreferences: SharedPreferences) {
    private val fragmentManager: FragmentManager = fragmentActivity.supportFragmentManager
    private val context: Context = fragmentActivity.applicationContext

    fun getTimeFormat(): String? {
        when (sharedPreferences.getInt(Constants().KEY_TIME_FORMAT, 0)) {
            0 -> return if (DateFormat.is24HourFormat(context)) {
                "kk:mm"
            } else {
                "h:mm a"
            }
            1 -> return "h:mm a"
            2 -> return "kk:mm"
        }
        return null
    }

    private fun getDateNumberSuffix(): String {
        val calendar = Calendar.getInstance()
        return when (calendar[Calendar.DAY_OF_MONTH]) {
            1, 21, 31 -> "ˢᵗ"
            2, 22 -> "ⁿᵈ"
            3, 23 -> "ʳᵈ"
            else -> "ᵗʰ"
        }
    }

    fun getDateFormat(): String {
        val dateFormatValue = sharedPreferences.getString(
            Constants().KEY_DATE_FORMAT,
            Constants().DEFAULT_DATE_FORMAT
        )
        return if (dateFormatValue!!.contains("x")) {
            dateFormatValue.replace("x", getDateNumberSuffix())
        } else {
            dateFormatValue
        }
    }

    fun rootViewGestures(view: View, lockMethodValue: Int) {
        view.setOnTouchListener(object : SwipeTouchListener(context) {
            override fun onSwipeUp() {
                super.onSwipeUp()
                QuickAccess().show(fragmentManager, Constants().BOTTOM_SHEET_TAG)
            }
            override fun onSwipeDown() {
                super.onSwipeDown()
                UniUtils().expandNotificationPanel(context)
            }
            override fun onDoubleClick() {
                super.onDoubleClick()
                UniUtils().lockMethod(lockMethodValue, context, fragmentActivity)
            }
        })
    }

    fun batteryProgressGestures(view: View, lockMethodValue: Int) {
        view.setOnTouchListener(object : SwipeTouchListener(context) {
            override fun onLongClick() {
                super.onLongClick()
                fragmentActivity.startActivity(Intent(context, SettingsActivity::class.java))
            }
            override fun onSwipeDown() {
                super.onSwipeDown()
                UniUtils().expandNotificationPanel(context)
            }
            override fun onDoubleClick() {
                super.onDoubleClick()
                UniUtils().lockMethod(lockMethodValue, context, fragmentActivity)
            }
        })
    }

    fun todosGestures(view: View, lockMethodValue: Int) {
        view.setOnTouchListener(object : SwipeTouchListener(context) {
            override fun onLongClick() {
                super.onLongClick()
                when (sharedPreferences.getBoolean(Constants().KEY_TODO_LOCK, false)) {
                    false -> fragmentManager.beginTransaction().add(R.id.main_fragments_container, TodoManager())
                        .addToBackStack("").commit()
                    true -> {
                        if (UniUtils().canAuthenticate(context)) {
                            val biometricPrompt = BiometricPrompt(fragmentActivity, authenticationCallback)
                            try {
                                biometricPrompt.authenticate(UniUtils().biometricPromptInfo(fragmentActivity.getString(R.string.todo_manager), fragmentActivity))
                            } catch (exception: Exception) {
                                exception.printStackTrace()
                            }
                        }
                    }
                }
            }
            override fun onSwipeUp() {
                super.onSwipeUp()
                QuickAccess().show(fragmentManager, Constants().BOTTOM_SHEET_TAG)
            }
            override fun onSwipeDown() {
                super.onSwipeDown()
                UniUtils().expandNotificationPanel(context)
            }
            override fun onDoubleClick() {
                super.onDoubleClick()
                UniUtils().lockMethod(lockMethodValue, context, fragmentActivity)
            }
        })
    }

    private val authenticationCallback = object : BiometricPrompt.AuthenticationCallback() {
        override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult) {
            fragmentManager.beginTransaction().add(R.id.main_fragments_container, TodoManager())
                .addToBackStack("").commit()
        }
        override fun onAuthenticationError(errorCode: Int, errString: CharSequence) {
            Toast.makeText(context, fragmentActivity.getString(R.string.authentication_error), Toast.LENGTH_SHORT).show()
        }
        override fun onAuthenticationFailed() {
            Toast.makeText(context, fragmentActivity.getString(R.string.authentication_failed), Toast.LENGTH_SHORT).show()
        }
    }
}