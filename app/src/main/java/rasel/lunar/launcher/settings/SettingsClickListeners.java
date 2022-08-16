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

package rasel.lunar.launcher.settings;

import android.content.Context;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import com.google.android.material.button.MaterialButton;

import rasel.lunar.launcher.helpers.Constants;

public class SettingsClickListeners {

    private final Constants constants = new Constants();
    private final AppCompatActivity appCompatActivity;
    private final SettingsPrefsUtils settingsPrefsUtils = new SettingsPrefsUtils();
    private final Context context;

    protected SettingsClickListeners(AppCompatActivity appCompatActivity) {
        this.appCompatActivity = appCompatActivity;
        this.context = appCompatActivity.getApplicationContext();
    }

    protected void timeFormat(MaterialButton button0, MaterialButton button1, MaterialButton button2) {
        button0.setOnClickListener(v -> {
            button1.setChecked(false);
            button2.setChecked(false);
            settingsPrefsUtils.saveTimeFormat(context, 0);
        });
        button1.setOnClickListener(v -> {
            button0.setChecked(false);
            button2.setChecked(false);
            settingsPrefsUtils.saveTimeFormat(context, 1);
        });
        button2.setOnClickListener(v -> {
            button1.setChecked(false);
            button0.setChecked(false);
            settingsPrefsUtils.saveTimeFormat(context, 2);
        });
    }

    protected void showYear(MaterialButton button0, MaterialButton button1) {
        button0.setOnClickListener(v -> {
            button1.setChecked(false);
            settingsPrefsUtils.showYear(context, 0);
        });
        button1.setOnClickListener(v -> {
            button0.setChecked(false);
            settingsPrefsUtils.showYear(context, 1);
        });
    }

    protected void tempUnit(MaterialButton button0, MaterialButton button1) {
        button0.setOnClickListener(v -> {
            button1.setChecked(false);
            settingsPrefsUtils.saveTempUnit(context, 0);
        });
        button1.setOnClickListener(v -> {
            button0.setChecked(false);
            settingsPrefsUtils.saveTempUnit(context, 1);
        });
    }

    protected void showCity(MaterialButton button0, MaterialButton button1) {
        button0.setOnClickListener(v -> {
            button1.setChecked(false);
            settingsPrefsUtils.showCity(context, 0);
        });
        button1.setOnClickListener(v -> {
            button0.setChecked(false);
            settingsPrefsUtils.showCity(context, 1);
        });
    }

    protected void names99(MaterialButton button0, MaterialButton button1, MaterialButton button2, MaterialButton button3) {
        button0.setOnClickListener(v -> {
            button1.setChecked(false);
            button2.setChecked(false);
            button3.setChecked(false);
            settingsPrefsUtils.saveNamesMode(context, 0);
        });
        button1.setOnClickListener(v -> {
            button0.setChecked(false);
            button2.setChecked(false);
            button3.setChecked(false);
            settingsPrefsUtils.saveNamesMode(context, 1);
        });
        button2.setOnClickListener(v -> {
            button0.setChecked(false);
            button1.setChecked(false);
            button3.setChecked(false);
            settingsPrefsUtils.saveNamesMode(context, 2);
        });
        button3.setOnClickListener(v -> {
            button0.setChecked(false);
            button1.setChecked(false);
            button2.setChecked(false);
            settingsPrefsUtils.saveNamesMode(context, 3);
        });
    }

    protected void showTodos(MaterialButton button0, MaterialButton button1, MaterialButton button2) {
        button0.setOnClickListener(v -> {
            button1.setChecked(false);
            button2.setChecked(false);
            settingsPrefsUtils.showTodos(context, 0);
        });
        button1.setOnClickListener(v -> {
            button0.setChecked(false);
            button2.setChecked(false);
            settingsPrefsUtils.showTodos(context, 3);
        });
        button2.setOnClickListener(v -> {
            button0.setChecked(false);
            button1.setChecked(false);
            settingsPrefsUtils.showTodos(context, 5);
        });
    }

    protected void screenLock(MaterialButton button0, MaterialButton button1, MaterialButton button2, MaterialButton button3) {
        button0.setOnClickListener(v -> {
            button1.setChecked(false);
            button2.setChecked(false);
            button3.setChecked(false);
            settingsPrefsUtils.saveLockMode(context, 0);
        });
        button1.setOnClickListener(v -> {
            button0.setChecked(false);
            button2.setChecked(false);
            button3.setChecked(false);
            settingsPrefsUtils.saveLockMode(context, 1);
        });
        button2.setOnClickListener(v -> {
            button0.setChecked(false);
            button1.setChecked(false);
            button3.setChecked(false);
            settingsPrefsUtils.saveLockMode(context, 2);
        });
        button3.setOnClickListener(v -> {
            button0.setChecked(false);
            button1.setChecked(false);
            button2.setChecked(false);
            settingsPrefsUtils.saveLockMode(context, 3);
        });
    }

    protected void theme(MaterialButton button0, MaterialButton button1, MaterialButton button2) {
        button0.setOnClickListener(v -> {
            button1.setChecked(false);
            button2.setChecked(false);
            settingsPrefsUtils.saveTheme(context, 0);
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM);
        });
        button1.setOnClickListener(v -> {
            button0.setChecked(false);
            button2.setChecked(false);
            settingsPrefsUtils.saveTheme(context, 1);
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        });
        button2.setOnClickListener(v -> {
            button1.setChecked(false);
            button0.setChecked(false);
            settingsPrefsUtils.saveTheme(context, 2);
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        });
    }

    protected void openAbout(View view) {
        view.setOnClickListener(v ->
                (new About()).show(appCompatActivity.getSupportFragmentManager(), constants.MODAL_BOTTOM_SHEET_TAG));
    }
}
