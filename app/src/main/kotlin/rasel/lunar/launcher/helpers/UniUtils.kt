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

import android.annotation.SuppressLint
import android.app.admin.DevicePolicyManager
import android.content.*
import android.content.pm.PackageManager
import android.net.ConnectivityManager
import android.os.Build
import android.os.PowerManager
import android.provider.Settings
import android.util.DisplayMetrics
import android.util.TypedValue
import android.view.View
import android.view.WindowInsets
import android.widget.Toast
import androidx.appcompat.widget.LinearLayoutCompat
import androidx.biometric.BiometricManager
import androidx.biometric.BiometricManager.BIOMETRIC_SUCCESS
import androidx.biometric.BiometricPrompt
import androidx.core.view.isVisible
import com.google.android.material.imageview.ShapeableImageView
import rasel.lunar.launcher.LauncherActivity.Companion.lActivity
import rasel.lunar.launcher.R
import rasel.lunar.launcher.apps.IconPackManager.Companion.getDrawableIconForPackage
import rasel.lunar.launcher.helpers.Constants.Companion.ACCESSIBILITY_SERVICE_LOCK_SCREEN
import rasel.lunar.launcher.helpers.Constants.Companion.AUTHENTICATOR_TYPE
import rasel.lunar.launcher.helpers.Constants.Companion.DEFAULT_ICON_SIZE
import rasel.lunar.launcher.helpers.Constants.Companion.KEY_APPS_LAYOUT
import rasel.lunar.launcher.helpers.Constants.Companion.KEY_APP_NO_
import rasel.lunar.launcher.helpers.Constants.Companion.KEY_ICON_SIZE
import rasel.lunar.launcher.helpers.Constants.Companion.MAX_FAVORITE_APPS
import rasel.lunar.launcher.helpers.Constants.Companion.PREFS_FAVORITE_APPS
import rasel.lunar.launcher.helpers.Constants.Companion.PREFS_SETTINGS
import java.io.DataOutputStream


internal class UniUtils {

    companion object {

        /* get display width */
        val screenWidth: Int get() {
            return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                val windowMetrics = lActivity!!.windowManager.currentWindowMetrics
                val insets = windowMetrics.windowInsets
                    .getInsetsIgnoringVisibility(WindowInsets.Type.systemBars())
                windowMetrics.bounds.width() - insets.left - insets.right
            } else {
                val displayMetrics = DisplayMetrics()
                @Suppress("DEPRECATION") lActivity!!.windowManager.defaultDisplay.getMetrics(displayMetrics)
                displayMetrics.widthPixels
            }
        }

