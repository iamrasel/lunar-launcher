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
import android.content.IntentFilter
import android.content.SharedPreferences
import android.os.Bundle
import android.text.format.DateFormat
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.FragmentManager
import dev.chrisbanes.insetter.applyInsetter
import rasel.lunar.launcher.LauncherActivity
import rasel.lunar.launcher.databinding.LauncherHomeBinding
import rasel.lunar.launcher.helpers.Constants
import rasel.lunar.launcher.home.weather.WeatherExecutor
import rasel.lunar.launcher.todos.DatabaseHandler
import rasel.lunar.launcher.todos.TodoAdapter
import rasel.lunar.launcher.todos.TodoManager

internal class LauncherHome : Fragment() {

    private lateinit var binding: LauncherHomeBinding
    private lateinit var fragmentActivity: FragmentActivity
    private lateinit var _context: Context
    private lateinit var fragManager: FragmentManager
    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var homeUtils: HomeUtils
    private lateinit var batteryReceiver: BatteryReceiver

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = LauncherHomeBinding.inflate(inflater, container, false)
        binding.root.applyInsetter {
            type(systemGestures = true) {
                margin()
            }
        }

        fragmentActivity = if (isAdded) {
            requireActivity()
        } else {
            LauncherActivity()
        }

        _context = fragmentActivity.applicationContext
        fragManager = fragmentActivity.supportFragmentManager
        sharedPreferences = _context.getSharedPreferences(Constants().SHARED_PREFS_SETTINGS, 0)
        homeUtils = HomeUtils(fragmentActivity, sharedPreferences)
        batteryReceiver = BatteryReceiver(binding.batteryProgress)

        fragManager.addOnBackStackChangedListener { this.showTodoList() }
        _context.registerReceiver(batteryReceiver, IntentFilter(Intent.ACTION_BATTERY_CHANGED))

        return binding.root
    }

    private fun showTodoList() {
        binding.todos.adapter =
            context?.let { TodoAdapter(DatabaseHandler(context).todos, TodoManager(), fragmentActivity, it) }
    }

    override fun onResume() {
        _context.registerReceiver(batteryReceiver, IntentFilter(Intent.ACTION_BATTERY_CHANGED)) // Battery

        // Time and date
        if (DateFormat.is24HourFormat(context)) {
            binding.time.format24Hour = homeUtils.getTimeFormat()
            binding.date.format24Hour = homeUtils.getDateFormat()
        } else {
            binding.time.format12Hour = homeUtils.getTimeFormat()
            binding.date.format12Hour = homeUtils.getDateFormat()
        }

        WeatherExecutor(sharedPreferences).generateTempString(binding.temp, fragmentActivity) // Weather
        showTodoList()

        // handle gesture events
        val lockMethod = sharedPreferences.getInt(Constants().SHARED_PREF_LOCK, 0)
        homeUtils.rootViewGestures(binding.root, lockMethod)
        homeUtils.batteryProgressGestures(binding.batteryProgress, lockMethod)
        homeUtils.todosGestures(binding.todos, lockMethod)
        super.onResume()
    }

    override fun onDestroy() {
        _context.unregisterReceiver(batteryReceiver)
        super.onDestroy()
    }
}