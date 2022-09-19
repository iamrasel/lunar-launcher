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

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.bottomsheet.BottomSheetDialog
import rasel.lunar.launcher.BuildConfig
import rasel.lunar.launcher.databinding.AboutBinding
import rasel.lunar.launcher.databinding.SettingsActivityBinding
import rasel.lunar.launcher.helpers.Constants
import rasel.lunar.launcher.settings.childs.*
import rasel.lunar.launcher.settings.childs.Look
import rasel.lunar.launcher.settings.childs.TimeDate
import rasel.lunar.launcher.settings.childs.TodoSettings
import rasel.lunar.launcher.settings.childs.WeatherSettings

internal class SettingsActivity : AppCompatActivity() {
    private lateinit var binding: SettingsActivityBinding
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = SettingsActivityBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.version.text = BuildConfig.VERSION_NAME

        binding.timeDate.setOnClickListener {
            TimeDate().show(supportFragmentManager, Constants().MODAL_BOTTOM_SHEET_TAG)
        }

        binding.weather.setOnClickListener {
            WeatherSettings().show(supportFragmentManager, Constants().MODAL_BOTTOM_SHEET_TAG)
        }

        binding.todo.setOnClickListener {
            TodoSettings().show(supportFragmentManager, Constants().MODAL_BOTTOM_SHEET_TAG)
        }

        binding.look.setOnClickListener {
            Look().show(supportFragmentManager, Constants().MODAL_BOTTOM_SHEET_TAG)
        }

        binding.more.setOnClickListener {
            More().show(supportFragmentManager, Constants().MODAL_BOTTOM_SHEET_TAG)
        }

        binding.about.setOnClickListener {
            val bottomSheetDialog = BottomSheetDialog(this)
            val aboutBinding = AboutBinding.inflate(this.layoutInflater)
            bottomSheetDialog.setContentView(aboutBinding.root)
            bottomSheetDialog.show()
        }
    }
}