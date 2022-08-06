package rasel.lunar.launcher.apps;

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
import androidx.fragment.app.Fragment;

import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textview.MaterialTextView;

import java.util.ArrayList;
import java.util.List;

import rasel.lunar.launcher.R;
import rasel.lunar.launcher.databinding.AppDrawerBinding;
import rasel.lunar.launcher.helpers.UniUtils;

public class AppDrawer extends Fragment {

    private AppDrawerBinding binding;
    private Context context;
    private final AppMenuUtils appMenuUtils = new AppMenuUtils();
    private final UniUtils uniUtils = new UniUtils();

    private final String[] leftSearchArray = new String[]{"a", "b", "c", "d", "e", "f", "g", "h", "i",
            "j", "k", "l", "m"};
    private final String[] leftSearchArrayII = new String[]{"-", "_", "0", "1", "2", "3", "4"};
    private final String[] rightSearchArray = new String[]{".", "!", "9", "8", "7", "6", "5"};
    private final String[] rightSearchArrayII = new String[]{"z", "y", "x", "w", "v", "u", "t", "s", "r",
            "q", "p", "o", "n"};

    private ArrayList<String> packageNamesArrayList;
    private ArrayAdapter<String> appsAdapter;
    private PackageManager packageManager;
    private List<ResolveInfo> packageList;
    private String searchString;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = AppDrawerBinding.inflate(inflater, container, false);
        context = requireActivity().getApplicationContext();

        setupInitialView();
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        controlOnAppActions();
        controlOnSearchClicks();
        searchStringRemover();
    }

    private void setupInitialView() {
        packageManager = requireActivity().getPackageManager();
        packageNamesArrayList = new ArrayList<>();
        appsAdapter = new ArrayAdapter<>(context, R.layout.apps_list, R.id.app_textview, new ArrayList<>());
        // Left search textview list
        ArrayAdapter<String> leftSearchAdapter = new ArrayAdapter<>(context, R.layout.apps_search_list, R.id.search_textview, leftSearchArray);
        ArrayAdapter<String> leftSearchAdapterII = new ArrayAdapter<>(context, R.layout.apps_search_list, R.id.search_textview, leftSearchArrayII);
        // Right search textview list
        ArrayAdapter<String> rightSearchAdapter = new ArrayAdapter<>(context, R.layout.apps_search_list, R.id.search_textview, rightSearchArray);
        ArrayAdapter<String> rightSearchAdapterII = new ArrayAdapter<>(context, R.layout.apps_search_list, R.id.search_textview, rightSearchArrayII);

        binding.leftSearchList.setAdapter(leftSearchAdapter);
        binding.leftSearchListII.setAdapter(leftSearchAdapterII);
        binding.rightSearchList.setAdapter(rightSearchAdapter);
        binding.rightSearchListII.setAdapter(rightSearchAdapterII);

        binding.searchStringBox.setVisibility(View.GONE);
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
            binding.noAppsMessage.setVisibility(View.VISIBLE);
            return;
        } else {
            binding.noAppsMessage.setVisibility(View.GONE);
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
            try {
                launchAppMenu(packageNamesArrayList.get(i));
            } catch (PackageManager.NameNotFoundException ignored) {}
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
        binding.searchStringBox.setVisibility(View.VISIBLE);
        binding.searchStringBox.setText(searchString);
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
            if (appNm.toLowerCase().contains(searchString)) {
                appsAdapter.add(appNm);
                packageNamesArrayList.add(resolver.activityInfo.packageName);
            }
        }

        // If only one app contains the search string, then launch it
        if (appsAdapter.getCount() == 1) {
            startActivity(packageManager.getLaunchIntentForPackage(packageNamesArrayList.get(0)));
        } else if (appsAdapter.getCount() < 1) {
            binding.noAppsMessage.setVisibility(View.VISIBLE);
        } else {
            showApps();
            binding.noAppsMessage.setVisibility(View.GONE);
        }
    }

    /* On back press, remove the last character of the string
        and filter app list */
    private void searchStringRemover() {
        binding.searchStringBox.setOnClickListener(v -> {
            if(!searchString.isEmpty()) {
                searchString = searchString.substring(0, (searchString.length()) - 1);
                binding.searchStringBox.setText(searchString);
                filterAppsList();
                if(searchString.isEmpty()) {
                    binding.searchStringBox.setVisibility(View.GONE);
                }
            }
        });

        binding.searchStringBox.setOnLongClickListener(v -> {
            binding.searchStringBox.setVisibility(View.GONE);
            fetchAllApps();
            return false;
        });
    }

    private void launchAppMenu(String packageName) throws PackageManager.NameNotFoundException {
        BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(requireActivity());
        View view = getLayoutInflater().inflate(R.layout.app_menu, null);
        bottomSheetDialog.setContentView(view);
        bottomSheetDialog.show();

        MaterialTextView appName = view.findViewById(R.id.app_name);
        MaterialButton appPackage = view.findViewById(R.id.app_package);
        appName.setText(appMenuUtils.getAppName(context.getPackageManager(), packageName));
        appPackage.setText(packageName);

        view.findViewById(R.id.app_package).setOnClickListener(v ->
                uniUtils.copyToClipboard(requireActivity(), context, packageName));
        view.findViewById(R.id.app_info).setOnClickListener(v ->
                appMenuUtils.openAppInfo(context, packageName, bottomSheetDialog));
        view.findViewById(R.id.app_store).setOnClickListener(v ->
                appMenuUtils.openAppStore(context, packageName, bottomSheetDialog));
        view.findViewById(R.id.app_freeform).setOnClickListener(v ->
                appMenuUtils.launchAsFreeform(requireActivity(),context, uniUtils, packageName, bottomSheetDialog));
        view.findViewById(R.id.app_uninstall).setOnClickListener(v ->
                appMenuUtils.uninstallApp(context, packageName, bottomSheetDialog));
    }

    @Override
    public void onResume() {
        super.onResume();
        fetchAllApps();
    }

}
