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
import android.view.*
import android.widget.Toast
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
import rasel.lunar.launcher.R
import rasel.lunar.launcher.databinding.FeedsBinding
import rasel.lunar.launcher.feeds.rss.Rss
import rasel.lunar.launcher.feeds.rss.RssAdapter
import rasel.lunar.launcher.feeds.rss.RssService
import rasel.lunar.launcher.helpers.Constants.Companion.KEY_RSS_URL
import rasel.lunar.launcher.helpers.Constants.Companion.PREFS_SETTINGS
import rasel.lunar.launcher.helpers.Constants.Companion.RSS_ITEMS
import rasel.lunar.launcher.helpers.Constants.Companion.RSS_RECEIVER
import rasel.lunar.launcher.helpers.UniUtils.Companion.isNetworkAvailable


internal class Feeds : Fragment() {

    private lateinit var binding: FeedsBinding
    private lateinit var fragmentActivity: FragmentActivity
    private val rssJobId = 101

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = FeedsBinding.inflate(inflater, container, false)

        /* set insets */
        setInsets()

        fragmentActivity = if (isAdded) {
            requireActivity()
        } else {
            LauncherActivity()
        }

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        expandCollapse()
    }

    override fun onResume() {
        super.onResume()
        registerForContextMenu(binding.widgetContainer)
    }

    override fun onPause() {
        super.onPause()
        unregisterForContextMenu(binding.widgetContainer)
    }

    override fun onCreateContextMenu(menu: ContextMenu, v: View, menuInfo: ContextMenu.ContextMenuInfo?) {
        super.onCreateContextMenu(menu, v, menuInfo)
        menu.clearHeader()
        fragmentActivity.menuInflater.inflate(R.menu.add_widget, menu)
    }

    override fun onContextItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.add_widget)
            Toast.makeText(requireContext(), "soon", Toast.LENGTH_SHORT).show()
        return super.onContextItemSelected(item)
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
        val rssUrl = fragmentActivity.getSharedPreferences(PREFS_SETTINGS, 0)
            .getString(KEY_RSS_URL, "")
        if (isNetworkAvailable(fragmentActivity) && rssUrl != null && rssUrl.isNotEmpty()) {
            val intent = Intent(fragmentActivity, RssService::class.java)
            intent.putExtra(RSS_RECEIVER, resultReceiver)
            enqueueWork(fragmentActivity, RssService::class.java, rssJobId, intent)
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
            val items = resultData.getSerializable(RSS_ITEMS) as List<Rss>?
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
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.RESUMED) {
                val systemStats = SystemStats(fragmentActivity)
                systemStats.intStorage(binding.feedsSysInfos.intParent)
                systemStats.extStorage(binding.feedsSysInfos.extParent)
                while (isActive) {
                    systemStats.ram(binding.feedsSysInfos.ramParent)
                    systemStats.cpu(binding.feedsSysInfos.cpuParent)
                    systemStats.misc(binding.feedsSysInfos.misc)
                    delay(1000)
                }
            }
        }
    }

}
