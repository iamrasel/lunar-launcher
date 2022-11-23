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
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.google.android.material.button.MaterialButtonToggleGroup
import dev.chrisbanes.insetter.applyInsetter
import kotlinx.coroutines.*
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

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = FeedsBinding.inflate(inflater, container, false)

        /* set insets */
        setInsets()

        fragmentActivity = if (isAdded) {
            requireActivity()
        } else {
            LauncherActivity()
        }

        expandCollapse()

        return binding.root
    }

    /* insets */
    private fun setInsets() {
        binding.root.applyInsetter {
            type(statusBars = true, navigationBars = true) {
                padding()
            }
        }
    }

    /* control view's expand-collapse actions */
    private fun expandCollapse() {
        binding.expandableGroup.addOnButtonCheckedListener { _: MaterialButtonToggleGroup?, checkedId: Int, isChecked: Boolean ->
            if (isChecked) {
                when (checkedId) {
                    binding.expandRss.id -> {
                        binding.feedsSysInfos.expandableSystemInfo.collapse()
                        binding.feedsRss.expandableRss.expand()
                        startService()
                    }
                    binding.expandSystemInfo.id -> {
                        binding.feedsRss.expandableRss.collapse()
                        binding.feedsSysInfos.expandableSystemInfo.expand()
                        systemInfo()
                    }
                }
            } else {
                when (checkedId) {
                    binding.expandRss.id -> binding.feedsRss.expandableRss.collapse()
                    binding.expandSystemInfo.id -> binding.feedsSysInfos.expandableSystemInfo.collapse()
                }
            }
        }
    }

	/* start rss service if network is active and rss url is not empty */
    private fun startService() {
        val constants = Constants()
        val rssUrl = fragmentActivity.getSharedPreferences(constants.PREFS_SETTINGS, 0)
            .getString(constants.KEY_RSS_URL, "")
        if (UniUtils().isNetworkAvailable(fragmentActivity) && rssUrl != null && rssUrl.isNotEmpty()) {
            val intent = Intent(fragmentActivity, RssService::class.java)
            intent.putExtra(constants.RSS_RECEIVER, resultReceiver)
            enqueueWork(fragmentActivity, RssService::class.java, 101, intent)
        } else {
            resumeService()
        }
    }

	/* retry to start rss service */
    private fun resumeService() {
        binding.feedsRss.rss.visibility = View.GONE
        binding.feedsRss.loading.visibility = View.GONE
        binding.feedsRss.refresh.visibility = View.VISIBLE
        binding.feedsRss.refresh.setOnClickListener { startService() }
    }

	/* rss service's result receiver */
    @Suppress("UNCHECKED_CAST")
    private val resultReceiver: ResultReceiver = object : ResultReceiver(Handler(Looper.getMainLooper())) {
        override fun onReceiveResult(resultCode: Int, resultData: Bundle) {
            val items = resultData.getSerializable(Constants().RSS_ITEMS) as List<Rss>?
            if (items != null) {
                binding.feedsRss.rss.adapter = RssAdapter(items, requireContext())
                binding.feedsRss.refresh.visibility = View.GONE
                binding.feedsRss.loading.visibility = View.GONE
                binding.feedsRss.rss.visibility = View.VISIBLE
            } else {
                resumeService()
            }
        }
    }

    private fun systemInfo() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.RESUMED) {
                val feedsUtils = FeedsUtils(fragmentActivity)
                feedsUtils.intStorage(binding.feedsSysInfos.intProgress, binding.feedsSysInfos.intStorage)
                feedsUtils.extStorage(binding.feedsSysInfos.extProgress, binding.feedsSysInfos.extStorage)
                while (isActive) {
                    feedsUtils.ram(binding.feedsSysInfos.ramProgress, binding.feedsSysInfos.ram)
                    feedsUtils.cpu(binding.feedsSysInfos.cpuProgress, binding.feedsSysInfos.cpu)
                    feedsUtils.misc(binding.feedsSysInfos.misc)
                    delay(1000)
                }
            }
        }
    }

}
