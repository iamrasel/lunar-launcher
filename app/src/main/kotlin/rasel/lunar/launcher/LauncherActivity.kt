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
import androidx.activity.OnBackPressedCallback
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
    private val constants = Constants()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        /* vertically edge to edge view */
        WindowCompat.setDecorFitsSystemWindows(window, false)

        /*  if this is the first launch,
            then remember the event and show the welcome dialog */
        val prefsFirstLaunch = getSharedPreferences(constants.PREFS_FIRST_LAUNCH, 0)
        if (prefsFirstLaunch.getBoolean(constants.KEY_FIRST_LAUNCH, true)) {
            prefsFirstLaunch.edit().putBoolean(constants.KEY_FIRST_LAUNCH, false).apply()
            welcomeDialog()
        }

        /* set up activity's view */
        binding = LauncherActivityBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setupView()

        /* handle navigation back events */
        handleBackPress()
    }

    /* build the welcome dialog */
    private fun welcomeDialog() {
        MaterialAlertDialogBuilder(this)
            .setTitle(R.string.welcome)
            .setMessage(R.string.welcome_description)
            .setPositiveButton(R.string.got_it) { dialog, _ ->
                dialog.dismiss()
                /* ask for the permissions */
                UniUtils().askPermissions(this)
            }.show()
    }

    /* set up viewpager2 */
    private fun setupView() {
        viewPager = binding.viewPager
        val adapter = ViewPagerAdapter(supportFragmentManager, lifecycle)
        viewPager.adapter = adapter
        viewPager.setCurrentItem(1, false)
    }

    /* alternative of deprecated onBackPressed method */
    private fun handleBackPress() {
        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                /* while in todo manager, go back to home screen */
                if (supportFragmentManager.backStackEntryCount != 0)
                    supportFragmentManager.popBackStack()

                /* while in feeds or app drawer, go back to home screen */
                if (viewPager.currentItem != 1)
                    viewPager.currentItem = 1
            }
        })
    }

}
