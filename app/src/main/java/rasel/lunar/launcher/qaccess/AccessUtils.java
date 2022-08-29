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
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.media.AudioManager;
import android.net.Uri;
import android.provider.Settings;
import android.text.InputType;
import android.view.View;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.fragment.app.FragmentActivity;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;
import com.google.android.material.slider.Slider;

import java.util.Objects;
import java.util.concurrent.atomic.AtomicBoolean;

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

    protected void volumeControllers(Slider notifyBar, Slider alarmBar, Slider mediaBar, Slider voiceBar, Slider ringerBar) {
        AudioManager audioManager = (AudioManager) fragmentActivity.getSystemService(Context.AUDIO_SERVICE);

        notifyBar.setValueTo(audioManager.getStreamMaxVolume(AudioManager.STREAM_NOTIFICATION));
        alarmBar.setValueTo(audioManager.getStreamMaxVolume(AudioManager.STREAM_ALARM));
        mediaBar.setValueTo(audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC));
        voiceBar.setValueTo(audioManager.getStreamMaxVolume(AudioManager.STREAM_VOICE_CALL));
        ringerBar.setValueTo(audioManager.getStreamMaxVolume(AudioManager.STREAM_RING));

        notifyBar.setValue(audioManager.getStreamVolume(AudioManager.STREAM_NOTIFICATION));
        alarmBar.setValue(audioManager.getStreamVolume(AudioManager.STREAM_ALARM));
        mediaBar.setValue(audioManager.getStreamVolume(AudioManager.STREAM_MUSIC));
        voiceBar.setValue(audioManager.getStreamVolume(AudioManager.STREAM_VOICE_CALL));
        ringerBar.setValue(audioManager.getStreamVolume(AudioManager.STREAM_RING));

        notifyBar.addOnChangeListener((slider, value, fromUser) -> {
            try {
                audioManager.setStreamVolume(AudioManager.STREAM_NOTIFICATION, (int) value, 0);
            } catch (Exception exception) {
                uniUtils.exceptionViewer(fragmentActivity, exception.getMessage() + "\nDisable DND mode first.");
                exception.printStackTrace();
            }
        });

        alarmBar.addOnChangeListener((slider, value, fromUser) ->
                audioManager.setStreamVolume(AudioManager.STREAM_ALARM, (int) value, 0));

        mediaBar.addOnChangeListener((slider, value, fromUser) ->
                audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, (int) value, 0));

        voiceBar.addOnChangeListener((slider, value, fromUser) ->
                audioManager.setStreamVolume(AudioManager.STREAM_VOICE_CALL, (int) value, 0));

        ringerBar.addOnChangeListener((slider, value, fromUser) -> {
            try {
                audioManager.setStreamVolume(AudioManager.STREAM_RING, (int) value, 0);
            } catch (Exception exception) {
                uniUtils.exceptionViewer(fragmentActivity, exception.getMessage() + "\nDisable DND mode first.");
                exception.printStackTrace();
            }
        });
    }

    protected void phonesAndUrls(String root, String intentString, String thumbLetter, ExtendedFloatingActionButton efab, int position) {
        if(intentString == null) {
            efab.setText("+");
            efab.setOnClickListener(v -> saverDialog(position, root));
        } else {
            efab.setText(thumbLetter);
            efab.setOnClickListener(v -> {
                if(root.equals(constants.PHONE_NO)) {
                    if(fragmentActivity.checkSelfPermission(Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                        fragmentActivity.requestPermissions(new String[]{Manifest.permission.CALL_PHONE}, 1);
                    } else {
                        fragmentActivity.startActivity(new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + intentString)));
                    }
                } else if(root.equals(constants.URL_ADDRESS)) {
                    String url = intentString;
                    if(!url.startsWith("http://") && !url.startsWith("https://")) {
                        url = "http://" + intentString;
                    }
                    fragmentActivity.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(url)).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
                }
                bottomSheetDialogFragment.dismiss();
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
                bottomSheetDialogFragment.onResume();
                return true;
            });
        }
    }

    protected void controlBrightness(Slider seekBar) {
        ContentResolver resolver = fragmentActivity.getContentResolver();
        seekBar.setValueTo(255);
        try {
            int brightness = Settings.System.getInt(resolver, Settings.System.SCREEN_BRIGHTNESS);
            seekBar.setValue(brightness);
        } catch(Settings.SettingNotFoundException settingNotFoundException) {
            uniUtils.exceptionViewer(fragmentActivity, settingNotFoundException.getMessage());
            settingNotFoundException.printStackTrace();
        }
        seekBar.addOnChangeListener((slider, value, fromUser) -> {
            if(!Settings.System.canWrite(fragmentActivity)) {
                fragmentActivity.startActivity(
                        (new Intent(Settings.ACTION_MANAGE_WRITE_SETTINGS))
                                .setData(Uri.parse("package:" + fragmentActivity.getPackageName()))
                                .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                );
            } else {
                Settings.System.putInt(resolver, Settings.System.SCREEN_BRIGHTNESS_MODE, Settings.System.SCREEN_BRIGHTNESS_MODE_MANUAL);
                Settings.System.putInt(resolver, Settings.System.SCREEN_BRIGHTNESS, (int) value);
            }
        });
    }

    protected void favApps(String packageName, AppCompatImageView imageView, int position) {
        PackageManager packageManager = context.getPackageManager();
        if(packageName != null) {
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
        AtomicBoolean isAlphabetPicked = new AtomicBoolean(false);

        dialogBinding.alphabetPicker.setMinValue(0);
        dialogBinding.alphabetPicker.setMaxValue(alphabets.length - 1);
        dialogBinding.alphabetPicker.setDisplayedValues(alphabets);
        dialogBinding.alphabetPicker.setOnValueChangedListener((picker, oldVal, newVal) -> {
            isAlphabetPicked.set(true);
            if(hintText.equals(constants.PHONE_NO)) {
                sharedPreferences.edit().putString(constants.PHONE_THUMB_LETTER_ + position, alphabets[newVal]).apply();
            } else if(hintText.equals(constants.URL_ADDRESS)) {
                sharedPreferences.edit().putString(constants.URL_THUMB_LETTER_ + position, alphabets[newVal]).apply();
            }
        });


        dialogBinding.cancel.setOnClickListener(v -> dialog.dismiss());
        dialogBinding.ok.setOnClickListener(v -> {
            String urlPhone = Objects.requireNonNull(dialogBinding.urlPhone.getText()).toString().trim();
            if(urlPhone.length() > 0 && isAlphabetPicked.get()) {
                if(hintText.equals(constants.PHONE_NO)) {
                    sharedPreferences.edit().putString(constants.PHONE_NO_ + position, urlPhone).apply();
                } else if(hintText.equals(constants.URL_ADDRESS)) {
                    sharedPreferences.edit().putString(constants.URL_NO_ + position, urlPhone).apply();
                }
                dialog.dismiss();
                bottomSheetDialogFragment.onResume();
            } else {
                dialogBinding.urlPhone.setError(context.getString(R.string.empty_text_field) + " or alphabet field is unchanged");
            }
        });
    }
}
