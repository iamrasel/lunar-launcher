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

package rasel.lunar.launcher

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.WindowCompat
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import rasel.lunar.launcher.databinding.LauncherActivityBinding
import rasel.lunar.launcher.helpers.Constants
import rasel.lunar.launcher.helpers.UniUtils
import rasel.lunar.launcher.helpers.ViewPagerAdapter

internal class LauncherActivity : AppCompatActivity() {

    private lateinit var binding: LauncherActivityBinding
    private lateinit var viewPager: ViewPager2

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = LauncherActivityBinding.inflate(layoutInflater)
        setContentView(binding.root)
        WindowCompat.setDecorFitsSystemWindows(window, false)

        if (isFirstLaunch()) {
            welcomeDialog()
        }

        setupView()
    }

    private fun isFirstLaunch(): Boolean {
        val firstLaunchPrefs =
            getSharedPreferences(Constants().SHARED_PREFS_FIRST_LAUNCH, 0)
        return if (firstLaunchPrefs.getBoolean(Constants().FIRST_LAUNCH, true)) {
            firstLaunchPrefs.edit().putBoolean(Constants().FIRST_LAUNCH, false).apply()
            true
        } else {
            false
        }
    }

    private fun welcomeDialog() {
        MaterialAlertDialogBuilder(this)
            .setTitle(R.string.welcome)
            .setMessage(R.string.welcome_description)
            .setPositiveButton(R.string.got_it) { dialog, _ ->
                dialog.dismiss()
                UniUtils().askPermissions(this)
            }.show()
    }

    private fun setupView() {
        viewPager = binding.viewPager
        val adapter = ViewPagerAdapter(supportFragmentManager, lifecycle)
        viewPager.adapter = adapter
        viewPager.setCurrentItem(1, false)
    }

    override fun onBackPressed() {
        if (supportFragmentManager.backStackEntryCount != 0) {
            supportFragmentManager.popBackStack()
        }
        if (viewPager.currentItem != 1) {
            viewPager.currentItem = 1
        }
    }
}