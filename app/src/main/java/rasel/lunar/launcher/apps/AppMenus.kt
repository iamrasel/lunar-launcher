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
import android.content.pm.ApplicationInfo
import android.content.pm.PackageManager
import android.icu.text.SimpleDateFormat
import android.os.Build
import android.os.Bundle
import android.text.method.ScrollingMovementMethod
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.pm.PackageInfoCompat
import androidx.fragment.app.FragmentActivity
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import rasel.lunar.launcher.LauncherActivity
import rasel.lunar.launcher.databinding.AppInfoDialogBinding
import rasel.lunar.launcher.databinding.AppMenuBinding
import rasel.lunar.launcher.helpers.UniUtils
import java.util.*

internal class AppMenus : BottomSheetDialogFragment() {

    private lateinit var binding: AppMenuBinding
    private lateinit var fragmentActivity: FragmentActivity
    private lateinit var packageName: String
    private lateinit var packageManager: PackageManager
    private lateinit var appInfo: ApplicationInfo

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = AppMenuBinding.inflate(inflater, container, false)

        fragmentActivity = if (isAdded) {
            requireActivity()
        } else {
            LauncherActivity()
        }

        packageName = tag.toString()
        packageManager = requireContext().packageManager

        appInfo = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            packageManager.getApplicationInfo(packageName,
                PackageManager.ApplicationInfoFlags.of(PackageManager.GET_META_DATA.toLong()))
        } else {
            @Suppress("DEPRECATION")
            packageManager.getApplicationInfo(packageName, PackageManager.GET_META_DATA)
        }

        binding.appName.text = packageManager.getApplicationLabel(appInfo)
        binding.appPackage.text = packageName
        FavouriteUtils().setPreview(requireContext(), packageName, binding.one,
            binding.two, binding.three, binding.four, binding.five, binding.six)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        FavouriteUtils().saveFavPosition(binding.favGroup, binding.one, binding.two,
            binding.three, binding.four, binding.five, binding.six, requireContext(), packageName)

        binding.appPackage.setOnClickListener {
            UniUtils().copyToClipboard(fragmentActivity, requireContext(), packageName)
        }

        binding.detailedInfo.setOnClickListener { detailedInfo() }

        binding.appStore.setOnClickListener {
            AppMenuUtils().openAppStore(requireContext(), packageName, this)
        }

        binding.appFreeform.setOnClickListener {
            AppMenuUtils().launchAsFreeform(fragmentActivity, requireContext(), packageName, this)
        }

        binding.appInfo.setOnClickListener {
            AppMenuUtils().openAppInfo(requireContext(), packageName, this)
        }

        binding.appUninstall.setOnClickListener {
            AppMenuUtils().uninstallApp(requireContext(), packageName, this)
        }
    }

    @SuppressLint("SetTextI18n")
    private fun detailedInfo() {
        val dialogBuilder = MaterialAlertDialogBuilder(fragmentActivity)
        val dialogBinding = AppInfoDialogBinding.inflate(fragmentActivity.layoutInflater)
        dialogBuilder.setView(dialogBinding.root)
        dialogBuilder.show()

        val packageInfo = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            packageManager.getPackageInfo(packageName, PackageManager.PackageInfoFlags.of(0))
        } else {
            @Suppress("DEPRECATION") packageManager.getPackageInfo(packageName, 0)
        }

        dialogBinding.appName.text = packageManager.getApplicationLabel(appInfo)
        dialogBinding.mixed.text =
            "Version: ${packageInfo.versionName} (${PackageInfoCompat.getLongVersionCode(packageInfo).toInt()})\n" +
                    "SDK: ${appInfo.minSdkVersion} ~ ${appInfo.targetSdkVersion}\n" +
                    "UID: ${appInfo.uid}\n" +
                    "First Install: ${dateTimeFormat(packageInfo.firstInstallTime)}\n" +
                    "Last Update: ${dateTimeFormat(packageInfo.lastUpdateTime)}"

        dialogBinding.permissions.text = permissionsForPackage()
    }

    private fun dateTimeFormat(long: Long) : String {
        val sdf = SimpleDateFormat.getDateTimeInstance()
        return sdf.format(Date(long))
    }

    private fun permissionsForPackage() : String {
        val packageInfo = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            packageManager.getPackageInfo(packageName, PackageManager.PackageInfoFlags.of(PackageManager.GET_PERMISSIONS.toLong()))
        } else {
            @Suppress("DEPRECATION") packageManager.getPackageInfo(packageName, PackageManager.GET_PERMISSIONS)
        }
        return if (packageInfo.requestedPermissions.isNotEmpty()) {
            val stringBuilder = StringBuilder()
            for (i in 0 until packageInfo.requestedPermissions.size) {
                stringBuilder.append("${packageInfo.requestedPermissions[i]}\n")
            }
            stringBuilder.toString()
        } else {
            ""
        }
    }
}
