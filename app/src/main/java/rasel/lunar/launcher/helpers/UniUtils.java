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

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.admin.DevicePolicyManager;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.graphics.Insets;
import android.os.Build;
import android.os.PowerManager;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.WindowInsets;
import android.view.WindowMetrics;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;

import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.textview.MaterialTextView;

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

    public void exceptionViewer(FragmentActivity fragmentActivity, String exceptionText) {
        BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(fragmentActivity);
        View view = fragmentActivity.getLayoutInflater().inflate(R.layout.exception_viewer, null);
        bottomSheetDialog.setContentView(view);
        bottomSheetDialog.show();

        MaterialTextView exceptionViewer = view.findViewById(R.id.exception_text);
        exceptionViewer.setText(exceptionText);

        view.findViewById(R.id.copy).setOnClickListener(v ->
                copyToClipboard(fragmentActivity, fragmentActivity.getApplicationContext(), exceptionText));
    }

    // Copies texts to clipboard
    public void copyToClipboard(FragmentActivity fragmentActivity, Context context, String copiedString) {
        ClipboardManager clipBoard = (ClipboardManager) fragmentActivity.getSystemService(CLIPBOARD_SERVICE);
        ClipData clipData = ClipData.newPlainText(null, copiedString);
        clipBoard.setPrimaryClip(clipData);
        Toast.makeText(context, context.getString(R.string.copied_message), Toast.LENGTH_SHORT).show();
    }

    // Expands notification panel
    @SuppressLint("WrongConstant")
    public void expandNotificationPanel(Context context, FragmentActivity fragmentActivity) {
        try {
            ((Class.forName("android.app.StatusBarManager"))
                    .getMethod("expandNotificationsPanel"))
                    .invoke(context.getSystemService("statusbar"));
        } catch (Exception exception) {
            exceptionViewer(fragmentActivity, exception.getMessage());
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
}
