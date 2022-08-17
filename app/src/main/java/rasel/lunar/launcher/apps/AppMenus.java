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

package rasel.lunar.launcher.apps;

import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import rasel.lunar.launcher.databinding.AppMenuBinding;
import rasel.lunar.launcher.helpers.UniUtils;

public class AppMenus extends BottomSheetDialogFragment {

    private AppMenuBinding binding;
    private final AppMenuUtils appMenuUtils = new AppMenuUtils();
    private final FavouriteUtils favouriteUtils = new FavouriteUtils();
    private final UniUtils uniUtils = new UniUtils();
    private Context context;
    private String packageName;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = AppMenuBinding.inflate(inflater, container, false);
        context = requireActivity().getApplicationContext();
        packageName = getTag();

        try {
            binding.appName.setText(appMenuUtils.getAppName(context.getPackageManager(), packageName));
        } catch (PackageManager.NameNotFoundException nameNotFoundException) {
            (new UniUtils()).exceptionViewer(requireActivity(), nameNotFoundException.getMessage());
            nameNotFoundException.printStackTrace();
        }
        binding.appPackage.setText(packageName);
        favouriteUtils.setPreview(context, packageName, binding.one, binding.two, binding.three, binding.four, binding.five, binding.six);

        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        favouriteUtils.saveFavPosition(binding.favGroup, binding.one, binding.two, binding.three, binding.four, binding.five, binding.six, context, packageName);
        binding.appPackage.setOnClickListener(v ->
                uniUtils.copyToClipboard(requireActivity(), context, packageName));
        binding.appStore.setOnClickListener(v ->
                appMenuUtils.openAppStore(context, packageName, this));
        binding.appFreeform.setOnClickListener(v ->
                appMenuUtils.launchAsFreeform(requireActivity(),context, uniUtils, packageName, this));
        binding.appInfo.setOnClickListener(v ->
                appMenuUtils.openAppInfo(context, packageName, this));
        binding.appUninstall.setOnClickListener(v ->
                appMenuUtils.uninstallApp(context, packageName, this));
    }
}
