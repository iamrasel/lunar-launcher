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

package rasel.lunar.launcher.qaccess

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import rasel.lunar.launcher.databinding.QuickAccessBinding
import rasel.lunar.launcher.helpers.Constants

internal class QuickAccess : BottomSheetDialogFragment() {
    private lateinit var binding: QuickAccessBinding
    private lateinit var accessUtils: AccessUtils
    private lateinit var packageOne: String
    private lateinit var packageTwo: String
    private lateinit var packageThree: String
    private lateinit var packageFour: String
    private lateinit var packageFive: String
    private lateinit var packageSix: String
    private lateinit var phoneNumOne: String
    private lateinit var phoneNumTwo: String
    private lateinit var phoneNumThree: String
    private lateinit var thumbPhoneOne: String
    private lateinit var thumbPhoneTwo: String
    private lateinit var thumbPhoneThree: String
    private lateinit var urlStringOne: String
    private lateinit var urlStringTwo: String
    private lateinit var urlStringThree: String
    private lateinit var thumbUrlOne: String
    private lateinit var thumbUrlTwo: String
    private lateinit var thumbUrlThree: String

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = QuickAccessBinding.inflate(inflater, container, false)
        accessUtils = AccessUtils(requireContext(), this, requireActivity())

        favApps()
        accessUtils.controlBrightness(binding.brightness)
        accessUtils.volumeControllers(binding.notification, binding.alarm, binding.media, binding.voice, binding.ring)

        return binding.root
    }

    private fun favApps() {
        val prefsFavApps = requireContext().getSharedPreferences(Constants().SHARED_PREFS_FAV_APPS, Context.MODE_PRIVATE)
        packageOne = prefsFavApps.getString(Constants().FAV_APP_ + 1, "").toString()
        packageTwo = prefsFavApps.getString(Constants().FAV_APP_ + 2, "").toString()
        packageThree = prefsFavApps.getString(Constants().FAV_APP_ + 3, "").toString()
        packageFour = prefsFavApps.getString(Constants().FAV_APP_ + 4, "").toString()
        packageFive = prefsFavApps.getString(Constants().FAV_APP_ + 5, "").toString()
        packageSix = prefsFavApps.getString(Constants().FAV_APP_ + 6, "").toString()

        accessUtils.favApps(packageOne, binding.appOne, 1)
        accessUtils.favApps(packageTwo, binding.appTwo, 2)
        accessUtils.favApps(packageThree, binding.appThree, 3)
        accessUtils.favApps(packageFour, binding.appFour, 4)
        accessUtils.favApps(packageFive, binding.appFive, 5)
        accessUtils.favApps(packageSix, binding.appSix, 6)
    }

    private fun favPhoneAndUrls() {
        val prefsPhonesAndUrls = requireContext().getSharedPreferences(Constants().SHARED_PREFS_PHONES_URLS, Context.MODE_PRIVATE)
        phoneNumOne = prefsPhonesAndUrls.getString(Constants().PHONE_NO_ + 1, "").toString()
        phoneNumTwo = prefsPhonesAndUrls.getString(Constants().PHONE_NO_ + 2, "").toString()
        phoneNumThree = prefsPhonesAndUrls.getString(Constants().PHONE_NO_ + 3, "").toString()
        thumbPhoneOne = prefsPhonesAndUrls.getString(Constants().PHONE_THUMB_LETTER_ + 1, "").toString()
        thumbPhoneTwo = prefsPhonesAndUrls.getString(Constants().PHONE_THUMB_LETTER_ + 2, "").toString()
        thumbPhoneThree = prefsPhonesAndUrls.getString(Constants().PHONE_THUMB_LETTER_ + 3, "").toString()
        urlStringOne = prefsPhonesAndUrls.getString(Constants().URL_NO_ + 1, "").toString()
        urlStringTwo = prefsPhonesAndUrls.getString(Constants().URL_NO_ + 2, "").toString()
        urlStringThree = prefsPhonesAndUrls.getString(Constants().URL_NO_ + 3, "").toString()
        thumbUrlOne = prefsPhonesAndUrls.getString(Constants().URL_THUMB_LETTER_ + 1, "").toString()
        thumbUrlTwo = prefsPhonesAndUrls.getString(Constants().URL_THUMB_LETTER_ + 2, "").toString()
        thumbUrlThree = prefsPhonesAndUrls.getString(Constants().URL_THUMB_LETTER_ + 3, "").toString()

        accessUtils.phonesAndUrls(Constants().URL_ADDRESS, urlStringOne, thumbUrlOne, binding.urlOne, 1)
        accessUtils.phonesAndUrls(Constants().URL_ADDRESS, urlStringTwo, thumbUrlTwo, binding.urlTwo, 2)
        accessUtils.phonesAndUrls(Constants().URL_ADDRESS, urlStringThree, thumbUrlThree, binding.urlThree, 3)
        accessUtils.phonesAndUrls(Constants().PHONE_NO, phoneNumOne, thumbPhoneOne, binding.phoneOne, 1)
        accessUtils.phonesAndUrls(Constants().PHONE_NO, phoneNumTwo, thumbPhoneTwo, binding.phoneTwo, 2)
        accessUtils.phonesAndUrls(Constants().PHONE_NO, phoneNumThree, thumbPhoneThree, binding.phoneThree, 3)
    }

    override fun onResume() {
        super.onResume()
        favPhoneAndUrls()
    }
}