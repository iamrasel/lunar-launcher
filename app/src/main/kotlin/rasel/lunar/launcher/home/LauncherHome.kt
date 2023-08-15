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

import android.annotation.SuppressLint
import android.content.Intent
import android.content.IntentFilter
import android.content.SharedPreferences
import android.os.Bundle
import android.text.format.DateFormat
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.biometric.BiometricPrompt
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import rasel.lunar.launcher.LauncherActivity.Companion.lActivity
import rasel.lunar.launcher.R
import rasel.lunar.launcher.databinding.LauncherHomeBinding
import rasel.lunar.launcher.helpers.Constants.Companion.BOTTOM_SHEET_TAG
import rasel.lunar.launcher.helpers.Constants.Companion.DEFAULT_DATE_FORMAT
import rasel.lunar.launcher.helpers.Constants.Companion.KEY_DATE_FORMAT
import rasel.lunar.launcher.helpers.Constants.Companion.KEY_LOCK_METHOD
import rasel.lunar.launcher.helpers.Constants.Companion.KEY_TIME_FORMAT
import rasel.lunar.launcher.helpers.Constants.Companion.KEY_TODO_LOCK
import rasel.lunar.launcher.helpers.Constants.Companion.PREFS_SETTINGS
import rasel.lunar.launcher.helpers.SwipeTouchListener
import rasel.lunar.launcher.helpers.UniUtils.Companion.biometricPromptInfo
import rasel.lunar.launcher.helpers.UniUtils.Companion.canAuthenticate
import rasel.lunar.launcher.helpers.UniUtils.Companion.expandNotificationPanel
import rasel.lunar.launcher.helpers.UniUtils.Companion.lockMethod
import rasel.lunar.launcher.home.weather.WeatherExecutor
import rasel.lunar.launcher.qaccess.QuickAccess
import rasel.lunar.launcher.settings.SettingsActivity
import rasel.lunar.launcher.todos.TodoAdapter
import rasel.lunar.launcher.todos.TodoManager
import java.util.*


internal class LauncherHome : Fragment() {

    private lateinit var binding: LauncherHomeBinding
    private lateinit var fragManager: FragmentManager
    private lateinit var settingsPrefs: SharedPreferences
    private lateinit var batteryReceiver: BatteryReceiver
    private var shouldResume = true

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {

        binding = LauncherHomeBinding.inflate(inflater, container, false)
        fragManager = lActivity!!.supportFragmentManager
        settingsPrefs = requireContext().getSharedPreferences(PREFS_SETTINGS, 0)
        batteryReceiver = BatteryReceiver(binding.batteryProgress)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        /* handle gesture events */
        rootViewGestures()
        batteryProgressGestures()
        todosGestures()

        /* refresh the to-do list after getting back from TodoManager */
        fragManager.addOnBackStackChangedListener {
            shouldResume = if (fragManager.backStackEntryCount == 0) {
                binding.root.visibility = View.VISIBLE
                showTodoList()
                true
            } else {
                binding.root.visibility = View.GONE
                false
            }
        }
    }

    override fun onResume() {
        super.onResume()
        if (shouldResume) {
            /* register battery changes */
            requireContext().registerReceiver(batteryReceiver, IntentFilter(Intent.ACTION_BATTERY_CHANGED))

            /* time and date */
            if (DateFormat.is24HourFormat(requireContext())) {
                binding.time.format24Hour = timeFormat
                binding.date.format24Hour = dateFormat
            } else {
                binding.time.format12Hour = timeFormat
                binding.date.format12Hour = dateFormat
            }

            /* show weather */
            WeatherExecutor(settingsPrefs).generateWeatherString(binding.weather)
            /* show to-do list */
            showTodoList()
        }
    }

    override fun onPause() {
        super.onPause()
        /* unregister battery changes */
        if (shouldResume) requireContext().unregisterReceiver(batteryReceiver)
    }

