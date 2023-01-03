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
import dev.chrisbanes.insetter.applyInsetter
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
import kotlin.collections.ArrayList


internal class Feeds : Fragment() {

    private lateinit var binding: FeedsBinding
    private val requestCodeString = "requestCode"

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = FeedsBinding.inflate(inflater, container, false)

        setInsets()
        updateWidgets()

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
        if (isNetworkAvailable() && rssUrl != null && rssUrl.isNotEmpty()) {
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
        val appWidgetId = appWidgetHost?.allocateAppWidgetId()
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
            val appWidgetId = data?.getIntExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, -1)
            val requestCode = data?.getIntExtra(requestCodeString, requestPickWidget)
            try {
                if (result.resultCode == RESULT_OK) {
                    when (requestCode) {
                        requestPickWidget -> configureWidget(appWidgetId!!)
                        requestCreateWidget -> createWidget(appWidgetId!!, null)
                    }
                } else if (result.resultCode == RESULT_CANCELED && data != null) {
                    if (appWidgetId != -1) appWidgetHost?.deleteAppWidgetId(appWidgetId!!)
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }

    private fun configureWidget(appWidgetId: Int) {
        val appWidgetInfo = appWidgetManager!!.getAppWidgetInfo(appWidgetId)
        if (appWidgetInfo.configure != null) {
            val intent = Intent(AppWidgetManager.ACTION_APPWIDGET_CONFIGURE)
            intent.component = appWidgetInfo.configure
            intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId)
            intent.putExtra(requestCodeString, requestCreateWidget)
            widgetPicker.launch(intent)
        } else {
            createWidget(appWidgetId, null)
        }
    }

    private fun createWidget(appWidgetId: Int, height: Int?) {
        val appWidgetInfo = appWidgetManager!!.getAppWidgetInfo(appWidgetId)
        val hostView =
            appWidgetHost?.createView(lActivity!!.applicationContext, appWidgetId, appWidgetInfo) as WidgetHostView
        hostView.setAppWidget(appWidgetId, appWidgetInfo)

        var params: LayoutParams? = null
        if (height == null) {
            params = LayoutParams(LayoutParams.MATCH_PARENT, appWidgetInfo.minHeight)
            val updatedIds = splitWidgetIds.plus("$appWidgetId")
            val updatedHeights = splitWidgetHeights.plus("${appWidgetInfo.minHeight}")
            saveWidgetData(updatedIds, updatedHeights)
        } else {
            params = LayoutParams(LayoutParams.MATCH_PARENT, height)
        }

        binding.widgetContainer.addView(hostView, params)
        widgetMenu(hostView)
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
            val popupMenu = PopupMenu(requireContext(), it, Gravity.END)
            popupMenu.apply {
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
                    true
                }
            }
            true
        }
    }

    private fun moveWidget(widgetId: Int, moveUp: Boolean) {
        val i = splitWidgetIds.indexOf(widgetId.toString())
        val tempIds = splitWidgetIds
        val tempHeights = splitWidgetHeights
        if (moveUp && i > 0) {
            tempIds.swap(i-1, i)
            tempHeights.swap(i-1, i)
        } else if (!moveUp && i < splitWidgetIds.size - 1) {
            tempIds.swap(i, i+1)
            tempHeights.swap(i, i+1)
        } else return
        saveWidgetData(tempIds, tempHeights)
        updateWidgets()
    }

    private fun resizeWidget(widgetId: Int, shouldAdd: Boolean) {
        val i = splitWidgetIds.indexOf(widgetId.toString())
        val tempList = splitWidgetHeights
        tempList[i] = if (shouldAdd) (splitWidgetHeights[i]!!.int().plus(50)).toString()
        else (splitWidgetHeights[i]!!.int().minus(50)).toString()
        widgetPref.edit().putString(KEY_WIDGET_HEIGHTS, tempList.joinToString(separator = SEPARATOR)).apply()
        updateWidgets()
    }

    private fun removeWidget(hostView: WidgetHostView) {
        appWidgetHost?.deleteAppWidgetId(hostView.appWidgetId)
        binding.widgetContainer.removeView(hostView)

        val i = splitWidgetIds.indexOf(hostView.appWidgetId.toString())
        saveWidgetData(splitWidgetIds.minus(splitWidgetIds[i]), splitWidgetHeights.minus(splitWidgetHeights[i]))
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
            0
        }
    }

}
