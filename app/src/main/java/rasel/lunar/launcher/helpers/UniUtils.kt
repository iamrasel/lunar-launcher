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

package rasel.lunar.launcher.helpers

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.app.admin.DevicePolicyManager
import android.content.*
import android.content.pm.PackageManager
import android.net.ConnectivityManager
import android.net.Uri
import android.os.Build
import android.os.PowerManager
import android.provider.Settings
import android.util.DisplayMetrics
import android.view.WindowInsets
import android.widget.Toast
import androidx.fragment.app.FragmentActivity
import rasel.lunar.launcher.R
import java.io.DataOutputStream

internal class UniUtils {

    fun getScreenWidth(activity: Activity): Int {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            val windowMetrics = activity.windowManager.currentWindowMetrics
            val insets = windowMetrics.windowInsets
                .getInsetsIgnoringVisibility(WindowInsets.Type.systemBars())
            windowMetrics.bounds.width() - insets.left - insets.right
        } else {
            val displayMetrics = DisplayMetrics()
            @Suppress("DEPRECATION") activity.windowManager.defaultDisplay.getMetrics(displayMetrics)
            displayMetrics.widthPixels
        }
    }

    fun getScreenHeight(activity: Activity): Int {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            val windowMetrics = activity.windowManager.currentWindowMetrics
            val insets = windowMetrics.windowInsets
                .getInsetsIgnoringVisibility(WindowInsets.Type.systemBars())
            windowMetrics.bounds.height() - insets.top - insets.bottom
        } else {
            val displayMetrics = DisplayMetrics()
            @Suppress("DEPRECATION") activity.windowManager.defaultDisplay.getMetrics(displayMetrics)
            displayMetrics.heightPixels
        }
    }

    fun askPermissions(fragmentActivity: FragmentActivity) {
        if (fragmentActivity.checkSelfPermission(Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
            fragmentActivity.requestPermissions(arrayOf(Manifest.permission.CALL_PHONE), 1)
        }
        if (!Settings.System.canWrite(fragmentActivity)) {
            fragmentActivity.startActivity(
                Intent(Settings.ACTION_MANAGE_WRITE_SETTINGS)
                    .setData(Uri.parse("package:" + fragmentActivity.packageName))
                    .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            )
        }
    }

    // Copies texts to clipboard
    fun copyToClipboard(fragmentActivity: FragmentActivity, context: Context, copiedString: String?) {
        val clipBoard =
            fragmentActivity.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        val clipData = ClipData.newPlainText("", copiedString)
        clipBoard.setPrimaryClip(clipData)
        Toast.makeText(context, context.getString(R.string.copied_message), Toast.LENGTH_SHORT).show()
    }

    // Expands notification panel
    @SuppressLint("WrongConstant")
    fun expandNotificationPanel(context: Context) {
        try {
            Class.forName("android.app.StatusBarManager")
                .getMethod("expandNotificationsPanel")
                .invoke(context.getSystemService("statusbar"))
        } catch (exception: Exception) {
            exception.printStackTrace()
        }
    }

    // Lock screen using device admin
    private fun lockDeviceAdmin(context: Context, fragmentActivity: FragmentActivity) {
        val powerManager = context.getSystemService(Context.POWER_SERVICE) as PowerManager
        if (powerManager.isInteractive) {
            val policy =
                context.getSystemService(Context.DEVICE_POLICY_SERVICE) as DevicePolicyManager
            try {
                policy.lockNow()
            } catch (exception: SecurityException) {
                fragmentActivity.startActivity(
                    Intent().setComponent(
                        ComponentName(
                            "com.android.settings",
                            "com.android.settings.DeviceAdminSettings"
                        )
                    )
                )
                exception.printStackTrace()
            }
        }
    }

    // Lock screen using accessibility service
    private fun lockAccessibility(fragmentActivity: FragmentActivity) {
        if (LockService().isAccessibilityServiceEnabled(fragmentActivity.applicationContext)) {
            try {
                fragmentActivity.startService(
                    Intent(fragmentActivity.applicationContext, LockService::class.java)
                        .setAction(Constants().ACCESSIBILITY_SERVICE_LOCK_SCREEN)
                )
            } catch (exception: Exception) {
                exception.printStackTrace()
            }
        } else {
            fragmentActivity.startActivity(Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS))
        }
    }

    // Lock screen using root
    private fun lockRoot() {
        try {
            val process = Runtime.getRuntime().exec("su")
            val dataOutputStream = DataOutputStream(process.outputStream)
            dataOutputStream.writeBytes("input keyevent \${KeyEvent.KEYCODE_POWER}\n")
            dataOutputStream.writeBytes("exit\n")
            dataOutputStream.flush()
            dataOutputStream.close()
            process.waitFor()
            process.destroy()
        } catch (exception: Exception) {
            exception.printStackTrace()
        }
    }

    // Checks if the device is rooted
    val isRooted: Boolean get() {
        var process: Process? = null
        return try {
            process = Runtime.getRuntime().exec("su")
            true
        } catch (exception: Exception) {
            exception.printStackTrace()
            false
        } finally {
            if (process != null) {
                try {
                    process.destroy()
                } catch (exception: Exception) {
                    exception.printStackTrace()
                }
            }
        }
    }

    fun lockMethod(lockMethodValue: Int, context: Context, fragmentActivity: FragmentActivity) {
        when (lockMethodValue) {
            1 -> lockAccessibility(fragmentActivity)
            2 -> lockDeviceAdmin(context, fragmentActivity)
            3 -> lockRoot()
        }
    }

    fun isNetworkAvailable(fragmentActivity: FragmentActivity): Boolean {
        val connectivityManager =
            fragmentActivity.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        @Suppress("DEPRECATION") val activeNetworkInfo = connectivityManager.activeNetworkInfo
        @Suppress("DEPRECATION") return activeNetworkInfo != null && activeNetworkInfo.isConnectedOrConnecting
    }
}