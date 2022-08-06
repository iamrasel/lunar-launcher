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
import java.util.concurrent.atomic.AtomicInteger;

import rasel.lunar.launcher.helpers.Constants;
import rasel.lunar.launcher.databinding.LauncherHomeSettingsBinding;

public class LauncherHomeSettings extends BottomSheetDialogFragment {

    private LauncherHomeSettingsBinding binding;
    private Context context;
    private final Constants constants = new Constants();
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
        SettingsPrefsUtils settingsPrefsUtils = new SettingsPrefsUtils();
        AtomicInteger timeFormatValue = new AtomicInteger();
        AtomicInteger showYearValue = new AtomicInteger();
        AtomicInteger tempUnitValue = new AtomicInteger();
        AtomicInteger showCityValue = new AtomicInteger();
        AtomicInteger showTodosValue = new AtomicInteger();

        binding.selectTwelve.setOnClickListener(v -> {
            binding.followSystemTime.setChecked(false);
            binding.selectTwentyFour.setChecked(false);
            timeFormatValue.set(1);
        });
        binding.followSystemTime.setOnClickListener(v -> {
            binding.selectTwelve.setChecked(false);
            binding.selectTwentyFour.setChecked(false);
            timeFormatValue.set(0);
        });
        binding.selectTwentyFour.setOnClickListener(v -> {
            binding.selectTwelve.setChecked(false);
            binding.followSystemTime.setChecked(false);
            timeFormatValue.set(2);
        });

        binding.selectYearPositive.setOnClickListener(v -> {
            binding.selectYearNegative.setChecked(false);
            showYearValue.set(1);
        });
        binding.selectYearNegative.setOnClickListener(v -> {
            binding.selectYearPositive.setChecked(false);
            showYearValue.set(0);
        });

        binding.selectCelsius.setOnClickListener(v -> {
            binding.selectFahrenheit.setChecked(false);
            tempUnitValue.set(0);
        });
        binding.selectFahrenheit.setOnClickListener(v -> {
            binding.selectCelsius.setChecked(false);
            tempUnitValue.set(1);
        });

        binding.showCityPositive.setOnClickListener(v -> {
            binding.showCityNegative.setChecked(false);
            showCityValue.set(1);
        });
        binding.showCityNegative.setOnClickListener(v -> {
            binding.showCityPositive.setChecked(false);
            showCityValue.set(0);
        });

        binding.showTodosNegative.setOnClickListener(v -> {
            binding.showTodosThree.setChecked(false);
            binding.showTodosFive.setChecked(false);
            showTodosValue.set(0);
        });
        binding.showTodosThree.setOnClickListener(v -> {
            binding.showTodosNegative.setChecked(false);
            binding.showTodosFive.setChecked(false);
            showTodosValue.set(3);
        });
        binding.showTodosFive.setOnClickListener(v -> {
            binding.showTodosNegative.setChecked(false);
            binding.showTodosThree.setChecked(false);
            showTodosValue.set(5);
        });


        binding.cancelButton.setOnClickListener(v -> dismiss());
        binding.okButton.setOnClickListener(v -> {
            settingsPrefsUtils.saveTimeFormat(context, timeFormatValue.get());
            settingsPrefsUtils.showYear(context, showYearValue.get());
            settingsPrefsUtils.saveCityName(context, getCityName());
            settingsPrefsUtils.saveOwmKey(context, getOwmKey());
            settingsPrefsUtils.saveTempUnit(context, tempUnitValue.get());
            settingsPrefsUtils.showCity(context, showCityValue.get());
            settingsPrefsUtils.showTodos(context, showTodosValue.get());
            dismiss();
        });
    }

    private String getCityName() {
        return Objects.requireNonNull(binding.inputCity.getText()).toString().trim();
    }

    private String getOwmKey() {
        return Objects.requireNonNull(binding.inputOwm.getText()).toString().trim();
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
