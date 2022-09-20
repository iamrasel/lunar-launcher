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

/*
 * This file is based on this project <https://gitlab.com/biotstoiq/launch/>,
 * which is licensed under MIT.
 */

package rasel.lunar.launcher.apps

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.content.pm.ResolveInfo
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.AdapterView.OnItemClickListener
import android.widget.AdapterView.OnItemLongClickListener
import android.widget.ArrayAdapter
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import dev.chrisbanes.insetter.Insetter
import dev.chrisbanes.insetter.windowInsetTypesOf
import rasel.lunar.launcher.LauncherActivity
import rasel.lunar.launcher.R
import rasel.lunar.launcher.databinding.AppDrawerBinding
import rasel.lunar.launcher.helpers.Constants
import rasel.lunar.launcher.helpers.SwipeTouchListener
import rasel.lunar.launcher.helpers.UniUtils
import java.util.*

internal class AppDrawer : Fragment() {

    private lateinit var binding: AppDrawerBinding
    private lateinit var fragmentActivity: FragmentActivity
    private val leftSearchArray = arrayOf("a", "b", "c", "d", "e", "f", "g", "h", "i", "j", "k", "l", "m")
    private val leftSearchArrayII = arrayOf("0", "1", "2", "3", "4", "\u290B")
    private val rightSearchArray = arrayOf("9", "8", "7", "6", "5", "\u290A")
    private val rightSearchArrayII = arrayOf("z", "y", "x", "w", "v", "u", "t", "s", "r", "q", "p", "o", "n")
    private lateinit var packageNamesArrayList: ArrayList<String>
    private lateinit var appsAdapter: ArrayAdapter<String>
    private lateinit var packageManager: PackageManager
    private lateinit var packageList: List<ResolveInfo>
    private lateinit var searchString: String

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = AppDrawerBinding.inflate(inflater, container, false)

        fragmentActivity = if (isAdded) {
            requireActivity()
        } else {
            LauncherActivity()
        }

        Insetter.builder()
            .padding(windowInsetTypesOf(systemGestures = true))
            .applyToView(binding.appsList)
        Insetter.builder()
            .marginBottom(windowInsetTypesOf(navigationBars = true))
            .applyToView(binding.leftSearchList)
            .applyToView(binding.leftSearchListII)
        Insetter.builder()
            .marginBottom(windowInsetTypesOf(navigationBars = true))
            .applyToView(binding.rightSearchList)
            .applyToView(binding.rightSearchListII)
        Insetter.builder()
            .marginBottom(windowInsetTypesOf(navigationBars = true))
            .applyToView(binding.searchStringChip)

        setupInitialView()
        return binding.root
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        controlOnAppActions()
        controlOnSearchClicks()
        searchStringRemover()

