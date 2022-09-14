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

import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import rasel.lunar.launcher.helpers.UniUtils
import android.view.LayoutInflater
import android.view.ViewGroup
import android.os.Bundle
import android.content.pm.PackageManager
import android.view.View
import rasel.lunar.launcher.databinding.AppMenuBinding

internal class AppMenus : BottomSheetDialogFragment() {

    private lateinit var binding: AppMenuBinding
    private lateinit var packageName: String

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = AppMenuBinding.inflate(inflater, container, false)
        packageName = tag.toString()

        try {
            binding.appName.text = AppMenuUtils().getAppName(requireContext().packageManager, packageName)
        } catch (nameNotFoundException: PackageManager.NameNotFoundException) {
            nameNotFoundException.printStackTrace()
        }

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
            UniUtils().copyToClipboard(requireActivity(), requireContext(), packageName)
        }

        binding.appStore.setOnClickListener {
            AppMenuUtils().openAppStore(requireContext(), packageName, this)
        }

        binding.appFreeform.setOnClickListener {
            AppMenuUtils().launchAsFreeform(requireActivity(), requireContext(), packageName, this)
        }

        binding.appInfo.setOnClickListener {
            AppMenuUtils().openAppInfo(requireContext(), packageName, this)
        }

        binding.appUninstall.setOnClickListener {
            AppMenuUtils().uninstallApp(requireContext(), packageName, this)
        }
    }
}