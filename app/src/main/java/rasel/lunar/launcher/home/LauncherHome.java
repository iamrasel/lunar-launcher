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

package rasel.lunar.launcher.home;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.LinearLayoutManager;

import dev.chrisbanes.insetter.Insetter;
import rasel.lunar.launcher.MainActivity;
import rasel.lunar.launcher.databinding.LauncherHomeBinding;
import rasel.lunar.launcher.helpers.Constants;
import rasel.lunar.launcher.home.weather.WeatherExecutor;
import rasel.lunar.launcher.todos.DatabaseHandler;
import rasel.lunar.launcher.todos.TodoAdapter;
import rasel.lunar.launcher.todos.TodoManager;

public class LauncherHome extends Fragment {

    private LauncherHomeBinding binding;
    private FragmentActivity activity;
    private Context context;
    private FragmentManager fragmentManager;
    private SharedPreferences sharedPreferences;
    private final Constants constants = new Constants();
    private HomeUtils homeUtils;
    private BatteryReceiver batteryReceiver;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = LauncherHomeBinding.inflate(inflater, container, false);

        if(isAdded()) {
            activity = requireActivity();
        } else {
            activity = new MainActivity();
        }

        context = activity.getApplicationContext();
        fragmentManager = activity.getSupportFragmentManager();
        sharedPreferences = context.getSharedPreferences(constants.SHARED_PREFS_SETTINGS, Context.MODE_PRIVATE);
        int lockMethodValue = sharedPreferences.getInt(constants.SHARED_PREF_LOCK, 0);
        homeUtils = new HomeUtils(activity, fragmentManager, sharedPreferences, lockMethodValue);
        batteryReceiver = new BatteryReceiver(binding.batteryProgress);

        Insetter.builder()
                .padding(WindowInsetsCompat.Type.systemBars())
                .applyToView(binding.getRoot());

        fragmentManager.addOnBackStackChangedListener(this::showTodoList);
        context.registerReceiver(batteryReceiver, new IntentFilter(Intent.ACTION_BATTERY_CHANGED));

        return binding.getRoot();
    }

    public void showTodoList() {
        binding.todos.setLayoutManager(new LinearLayoutManager(context));
        binding.todos.setAdapter(new TodoAdapter((new TodoManager()), (new DatabaseHandler(context)).getTodos(), context, fragmentManager, this));
    }

    @Override
    public void onResume() {
        context.registerReceiver(batteryReceiver, new IntentFilter(Intent.ACTION_BATTERY_CHANGED)); // Battery

        // Time and date
        if(DateFormat.is24HourFormat(requireContext())) {
            binding.time.setFormat24Hour(homeUtils.getTimeFormat());
            binding.date.setFormat24Hour(homeUtils.getDateFormat());
        } else {
            binding.time.setFormat12Hour(homeUtils.getTimeFormat());
            binding.date.setFormat12Hour(homeUtils.getDateFormat());
        }

        new WeatherExecutor(sharedPreferences).generateTempString(binding.temp, activity); // Weather
        showTodoList();

        // handle gesture events
        homeUtils.rootViewGestures(binding.getRoot());
        homeUtils.batteryProgressGestures(binding.batteryProgress);
        homeUtils.todosGestures(binding.todos);
        super.onResume();
    }

    @Override
    public void onDestroy() {
        context.unregisterReceiver(batteryReceiver);
        super.onDestroy();
    }
}
