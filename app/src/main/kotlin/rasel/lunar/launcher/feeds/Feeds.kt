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

import android.app.Activity
import android.appwidget.AppWidgetManager
import android.content.Intent
import android.os.*
import android.view.*
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.widget.LinearLayoutCompat
import androidx.appcompat.widget.PopupMenu
import androidx.core.app.JobIntentService.enqueueWork
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.google.android.material.button.MaterialButtonToggleGroup
import dev.chrisbanes.insetter.applyInsetter
import kotlinx.coroutines.*
import rasel.lunar.launcher.LauncherActivity.Companion.lActivity
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
    private lateinit var appWidgetManager: AppWidgetManager
    private lateinit var appWidgetHost: WidgetHost

    private val requestCodeString = "requestCode"
    private val rssJobId = 101
    private val widgetHostId = 102
    private val requestPickWidget = 103
    private val requestCreateWidget = 104

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = FeedsBinding.inflate(inflater, container, false)

        /* set insets */
        setInsets()

        appWidgetManager = AppWidgetManager.getInstance(requireContext())
        appWidgetHost = WidgetHost(requireContext(), widgetHostId)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        expandCollapse()
    }

    override fun onStart() {
        super.onStart()
        registerForContextMenu(binding.widgetContainer)
        appWidgetHost.startListening()
    }

    override fun onStop() {
        super.onStop()
        unregisterForContextMenu(binding.widgetContainer)
        appWidgetHost.stopListening()
    }

    override fun onCreateContextMenu(menu: ContextMenu, v: View, menuInfo: ContextMenu.ContextMenuInfo?) {
        super.onCreateContextMenu(menu, v, menuInfo)
        menu.clearHeader()
        lActivity!!.menuInflater.inflate(R.menu.add_widget, menu)
    }

    override fun onContextItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.add_widget) selectWidget()
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
        val rssUrl = lActivity!!.getSharedPreferences(PREFS_SETTINGS, 0)
            .getString(KEY_RSS_URL, "")
        if (isNetworkAvailable(lActivity!!) && rssUrl != null && rssUrl.isNotEmpty()) {
            val intent = Intent(lActivity!!, RssService::class.java)
            intent.putExtra(RSS_RECEIVER, resultReceiver)
            enqueueWork(lActivity!!, RssService::class.java, rssJobId, intent)
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
                val systemStats = SystemStats()
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

    private fun selectWidget() {
        val appWidgetId = appWidgetHost.allocateAppWidgetId()
        val pickIntent = Intent(AppWidgetManager.ACTION_APPWIDGET_PICK)
        pickIntent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId)
        pickIntent.putExtra(requestCodeString, requestPickWidget)
        addEmptyData(pickIntent)
        widgetPicker.launch(pickIntent)
    }

    private fun addEmptyData(pickIntent: Intent) {
        val customInfo = ArrayList<Parcelable>()
        pickIntent.putParcelableArrayListExtra(AppWidgetManager.EXTRA_CUSTOM_INFO, customInfo)
        val customExtras = ArrayList<Parcelable>()
        pickIntent.putParcelableArrayListExtra(AppWidgetManager.EXTRA_CUSTOM_EXTRAS, customExtras)
    }

    private val widgetPicker =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            val data = result.data
            val requestCode = data?.getIntExtra(requestCodeString, requestPickWidget)
            try {
                if (result.resultCode == Activity.RESULT_OK) {
                    when (requestCode) {
                        requestPickWidget -> configureWidget(data)
                        requestCreateWidget -> createWidget(data)
                    }
                } else if (result.resultCode == Activity.RESULT_CANCELED && data != null) {
                    val appWidgetId = data.getIntExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, -1)
                    if (appWidgetId != -1) {
                        appWidgetHost.deleteAppWidgetId(appWidgetId)
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }

    private fun configureWidget(data: Intent) {
        val extras = data.extras
        val appWidgetId = extras?.getInt(AppWidgetManager.EXTRA_APPWIDGET_ID, -1)
        val appWidgetInfo = appWidgetManager.getAppWidgetInfo(appWidgetId!!)
        if (appWidgetInfo.configure != null) {
            val intent = Intent(AppWidgetManager.ACTION_APPWIDGET_CONFIGURE)
            intent.component = appWidgetInfo.configure
            intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId)
            intent.putExtra(requestCodeString, requestCreateWidget)
            widgetPicker.launch(intent)
        } else {
            createWidget(data)
        }
    }

    private fun createWidget(data: Intent) {
        val extras = data.extras
        val appWidgetId = extras!!.getInt(AppWidgetManager.EXTRA_APPWIDGET_ID, -1)
        val appWidgetInfo = appWidgetManager.getAppWidgetInfo(appWidgetId)

        val hostView =
            appWidgetHost.createView(lActivity!!.applicationContext, appWidgetId, appWidgetInfo) as WidgetHostView
        hostView.setAppWidget(appWidgetId, appWidgetInfo)
        val params =
            LinearLayoutCompat.LayoutParams(LinearLayoutCompat.LayoutParams.MATCH_PARENT, 200)
        binding.widgetContainer.addView(hostView, params)

        hostView.setOnLongClickListener {
            val popupMenu = PopupMenu(requireContext(), it, Gravity.END)
            popupMenu.menuInflater.inflate(R.menu.widget_menu, popupMenu.menu)
            popupMenu.show()
            popupMenu.setOnMenuItemClickListener { menuItem ->
                when (menuItem.itemId) {
                    R.id.delete_widget -> removeWidget(it as WidgetHostView)
                    else -> Toast.makeText(context, "Clicked on " + menuItem.title, Toast.LENGTH_SHORT).show()
                }
                true
            }
            true
        }
    }

    private fun removeWidget(hostView: WidgetHostView) {
        appWidgetHost.deleteAppWidgetId(hostView.appWidgetId)
        binding.widgetContainer.removeView(hostView)
    }

}
