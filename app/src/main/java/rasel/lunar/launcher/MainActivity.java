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

package rasel.lunar.launcher;

import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.WindowCompat;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;

import rasel.lunar.launcher.databinding.MainActivityBinding;
import rasel.lunar.launcher.helpers.Constants;
import rasel.lunar.launcher.helpers.ViewPagerAdapter;
import rasel.lunar.launcher.settings.SettingsPrefsUtils;

public class MainActivity extends AppCompatActivity {

    private MainActivityBinding binding;
    private ViewPager2 viewPager;
    private boolean executeOnResume;
    private FragmentRefreshListener fragmentRefreshListener;
    private final Constants constants = new Constants();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = MainActivityBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        WindowCompat.setDecorFitsSystemWindows(getWindow(), false);
        executeOnResume = false;
        putSettings();
        setUpView();
    }

    private boolean firstLaunch() {
        SharedPreferences firstLaunchPrefs = getSharedPreferences(constants.SHARED_PREFS_FIRST_LAUNCH, 0);
        if(firstLaunchPrefs.getBoolean(constants.FIRST_LAUNCH, true)) {
            firstLaunchPrefs.edit().putBoolean(constants.FIRST_LAUNCH, false).apply();
            return true;
        } else {
            return false;
        }
    }

    private void putSettings() {
        if(firstLaunch()) {
            new SettingsPrefsUtils().saveCityName(getApplicationContext(), null);
            new SettingsPrefsUtils().saveOwmKey(getApplicationContext(), null);
        }
    }

    private void setUpView() {
        final RecyclerView.Adapter<?> adapter;
        viewPager = binding.viewPager;
        adapter = new ViewPagerAdapter(getSupportFragmentManager(), getLifecycle());
        viewPager.setAdapter(adapter);
        viewPager.setCurrentItem(1, false);
    }

    @Override
    public void onBackPressed() {
        if(getSupportFragmentManager().getBackStackEntryCount() != 0) {
            if(getFragmentRefreshListener() != null){
                getFragmentRefreshListener().onRefresh();
            }
            getSupportFragmentManager().popBackStack();
        }
        if (viewPager.getCurrentItem() == 0 | viewPager.getCurrentItem() == 2) {
            viewPager.setCurrentItem(1);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if(executeOnResume) {
            this.recreate();
        } else {
            executeOnResume = true;
        }
    }

    public interface FragmentRefreshListener{
        void onRefresh();
    }
    public FragmentRefreshListener getFragmentRefreshListener() {
        return fragmentRefreshListener;
    }
    public void setFragmentRefreshListener(FragmentRefreshListener fragmentRefreshListener) {
        this.fragmentRefreshListener = fragmentRefreshListener;
    }
}
