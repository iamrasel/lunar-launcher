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
import java.util.concurrent.atomic.AtomicInteger;

import rasel.lunar.launcher.databinding.MoreSettingsBinding;
import rasel.lunar.launcher.helpers.Constants;

public class MoreSettings extends BottomSheetDialogFragment {

    private MoreSettingsBinding binding;
    private Context context;
    private final Constants constants = new Constants();
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
        themeValue = sharedPreferences.getInt(constants.SHARED_PREF_THEME, -1);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        SettingsPrefsUtils settingsPrefsUtils = new SettingsPrefsUtils();
        AtomicInteger lockModeValue = new AtomicInteger();
        AtomicInteger themeValue = new AtomicInteger();

        binding.selectLockNegative.setOnClickListener(v -> {
            binding.selectLockAccessibility.setChecked(false);
            binding.selectLockAdmin.setChecked(false);
            binding.selectLockRoot.setChecked(false);
            lockModeValue.set(0);
        });
        binding.selectLockAccessibility.setOnClickListener(v -> {
            binding.selectLockNegative.setChecked(false);
            binding.selectLockAdmin.setChecked(false);
            binding.selectLockRoot.setChecked(false);
            lockModeValue.set(1);
        });
        binding.selectLockAdmin.setOnClickListener(v -> {
            binding.selectLockNegative.setChecked(false);
            binding.selectLockAccessibility.setChecked(false);
            binding.selectLockRoot.setChecked(false);
            lockModeValue.set(2);
        });
        binding.selectLockRoot.setOnClickListener(v -> {
            binding.selectLockNegative.setChecked(false);
            binding.selectLockAccessibility.setChecked(false);
            binding.selectLockAdmin.setChecked(false);
            lockModeValue.set(3);
        });

        binding.selectDarkTheme.setOnClickListener(v -> {
            binding.followSystemTheme.setChecked(false);
            binding.selectLightTheme.setChecked(false);
            themeValue.set(2);
        });
        binding.followSystemTheme.setOnClickListener(v -> {
            binding.selectDarkTheme.setChecked(false);
            binding.selectLightTheme.setChecked(false);
            themeValue.set(-1);
        });
        binding.selectLightTheme.setOnClickListener(v -> {
            binding.selectDarkTheme.setChecked(false);
            binding.followSystemTheme.setChecked(false);
            themeValue.set(1);
        });

        binding.cancelButton.setOnClickListener(v -> dismiss());
        binding.okButton.setOnClickListener(v -> {
            settingsPrefsUtils.saveFeedUrl(context, getFeedUrl());
            settingsPrefsUtils.saveLockMode(context, lockModeValue.get());
            settingsPrefsUtils.saveTheme(context, themeValue.get());
            AppCompatDelegate.setDefaultNightMode(themeValue.get());
            dismiss();
        });
    }

    private String getFeedUrl() {
        return Objects.requireNonNull(binding.inputFeedUrl.getText()).toString().trim();
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
            case -1: binding.followSystemTheme.setChecked(true); break;
            case 2: binding.selectDarkTheme.setChecked(true); break;
            case 1: binding.selectLightTheme.setChecked(true); break;
        }
    }
}
