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

package rasel.lunar.launcher.helpers;

import static android.content.Context.CLIPBOARD_SERVICE;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.admin.DevicePolicyManager;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Insets;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.os.PowerManager;
import android.provider.Settings;
import android.util.DisplayMetrics;
import android.view.WindowInsets;
import android.view.WindowMetrics;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;

import java.io.DataOutputStream;

import rasel.lunar.launcher.R;

public class UniUtils {

    public int getScreenWidth(@NonNull Activity activity) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            WindowMetrics windowMetrics = activity.getWindowManager().getCurrentWindowMetrics();
            Insets insets = windowMetrics.getWindowInsets()
                    .getInsetsIgnoringVisibility(WindowInsets.Type.systemBars());
            return windowMetrics.getBounds().width() - insets.left - insets.right;
        } else {
            DisplayMetrics displayMetrics = new DisplayMetrics();
            activity.getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
            return displayMetrics.widthPixels;
        }
    }

    public int getScreenHeight(@NonNull Activity activity) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            WindowMetrics windowMetrics = activity.getWindowManager().getCurrentWindowMetrics();
            Insets insets = windowMetrics.getWindowInsets()
                    .getInsetsIgnoringVisibility(WindowInsets.Type.systemBars());
            return windowMetrics.getBounds().height() - insets.top - insets.bottom;
        } else {
            DisplayMetrics displayMetrics = new DisplayMetrics();
            activity.getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
            return displayMetrics.heightPixels;
        }
    }

    public void askPermissions(FragmentActivity fragmentActivity) {
        if(fragmentActivity.checkSelfPermission(Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
            fragmentActivity.requestPermissions(new String[]{Manifest.permission.CALL_PHONE}, 1);
        }

        if(!Settings.System.canWrite(fragmentActivity)) {
            fragmentActivity.startActivity(
                    (new Intent(Settings.ACTION_MANAGE_WRITE_SETTINGS))
                            .setData(Uri.parse("package:" + fragmentActivity.getPackageName()))
                            .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            );
        }
    }

    // Copies texts to clipboard
    public void copyToClipboard(FragmentActivity fragmentActivity, Context context, String copiedString) {
        ClipboardManager clipBoard = (ClipboardManager) fragmentActivity.getSystemService(CLIPBOARD_SERVICE);
        ClipData clipData = ClipData.newPlainText("", copiedString);
        clipBoard.setPrimaryClip(clipData);
        Toast.makeText(context, context.getString(R.string.copied_message), Toast.LENGTH_SHORT).show();
    }

    // Expands notification panel
    @SuppressLint("WrongConstant")
    public void expandNotificationPanel(Context context) {
        try {
            ((Class.forName("android.app.StatusBarManager"))
                    .getMethod("expandNotificationsPanel"))
                    .invoke(context.getSystemService("statusbar"));
        } catch (Exception exception) {
            exception.printStackTrace();
        }
    }

    // Lock screen using device admin
    public void lockDeviceAdmin(Context context, FragmentActivity fragmentActivity) {
        PowerManager powerManager = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
        if (powerManager.isInteractive()) {
            DevicePolicyManager policy = (DevicePolicyManager) context.getSystemService(Context.DEVICE_POLICY_SERVICE);
            try {
                policy.lockNow();
            } catch (SecurityException exception) {
                fragmentActivity.startActivity(new Intent().setComponent(new ComponentName("com.android.settings", "com.android.settings.DeviceAdminSettings")));
                exception.printStackTrace();
            }
        }
    }

    // Lock screen using accessibility service
    public void lockAccessibility(FragmentActivity fragmentActivity) {
        if ((new LockService()).isAccessibilityServiceEnabled(fragmentActivity.getApplicationContext())) {
            try {
                fragmentActivity.startService(new Intent(fragmentActivity.getApplicationContext(), LockService.class)
                        .setAction((new Constants()).ACCESSIBILITY_SERVICE_LOCK_SCREEN));
            } catch (Exception exception) {
                exception.printStackTrace();
            }
        } else {
            fragmentActivity.startActivity(new Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS));
        }
    }

    // Lock screen using root
    public void lockRoot() {
        try {
            Process process = Runtime.getRuntime().exec("su");
            DataOutputStream dataOutputStream = new DataOutputStream(process.getOutputStream());
            dataOutputStream.writeBytes("input keyevent ${KeyEvent.KEYCODE_POWER}\n");
            dataOutputStream.writeBytes("exit\n");
            dataOutputStream.flush();
            dataOutputStream.close();
            process.waitFor();
            process.destroy();
        } catch (Exception exception) {
            exception.printStackTrace();
        }
    }

    // Checks if the device is rooted
    public boolean isRooted() {
        Process process = null;
        try {
            process = Runtime.getRuntime().exec("su");
            return true;
        } catch (Exception exception) {
            exception.printStackTrace();
            return false;
        } finally {
            if(process != null) {
                try {
                    process.destroy();
                } catch (Exception exception) {
                    exception.printStackTrace();
                }
            }
        }
    }

    public void lockMethod(int lockMethodValue, Context context, FragmentActivity fragmentActivity) {
        if(lockMethodValue == 1) {
            lockAccessibility(fragmentActivity);
        } else if(lockMethodValue == 2) {
            lockDeviceAdmin(context, fragmentActivity);
        } else if(lockMethodValue == 3) {
            lockRoot();
        }
    }

    public boolean isNetworkAvailable(FragmentActivity fragmentActivity) {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) fragmentActivity.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnectedOrConnecting();
    }
}
