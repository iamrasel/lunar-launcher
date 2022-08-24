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

package rasel.lunar.launcher.qaccess;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;

import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import rasel.lunar.launcher.R;
import rasel.lunar.launcher.databinding.ExceptionViewerBinding;
import rasel.lunar.launcher.databinding.QuickAccessBinding;
import rasel.lunar.launcher.helpers.Constants;

public class QuickAccess extends BottomSheetDialogFragment {

    private final Constants constants = new Constants();
    private AccessUtils accessUtils;
    String packageOne, packageTwo, packageThree, packageFour, packageFive, packageSix,
            phoneNumOne, phoneNumTwo, phoneNumThree, thumbPhoneOne, thumbPhoneTwo, thumbPhoneThree,
            urlStringOne, urlStringTwo, urlStringThree, thumbUrlOne, thumbUrlTwo, thumbUrlThree;

    @Override
    public void onStart() {
        super.onStart();
        if(!Settings.System.canWrite(getContext())) {
            BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(requireActivity());
            ExceptionViewerBinding exceptionViewerBinding = ExceptionViewerBinding.inflate(requireActivity().getLayoutInflater());
            bottomSheetDialog.setContentView(exceptionViewerBinding.getRoot());
            bottomSheetDialog.show();

            exceptionViewerBinding.textViewer.setText(R.string.modify_system_settings);
            exceptionViewerBinding.button.setText(R.string.allow);
            exceptionViewerBinding.button.setOnClickListener(v -> {
                startActivity(
                        (new Intent(Settings.ACTION_MANAGE_WRITE_SETTINGS))
                                .setData(Uri.parse("package:" + requireActivity().getPackageName()))
                                .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                );
                bottomSheetDialog.dismiss();
            });
        }

        if (requireActivity().checkSelfPermission(Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED){
            requireActivity().requestPermissions(new String[]{Manifest.permission.CALL_PHONE}, 1);
        }
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rasel.lunar.launcher.databinding.QuickAccessBinding binding = QuickAccessBinding.inflate(inflater, container, false);

        initializer();

        accessUtils.favApps(packageOne, binding.appOne, 1); accessUtils.favApps(packageTwo, binding.appTwo, 2);
        accessUtils.favApps(packageThree, binding.appThree, 3); accessUtils.favApps(packageFour, binding.appFour, 4);
        accessUtils.favApps(packageFive, binding.appFive, 5); accessUtils.favApps(packageSix, binding.appSix, 6);

        accessUtils.controlBrightness(binding.brightnessController);

        accessUtils.phonesAndUrls(constants.URL_ADDRESS, urlStringOne, thumbUrlOne, binding.urlOne, 1);
        accessUtils.phonesAndUrls(constants.URL_ADDRESS, urlStringTwo, thumbUrlTwo, binding.urlTwo, 2);
        accessUtils.phonesAndUrls(constants.URL_ADDRESS, urlStringThree, thumbUrlThree, binding.urlThree, 3);

        accessUtils.phonesAndUrls(constants.PHONE_NO, phoneNumOne, thumbPhoneOne, binding.phoneOne, 1);
        accessUtils.phonesAndUrls(constants.PHONE_NO, phoneNumTwo, thumbPhoneTwo, binding.phoneTwo, 2);
        accessUtils.phonesAndUrls(constants.PHONE_NO, phoneNumThree, thumbPhoneThree, binding.phoneThree, 3);

        return binding.getRoot();
    }

    private void initializer() {
        Context context = requireActivity().getApplicationContext();
        accessUtils = new AccessUtils(context, this, requireActivity());
        SharedPreferences prefsFavApps = context.getSharedPreferences(constants.SHARED_PREFS_FAV_APPS, Context.MODE_PRIVATE);
        packageOne = prefsFavApps.getString(constants.FAV_APP_ + 1, null);
        packageTwo = prefsFavApps.getString(constants.FAV_APP_ + 2, null);
        packageThree = prefsFavApps.getString(constants.FAV_APP_ + 3, null);
        packageFour = prefsFavApps.getString(constants.FAV_APP_ + 4, null);
        packageFive = prefsFavApps.getString(constants.FAV_APP_ + 5, null);
        packageSix = prefsFavApps.getString(constants.FAV_APP_ + 6, null);

        SharedPreferences prefsPhonesAndUrls = context.getSharedPreferences(constants.SHARED_PREFS_PHONES_URLS, Context.MODE_PRIVATE);

        phoneNumOne = prefsPhonesAndUrls.getString(constants.PHONE_NO_ + 1, null);
        phoneNumTwo = prefsPhonesAndUrls.getString(constants.PHONE_NO_ + 2, null);
        phoneNumThree = prefsPhonesAndUrls.getString(constants.PHONE_NO_ + 3, null);
        thumbPhoneOne = prefsPhonesAndUrls.getString(constants.PHONE_THUMB_LETTER_ + 1, null);
        thumbPhoneTwo = prefsPhonesAndUrls.getString(constants.PHONE_THUMB_LETTER_ + 2, null);
        thumbPhoneThree = prefsPhonesAndUrls.getString(constants.PHONE_THUMB_LETTER_ + 3, null);

        urlStringOne = prefsPhonesAndUrls.getString(constants.URL_NO_ + 1, null);
        urlStringTwo = prefsPhonesAndUrls.getString(constants.URL_NO_ + 2, null);
        urlStringThree = prefsPhonesAndUrls.getString(constants.URL_NO_ + 3, null);
        thumbUrlOne = prefsPhonesAndUrls.getString(constants.URL_THUMB_LETTER_ + 1, null);
        thumbUrlTwo = prefsPhonesAndUrls.getString(constants.URL_THUMB_LETTER_ + 2, null);
        thumbUrlThree = prefsPhonesAndUrls.getString(constants.URL_THUMB_LETTER_ + 3, null);
    }
}
