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

import android.R.attr.*
import android.app.Activity.RESULT_CANCELED
import android.app.Activity.RESULT_OK
import android.appwidget.AppWidgetManager
import android.content.Intent
import android.content.SharedPreferences
import android.os.*
import android.view.*
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.widget.LinearLayoutCompat.LayoutParams
import androidx.appcompat.widget.PopupMenu
import androidx.core.app.JobIntentService.enqueueWork
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.google.android.material.button.MaterialButtonToggleGroup
import kotlinx.coroutines.*
import rasel.lunar.launcher.LauncherActivity.Companion.appWidgetHost
import rasel.lunar.launcher.LauncherActivity.Companion.appWidgetManager
import rasel.lunar.launcher.LauncherActivity.Companion.lActivity
import rasel.lunar.launcher.R
import rasel.lunar.launcher.databinding.FeedsBinding
import rasel.lunar.launcher.feeds.rss.Rss
import rasel.lunar.launcher.feeds.rss.RssAdapter
import rasel.lunar.launcher.feeds.rss.RssService
import rasel.lunar.launcher.helpers.Constants.Companion.KEY_RSS_URL
import rasel.lunar.launcher.helpers.Constants.Companion.KEY_WIDGET_HEIGHTS
import rasel.lunar.launcher.helpers.Constants.Companion.KEY_WIDGET_IDS
import rasel.lunar.launcher.helpers.Constants.Companion.PREFS_SETTINGS
import rasel.lunar.launcher.helpers.Constants.Companion.PREFS_WIDGETS
import rasel.lunar.launcher.helpers.Constants.Companion.RSS_ITEMS
import rasel.lunar.launcher.helpers.Constants.Companion.RSS_RECEIVER
import rasel.lunar.launcher.helpers.Constants.Companion.SEPARATOR
import rasel.lunar.launcher.helpers.Constants.Companion.requestCreateWidget
import rasel.lunar.launcher.helpers.Constants.Companion.requestPickWidget
import rasel.lunar.launcher.helpers.Constants.Companion.rssJobId
import rasel.lunar.launcher.helpers.UniUtils.Companion.isNetworkAvailable
import java.util.*


internal class Feeds : Fragment() {

    private lateinit var binding: FeedsBinding
    private val requestCodeString = "requestCode"

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = FeedsBinding.inflate(inflater, container, false)

