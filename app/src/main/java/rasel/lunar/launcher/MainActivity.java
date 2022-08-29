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

import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import rasel.lunar.launcher.databinding.MainActivityBinding;
import rasel.lunar.launcher.helpers.Constants;
import rasel.lunar.launcher.helpers.UniUtils;
import rasel.lunar.launcher.helpers.ViewPagerAdapter;

public class MainActivity extends AppCompatActivity {

    private MainActivityBinding binding;
    private ViewPager2 viewPager;
    private final Constants constants = new Constants();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = MainActivityBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        WindowCompat.setDecorFitsSystemWindows(getWindow(), false);

        if(isFirstLaunch()) {
            welcomeDialog();
        }

        setUpView();
    }

    private boolean isFirstLaunch() {
        SharedPreferences firstLaunchPrefs = getSharedPreferences(constants.SHARED_PREFS_FIRST_LAUNCH, 0);
        if(firstLaunchPrefs.getBoolean(constants.FIRST_LAUNCH, true)) {
            firstLaunchPrefs.edit().putBoolean(constants.FIRST_LAUNCH, false).apply();
            return true;
        } else {
            return false;
        }
    }

    private void welcomeDialog() {
        MaterialAlertDialogBuilder dialogBuilder = new MaterialAlertDialogBuilder(this);
        dialogBuilder.setTitle(R.string.welcome);
        dialogBuilder.setMessage(R.string.welcome_description);
        dialogBuilder.setPositiveButton(R.string.got_it, (dialog, which) -> {
            dialog.dismiss();
            new UniUtils().askPermissions(this);
        });
        dialogBuilder.show();
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
            getSupportFragmentManager().popBackStack();
        }
        if (viewPager.getCurrentItem() == 0 | viewPager.getCurrentItem() == 2) {
            viewPager.setCurrentItem(1);
        }
    }
}
