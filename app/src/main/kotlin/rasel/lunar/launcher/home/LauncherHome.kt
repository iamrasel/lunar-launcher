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
import android.content.IntentFilter
import android.content.SharedPreferences
import android.os.Bundle
import android.text.format.DateFormat
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
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
    private val constants = Constants()
    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var homeUtils: HomeUtils
    private lateinit var batteryReceiver: BatteryReceiver

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = LauncherHomeBinding.inflate(inflater, container, false)

        /* set insets of the root view */
        setInsets()

        fragmentActivity = if (isAdded) {
            requireActivity()
        } else {
            LauncherActivity()
        }

        sharedPreferences = requireContext().getSharedPreferences(constants.PREFS_SETTINGS, 0)
        homeUtils = HomeUtils(fragmentActivity, sharedPreferences)
        batteryReceiver = BatteryReceiver(binding.batteryProgress)

        /* refresh the todo list after getting back from TodoManager */
        fragmentActivity.supportFragmentManager.addOnBackStackChangedListener { this.showTodoList() }

        return binding.root
    }

    /* insets */
    private fun setInsets() {
        binding.root.applyInsetter {
            type(statusBars = true, navigationBars = true) {
                padding()
            }
        }
    }

    /* todo list */
    private fun showTodoList() {
        binding.todos.adapter =
            context?.let { TodoAdapter(DatabaseHandler(context).todos, TodoManager(), fragmentActivity, it) }
    }

    override fun onResume() {
        super.onResume()
        /* register battery changes */
        requireContext().registerReceiver(batteryReceiver, IntentFilter(Intent.ACTION_BATTERY_CHANGED))

        /* time and date */
        if (DateFormat.is24HourFormat(context)) {
            binding.time.format24Hour = homeUtils.timeFormat
            binding.date.format24Hour = homeUtils.dateFormat
        } else {
            binding.time.format12Hour = homeUtils.timeFormat
            binding.date.format12Hour = homeUtils.dateFormat
        }

        /* show weather */
        WeatherExecutor(sharedPreferences).generateWeatherString(binding.temp, fragmentActivity)
        /* show todo list */
        showTodoList()

        /* handle gesture events */
        val lockMethod = sharedPreferences.getInt(constants.KEY_LOCK_METHOD, 0)
        homeUtils.rootViewGestures(binding.root, lockMethod)
        homeUtils.batteryProgressGestures(binding.batteryProgress, lockMethod)
        homeUtils.todosGestures(binding.todos, lockMethod)
    }

    override fun onPause() {
        super.onPause()
        /* unregister battery changes */
        requireContext().unregisterReceiver(batteryReceiver)
    }

}
