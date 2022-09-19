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

package rasel.lunar.launcher.settings

import android.os.Build
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import rasel.lunar.launcher.BuildConfig
import rasel.lunar.launcher.databinding.SettingsActivityBinding
import rasel.lunar.launcher.helpers.Constants
import rasel.lunar.launcher.helpers.UniUtils
import rasel.lunar.launcher.settings.childs.TimeDate
import rasel.lunar.launcher.settings.childs.TodoSettings
import rasel.lunar.launcher.settings.childs.WeatherSettings
import java.util.*

internal class SettingsActivity : AppCompatActivity() {
    private lateinit var binding: SettingsActivityBinding
    private lateinit var settingsClickListeners: SettingsClickListeners
    private var lockMode = 0
    private var themeValue = 0
    private lateinit var feedUrl: String
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = SettingsActivityBinding.inflate(layoutInflater)
        setContentView(binding.root)
        initializer()
        loadSettings()

        binding.timeDate.setOnClickListener {
            TimeDate().show(supportFragmentManager, Constants().MODAL_BOTTOM_SHEET_TAG)
        }

        binding.weather.setOnClickListener {
            WeatherSettings().show(supportFragmentManager, Constants().MODAL_BOTTOM_SHEET_TAG)
        }

        binding.todo.setOnClickListener {
            TodoSettings().show(supportFragmentManager, Constants().MODAL_BOTTOM_SHEET_TAG)
        }

        settingsClickListeners.screenLock(binding.lockGroup, binding.selectLockNegative,
            binding.selectLockAccessibility, binding.selectLockAdmin, binding.selectLockRoot)
        settingsClickListeners.theme(binding.themeGroup, binding.followSystemTheme,
            binding.selectDarkTheme, binding.selectLightTheme)
        settingsClickListeners.openAbout(binding.about)
    }

    private fun initializer() {
        settingsClickListeners = SettingsClickListeners(this)
        val sharedPreferences = applicationContext.getSharedPreferences(Constants().SHARED_PREFS_SETTINGS, MODE_PRIVATE)
        feedUrl = sharedPreferences.getString(Constants().SHARED_PREF_FEED_URL, "").toString()
        lockMode = sharedPreferences.getInt(Constants().SHARED_PREF_LOCK, 0)
        themeValue = sharedPreferences.getInt(Constants().SHARED_PREF_THEME, 0)
    }

    private fun loadSettings() {
        binding.inputFeedUrl.setText(feedUrl)

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.P) {
            binding.selectLockAccessibility.isEnabled = false
        }
        if (!UniUtils().isRooted) {
            binding.selectLockRoot.isEnabled = false
        }

        when (lockMode) {
            0 -> binding.selectLockNegative.isChecked = true
            1 -> binding.selectLockAccessibility.isChecked = true
            2 -> binding.selectLockAdmin.isChecked = true
            3 -> binding.selectLockRoot.isChecked = true
        }
        when (themeValue) {
            0 -> binding.followSystemTheme.isChecked = true
            1 -> binding.selectDarkTheme.isChecked = true
            2 -> binding.selectLightTheme.isChecked = true
        }
        binding.version.text = BuildConfig.VERSION_NAME
    }

    private fun getFeedUrl(): String {
        return Objects.requireNonNull(binding.inputFeedUrl.text).toString().trim { it <= ' ' }
    }

    @Deprecated("Deprecated in Java")
    @Suppress("DEPRECATION")
    override fun onBackPressed() {
        super.onBackPressed()
        SettingsPrefsUtils().saveFeedUrl(applicationContext, getFeedUrl())
    }
}