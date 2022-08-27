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
import com.google.android.material.button.MaterialButtonToggleGroup;

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

    protected void timeFormat(MaterialButtonToggleGroup buttonToggleGroup, MaterialButton button0, MaterialButton button1, MaterialButton button2) {
        buttonToggleGroup.addOnButtonCheckedListener((group, checkedId, isChecked) -> {
            if (isChecked) {
                if (checkedId == button0.getId()) {
                    settingsPrefsUtils.saveTimeFormat(context, 0);
                } else if(checkedId == button1.getId()) {
                    settingsPrefsUtils.saveTimeFormat(context, 1);
                } else if(checkedId == button2.getId()) {
                    settingsPrefsUtils.saveTimeFormat(context, 2);
                }
            }
        });
    }

    protected void showYear(MaterialButtonToggleGroup buttonToggleGroup, MaterialButton button0, MaterialButton button1) {
        buttonToggleGroup.addOnButtonCheckedListener((group, checkedId, isChecked) -> {
            if (isChecked) {
                if (checkedId == button0.getId()) {
                    settingsPrefsUtils.showYear(context, 0);
                } else if(checkedId == button1.getId()) {
                    settingsPrefsUtils.showYear(context, 1);
                }
            }
        });
    }

    protected void tempUnit(MaterialButtonToggleGroup buttonToggleGroup, MaterialButton button0, MaterialButton button1) {
        buttonToggleGroup.addOnButtonCheckedListener((group, checkedId, isChecked) -> {
            if (isChecked) {
                if (checkedId == button0.getId()) {
                    settingsPrefsUtils.saveTempUnit(context, 0);
                } else if(checkedId == button1.getId()) {
                    settingsPrefsUtils.saveTempUnit(context, 1);
                }
            }
        });
    }

    protected void showCity(MaterialButtonToggleGroup buttonToggleGroup, MaterialButton button0, MaterialButton button1) {
        buttonToggleGroup.addOnButtonCheckedListener((group, checkedId, isChecked) -> {
            if (isChecked) {
                if (checkedId == button0.getId()) {
                    settingsPrefsUtils.showCity(context, 0);
                } else if(checkedId == button1.getId()) {
                    settingsPrefsUtils.showCity(context, 1);
                }
            }
        });
    }

    protected void showTodos(MaterialButtonToggleGroup buttonToggleGroup, MaterialButton button0, MaterialButton button1, MaterialButton button2) {
        buttonToggleGroup.addOnButtonCheckedListener((group, checkedId, isChecked) -> {
            if (isChecked) {
                if (checkedId == button0.getId()) {
                    settingsPrefsUtils.showTodos(context, 0);
                } else if(checkedId == button1.getId()) {
                    settingsPrefsUtils.showTodos(context, 3);
                } else if(checkedId == button2.getId()) {
                    settingsPrefsUtils.showTodos(context, 5);
                }
            }
        });
    }

    protected void screenLock(MaterialButtonToggleGroup buttonToggleGroup, MaterialButton button0, MaterialButton button1, MaterialButton button2, MaterialButton button3) {
        buttonToggleGroup.addOnButtonCheckedListener((group, checkedId, isChecked) -> {
            if (isChecked) {
                if (checkedId == button0.getId()) {
                    settingsPrefsUtils.saveLockMode(context, 0);
                } else if(checkedId == button1.getId()) {
                    settingsPrefsUtils.saveLockMode(context, 1);
                } else if(checkedId == button2.getId()) {
                    settingsPrefsUtils.saveLockMode(context, 2);
                } else if(checkedId == button3.getId()) {
                    settingsPrefsUtils.saveLockMode(context, 3);
                }
            }
        });
    }

    protected void theme(MaterialButtonToggleGroup buttonToggleGroup, MaterialButton button0, MaterialButton button1, MaterialButton button2) {
        buttonToggleGroup.addOnButtonCheckedListener((group, checkedId, isChecked) -> {
            if (isChecked) {
                if (checkedId == button0.getId()) {
                    settingsPrefsUtils.saveTheme(context, 0);
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM);
                } else if(checkedId == button1.getId()) {
                    settingsPrefsUtils.saveTheme(context, 1);
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
                } else if(checkedId == button2.getId()) {
                    settingsPrefsUtils.saveTheme(context, 2);
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
                }
            }
        });
    }

    protected void openAbout(View view) {
        view.setOnClickListener(v ->
                (new About()).show(appCompatActivity.getSupportFragmentManager(), constants.MODAL_BOTTOM_SHEET_TAG));
    }
}
