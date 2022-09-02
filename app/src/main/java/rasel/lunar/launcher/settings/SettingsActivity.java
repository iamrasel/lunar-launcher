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
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import java.util.Objects;

import rasel.lunar.launcher.BuildConfig;
import rasel.lunar.launcher.databinding.SettingsActivityBinding;
import rasel.lunar.launcher.helpers.Constants;
import rasel.lunar.launcher.helpers.UniUtils;

public class SettingsActivity extends AppCompatActivity {

    private SettingsActivityBinding binding;
    private Context context;
    private final Constants constants = new Constants();
    private final SettingsPrefsUtils settingsPrefsUtils = new SettingsPrefsUtils();
    private SettingsClickListeners settingsClickListeners;
    private int timeFormatValue, showYear, tempUnit, showCity, showTodos, lockMode, themeValue;
    private String cityName, owmKey, feedUrl;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = SettingsActivityBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        initializer();
        loadSettings();

        settingsClickListeners.timeFormat(binding.timeGroup, binding.followSystemTime, binding.selectTwelve, binding.selectTwentyFour);
        settingsClickListeners.showYear(binding.yearGroup, binding.selectYearNegative, binding.selectYearPositive);
        settingsClickListeners.tempUnit(binding.tempGroup, binding.selectCelsius, binding.selectFahrenheit);
        settingsClickListeners.showCity(binding.cityGroup, binding.showCityNegative, binding.showCityPositive);
        settingsClickListeners.showTodos(binding.showTodos);
        settingsClickListeners.screenLock(binding.lockGroup, binding.selectLockNegative, binding.selectLockAccessibility, binding.selectLockAdmin, binding.selectLockRoot);
        settingsClickListeners.theme(binding.themeGroup, binding.followSystemTheme, binding.selectDarkTheme, binding.selectLightTheme);
        settingsClickListeners.openAbout(binding.about);
    }

    private void initializer() {
        context = getApplicationContext();
        settingsClickListeners = new SettingsClickListeners(this);
        SharedPreferences sharedPreferences = context.getSharedPreferences(constants.SHARED_PREFS_SETTINGS, Context.MODE_PRIVATE);
        timeFormatValue = sharedPreferences.getInt(constants.SHARED_PREF_TIME_FORMAT, 0);
        showYear = sharedPreferences.getInt(constants.SHARED_PREF_SHOW_YEAR, 1);
        cityName = sharedPreferences.getString(constants.SHARED_PREF_CITY_NAME, "");
        owmKey = sharedPreferences.getString(constants.SHARED_PREF_OWM_KEY, "");
        tempUnit = sharedPreferences.getInt(constants.SHARED_PREF_TEMP_UNIT, 0);
        showCity = sharedPreferences.getInt(constants.SHARED_PREF_SHOW_CITY, 0);
        showTodos = sharedPreferences.getInt(constants.SHARED_PREF_SHOW_TODOS, 3);
        feedUrl = sharedPreferences.getString(constants.SHARED_PREF_FEED_URL, "");
        lockMode = sharedPreferences.getInt(constants.SHARED_PREF_LOCK, 0);
        themeValue = sharedPreferences.getInt(constants.SHARED_PREF_THEME, 0);
    }

    private void loadSettings() {
        switch(timeFormatValue) {
            case 0: binding.followSystemTime.setChecked(true); break;
            case 1: binding.selectTwelve.setChecked(true); break;
            case 2: binding.selectTwentyFour.setChecked(true); break;
        }

        switch(showYear) {
            case 0: binding.selectYearNegative.setChecked(true); break;
            case 1: binding.selectYearPositive.setChecked(true); break;
        }

        binding.inputCity.setText(cityName);
        binding.inputOwm.setText(owmKey);

        switch(tempUnit) {
            case 0: binding.selectCelsius.setChecked(true); break;
            case 1: binding.selectFahrenheit.setChecked(true); break;
        }

        switch(showCity) {
            case 0: binding.showCityNegative.setChecked(true); break;
            case 1: binding.showCityPositive.setChecked(true); break;
        }

        binding.showTodos.setValue(showTodos);

        binding.inputFeedUrl.setText(feedUrl);

        if(!(Build.VERSION.SDK_INT >= Build.VERSION_CODES.P)) {
            binding.selectLockAccessibility.setEnabled(false);
        }
        if(!(new UniUtils()).isRooted()) {
            binding.selectLockRoot.setEnabled(false);
        }

        switch(lockMode) {
            case 0: binding.selectLockNegative.setChecked(true); break;
            case 1: binding.selectLockAccessibility.setChecked(true); break;
            case 2: binding.selectLockAdmin.setChecked(true); break;
            case 3: binding.selectLockRoot.setChecked(true); break;
        }

        switch(themeValue) {
            case 0: binding.followSystemTheme.setChecked(true); break;
            case 1: binding.selectDarkTheme.setChecked(true); break;
            case 2: binding.selectLightTheme.setChecked(true); break;
        }

        binding.version.setText(BuildConfig.VERSION_NAME);
    }

    private String getCityName() {
        return Objects.requireNonNull(binding.inputCity.getText()).toString().trim();
    }

    private String getOwmKey() {
        return Objects.requireNonNull(binding.inputOwm.getText()).toString().trim();
    }

    private String getFeedUrl() {
        return Objects.requireNonNull(binding.inputFeedUrl.getText()).toString().trim();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        settingsPrefsUtils.saveCityName(context, getCityName());
        settingsPrefsUtils.saveOwmKey(context, getOwmKey());
        settingsPrefsUtils.saveFeedUrl(context, getFeedUrl());
    }
}