        /* get display height */
        val screenHeight: Int get() {
            return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                val windowMetrics = lActivity!!.windowManager.currentWindowMetrics
                val insets = windowMetrics.windowInsets
                    .getInsetsIgnoringVisibility(WindowInsets.Type.systemBars())
                windowMetrics.bounds.height() - insets.top - insets.bottom
            } else {
                val displayMetrics = DisplayMetrics()
                @Suppress("DEPRECATION") lActivity!!.windowManager.defaultDisplay.getMetrics(displayMetrics)
                displayMetrics.heightPixels
            }
        }

        /* copy texts to clipboard */
        fun copyToClipboard(context: Context, copiedString: String?) {
            val clipBoard =
                lActivity!!.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
            clipBoard.setPrimaryClip(ClipData.newPlainText("", copiedString))
            Toast.makeText(context, context.getString(R.string.copied_message), Toast.LENGTH_SHORT).show()
        }

        /* expand notification panel */
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

        /* lock using preferred method */
        fun lockMethod(lockMethodValue: Int, context: Context, linearLayoutCompat: LinearLayoutCompat) {
            when (lockMethodValue) {
                0 -> populateFavApps(context, linearLayoutCompat)
                1 -> lockAccessibility()
                2 -> lockDeviceAdmin(context)
                3 -> lockRoot()
            }
        }

        /* check if the device is rooted */
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

        /* check if the device is connected to the internet */
        val isNetworkAvailable: Boolean get() {
            val connectivityManager =
                lActivity!!.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
            @Suppress("DEPRECATION") val activeNetworkInfo = connectivityManager.activeNetworkInfo
            @Suppress("DEPRECATION") return activeNetworkInfo != null && activeNetworkInfo.isConnectedOrConnecting
        }

        /* check if authenticator available */
        fun canAuthenticate(context: Context): Boolean {
            val biometricManager = BiometricManager.from(context)
            return biometricManager.canAuthenticate(AUTHENTICATOR_TYPE) == BIOMETRIC_SUCCESS
        }

        /* show device authenticator */
        fun biometricPromptInfo(title: String): BiometricPrompt.PromptInfo {
            return BiometricPrompt.PromptInfo.Builder()
                .setTitle(title)
                .setSubtitle(lActivity!!.getString(R.string.authentication_subtitle))
                .setConfirmationRequired(true)
                .setAllowedAuthenticators(AUTHENTICATOR_TYPE)
                .build()
        }

        /* get color red id from attribute */
        fun getColorResId(context: Context, colorAttr: Int) : Int {
            val typedValue = TypedValue()
            context.theme.resolveAttribute(colorAttr, typedValue, true)
            return typedValue.resourceId
        }

        /* convert dp value to px */
        fun dpToPx(context: Context, id: Int) : Int = (context.resources.getDimension(id) * context.resources.displayMetrics.density).toInt()

        /* lock screen using device admin */
        private fun lockDeviceAdmin(context: Context) {
            val powerManager = context.getSystemService(Context.POWER_SERVICE) as PowerManager
            if (powerManager.isInteractive) {
                val policy =
                    context.getSystemService(Context.DEVICE_POLICY_SERVICE) as DevicePolicyManager
                try {
                    policy.lockNow()
                } catch (exception: SecurityException) {
                    /* open device admin manager screen */
                    lActivity!!.startActivity(
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

        /* favorite apps */
        private fun populateFavApps(context: Context, linearLayoutCompat: LinearLayoutCompat) {
            val prefsFavApps = context.getSharedPreferences(PREFS_FAVORITE_APPS, 0)
            val useIconPack = context.getSharedPreferences(PREFS_SETTINGS, 0).getInt(KEY_APPS_LAYOUT, 0) != 0
            if (linearLayoutCompat.isVisible || prefsFavApps.all.toString().length < 3) {
                linearLayoutCompat.visibility = View.GONE
            } else {
                linearLayoutCompat.removeAllViews()
                linearLayoutCompat.visibility = View.VISIBLE
                val iconSize = context.getSharedPreferences(PREFS_SETTINGS, 0).getInt(KEY_ICON_SIZE, DEFAULT_ICON_SIZE)

                for (position in 1..MAX_FAVORITE_APPS) {
                    val packageName = prefsFavApps.getString(KEY_APP_NO_ + position.toString(), "").toString()
                    /* package name is not empty for a specific position */
                    if (packageName.isNotEmpty()) {
                        try {
                            ShapeableImageView(context).apply {
                                layoutParams = LinearLayoutCompat.LayoutParams(
                                    (iconSize * resources.displayMetrics.density).toInt(),
                                    (iconSize * resources.displayMetrics.density).toInt(), 1F)
                            }.let { sImageView ->
                                context.packageManager.getApplicationIcon(packageName).let { defaultIcon ->
                                    sImageView.setImageDrawable(
                                        if (context.getSharedPreferences(PREFS_SETTINGS, 0).getInt(KEY_APPS_LAYOUT, 0) != 0)
                                            getDrawableIconForPackage(packageName, defaultIcon)
                                        else defaultIcon
                                    )
                                }
                                sImageView.setOnClickListener {
                                    context.startActivity(context.packageManager.getLaunchIntentForPackage(packageName))
                                }
                                linearLayoutCompat.addView(sImageView)
                            }
                        } catch (nameNotFoundException: PackageManager.NameNotFoundException) {
                            context.getSharedPreferences(PREFS_FAVORITE_APPS, 0)
                                .edit().remove(KEY_APP_NO_ + position).apply()
                        }
                    }
                }
            }
        }

        /* lock screen using accessibility service */
        private fun lockAccessibility() {
            if (LockService().isAccessibilityServiceEnabled(lActivity!!.applicationContext)) {
                try {
                    lActivity!!.startService(
                        Intent(lActivity!!.applicationContext, LockService::class.java)
                            .setAction(ACCESSIBILITY_SERVICE_LOCK_SCREEN)
                    )
                } catch (exception: Exception) {
                    exception.printStackTrace()
                }
            } else {
                /* open accessibility service screen */
                lActivity!!.startActivity(Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS))
            }
        }

        /* lock screen using root */
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

    }

}
