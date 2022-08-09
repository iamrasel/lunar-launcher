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
import androidx.appcompat.app.AppCompatDelegate;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import java.util.Objects;

import rasel.lunar.launcher.databinding.MoreSettingsBinding;
import rasel.lunar.launcher.helpers.Constants;

public class MoreSettings extends BottomSheetDialogFragment {

    private MoreSettingsBinding binding;
    private Context context;
    private final Constants constants = new Constants();
    private final SettingsPrefsUtils settingsPrefsUtils = new SettingsPrefsUtils();
    private String feedUrl;
    private int lockMode, themeValue;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = MoreSettingsBinding.inflate(inflater, container, false);
        initialize();
        loadSettings();
        return binding.getRoot();
    }

    private void initialize() {
        context = requireActivity().getApplicationContext();
        SharedPreferences sharedPreferences = context.getSharedPreferences(constants.SHARED_PREFS_SETTINGS, Context.MODE_PRIVATE);
        feedUrl = sharedPreferences.getString(constants.SHARED_PREF_FEED_URL, null);
        lockMode = sharedPreferences.getInt(constants.SHARED_PREF_LOCK, 0);
        themeValue = sharedPreferences.getInt(constants.SHARED_PREF_THEME, 0);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        getLockMode();
        getThemeValue();

        binding.okButton.setOnClickListener(v -> {
            settingsPrefsUtils.saveFeedUrl(context, getFeedUrl());
            dismiss();
        });
    }

    private String getFeedUrl() {
        return Objects.requireNonNull(binding.inputFeedUrl.getText()).toString().trim();
    }

    private void getLockMode() {
        binding.selectLockNegative.setOnClickListener(v -> {
            binding.selectLockAccessibility.setChecked(false);
            binding.selectLockAdmin.setChecked(false);
            binding.selectLockRoot.setChecked(false);
            settingsPrefsUtils.saveLockMode(context, 0);
        });
        binding.selectLockAccessibility.setOnClickListener(v -> {
            binding.selectLockNegative.setChecked(false);
            binding.selectLockAdmin.setChecked(false);
            binding.selectLockRoot.setChecked(false);
            settingsPrefsUtils.saveLockMode(context, 1);
        });
        binding.selectLockAdmin.setOnClickListener(v -> {
            binding.selectLockNegative.setChecked(false);
            binding.selectLockAccessibility.setChecked(false);
            binding.selectLockRoot.setChecked(false);
            settingsPrefsUtils.saveLockMode(context, 2);
        });
        binding.selectLockRoot.setOnClickListener(v -> {
            binding.selectLockNegative.setChecked(false);
            binding.selectLockAccessibility.setChecked(false);
            binding.selectLockAdmin.setChecked(false);
            settingsPrefsUtils.saveLockMode(context, 3);
        });
    }

    private void getThemeValue() {
        binding.selectDarkTheme.setOnClickListener(v -> {
            binding.followSystemTheme.setChecked(false);
            binding.selectLightTheme.setChecked(false);
            settingsPrefsUtils.saveTheme(context, 1);
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        });
        binding.followSystemTheme.setOnClickListener(v -> {
            binding.selectDarkTheme.setChecked(false);
            binding.selectLightTheme.setChecked(false);
            settingsPrefsUtils.saveTheme(context, 0);
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM);
        });
        binding.selectLightTheme.setOnClickListener(v -> {
            binding.selectDarkTheme.setChecked(false);
            binding.followSystemTheme.setChecked(false);
            settingsPrefsUtils.saveTheme(context, 2);
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        });
    }

    private void loadSettings() {
        binding.inputFeedUrl.setText(feedUrl);

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
    }
}