        updateWidgets()

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        expandCollapse()
        systemInfo()
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
        lActivity!!.menuInflater.inflate(R.menu.add_widget, menu)
    }

    override fun onContextItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.add_widget) selectWidget()
        return super.onContextItemSelected(item)
    }

    /* control view's expand-collapse actions */
    private fun expandCollapse() {
        binding.expandableButtons.addOnButtonCheckedListener { _: MaterialButtonToggleGroup?, checkedId: Int, isChecked: Boolean ->
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
        when {
            isNetworkAvailable && !rssUrl.isNullOrEmpty() -> {
                Intent(lActivity!!, RssService::class.java)
                    .putExtra(RSS_RECEIVER, resultReceiver).let {
                        enqueueWork(lActivity!!, RssService::class.java, rssJobId, it)
                    }
            }
            else -> resumeService()
        }
    }

	/* retry to start rss service */
    private fun resumeService() {
        binding.feedsRss.apply {
            rss.visibility = View.GONE
            loading.visibility = View.GONE
            refresh.visibility = View.VISIBLE
            refresh.setOnClickListener { startService() }
        }
    }

	/* rss service's result receiver */
    @Suppress("UNCHECKED_CAST")
    private val resultReceiver: ResultReceiver = object : ResultReceiver(Handler(Looper.getMainLooper())) {
        override fun onReceiveResult(resultCode: Int, resultData: Bundle) {
            when (val items = resultData.getSerializable(RSS_ITEMS) as List<Rss>?) {
                null -> resumeService()
                else -> {
                    binding.feedsRss.apply {
                        rss.adapter = RssAdapter(items, requireContext())
                        refresh.visibility = View.GONE
                        loading.visibility = View.GONE
                        rss.visibility = View.VISIBLE
                    }
                }
            }
        }
    }

    private fun systemInfo() {
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.RESUMED) {
                SystemStats().apply {
                    intStorage(binding.feedsSysInfos.intParent)
                    extStorage(binding.feedsSysInfos.extParent)
                    while (isActive) {
                        ram(binding.feedsSysInfos.ramParent)
                        cpu(binding.feedsSysInfos.cpuParent)
                        misc(binding.feedsSysInfos.misc)
                        delay(1000)
                    }
                }
            }
        }
    }

    private fun selectWidget() {
        Intent(AppWidgetManager.ACTION_APPWIDGET_PICK).apply {
            putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetHost?.allocateAppWidgetId())
            putParcelableArrayListExtra(AppWidgetManager.EXTRA_CUSTOM_INFO, ArrayList())
            putParcelableArrayListExtra(AppWidgetManager.EXTRA_CUSTOM_EXTRAS, ArrayList())
            putExtra(requestCodeString, requestPickWidget)
        }.let { widgetPicker.launch(it) }
    }

    private val widgetPicker =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            val data = result.data
            val appWidgetId = data?.getIntExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, -1)
            if (result.resultCode == RESULT_OK) {
                when (data?.getIntExtra(requestCodeString, requestPickWidget)) {
                    requestPickWidget -> configureWidget(appWidgetId!!)
                    requestCreateWidget -> createWidget(appWidgetId!!, null)
                }
            } else if (result.resultCode == RESULT_CANCELED && data != null) {
                if (appWidgetId != -1) appWidgetHost?.deleteAppWidgetId(appWidgetId!!)
            }
        }

    private fun configureWidget(appWidgetId: Int) {
        when (val appWidgetConfig = appWidgetManager!!.getAppWidgetInfo(appWidgetId).configure) {
            null -> createWidget(appWidgetId, null)
            else -> {
                Intent(AppWidgetManager.ACTION_APPWIDGET_CONFIGURE).apply {
                    component = appWidgetConfig
                    putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId)
                    putExtra(requestCodeString, requestCreateWidget)
                }.let {
                    try { widgetPicker.launch(it) }
                    catch (e: Exception) { e.printStackTrace() }
                }
            }
        }
    }

    private fun createWidget(appWidgetId: Int, height: Int?) {
        if (appWidgetId == -1) return

        val appWidgetInfo = appWidgetManager!!.getAppWidgetInfo(appWidgetId)
        val params: LayoutParams?

        when (height) {
            null -> {
                params = LayoutParams(LayoutParams.MATCH_PARENT, appWidgetInfo.minHeight)
                val updatedIds = splitWidgetIds.plus("$appWidgetId")
                val updatedHeights = splitWidgetHeights.plus("${appWidgetInfo.minHeight}")
                saveWidgetData(updatedIds, updatedHeights)
            }
            else -> params = LayoutParams(LayoutParams.MATCH_PARENT, height)
        }

        (appWidgetHost?.createView(lActivity!!.applicationContext, appWidgetId, appWidgetInfo) as WidgetHostView)
            .apply {
                setAppWidget(appWidgetId, appWidgetInfo)
            }.let {
                binding.widgetContainer.addView(it, params)
                widgetMenu(it)
            }
    }

    private fun updateWidgets() {
        if (splitWidgetIds.size > 0) {
            viewLifecycleOwner.lifecycleScope.launch {
                binding.widgetContainer.removeAllViews()
                for (i in 0 until splitWidgetIds.size) {
                    createWidget(splitWidgetIds[i]!!.int(), splitWidgetHeights[i]!!.int())
                }
            }
        }
    }

    private fun widgetMenu(hostView: WidgetHostView) {
        val appWidgetId = hostView.appWidgetId
        hostView.setOnLongClickListener {
            PopupMenu(requireContext(), it, Gravity.END).apply {
                menuInflater.inflate(R.menu.widget_menu, this.menu)
                show()
                setOnMenuItemClickListener { menuItem ->
                    when (menuItem.itemId) {
                        R.id.move_up -> moveWidget(appWidgetId, true)
                        R.id.move_down -> moveWidget(appWidgetId, false)
                        R.id.increase_height -> resizeWidget(appWidgetId, true)
                        R.id.decrease_height -> resizeWidget(appWidgetId, false)
                        R.id.delete_widget -> removeWidget(it as WidgetHostView)
                    }
                    false
                }
            }
            true
        }
    }

    private fun moveWidget(widgetId: Int, moveUp: Boolean) {
        val tempIds = splitWidgetIds
        val tempHeights = splitWidgetHeights

        splitWidgetIds.indexOf(widgetId.toString()).let { i ->
            when {
                moveUp && i > 0 -> {
                    tempIds.swap(i-1, i)
                    tempHeights.swap(i-1, i)
                }
                !moveUp && i < splitWidgetIds.size - 1 -> {
                    tempIds.swap(i, i+1)
                    tempHeights.swap(i, i+1)
                }
                else -> return
            }
        }

        saveWidgetData(tempIds, tempHeights)
        updateWidgets()
    }

    private fun resizeWidget(widgetId: Int, shouldAdd: Boolean) {
        val tempList = splitWidgetHeights

        splitWidgetIds.indexOf(widgetId.toString()).let { i ->
            tempList[i] = when (shouldAdd) {
                true -> (splitWidgetHeights[i]!!.int().plus(50)).toString()
                false -> (splitWidgetHeights[i]!!.int().minus(50)).toString()
            }
        }

        widgetPref.edit().putString(KEY_WIDGET_HEIGHTS, tempList.joinToString(separator = SEPARATOR)).apply()
        updateWidgets()
    }

    private fun removeWidget(hostView: WidgetHostView) {
        hostView.let { v ->
            appWidgetHost?.deleteAppWidgetId(v.appWidgetId)
            binding.widgetContainer.removeView(v)

            splitWidgetIds.indexOf(v.appWidgetId.toString()).let { i ->
                saveWidgetData(splitWidgetIds.minus(splitWidgetIds[i]), splitWidgetHeights.minus(splitWidgetHeights[i]))
            }
        }
    }

    private fun saveWidgetData(idList: List<String?>, heightList: List<String?>) {
        widgetPref.edit()
            .putString(KEY_WIDGET_IDS, idList.joinToString(separator = SEPARATOR))
            .putString(KEY_WIDGET_HEIGHTS, heightList.joinToString(separator = SEPARATOR))
            .apply()
    }

    private val widgetPref: SharedPreferences get() = lActivity!!.getSharedPreferences(PREFS_WIDGETS, 0)
    private val widgetIds: String? get() = widgetPref.getString(KEY_WIDGET_IDS, "")
    private val widgetHeights: String? get() = widgetPref.getString(KEY_WIDGET_HEIGHTS, "")
    private val splitWidgetIds: MutableList<String?> get() = widgetIds!!.split(SEPARATOR).toMutableList()
    private val splitWidgetHeights: MutableList<String?> get() = widgetHeights!!.split(SEPARATOR).toMutableList()

    private fun <T> MutableList<T>.swap(index1: Int, index2: Int){
        val temp = this[index1]
        this[index1] = this[index2]
        this[index2] = temp
    }

    private fun String.int() : Int {
        return try {
            this.toInt()
        } catch (e: Exception) {
            -1
        }
    }

}
