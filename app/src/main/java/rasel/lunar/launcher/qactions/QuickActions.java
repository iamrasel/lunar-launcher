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

package rasel.lunar.launcher.qactions;

import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import rasel.lunar.launcher.apps.FavouriteUtils;
import rasel.lunar.launcher.databinding.QuickActionsBinding;

public class QuickActions extends BottomSheetDialogFragment {

    private QuickActionsBinding binding;
    private Context context;
    private PackageManager packageManager;
    private final FavouriteUtils favouriteUtils = new FavouriteUtils();

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = QuickActionsBinding.inflate(inflater, container, false);

        initializer();
        favOne(); favTwo(); favThree(); favFour(); favFive(); favSix();
        return binding.getRoot();
    }

    private void initializer() {
        context = requireActivity().getApplicationContext();
        packageManager = context.getPackageManager();
        favouriteUtils.initialize(context);
    }

    private void favOne() {
        if(!(favouriteUtils.packageOne == null)) {
            try{
                Drawable iconOne = packageManager.getApplicationIcon(favouriteUtils.packageOne);
                binding.one.setImageDrawable(iconOne);
                binding.one.setOnClickListener(v -> {
                    startActivity(packageManager.getLaunchIntentForPackage(favouriteUtils.packageOne));
                    dismiss();
                });
                binding.one.setOnLongClickListener(v -> {
                    favouriteUtils.saveFavApps(context, 1, null);
                    binding.one.setVisibility(View.GONE);
                    return true;
                });
            } catch(PackageManager.NameNotFoundException nameNotFoundException) {
				binding.one.setVisibility(View.GONE);
                nameNotFoundException.printStackTrace();
            }
        } else {
            binding.one.setVisibility(View.GONE);
        }
    }

    private void favTwo() {
        if(!(favouriteUtils.packageTwo == null)) {
            try {
                Drawable iconTwo = packageManager.getApplicationIcon(favouriteUtils.packageTwo);
                binding.two.setImageDrawable(iconTwo);
                binding.two.setOnClickListener(v -> {
                    startActivity(packageManager.getLaunchIntentForPackage(favouriteUtils.packageTwo));
                    dismiss();
                });
                binding.two.setOnLongClickListener(v -> {
                    favouriteUtils.saveFavApps(context, 2, null);
                    binding.two.setVisibility(View.GONE);
                    return true;
                });
            } catch(PackageManager.NameNotFoundException nameNotFoundException) {
				binding.two.setVisibility(View.GONE);
                nameNotFoundException.printStackTrace();
            }
        } else {
            binding.two.setVisibility(View.GONE);
        }
    }

    private void favThree() {
        if(!(favouriteUtils.packageThree == null)) {
            try {
                Drawable iconThree = packageManager.getApplicationIcon(favouriteUtils.packageThree);
                binding.three.setImageDrawable(iconThree);
                binding.three.setOnClickListener(v -> {
                    startActivity(packageManager.getLaunchIntentForPackage(favouriteUtils.packageThree));
                    dismiss();
                });
                binding.three.setOnLongClickListener(v -> {
                    favouriteUtils.saveFavApps(context, 3, null);
                    binding.three.setVisibility(View.GONE);
                    return true;
                });
            } catch(PackageManager.NameNotFoundException nameNotFoundException) {
				binding.three.setVisibility(View.GONE);
                nameNotFoundException.printStackTrace();
            }
        } else {
            binding.three.setVisibility(View.GONE);
        }
    }

    private void favFour() {
        if(!(favouriteUtils.packageFour == null)) {
            try {
                Drawable iconFour = packageManager.getApplicationIcon(favouriteUtils.packageFour);
                binding.four.setImageDrawable(iconFour);
                binding.four.setOnClickListener(v -> {
                    startActivity(packageManager.getLaunchIntentForPackage(favouriteUtils.packageFour));
                    dismiss();
                });
                binding.four.setOnLongClickListener(v -> {
                    favouriteUtils.saveFavApps(context, 4, null);
                    binding.four.setVisibility(View.GONE);
                    return true;
                });
            } catch(PackageManager.NameNotFoundException nameNotFoundException) {
				binding.four.setVisibility(View.GONE);
                nameNotFoundException.printStackTrace();
            }
        } else {
            binding.four.setVisibility(View.GONE);
        }
    }

    private void favFive() {
        if(!(favouriteUtils.packageFive == null)) {
            try {
                Drawable iconFive = packageManager.getApplicationIcon(favouriteUtils.packageFive);
                binding.five.setImageDrawable(iconFive);
                binding.five.setOnClickListener(v -> {
                    startActivity(packageManager.getLaunchIntentForPackage(favouriteUtils.packageFive));
                    dismiss();
                });
                binding.five.setOnLongClickListener(v -> {
                    favouriteUtils.saveFavApps(context, 5, null);
                    binding.five.setVisibility(View.GONE);
                    return true;
                });
            } catch(PackageManager.NameNotFoundException nameNotFoundException) {
				binding.five.setVisibility(View.GONE);
                nameNotFoundException.printStackTrace();
            }
        } else {
            binding.five.setVisibility(View.GONE);
        }
    }

    private void favSix() {
        if(!(favouriteUtils.packageSix == null)) {
            try {
                Drawable iconSix = packageManager.getApplicationIcon(favouriteUtils.packageSix);
                binding.six.setImageDrawable(iconSix);
                binding.six.setOnClickListener(v -> {
                    startActivity(packageManager.getLaunchIntentForPackage(favouriteUtils.packageSix));
                    dismiss();
                });
                binding.six.setOnLongClickListener(v -> {
                    favouriteUtils.saveFavApps(context, 6, null);
                    binding.six.setVisibility(View.GONE);
                    return true;
                });
            } catch(PackageManager.NameNotFoundException nameNotFoundException) {
				binding.six.setVisibility(View.GONE);
                nameNotFoundException.printStackTrace();
            }
        } else {
            binding.six.setVisibility(View.GONE);
        }
    }
}