    /* gestures on root view */
    @SuppressLint("ClickableViewAccessibility")
    private fun rootViewGestures() {
        binding.root.setOnTouchListener(object : SwipeTouchListener(requireContext()) {
            /* open quick access panel on swipe up */
            override fun onSwipeUp() {
                super.onSwipeUp()
                QuickAccess().show(fragManager, BOTTOM_SHEET_TAG)
            }
            /* expand notification panel on swipe down */
            override fun onSwipeDown() {
                super.onSwipeDown()
                expandNotificationPanel(requireContext())
            }
            /* lock the screen on double tap (optional) */
            override fun onDoubleClick() {
                super.onDoubleClick()
                lockMethod(settingsPrefs.getInt(KEY_LOCK_METHOD, 0), requireContext())
            }
        })
    }

    /* gestures on battery progress indicator area */
    @SuppressLint("ClickableViewAccessibility")
    private fun batteryProgressGestures() {
        binding.batteryProgress.setOnTouchListener(object : SwipeTouchListener(requireContext()) {
            /* open settings activity on long click */
            override fun onLongClick() {
                super.onLongClick()
                lActivity!!.startActivity(Intent(requireContext(), SettingsActivity::class.java))
            }
            /* expand notification panel on swipe down */
            override fun onSwipeDown() {
                super.onSwipeDown()
                expandNotificationPanel(requireContext())
            }
            /* lock the screen on double tap (optional) */
            override fun onDoubleClick() {
                super.onDoubleClick()
                lockMethod(settingsPrefs.getInt(KEY_LOCK_METHOD, 0), requireContext())
            }
        })
    }

    /* gestures on to-do area */
    @SuppressLint("ClickableViewAccessibility")
    private fun todosGestures() {
        binding.notes.setOnTouchListener(object : SwipeTouchListener(requireContext()) {
            /* open TodoManager on long click */
            override fun onLongClick() {
                super.onLongClick()
                when (settingsPrefs.getBoolean(KEY_TODO_LOCK, false)) {
                    false -> launchTodoManager()
                    /* show authentication screen if lock is on */
                    true -> {
                        if (canAuthenticate(requireContext())) {
                            val biometricPrompt = BiometricPrompt(lActivity!!, authenticationCallback)
                            try {
                                biometricPrompt.authenticate(biometricPromptInfo(lActivity!!.getString(R.string.todo_manager)))
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
                QuickAccess().show(fragManager, BOTTOM_SHEET_TAG)
            }
            /* expand notification panel on swipe down */
            override fun onSwipeDown() {
                super.onSwipeDown()
                expandNotificationPanel(requireContext())
            }
            /* lock the screen on double tap (optional) */
            override fun onDoubleClick() {
                super.onDoubleClick()
                lockMethod(settingsPrefs.getInt(KEY_LOCK_METHOD, 0), requireContext())
            }
        })
    }

    /* authentication callback for TodoManager lock */
    private val authenticationCallback = object : BiometricPrompt.AuthenticationCallback() {
        override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult) {
            launchTodoManager()
        }
        override fun onAuthenticationError(errorCode: Int, errString: CharSequence) {
            Toast.makeText(requireContext(), lActivity!!.getString(R.string.authentication_error), Toast.LENGTH_SHORT).show()
        }
        override fun onAuthenticationFailed() {
            Toast.makeText(requireContext(), lActivity!!.getString(R.string.authentication_failed), Toast.LENGTH_SHORT).show()
        }
    }

    /* launch TodoManager fragment */
    private fun launchTodoManager() {
        fragManager.beginTransaction().replace(R.id.mainFragmentsContainer, TodoManager())
            .addToBackStack("").commit()
    }

    /* to-do list */
    private fun showTodoList() {
        binding.notes.adapter = TodoAdapter(null, requireContext())
    }

    /* get time format string */
    private val timeFormat: String? get() {
        when (settingsPrefs.getInt(KEY_TIME_FORMAT, 0)) {
            0 -> return if (DateFormat.is24HourFormat(requireContext())) {
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
        return when (Calendar.getInstance()[Calendar.DAY_OF_MONTH]) {
            1, 21, 31 -> "ˢᵗ"
            2, 22 -> "ⁿᵈ"
            3, 23 -> "ʳᵈ"
            else -> "ᵗʰ"
        }
    }

    /* get date format string */
    private val dateFormat: String get() {
        settingsPrefs.getString(KEY_DATE_FORMAT, DEFAULT_DATE_FORMAT).let {
            return if (it!!.contains("x")) {
                it.replace("x", dateNumberSuffix)
            } else {
                it
            }
        }
    }

}
