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

import static android.content.Intent.FLAG_ACTIVITY_CLEAR_TOP;
import static android.content.Intent.FLAG_ACTIVITY_NEW_TASK;

import android.app.ActivityOptions;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Rect;
import android.net.Uri;
import android.provider.Settings;
import android.widget.Toast;

import androidx.fragment.app.FragmentActivity;

import java.lang.reflect.Method;

import rasel.lunar.launcher.R;
import rasel.lunar.launcher.helpers.UniUtils;

public class AppMenuUtils {

    // Gets app name from it's package name
    protected String getAppName(PackageManager packageManager, String packageName) throws PackageManager.NameNotFoundException {
        return (String) packageManager.getApplicationLabel(packageManager.getApplicationInfo(packageName, PackageManager.GET_META_DATA));
    }

    // Launches app in freeform window mode
    protected void launchAsFreeform(FragmentActivity fragmentActivity, Context context, UniUtils uniUtils, String packageName, AppMenus appMenus) {
        Intent freeformIntent = context.getPackageManager().getLaunchIntentForPackage(packageName);
        freeformIntent.addFlags(Intent.FLAG_ACTIVITY_LAUNCH_ADJACENT |
                Intent.FLAG_ACTIVITY_NEW_TASK |
                Intent.FLAG_ACTIVITY_MULTIPLE_TASK);

        Rect rect = new Rect(0, (uniUtils.getScreenHeight(fragmentActivity)/2), uniUtils.getScreenWidth(fragmentActivity), uniUtils.getScreenHeight(fragmentActivity));
        ActivityOptions activityOptions = getActivityOptions(fragmentActivity);
        activityOptions = activityOptions.setLaunchBounds(rect);

        context.startActivity(freeformIntent, activityOptions.toBundle());
        appMenus.dismiss();
    }

    // Opens app info screen
    protected void openAppInfo(Context context, String packageName, AppMenus appMenus) {
        Intent infoIntent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        infoIntent.setData(Uri.parse("package:" + packageName));
        infoIntent.addFlags(FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(infoIntent);
        appMenus.dismiss();
    }

    // Tries to open app's page in app market/store
    protected void openAppStore(Context context, String packageName, AppMenus appMenus) {
        try {
            Intent storeIntent = new Intent(Intent.ACTION_VIEW);
            storeIntent.setData(Uri.parse("market://details?id=" + packageName));
            storeIntent.addFlags(FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(storeIntent);
        } catch (ActivityNotFoundException activityNotFoundException) {
            Toast.makeText(context, context.getString(R.string.null_app_store_message),Toast.LENGTH_SHORT).show();
            activityNotFoundException.printStackTrace();
        }
        appMenus.dismiss();
    }

    // Deletes the app from device
    protected void uninstallApp(Context context, String packageName, AppMenus appMenus) {
        Intent uninstallIntent = new Intent(Intent.ACTION_DELETE);
        uninstallIntent.setData(Uri.parse("package:" + packageName));
        uninstallIntent.addFlags(FLAG_ACTIVITY_CLEAR_TOP | FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(uninstallIntent);
        appMenus.dismiss();
    }

    private ActivityOptions getActivityOptions(FragmentActivity fragmentActivity) {
        ActivityOptions activityOptions = ActivityOptions.makeBasic();
        int freeform_stackId = 5;
        try {
            Method method = ActivityOptions.class.getMethod("setLaunchWindowingMode", int.class);
            method.invoke(activityOptions, freeform_stackId);
        } catch (Exception exception) {
            (new UniUtils()).exceptionViewer(fragmentActivity, exception.getMessage());
            exception.printStackTrace();
        }
        return activityOptions;
    }
}
