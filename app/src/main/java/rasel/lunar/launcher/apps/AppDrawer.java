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

/*
 * This file is based on this project <https://gitlab.com/biotstoiq/launch/>,
 * which is licensed under MIT.
 */

package rasel.lunar.launcher.apps;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.Fragment;

import java.util.ArrayList;
import java.util.List;

import dev.chrisbanes.insetter.Insetter;
import rasel.lunar.launcher.R;
import rasel.lunar.launcher.databinding.AppDrawerBinding;
import rasel.lunar.launcher.helpers.Constants;
import rasel.lunar.launcher.helpers.SwipeTouchListener;
import rasel.lunar.launcher.helpers.UniUtils;

public class AppDrawer extends Fragment {

    private AppDrawerBinding binding;

    private final String[] leftSearchArray = new String[]{"a", "b", "c", "d", "e", "f", "g", "h", "i",
            "j", "k", "l", "m"};
    private final String[] leftSearchArrayII = new String[]{"0", "1", "2", "3", "4"};
    private final String[] rightSearchArray = new String[]{"9", "8", "7", "6", "5"};
    private final String[] rightSearchArrayII = new String[]{"z", "y", "x", "w", "v", "u", "t", "s", "r",
            "q", "p", "o", "n"};

    private ArrayList<String> packageNamesArrayList;
    private ArrayAdapter<String> appsAdapter;
    private PackageManager packageManager;
    private List<ResolveInfo> packageList;
    private String searchString;
    private Context context;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = AppDrawerBinding.inflate(inflater, container, false);
        context = requireActivity().getApplicationContext();

        Insetter.builder()
                .padding(WindowInsetsCompat.Type.systemBars())
                .applyToView(binding.appsList);
        Insetter.builder()
                .padding(WindowInsetsCompat.Type.systemBars())
                .applyToView(binding.leftSearchList)
                .applyToView(binding.leftSearchListII);
        Insetter.builder()
                .padding(WindowInsetsCompat.Type.systemBars())
                .applyToView(binding.rightSearchList)
                .applyToView(binding.rightSearchListII);

        setupInitialView();
        return binding.getRoot();
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        controlOnAppActions();
        controlOnSearchClicks();
        searchStringRemover();

