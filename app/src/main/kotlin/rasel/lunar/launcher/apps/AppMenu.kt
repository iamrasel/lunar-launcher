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

package rasel.lunar.launcher.apps

import android.annotation.SuppressLint
import android.app.ActivityOptions
import android.content.ActivityNotFoundException
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.pm.ApplicationInfo
import android.content.pm.PackageManager
import android.content.res.ColorStateList
import android.graphics.Rect
import android.icu.text.SimpleDateFormat
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.widget.LinearLayoutCompat
import androidx.core.content.FileProvider
import androidx.core.content.pm.PackageInfoCompat
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.button.MaterialButton
import com.google.android.material.button.MaterialButtonToggleGroup
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import rasel.lunar.launcher.LauncherActivity.Companion.lActivity
import rasel.lunar.launcher.R
import rasel.lunar.launcher.apps.AppDrawer.Companion.appNamesPrefs
import rasel.lunar.launcher.databinding.ActivityBrowserDialogBinding
import rasel.lunar.launcher.databinding.AppInfoDialogBinding
import rasel.lunar.launcher.databinding.AppMenuBinding
import rasel.lunar.launcher.helpers.Constants.Companion.KEY_APP_NO_
import rasel.lunar.launcher.helpers.Constants.Companion.MAX_FAVORITE_APPS
import rasel.lunar.launcher.helpers.Constants.Companion.PREFS_FAVORITE_APPS
import rasel.lunar.launcher.helpers.UniUtils.Companion.copyToClipboard
import rasel.lunar.launcher.helpers.UniUtils.Companion.screenHeight
import rasel.lunar.launcher.helpers.UniUtils.Companion.screenWidth
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.io.IOException
import java.util.*


internal class AppMenu : BottomSheetDialogFragment() {

    private lateinit var binding: AppMenuBinding
    private lateinit var packageName: String
    private lateinit var packageManager: PackageManager
    private lateinit var appInfo: ApplicationInfo
    private lateinit var defAppName: String

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = AppMenuBinding.inflate(inflater, container, false)

        /* get package name from fragment's tag */
        packageName = tag.toString()
        packageManager = requireContext().packageManager

