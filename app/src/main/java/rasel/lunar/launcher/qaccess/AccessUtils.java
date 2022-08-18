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

import android.content.ContentResolver;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.provider.Settings;
import android.view.View;
import android.widget.SeekBar;

import androidx.appcompat.widget.AppCompatImageView;
import androidx.appcompat.widget.AppCompatSeekBar;
import androidx.fragment.app.FragmentActivity;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import rasel.lunar.launcher.apps.FavouriteUtils;
import rasel.lunar.launcher.helpers.Constants;
import rasel.lunar.launcher.helpers.UniUtils;

public class AccessUtils {

    private final Context context;
    private final PackageManager packageManager;
    private final BottomSheetDialogFragment bottomSheetDialogFragment;
    private final SharedPreferences sharedPreferences;
    private final FavouriteUtils favouriteUtils = new FavouriteUtils();
    private final Constants constants = new Constants();

    protected AccessUtils(Context context, BottomSheetDialogFragment bottomSheetDialogFragment) {
        this.context = context;
        this.packageManager = context.getPackageManager();
        this.bottomSheetDialogFragment = bottomSheetDialogFragment;
        this.sharedPreferences = context.getSharedPreferences(constants.SHARED_PREFS_FAV_APPS, Context.MODE_PRIVATE);
    }

