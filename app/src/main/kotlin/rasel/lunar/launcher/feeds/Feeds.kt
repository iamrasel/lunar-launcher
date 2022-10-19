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

package rasel.lunar.launcher.feeds

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.os.ResultReceiver
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.app.JobIntentService.enqueueWork
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import dev.chrisbanes.insetter.Insetter
import dev.chrisbanes.insetter.windowInsetTypesOf
import rasel.lunar.launcher.LauncherActivity
import rasel.lunar.launcher.databinding.FeedsBinding
import rasel.lunar.launcher.feeds.rss.Rss
import rasel.lunar.launcher.feeds.rss.RssAdapter
import rasel.lunar.launcher.feeds.rss.RssService
import rasel.lunar.launcher.helpers.Constants
import rasel.lunar.launcher.helpers.UniUtils

internal class Feeds : Fragment() {

    private lateinit var binding: FeedsBinding
    private lateinit var fragmentActivity: FragmentActivity
    private lateinit var feedsUtils: FeedsUtils
    private lateinit var handler: Handler
    private lateinit var runnable: Runnable

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = FeedsBinding.inflate(inflater, container, false)

        setInsets()

        fragmentActivity = if (isAdded) {
            requireActivity()
        } else {
            LauncherActivity()
        }

        feedsUtils = FeedsUtils(fragmentActivity)

        return binding.root
    }

    private fun setInsets() {
        Insetter.builder()
            .paddingBottom(windowInsetTypesOf(navigationBars = true))
            .applyToView(binding.rss)
        Insetter.builder()
            .marginTop(windowInsetTypesOf(statusBars = true))
            .applyToView(binding.ram)
            .applyToView(binding.cpu)
    }

    private fun startService() {
        val rssUrl = fragmentActivity.getSharedPreferences(Constants().PREFS_SETTINGS, Context.MODE_PRIVATE)
            .getString(Constants().KEY_RSS_URL, "")
        if (UniUtils().isNetworkAvailable(fragmentActivity) && rssUrl != null && rssUrl.isNotEmpty()) {
            val intent = Intent(fragmentActivity, RssService::class.java)
            intent.putExtra(Constants().RSS_RECEIVER, resultReceiver)
            enqueueWork(fragmentActivity, RssService::class.java, 101, intent)
        } else {
            resumeService()
        }
    }

    private fun resumeService() {
        binding.rss.visibility = View.GONE
        binding.loadingProgress.visibility = View.GONE
        binding.dataFetchingFailed.visibility = View.VISIBLE
        binding.dataFetchingFailed.setOnClickListener { startService() }
    }

    @Suppress("UNCHECKED_CAST")
    private val resultReceiver: ResultReceiver = object : ResultReceiver(Handler(Looper.getMainLooper())) {
        override fun onReceiveResult(resultCode: Int, resultData: Bundle) {
            val items = resultData.getSerializable(Constants().RSS_ITEMS) as List<Rss>?
            if (items != null) {
                binding.rss.adapter = RssAdapter(items, requireContext())
                binding.dataFetchingFailed.visibility = View.GONE
                binding.loadingProgress.visibility = View.GONE
                binding.rss.visibility = View.VISIBLE
            } else {
                resumeService()
            }
        }
    }

    override fun onResume() {
        super.onResume()
        startService()

        feedsUtils.ram(binding.ram)
        feedsUtils.cpuBattery(binding.cpu)
        feedsUtils.intStorage(binding.intStorage)
        feedsUtils.extStorage(binding.extStorage)

        handler = Handler(Looper.getMainLooper())
        runnable = object : Runnable {
            override fun run() {
                feedsUtils.ram(binding.ram)
                feedsUtils.cpuBattery(binding.cpu)
                handler.postDelayed(this, 1000)
            }
        }
        handler.post(runnable)
    }

    override fun onPause() {
        super.onPause()
        handler.removeCallbacks(runnable)
    }
}