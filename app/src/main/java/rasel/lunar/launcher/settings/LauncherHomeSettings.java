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
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import java.util.Objects;

import rasel.lunar.launcher.databinding.LauncherHomeSettingsBinding;
import rasel.lunar.launcher.helpers.Constants;

public class LauncherHomeSettings extends BottomSheetDialogFragment {

    private LauncherHomeSettingsBinding binding;
    private Context context;
    private final Constants constants = new Constants();
    private final SettingsPrefsUtils settingsPrefsUtils = new SettingsPrefsUtils();
    private int timeFormatValue, showYear, tempUnit, showCity, showTodos;
    private String cityName, owmKey;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = LauncherHomeSettingsBinding.inflate(inflater, container, false);
        initialize();
        loadSettings();
        return binding.getRoot();
    }

    private void initialize() {
        context = requireActivity().getApplicationContext();
        SharedPreferences sharedPreferences = context.getSharedPreferences(constants.SHARED_PREFS_SETTINGS, Context.MODE_PRIVATE);
        timeFormatValue = sharedPreferences.getInt(constants.SHARED_PREF_TIME_FORMAT, 0);
        showYear = sharedPreferences.getInt(constants.SHARED_PREF_SHOW_YEAR, 1);
        cityName = sharedPreferences.getString(constants.SHARED_PREF_CITY_NAME, null);
        owmKey = sharedPreferences.getString(constants.SHARED_PREF_OWM_KEY, null);
        tempUnit = sharedPreferences.getInt(constants.SHARED_PREF_TEMP_UNIT, 0);
        showCity = sharedPreferences.getInt(constants.SHARED_PREF_SHOW_CITY, 0);
        showTodos = sharedPreferences.getInt(constants.SHARED_PREF_SHOW_TODOS, 3);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        getTimeFormat();
        getShowYear();
        getTempUnit();
        getShowCity();
        getShowTodos();

        binding.okButton.setOnClickListener(v -> {
            settingsPrefsUtils.saveCityName(context, getCityName());
            settingsPrefsUtils.saveOwmKey(context, getOwmKey());
            dismiss();
        });
    }

    private void getTimeFormat() {
        binding.selectTwelve.setOnClickListener(v -> {
            binding.followSystemTime.setChecked(false);
            binding.selectTwentyFour.setChecked(false);
            settingsPrefsUtils.saveTimeFormat(context, 1);
        });
        binding.followSystemTime.setOnClickListener(v -> {
            binding.selectTwelve.setChecked(false);
            binding.selectTwentyFour.setChecked(false);
            settingsPrefsUtils.saveTimeFormat(context, 0);
        });
        binding.selectTwentyFour.setOnClickListener(v -> {
            binding.selectTwelve.setChecked(false);
            binding.followSystemTime.setChecked(false);
            settingsPrefsUtils.saveTimeFormat(context, 2);
        });
    }

    private void getShowYear() {
        binding.selectYearPositive.setOnClickListener(v -> {
            binding.selectYearNegative.setChecked(false);
            settingsPrefsUtils.showYear(context, 1);
        });
        binding.selectYearNegative.setOnClickListener(v -> {
            binding.selectYearPositive.setChecked(false);
            settingsPrefsUtils.showYear(context, 0);
        });
    }

    private String getCityName() {
        return Objects.requireNonNull(binding.inputCity.getText()).toString().trim();
    }

    private String getOwmKey() {
        return Objects.requireNonNull(binding.inputOwm.getText()).toString().trim();
    }

    private void getTempUnit() {
        binding.selectCelsius.setOnClickListener(v -> {
            binding.selectFahrenheit.setChecked(false);
            settingsPrefsUtils.saveTempUnit(context, 0);
        });
        binding.selectFahrenheit.setOnClickListener(v -> {
            binding.selectCelsius.setChecked(false);
            settingsPrefsUtils.saveTempUnit(context, 1);
        });
    }

    private void getShowCity() {
        binding.showCityPositive.setOnClickListener(v -> {
            binding.showCityNegative.setChecked(false);
            settingsPrefsUtils.showCity(context, 1);
        });
        binding.showCityNegative.setOnClickListener(v -> {
            binding.showCityPositive.setChecked(false);
            settingsPrefsUtils.showCity(context, 0);
        });
    }

    private void getShowTodos() {
        binding.showTodosNegative.setOnClickListener(v -> {
            binding.showTodosThree.setChecked(false);
            binding.showTodosFive.setChecked(false);
            settingsPrefsUtils.showTodos(context, 0);
        });
        binding.showTodosThree.setOnClickListener(v -> {
            binding.showTodosNegative.setChecked(false);
            binding.showTodosFive.setChecked(false);
            settingsPrefsUtils.showTodos(context, 3);
        });
        binding.showTodosFive.setOnClickListener(v -> {
            binding.showTodosNegative.setChecked(false);
            binding.showTodosThree.setChecked(false);
            settingsPrefsUtils.showTodos(context, 5);
        });
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

        switch(showTodos) {
            case 0: binding.showTodosNegative.setChecked(true); break;
            case 3: binding.showTodosThree.setChecked(true); break;
            case 5: binding.showTodosFive.setChecked(true); break;
        }
    }
}
