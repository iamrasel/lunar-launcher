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
import android.content.ComponentName
import android.content.Intent
import android.content.pm.ApplicationInfo
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.core.content.pm.PackageInfoCompat
import androidx.fragment.app.FragmentActivity
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import rasel.lunar.launcher.LauncherActivity
import rasel.lunar.launcher.R
import rasel.lunar.launcher.databinding.ActivityBrowserDialogBinding
import rasel.lunar.launcher.databinding.AppInfoDialogBinding
import rasel.lunar.launcher.databinding.AppMenuBinding
import rasel.lunar.launcher.helpers.UniUtils
import java.util.ArrayList


internal class AppMenus : BottomSheetDialogFragment() {

    private lateinit var binding: AppMenuBinding
    private lateinit var fragmentActivity: FragmentActivity
    private lateinit var packageName: String
    private lateinit var packageManager: PackageManager
    private lateinit var appInfo: ApplicationInfo
    private lateinit var appMenuUtils: AppMenuUtils

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = AppMenuBinding.inflate(inflater, container, false)

        fragmentActivity = if (isAdded) {
            requireActivity()
        } else {
            LauncherActivity()
        }

        /* get package name from fragment's tag */
        packageName = tag.toString()

        packageManager = requireContext().packageManager
        appMenuUtils = AppMenuUtils(this, fragmentActivity, requireContext(), packageManager, packageName)

        /* get application info */
        appInfo = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            packageManager.getApplicationInfo(packageName,
                PackageManager.ApplicationInfoFlags.of(PackageManager.GET_META_DATA.toLong()))
        } else {
            @Suppress("DEPRECATION")
            packageManager.getApplicationInfo(packageName, PackageManager.GET_META_DATA)
        }

        /* set application name and package name */
        binding.appName.text = packageManager.getApplicationLabel(appInfo)
        binding.appPackage.text = packageName

        /* favorite apps */
        FavouriteUtils().previewAndClicks(requireContext(), packageName, binding.favGroup)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        (requireDialog() as BottomSheetDialog).dismissWithAnimation = true

        /* copy package name */
        binding.appPackage.setOnClickListener {
            UniUtils().copyToClipboard(fragmentActivity, requireContext(), packageName)
        }

        binding.detailedInfo.setOnClickListener { detailedInfo() }
        binding.activityBrowser.setOnClickListener { activityBrowser() }
        binding.appStore.setOnClickListener { appMenuUtils.appStore() }
        binding.appFreeform.setOnClickListener { appMenuUtils.freeform() }
        binding.appInfo.setOnClickListener { appMenuUtils.appInfo() }
        binding.appUninstall.setOnClickListener { appMenuUtils.uninstall() }
    }

    /* detailed info dialog */
    @SuppressLint("SetTextI18n")
    private fun detailedInfo() {
        val dialogBuilder = MaterialAlertDialogBuilder(fragmentActivity)
        val dialogBinding = AppInfoDialogBinding.inflate(fragmentActivity.layoutInflater)
        dialogBuilder.setView(dialogBinding.root)
        dialogBuilder.show()

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
            "${resources.getString(R.string.first_install)}: ${appMenuUtils.dateTimeFormat(packageInfo.firstInstallTime)}\n" +
            "${resources.getString(R.string.last_update)}: ${appMenuUtils.dateTimeFormat(packageInfo.lastUpdateTime)}"

        /* show permissions */
        dialogBinding.permissions.text = appMenuUtils.permissionsForPackage
    }

    /* activity browser dialog */
    private fun activityBrowser() {
        val dialogBuilder = MaterialAlertDialogBuilder(fragmentActivity)
        val dialogBinding = ActivityBrowserDialogBinding.inflate(fragmentActivity.layoutInflater)
        dialogBuilder.setView(dialogBinding.root)
        val dialog = dialogBuilder.create()
        dialog.show()

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
            ArrayAdapter(requireContext(), R.layout.list_item, R.id.item_text, ArrayList())
        if (activityInfo.activities.isNotEmpty()) {
            for (i in 0 until activityInfo.activities.size) {
                val activity = activityInfo.activities[i].toString().split(" ").toTypedArray()
                activityAdapter.add(activity[1].replace("}", ""))
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
                dialog.dismiss()
            }
    }

}