        binding.getRoot().setOnTouchListener(new SwipeTouchListener(context) {
            @Override
            public void onSwipeDown() {
                super.onSwipeDown();
                new UniUtils().expandNotificationPanel(context, requireActivity());
            }
            @Override
            public void onDoubleClick() {
                super.onDoubleClick();
                new UniUtils().lockMethod(((context.getSharedPreferences(new Constants().SHARED_PREFS_SETTINGS, Context.MODE_PRIVATE))
                        .getInt(new Constants().SHARED_PREF_LOCK, 0)), context, requireActivity());
            }
        });
    }

    private void setupInitialView() {
        packageManager = requireActivity().getPackageManager();
        packageNamesArrayList = new ArrayList<>();
        appsAdapter = new ArrayAdapter<>(getContext(), R.layout.apps_child, R.id.child_textview, new ArrayList<>());
        // Left search textview list
        ArrayAdapter<String> leftSearchAdapter = new ArrayAdapter<>(getContext(), R.layout.apps_child, R.id.child_textview, leftSearchArray);
        ArrayAdapter<String> leftSearchAdapterII = new ArrayAdapter<>(getContext(), R.layout.apps_child, R.id.child_textview, leftSearchArrayII);
        // Right search textview list
        ArrayAdapter<String> rightSearchAdapter = new ArrayAdapter<>(getContext(), R.layout.apps_child, R.id.child_textview, rightSearchArray);
        ArrayAdapter<String> rightSearchAdapterII = new ArrayAdapter<>(getContext(), R.layout.apps_child, R.id.child_textview, rightSearchArrayII);

        binding.leftSearchList.setAdapter(leftSearchAdapter);
        binding.leftSearchListII.setAdapter(leftSearchAdapterII);
        binding.rightSearchList.setAdapter(rightSearchAdapter);
        binding.rightSearchListII.setAdapter(rightSearchAdapterII);

        binding.searchStringChip.setVisibility(View.GONE);
    }

    private void getAppsList() {
        searchString = "";
        // Fetch all the installed apps
        packageList = packageManager.queryIntentActivities(
                new Intent(Intent.ACTION_MAIN, null).addCategory(Intent.CATEGORY_LAUNCHER), 0);
        // Sort the app list
        packageList.sort(new ResolveInfo.DisplayNameComparator(packageManager));
    }

    private void fetchAllApps() {
        getAppsList();
        // Clear the list before repopulating
        appsAdapter.clear();
        packageNamesArrayList.clear();
        /* Add the apps names to the adapter,
            and the package name to the array list */
        for (ResolveInfo resolver : packageList) {
            String apNm = resolver.loadLabel(packageManager).toString();
            appsAdapter.add(apNm);
            packageNamesArrayList.add(resolver.activityInfo.packageName);
        }

        if(appsAdapter.getCount() < 1) {
            binding.loadingProgress.setVisibility(View.VISIBLE);
            return;
        } else {
            binding.loadingProgress.setVisibility(View.GONE);
            binding.appsCount.setText(String.valueOf(appsAdapter.getCount()));
        }
        showApps();
    }

    private void showApps() {
        // Show the app name adapter as the app list
        binding.appsList.setAdapter(appsAdapter);
    }

    private void controlOnAppActions() {
        // Launch the app
        binding.appsList.setOnItemClickListener((adapterView, view, i, l) -> startActivity(packageManager.getLaunchIntentForPackage(packageNamesArrayList.get(i))));

        binding.appsList.setOnItemLongClickListener((adapterView, view, i, l) -> {
            (new AppMenus()).show(requireActivity().getSupportFragmentManager(), packageNamesArrayList.get(i));
            return true;
        });
    }

    private void controlOnSearchClicks() {
        // Left column 1
        binding.leftSearchList.setOnItemClickListener((adapterView, view, i, l) -> searchClickHelper(adapterView, i));
        // Left column 2
        binding.leftSearchListII.setOnItemClickListener((adapterView, view, i, l) -> searchClickHelper(adapterView, i));
        // Right column 1
        binding.rightSearchList.setOnItemClickListener((adapterView, view, i, l) -> searchClickHelper(adapterView, i));
        // Right column 2
        binding.rightSearchListII.setOnItemClickListener((adapterView, view, i, l) -> searchClickHelper(adapterView, i));
    }

    private void searchClickHelper(AdapterView<?> adapterView, int i) {
        if (binding.appsList.getCount() < 2) return;
        searchString = searchString.concat(adapterView.getItemAtPosition(i).toString());
        binding.searchStringChip.setVisibility(View.VISIBLE);
        binding.searchStringChip.setText(searchString);
        filterAppsList();
    }

    private void filterAppsList() {
        // Return if the search string is empty
        if (searchString.equals("")) {
            fetchAllApps();
            return;
        }

        // Clear the current lists
        appsAdapter.clear();
        packageNamesArrayList.clear();

        /* Check each package name and add only the ones
            that match the search string */
        for (ResolveInfo resolver : packageList) {
            String appNm = (String) resolver.loadLabel(packageManager);
            if (appNm.replaceAll("[^a-zA-Z0-9]", "").toLowerCase().contains(searchString)) {
                appsAdapter.add(appNm);
                packageNamesArrayList.add(resolver.activityInfo.packageName);
            }
        }

        // If only one app contains the search string, then launch it
        if (appsAdapter.getCount() == 1) {
            startActivity(packageManager.getLaunchIntentForPackage(packageNamesArrayList.get(0)));
        } else if (appsAdapter.getCount() < 1) {
            binding.appsCount.setText("0");
        } else {
            showApps();
            binding.appsCount.setText(String.valueOf(appsAdapter.getCount()));
        }
    }

    /* On back press, remove the last character of the string
        and filter app list */
    private void searchStringRemover() {
        binding.searchStringChip.setOnClickListener(v -> {
            if(!searchString.isEmpty()) {
                searchString = searchString.substring(0, (searchString.length()) - 1);
                binding.searchStringChip.setText(searchString);
                filterAppsList();
                if(searchString.isEmpty()) {
                    binding.searchStringChip.setVisibility(View.GONE);
                }
            }
        });

        binding.searchStringChip.setOnCloseIconClickListener(v -> {
            binding.searchStringChip.setVisibility(View.GONE);
            fetchAllApps();
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        setupInitialView();
        fetchAllApps();
    }

}