        /* get application info */
        appInfo = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            packageManager.getApplicationInfo(packageName,
                PackageManager.ApplicationInfoFlags.of(PackageManager.GET_META_DATA.toLong()))
        } else {
            @Suppress("DEPRECATION")
            packageManager.getApplicationInfo(packageName, PackageManager.GET_META_DATA)
        }

        /* get default app name */
        val resolve = packageManager.resolveActivity(Intent(Intent.ACTION_MAIN).addCategory(Intent.CATEGORY_LAUNCHER).setPackage(packageName), 0)
        defAppName = resolve?.loadLabel(packageManager).toString()

        /* set application name and package name */
        binding.appName.setText(appNamesPrefs?.getString(packageName, defAppName))
        binding.appName.hint = defAppName
        binding.appPackage.text = packageName
        /* favorite apps */
        favoriteApps()

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        (requireDialog() as BottomSheetDialog).dismissWithAnimation = true

        /* copy package name */
        binding.appPackage.setOnClickListener {
            copyToClipboard(requireContext(), packageName)
        }

        appName()
        binding.detailedInfo.setOnClickListener { detailedInfo() }
        binding.activityBrowser.setOnClickListener { activityBrowser() }
        binding.appStore.setOnClickListener { appStore() }
        binding.appFreeform.setOnClickListener { freeform() }
        binding.appInfo.setOnClickListener { appInfo() }
        binding.appShare.setOnClickListener { share() }
        binding.appUninstall.setOnClickListener { uninstall() }
    }

    /* manage initial preview and clicks for favorite apps */
    @SuppressLint("PrivateResource")
    private fun favoriteApps() {
        val sharedPreferences = requireContext().getSharedPreferences(PREFS_FAVORITE_APPS, 0)
        val enabledStroke =
            ColorStateList.valueOf(requireContext().getColor(com.google.android.material.R.color.material_on_surface_stroke))
        val disabledStroke =
            ColorStateList.valueOf(requireContext().getColor(com.google.android.material.R.color.m3_chip_stroke_color))

        for (position in 1..MAX_FAVORITE_APPS) {
            val button = outlinedButton
            val savedPackageName = sharedPreferences.getString(KEY_APP_NO_ + position, "")

            /* set previews */
            if (packageName == savedPackageName) button.isChecked = true
            if (savedPackageName?.isNotEmpty() == true) button.strokeColor = enabledStroke

            try {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU)
                    packageManager.getPackageInfo(savedPackageName!!, PackageManager.PackageInfoFlags.of(0))
                else
                    @Suppress("DEPRECATION") packageManager.getPackageInfo(savedPackageName!!, 0)
            } catch (e: PackageManager.NameNotFoundException) {
                requireContext().getSharedPreferences(PREFS_FAVORITE_APPS, 0)
                    .edit().remove(KEY_APP_NO_ + position).apply()
                button.strokeColor = disabledStroke
                e.printStackTrace()
            }

            /* listen on clicks */
            binding.favGroup.addOnButtonCheckedListener { _: MaterialButtonToggleGroup?,
                                                           checkedId: Int, isChecked: Boolean ->
                try {
                    if (checkedId == button.id) {
                        if (isChecked) {
                            requireContext().getSharedPreferences(PREFS_FAVORITE_APPS, 0)
                                .edit().putString(KEY_APP_NO_ + position, packageName).apply()
                            button.strokeColor = enabledStroke
                        } else {
                            requireContext().getSharedPreferences(PREFS_FAVORITE_APPS, 0)
                                .edit().remove(KEY_APP_NO_ + position).apply()
                            button.strokeColor = disabledStroke
                        }
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }
    }

    private fun appName()
    {
        binding.appName.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus) {
                binding.appName.minWidth = resources.getDimensionPixelOffset(R.dimen.twoSeventySix)
            }
            else {
                val imm = requireContext().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                imm.hideSoftInputFromWindow(binding.appName.windowToken, 0)

                binding.appName.minWidth = resources.getDimensionPixelOffset(R.dimen.zero)
                binding.appName.apply {
                    if (text!!.isBlank()) setText(defAppName)
                    else setText(text!!.trim())

                    if (text.toString() == defAppName) appNamesPrefs?.edit()!!.remove(packageName).apply()
                    else appNamesPrefs?.edit()!!.putString(packageName, text.toString()).apply()

                    (requireParentFragment() as AppDrawer).fetchApps()
                }
            }
        }

        binding.appName.setOnKeyListener { _, keyCode, event ->
            if (event.action == KeyEvent.ACTION_DOWN)
            {
                if (keyCode == KeyEvent.KEYCODE_ENTER || keyCode == KeyEvent.KEYCODE_BACK) {
                    binding.appName.clearFocus()
                    return@setOnKeyListener true
                }
            }
            false
        }
    }

    /* detailed info dialog */
    @SuppressLint("SetTextI18n")
    private fun detailedInfo() {
        val dialogBinding = AppInfoDialogBinding.inflate(lActivity!!.layoutInflater)
        MaterialAlertDialogBuilder(lActivity!!)
            .setView(dialogBinding.root)
            .setPositiveButton(android.R.string.cancel, null)
            .show()

        /* show app name */
        dialogBinding.appName.text = packageManager.getApplicationLabel(appInfo)

        /* get package info */
        val packageInfo = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            packageManager.getPackageInfo(packageName, PackageManager.PackageInfoFlags.of(0))
        } else {
            @Suppress("DEPRECATION") packageManager.getPackageInfo(packageName, 0)
        }

        /* show infos */
        dialogBinding.mixed.text =
            "${resources.getString(R.string.version)}: ${packageInfo.versionName} (${PackageInfoCompat.getLongVersionCode(packageInfo).toInt()})\n" +
            "${resources.getString(R.string.sdk)}: ${appInfo.minSdkVersion} ~ ${appInfo.targetSdkVersion}\n" +
            "${resources.getString(R.string.uid)}: ${appInfo.uid}\n" +
            "${resources.getString(R.string.first_install)}: ${dateTimeFormat(packageInfo.firstInstallTime)}\n" +
            "${resources.getString(R.string.last_update)}: ${dateTimeFormat(packageInfo.lastUpdateTime)}"

        /* show permissions */
        dialogBinding.permissions.text = permissionsList
    }

    /* activity browser dialog */
    private fun activityBrowser() {
        val dialogBinding = ActivityBrowserDialogBinding.inflate(lActivity!!.layoutInflater)
        val dialogBuilder = MaterialAlertDialogBuilder(lActivity!!)
            .setView(dialogBinding.root)
            .setPositiveButton(android.R.string.cancel, null)
            .show()

        /* show app name */
        dialogBinding.appName.text = packageManager.getApplicationLabel(appInfo)

        /* get activity info */
        val activityInfo = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            packageManager.getPackageInfo(
                packageName, PackageManager.PackageInfoFlags.of(PackageManager.GET_ACTIVITIES.toLong())
            )
        } else {
            @Suppress("DEPRECATION") packageManager.getPackageInfo(packageName, PackageManager.GET_ACTIVITIES)
        }

        /* show activity list */
        val activityAdapter: ArrayAdapter<String> =
            ArrayAdapter(requireContext(), R.layout.list_item, R.id.itemText, ArrayList())
        if (activityInfo.activities.isNotEmpty()) {
            for (activity in activityInfo.activities) {
                activityAdapter.add(
                    activity.toString().split(" ").toTypedArray()[1].replace("}", "")
                )
            }
            dialogBinding.activityList.adapter = activityAdapter
        }

        /* listen item clicks */
        dialogBinding.activityList.onItemClickListener =
            AdapterView.OnItemClickListener { _: AdapterView<*>?, _: View?, i: Int, _: Long ->
                try {
                    /* open activity */
                    val intent = Intent()
                    intent.component = ComponentName(packageName, activityAdapter.getItem(i).toString())
                    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
                    requireContext().startActivity(intent)
                } catch (exception: Exception) {
                    /* couldn't open activity */
                    exception.printStackTrace()
                    val exceptionShort = (exception.toString().split(": ").toTypedArray())[0]
                    Toast.makeText(requireContext(),
                        "${resources.getString(R.string.unable_to_launch)} -\n$exceptionShort", Toast.LENGTH_LONG).show()
                }
                dialogBuilder.dismiss()
            }
    }

    /* open app's page in app store/market */
    private fun appStore() {
        try {
            val storeIntent = Intent(Intent.ACTION_VIEW)
            storeIntent.data = Uri.parse("market://details?id=$packageName")
            storeIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            requireContext().startActivity(storeIntent)
        } catch (activityNotFoundException: ActivityNotFoundException) {
            /* no app store found exception */
            Toast.makeText(requireContext(), requireContext().getString(R.string.null_app_store_message),
                Toast.LENGTH_SHORT).show()
            activityNotFoundException.printStackTrace()
        }
        this.dismiss()
    }

    /* launch app as a freeform window */
    private fun freeform() {
        val freeformIntent = requireContext().packageManager.getLaunchIntentForPackage(packageName)
        freeformIntent!!.addFlags(Intent.FLAG_ACTIVITY_LAUNCH_ADJACENT or
                Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_MULTIPLE_TASK)
        val rect = Rect(0, screenHeight / 2, screenWidth, screenHeight)
        var activityOptions = activityOptions
        activityOptions = activityOptions.setLaunchBounds(rect)
        requireContext().startActivity(freeformIntent, activityOptions.toBundle())
        this.dismiss()
    }

    /* open android's app info screen */
    private fun appInfo() {
        val infoIntent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
        infoIntent.data = Uri.parse("package:$packageName")
        infoIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        requireContext().startActivity(infoIntent)
        this.dismiss()
    }

    private fun share() {
        try {
            // Create a temporary file to copy the APK
            val apkLabel = packageManager.getApplicationLabel(appInfo).toString().lowercase().replace(" ", "_")
            val tempApkFile = File(requireContext().externalCacheDir, "$apkLabel.apk")

            // Copy the APK file
            FileInputStream(File(appInfo.sourceDir)).use { `in` ->
                FileOutputStream(tempApkFile).use { out ->
                    val buffer = ByteArray(1024)
                    var length: Int
                    while (`in`.read(buffer).also { length = it } > 0) {
                        out.write(buffer, 0, length)
                    }
                }
            }

            // Generate a content URI using FileProvider
            val contentUri =
                FileProvider.getUriForFile(requireContext(), "${requireContext().packageName}.fileprovider", tempApkFile)

            //requireContext().grantUriPermission(receivers.package.name, contentUri, Intent.FLAG_GRANT_READ_URI_PERMISSION)

            // Create a Share Intent
            Intent(Intent.ACTION_SEND).apply {
                type = "application/vnd.android.package-archive"
                putExtra(Intent.EXTRA_STREAM, contentUri)
                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            }.let {
                // Start the chooser activity
                startActivity(Intent.createChooser(it, getString(R.string.share_apk_message)))
            }
        }
        catch (e: PackageManager.NameNotFoundException) { e.printStackTrace() }
        catch (e: IOException) { e.printStackTrace() }
        this.dismiss()
    }

    /* uninstall the app */
    private fun uninstall() {
        val uninstallIntent = Intent(Intent.ACTION_DELETE)
        uninstallIntent.data = Uri.parse("package:$packageName")
        uninstallIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
        requireContext().startActivity(uninstallIntent)
        this.dismiss()
    }

    /* create and add an outlined button to the toggle group */
    private val outlinedButton: MaterialButton get() {
        val style = com.google.android.material.R.attr.materialButtonOutlinedStyle
        val button = MaterialButton(requireContext(), null, style)
        button.layoutParams = LinearLayoutCompat.LayoutParams(
            LinearLayoutCompat.LayoutParams.WRAP_CONTENT,
            LinearLayoutCompat.LayoutParams.WRAP_CONTENT, 1F
        )
        binding.favGroup.addView(button)
        return button
    }

    /* long value to local date-time format */
    private fun dateTimeFormat(long: Long) : String = SimpleDateFormat.getDateTimeInstance().format(Date(long))

    /* get and arrange all the permissions for an application */
    private val permissionsList : String get() {
        val packageInfo = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            packageManager.getPackageInfo(packageName, PackageManager.PackageInfoFlags.of(PackageManager.GET_PERMISSIONS.toLong()))
        } else {
            @Suppress("DEPRECATION") packageManager.getPackageInfo(packageName, PackageManager.GET_PERMISSIONS)
        }

        return if (packageInfo.requestedPermissions.isNotEmpty()) {
            val stringBuilder = StringBuilder()
            packageInfo.requestedPermissions.indices.forEach {  i: Int ->
                if (i != packageInfo.requestedPermissions.size - 1)
                    stringBuilder.append("${packageInfo.requestedPermissions[i]}\n\n")
                /* don't add any new line after the last entry */
                else
                    stringBuilder.append(packageInfo.requestedPermissions[i])
            }
            stringBuilder.toString()
        } else {
            ""
        }
    }

    /* get activity options for launching app in freeform mode */
    private val activityOptions: ActivityOptions get() {
        val activityOptions = ActivityOptions.makeBasic()
        try {
            val method =
                ActivityOptions::class.java.getMethod("setLaunchWindowingMode", Int::class.javaPrimitiveType)
            method.invoke(activityOptions, 5)
        } catch (exception: Exception) {
            exception.printStackTrace()
        }
        return activityOptions
    }

}
