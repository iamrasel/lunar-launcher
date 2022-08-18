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

import android.content.Context;
import android.content.Intent;
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

public class QuickAccess extends BottomSheetDialogFragment {

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
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        QuickAccessBinding binding = QuickAccessBinding.inflate(inflater, container, false);

        Context context = requireActivity().getApplicationContext();
        AccessUtils accessUtils = new AccessUtils(context, this);

        accessUtils.controlBrightness(binding.brightnessController, requireActivity());
        accessUtils.favOne(binding.one); accessUtils.favTwo(binding.two); accessUtils.favThree(binding.three);
        accessUtils.favFour(binding.four); accessUtils.favFive(binding.five); accessUtils.favSix(binding.six);
        return binding.getRoot();
    }
}