        binding.root.setOnTouchListener(object : SwipeTouchListener(context) {
            override fun onSwipeDown() {
                super.onSwipeDown()
                UniUtils().expandNotificationPanel(requireContext())
            }
            override fun onDoubleClick() {
                super.onDoubleClick()
                UniUtils().lockMethod(
                    requireContext().getSharedPreferences(Constants().SHARED_PREFS_SETTINGS, Context.MODE_PRIVATE)
                        .getInt(Constants().SHARED_PREF_LOCK, 0), requireContext(), fragmentActivity)
            }
        })
    }

    private fun setupInitialView() {
        packageManager = fragmentActivity.packageManager
        packageNamesArrayList = ArrayList()
        appsAdapter = ArrayAdapter(requireContext(), R.layout.apps_child, R.id.child_textview, ArrayList())
        // Left search textview list
        val leftSearchAdapter =
            ArrayAdapter(requireContext(), R.layout.apps_child, R.id.child_textview, leftSearchArray)
        val leftSearchAdapterII =
            ArrayAdapter(requireContext(), R.layout.apps_child, R.id.child_textview, leftSearchArrayII)
        // Right search textview list
        val rightSearchAdapter =
            ArrayAdapter(requireContext(), R.layout.apps_child, R.id.child_textview, rightSearchArray)
        val rightSearchAdapterII =
            ArrayAdapter(requireContext(), R.layout.apps_child, R.id.child_textview, rightSearchArrayII)

        binding.leftSearchList.adapter = leftSearchAdapter
        binding.leftSearchListII.adapter = leftSearchAdapterII
        binding.rightSearchList.adapter = rightSearchAdapter
        binding.rightSearchListII.adapter = rightSearchAdapterII
        binding.searchStringChip.visibility = View.GONE
    }

    // Fetch all the installed apps
    // Sort the app list
    private val appsList: Unit
        get() {
            searchString = ""
            // Fetch all the installed apps
            packageList = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                packageManager.queryIntentActivities(
                    Intent(Intent.ACTION_MAIN, null).addCategory(Intent.CATEGORY_LAUNCHER), PackageManager.ResolveInfoFlags.of(0))
            } else {
                @Suppress("DEPRECATION") packageManager.queryIntentActivities(
                    Intent(Intent.ACTION_MAIN, null).addCategory(Intent.CATEGORY_LAUNCHER), 0)
            }
            // Sort the app list
            (packageList as MutableList<ResolveInfo>).sortWith(ResolveInfo.DisplayNameComparator(packageManager))
        }

    private fun fetchAllApps() {
        appsList
        // Clear the list before repopulating
        appsAdapter.clear()
        packageNamesArrayList.clear()
        /* Add the apps names to the adapter,
            and the package name to the array list */
        for (resolver in packageList) {
            val apNm = resolver.loadLabel(packageManager).toString()
            appsAdapter.add(apNm)
            packageNamesArrayList.add(resolver.activityInfo.packageName)
        }
        if (appsAdapter.count < 1) {
            binding.loadingProgress.visibility = View.VISIBLE
            return
        } else {
            binding.loadingProgress.visibility = View.GONE
            binding.appsCount.text = appsAdapter.count.toString()
        }
        showApps()
    }

    private fun showApps() {
        // Show the app name adapter as the app list
        binding.appsList.adapter = appsAdapter
    }

    private fun controlOnAppActions() {
        binding.appsList.onItemClickListener =
            OnItemClickListener { _: AdapterView<*>?, _: View?, i: Int, _: Long ->
                startActivity(packageManager.getLaunchIntentForPackage(packageNamesArrayList[i]))
            }

        binding.appsList.onItemLongClickListener =
            OnItemLongClickListener { _: AdapterView<*>?, _: View?, i: Int, _: Long ->
                AppMenus().show(fragmentActivity.supportFragmentManager, packageNamesArrayList[i])
                true
            }
    }

    private fun controlOnSearchClicks() {
        // Left column 1
        binding.leftSearchList.onItemClickListener =
            OnItemClickListener { adapterView: AdapterView<*>, _: View?, i: Int, _: Long ->
                searchClickHelper(adapterView, i)
            }
        // Left column 2
        binding.leftSearchListII.onItemClickListener =
            OnItemClickListener { adapterView: AdapterView<*>, _: View?, i: Int, _: Long ->
                when (i) {
                    leftSearchArrayII.size - 1 -> binding.appsList.setSelection(appsAdapter.count - 1)
                    else -> searchClickHelper(adapterView, i)
                }
            }
        // Right column 1
        binding.rightSearchList.onItemClickListener =
            OnItemClickListener { adapterView: AdapterView<*>, _: View?, i: Int, _: Long ->
                when (i) {
                    rightSearchArray.size - 1 -> binding.appsList.setSelection(0)
                    else -> searchClickHelper(adapterView, i)
                }
            }
        // Right column 2
        binding.rightSearchListII.onItemClickListener =
            OnItemClickListener { adapterView: AdapterView<*>, _: View?, i: Int, _: Long ->
                searchClickHelper(adapterView, i)
            }
    }

    private fun searchClickHelper(adapterView: AdapterView<*>, i: Int) {
        if (binding.appsList.count < 2) return
        searchString += adapterView.getItemAtPosition(i).toString()
        binding.searchStringChip.visibility = View.VISIBLE
        binding.searchStringChip.text = searchString
        filterAppsList()
    }

    private fun filterAppsList() {
        // Return if the search string is empty
        if (searchString == "") {
            fetchAllApps()
            return
        }

        // Clear the current lists
        appsAdapter.clear()
        packageNamesArrayList.clear()

        /* Check each package name and add only the ones
            that match the search string */
        for (resolver in packageList) {
            val appNm = resolver.loadLabel(packageManager) as String
            if (appNm.replace("[^a-zA-Z0-9]".toRegex(), "").lowercase(Locale.getDefault())
                    .contains(searchString)) {
                appsAdapter.add(appNm)
                packageNamesArrayList.add(resolver.activityInfo.packageName)
            }
        }

        // If only one app contains the search string, then launch it
        if (appsAdapter.count == 1) {
            startActivity(packageManager.getLaunchIntentForPackage(packageNamesArrayList[0]))
        } else if (appsAdapter.count < 1) {
            binding.appsCount.text = appsAdapter.count.toString()
        } else {
            showApps()
            binding.appsCount.text = appsAdapter.count.toString()
        }
    }

    private fun searchStringRemover() {
        binding.searchStringChip.setOnClickListener {
            if (searchString.isNotEmpty()) {
                searchString = searchString.substring(0, searchString.length - 1)
                binding.searchStringChip.text = searchString
                filterAppsList()
                if (searchString.isEmpty()) {
                    binding.searchStringChip.visibility = View.GONE
                }
            }
        }

        binding.searchStringChip.setOnCloseIconClickListener {
            binding.searchStringChip.visibility = View.GONE
            fetchAllApps()
        }
    }

    override fun onResume() {
        super.onResume()
        setupInitialView()
        fetchAllApps()
    }
}