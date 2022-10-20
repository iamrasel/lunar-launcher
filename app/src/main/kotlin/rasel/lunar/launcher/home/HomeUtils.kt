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

import android.content.Intent
import android.content.SharedPreferences
import android.text.format.DateFormat
import android.view.View
import android.widget.Toast
import androidx.biometric.BiometricPrompt
import androidx.fragment.app.FragmentActivity
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

    private val fragmentManager = fragmentActivity.supportFragmentManager
    private val context = fragmentActivity.applicationContext
    private val constants = Constants()
    private val uniUtils = UniUtils()
    private val quickAccess = QuickAccess()

    /* get time format string */
    val timeFormat: String? get() {
        when (sharedPreferences.getInt(constants.KEY_TIME_FORMAT, 0)) {
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

    /* get date number suffix */
    private val dateNumberSuffix: String get() {
        val calendar = Calendar.getInstance()
        return when (calendar[Calendar.DAY_OF_MONTH]) {
            1, 21, 31 -> "ˢᵗ"
            2, 22 -> "ⁿᵈ"
            3, 23 -> "ʳᵈ"
            else -> "ᵗʰ"
        }
    }

    /* get date format string */
    val dateFormat: String get() {
        val dateFormatValue = sharedPreferences.getString(
            constants.KEY_DATE_FORMAT,
            constants.DEFAULT_DATE_FORMAT
        )
        return if (dateFormatValue!!.contains("x")) {
            dateFormatValue.replace("x", dateNumberSuffix)
        } else {
            dateFormatValue
        }
    }

    /* gestures on root view */
    fun rootViewGestures(view: View, lockMethodValue: Int) {
        view.setOnTouchListener(object : SwipeTouchListener(context) {
            /* open quick access panel on swipe up */
            override fun onSwipeUp() {
                super.onSwipeUp()
                quickAccess.show(fragmentManager, constants.BOTTOM_SHEET_TAG)
            }
            /* expand notification panel on swipe down */
            override fun onSwipeDown() {
                super.onSwipeDown()
                uniUtils.expandNotificationPanel(context)
            }
            /* lock the screen on double tap (optional) */
            override fun onDoubleClick() {
                super.onDoubleClick()
                uniUtils.lockMethod(lockMethodValue, context, fragmentActivity)
            }
        })
    }

    /* gestures on battery progress indicator area */
    fun batteryProgressGestures(view: View, lockMethodValue: Int) {
        view.setOnTouchListener(object : SwipeTouchListener(context) {
            /* open settings activity on long click */
            override fun onLongClick() {
                super.onLongClick()
                fragmentActivity.startActivity(Intent(context, SettingsActivity::class.java))
            }
            /* expand notification panel on swipe down */
            override fun onSwipeDown() {
                super.onSwipeDown()
                uniUtils.expandNotificationPanel(context)
            }
            /* lock the screen on double tap (optional) */
            override fun onDoubleClick() {
                super.onDoubleClick()
                uniUtils.lockMethod(lockMethodValue, context, fragmentActivity)
            }
        })
    }

    /* gestures on todo area */
    fun todosGestures(view: View, lockMethodValue: Int) {
        view.setOnTouchListener(object : SwipeTouchListener(context) {
            /* open TodoManager on long click */
            override fun onLongClick() {
                super.onLongClick()
                when (sharedPreferences.getBoolean(constants.KEY_TODO_LOCK, false)) {
                    false -> fragmentManager.beginTransaction().add(R.id.main_fragments_container, TodoManager())
                        .addToBackStack("").commit()
                    /* show authentication screen if lock is on */
                    true -> {
                        if (uniUtils.canAuthenticate(context)) {
                            val biometricPrompt = BiometricPrompt(fragmentActivity, authenticationCallback)
                            try {
                                biometricPrompt.authenticate(uniUtils.biometricPromptInfo(fragmentActivity.getString(R.string.todo_manager), fragmentActivity))
                            } catch (exception: Exception) {
                                exception.printStackTrace()
                            }
                        }
                    }
                }
            }
            /* open quick access panel on swipe up */
            override fun onSwipeUp() {
                super.onSwipeUp()
                quickAccess.show(fragmentManager, constants.BOTTOM_SHEET_TAG)
            }
            /* expand notification panel on swipe down */
            override fun onSwipeDown() {
                super.onSwipeDown()
                uniUtils.expandNotificationPanel(context)
            }
            /* lock the screen on double tap (optional) */
            override fun onDoubleClick() {
                super.onDoubleClick()
                uniUtils.lockMethod(lockMethodValue, context, fragmentActivity)
            }
        })
    }

    /* authentication callback for TodoManager lock */
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