    protected void controlBrightness(AppCompatSeekBar seekBar, FragmentActivity fragmentActivity) {
        ContentResolver resolver = fragmentActivity.getContentResolver();
        seekBar.setMax(255);
        seekBar.setKeyProgressIncrement(1);
        try {
            int brightness = Settings.System.getInt(resolver, Settings.System.SCREEN_BRIGHTNESS);
            seekBar.setProgress(brightness);
        } catch(Settings.SettingNotFoundException settingNotFoundException) {
            (new UniUtils()).exceptionViewer(fragmentActivity, settingNotFoundException.getMessage());
            settingNotFoundException.printStackTrace();
        }
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            public void onStartTrackingTouch(SeekBar seekBar) {}
            public void onStopTrackingTouch(SeekBar seekBar) {}
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                Settings.System.putInt(resolver, Settings.System.SCREEN_BRIGHTNESS_MODE, Settings.System.SCREEN_BRIGHTNESS_MODE_MANUAL);
                Settings.System.putInt(resolver, Settings.System.SCREEN_BRIGHTNESS, progress);
            }
        });
    }

    protected void favOne(AppCompatImageView imageView) {
        String packageOne = sharedPreferences.getString(constants.FAV_APP_ + 1, null);
        if(!(packageOne == null)) {
            try{
                Drawable iconOne = packageManager.getApplicationIcon(packageOne);
                imageView.setImageDrawable(iconOne);
                imageView.setOnClickListener(v -> {
                    context.startActivity(packageManager.getLaunchIntentForPackage(packageOne));
                    bottomSheetDialogFragment.dismiss();
                });
                imageView.setOnLongClickListener(v -> {
                    favouriteUtils.saveFavApps(context, 1, null);
                    imageView.setVisibility(View.GONE);
                    return true;
                });
            } catch(PackageManager.NameNotFoundException nameNotFoundException) {
                imageView.setVisibility(View.GONE);
                nameNotFoundException.printStackTrace();
            }
        } else {
            imageView.setVisibility(View.GONE);
        }
    }

    protected void favTwo(AppCompatImageView imageView) {
        String packageTwo = sharedPreferences.getString(constants.FAV_APP_ + 2, null);
        if(!(packageTwo == null)) {
            try {
                Drawable iconTwo = packageManager.getApplicationIcon(packageTwo);
                imageView.setImageDrawable(iconTwo);
                imageView.setOnClickListener(v -> {
                    context.startActivity(packageManager.getLaunchIntentForPackage(packageTwo));
                    bottomSheetDialogFragment.dismiss();
                });
                imageView.setOnLongClickListener(v -> {
                    favouriteUtils.saveFavApps(context, 2, null);
                    imageView.setVisibility(View.GONE);
                    return true;
                });
            } catch(PackageManager.NameNotFoundException nameNotFoundException) {
                imageView.setVisibility(View.GONE);
                nameNotFoundException.printStackTrace();
            }
        } else {
            imageView.setVisibility(View.GONE);
        }
    }

    protected void favThree(AppCompatImageView imageView) {
        String packageThree = sharedPreferences.getString(constants.FAV_APP_ + 3, null);
        if(!(packageThree == null)) {
            try {
                Drawable iconThree = packageManager.getApplicationIcon(packageThree);
                imageView.setImageDrawable(iconThree);
                imageView.setOnClickListener(v -> {
                    context.startActivity(packageManager.getLaunchIntentForPackage(packageThree));
                    bottomSheetDialogFragment.dismiss();
                });
                imageView.setOnLongClickListener(v -> {
                    favouriteUtils.saveFavApps(context, 3, null);
                    imageView.setVisibility(View.GONE);
                    return true;
                });
            } catch(PackageManager.NameNotFoundException nameNotFoundException) {
                imageView.setVisibility(View.GONE);
                nameNotFoundException.printStackTrace();
            }
        } else {
            imageView.setVisibility(View.GONE);
        }
    }

    protected void favFour(AppCompatImageView imageView) {
        String packageFour = sharedPreferences.getString(constants.FAV_APP_ + 4, null);
        if(!(packageFour == null)) {
            try {
                Drawable iconFour = packageManager.getApplicationIcon(packageFour);
                imageView.setImageDrawable(iconFour);
                imageView.setOnClickListener(v -> {
                    context.startActivity(packageManager.getLaunchIntentForPackage(packageFour));
                    bottomSheetDialogFragment.dismiss();
                });
                imageView.setOnLongClickListener(v -> {
                    favouriteUtils.saveFavApps(context, 4, null);
                    imageView.setVisibility(View.GONE);
                    return true;
                });
            } catch(PackageManager.NameNotFoundException nameNotFoundException) {
                imageView.setVisibility(View.GONE);
                nameNotFoundException.printStackTrace();
            }
        } else {
            imageView.setVisibility(View.GONE);
        }
    }

    protected void favFive(AppCompatImageView imageView) {
        String packageFive = sharedPreferences.getString(constants.FAV_APP_ + 5, null);
        if(!(packageFive == null)) {
            try {
                Drawable iconFive = packageManager.getApplicationIcon(packageFive);
                imageView.setImageDrawable(iconFive);
                imageView.setOnClickListener(v -> {
                    context.startActivity(packageManager.getLaunchIntentForPackage(packageFive));
                    bottomSheetDialogFragment.dismiss();
                });
                imageView.setOnLongClickListener(v -> {
                    favouriteUtils.saveFavApps(context, 5, null);
                    imageView.setVisibility(View.GONE);
                    return true;
                });
            } catch(PackageManager.NameNotFoundException nameNotFoundException) {
                imageView.setVisibility(View.GONE);
                nameNotFoundException.printStackTrace();
            }
        } else {
            imageView.setVisibility(View.GONE);
        }
    }

    protected void favSix(AppCompatImageView imageView) {
        String packageSix = sharedPreferences.getString(constants.FAV_APP_ + 6, null);
        if(!(packageSix == null)) {
            try {
                Drawable iconSix = packageManager.getApplicationIcon(packageSix);
                imageView.setImageDrawable(iconSix);
                imageView.setOnClickListener(v -> {
                    context.startActivity(packageManager.getLaunchIntentForPackage(packageSix));
                    bottomSheetDialogFragment.dismiss();
                });
                imageView.setOnLongClickListener(v -> {
                    favouriteUtils.saveFavApps(context, 6, null);
                    imageView.setVisibility(View.GONE);
                    return true;
                });
            } catch(PackageManager.NameNotFoundException nameNotFoundException) {
                imageView.setVisibility(View.GONE);
                nameNotFoundException.printStackTrace();
            }
        } else {
            imageView.setVisibility(View.GONE);
        }
    }
}
