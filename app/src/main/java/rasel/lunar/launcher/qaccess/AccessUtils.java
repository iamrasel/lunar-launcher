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
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.provider.Settings;
import android.text.InputType;
import android.view.View;
import android.widget.SeekBar;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.appcompat.widget.AppCompatSeekBar;
import androidx.fragment.app.FragmentActivity;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;

import java.util.Objects;

import rasel.lunar.launcher.R;
import rasel.lunar.launcher.apps.FavouriteUtils;
import rasel.lunar.launcher.databinding.SaverDialogBinding;
import rasel.lunar.launcher.helpers.Constants;
import rasel.lunar.launcher.helpers.UniUtils;

public class AccessUtils {

    private final Context context;
    private final BottomSheetDialogFragment bottomSheetDialogFragment;
    private final FragmentActivity fragmentActivity;
    private final FavouriteUtils favouriteUtils = new FavouriteUtils();
    private final UniUtils uniUtils = new UniUtils();
    private final Constants constants = new Constants();
    private final SharedPreferences sharedPreferences;

    protected AccessUtils(Context context, BottomSheetDialogFragment bottomSheetDialogFragment, FragmentActivity fragmentActivity) {
        this.context = context;
        this.bottomSheetDialogFragment = bottomSheetDialogFragment;
        this.fragmentActivity = fragmentActivity;
        this.sharedPreferences = context.getSharedPreferences(constants.SHARED_PREFS_PHONES_URLS, Context.MODE_PRIVATE);
    }

    protected void phonesAndUrls(String root, String intentString, String thumbLetter, ExtendedFloatingActionButton efab, int position) {
        if(intentString == null) {
            efab.setText("+");
            efab.setOnClickListener(v -> saverDialog(position, root));
        } else {
            efab.setText(thumbLetter);
            efab.setOnClickListener(v -> {
                if(root.equals(constants.PHONE_NO)) {
                    Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + intentString));
                    fragmentActivity.startActivity(intent);
                } else if(root.equals(constants.URL_ADDRESS)) {
                    String url = intentString;
                    if(!url.startsWith("http://") && !url.startsWith("https://")) {
                        url = "http://" + intentString;
                    }
                    fragmentActivity.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(url)).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
                }
            });
            efab.setOnLongClickListener(v -> {
                if(root.equals(constants.PHONE_NO)) {
                    sharedPreferences.edit().putString(constants.PHONE_NO_ + position, null).apply();
                    sharedPreferences.edit().putString(constants.PHONE_THUMB_LETTER_ + position, null).apply();
                } else if(root.equals(constants.URL_ADDRESS)) {
                    sharedPreferences.edit().putString(constants.URL_NO_ + position, null).apply();
                    sharedPreferences.edit().putString(constants.URL_THUMB_LETTER_ + position, null).apply();
                }
                efab.setText("+");
                fragmentActivity.recreate();
                return true;
            });
        }
    }

    protected void controlBrightness(AppCompatSeekBar seekBar) {
        ContentResolver resolver = fragmentActivity.getContentResolver();
        seekBar.setMax(255);
        seekBar.setKeyProgressIncrement(1);
        try {
            int brightness = Settings.System.getInt(resolver, Settings.System.SCREEN_BRIGHTNESS);
            seekBar.setProgress(brightness);
        } catch(Settings.SettingNotFoundException settingNotFoundException) {
            uniUtils.exceptionViewer(fragmentActivity, settingNotFoundException.getMessage());
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

    protected void favApps(String packageName, AppCompatImageView imageView, int position) {
        PackageManager packageManager = context.getPackageManager();
        if(!(packageName == null)) {
            try{
                Drawable appIcon = packageManager.getApplicationIcon(packageName);
                imageView.setImageDrawable(appIcon);
                imageView.setOnClickListener(v -> {
                    context.startActivity(packageManager.getLaunchIntentForPackage(packageName));
                    bottomSheetDialogFragment.dismiss();
                });
                imageView.setOnLongClickListener(v -> {
                    favouriteUtils.saveFavApps(context, position, null);
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

    private void saverDialog(int position, String hintText) {
        MaterialAlertDialogBuilder dialogBuilder = new MaterialAlertDialogBuilder(fragmentActivity);
        SaverDialogBinding dialogBinding = SaverDialogBinding.inflate(fragmentActivity.getLayoutInflater());
        dialogBuilder.setView(dialogBinding.getRoot());
        AlertDialog dialog = dialogBuilder.create();
        dialog.show();

        dialogBinding.inputLayout.setHint(hintText);
        if(hintText.equals(constants.PHONE_NO)) {
            dialogBinding.urlPhone.setInputType(InputType.TYPE_CLASS_PHONE);
        } else if(hintText.equals(constants.URL_ADDRESS)) {
            dialogBinding.urlPhone.setInputType(InputType.TYPE_TEXT_VARIATION_URI);
        }

        String[] alphabets = {"A","B","C","D","E","F","G","H","I","J","K","L","M","N","O","P","Q","R","S","T","U","V","W","X","Y","Z"};

        dialogBinding.alphabetPicker.setMinValue(0);
        dialogBinding.alphabetPicker.setMaxValue(alphabets.length - 1);
        dialogBinding.alphabetPicker.setDisplayedValues(alphabets);
        dialogBinding.alphabetPicker.setOnValueChangedListener((picker, oldVal, newVal) -> {
            if(hintText.equals(constants.PHONE_NO)) {
                sharedPreferences.edit().putString(constants.PHONE_THUMB_LETTER_ + position, alphabets[newVal]).apply();
            } else if(hintText.equals(constants.URL_ADDRESS)) {
                sharedPreferences.edit().putString(constants.URL_THUMB_LETTER_ + position, alphabets[newVal]).apply();
            }
        });


        dialogBinding.cancel.setOnClickListener(v -> dialog.dismiss());
        dialogBinding.ok.setOnClickListener(v -> {
            String urlPhone = Objects.requireNonNull(dialogBinding.urlPhone.getText()).toString().trim();
            if(urlPhone.length() > 0) {
                if(hintText.equals(constants.PHONE_NO)) {
                    sharedPreferences.edit().putString(constants.PHONE_NO_ + position, urlPhone).apply();
                } else if(hintText.equals(constants.URL_ADDRESS)) {
                    sharedPreferences.edit().putString(constants.URL_NO_ + position, urlPhone).apply();
                }
                dialog.dismiss();
                fragmentActivity.recreate();
            } else {
                dialogBinding.urlPhone.setError(context.getString(R.string.empty_text_field));
            }
        });
    }
}
