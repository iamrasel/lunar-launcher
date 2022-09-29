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
import android.content.Context.MODE_PRIVATE
import android.content.Intent
import android.content.pm.PackageManager
import android.content.pm.ResolveInfo
import android.os.Build
import android.os.Bundle
import android.text.SpannableStringBuilder
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.AdapterView
import android.widget.AdapterView.OnItemClickListener
import android.widget.AdapterView.OnItemLongClickListener
import android.widget.ArrayAdapter
import androidx.core.widget.doAfterTextChanged
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
    private lateinit var packageNamesArrayList: ArrayList<String>
    private lateinit var appsAdapter: ArrayAdapter<String>
    private lateinit var packageManager: PackageManager
    private lateinit var packageList: List<ResolveInfo>

    private val leftSearchArray = arrayOf("a", "b", "c", "d", "e", "f", "g", "h", "i", "j", "k", "l", "m")
    private val leftSearchArrayII = arrayOf("0", "1", "2", "3", "4", "\u290B")
    private val rightSearchArray = arrayOf("9", "8", "7", "6", "5", "\u290A")
    private val rightSearchArrayII = arrayOf("z", "y", "x", "w", "v", "u", "t", "s", "r", "q", "p", "o", "n")

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
            .marginBottom(windowInsetTypesOf(ime = true))
            .applyToView(binding.searchLayout)

        setupInitialView()
        return binding.root
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        controlOnAppActions()
        controlOnSearchActions()
        searchStringRemover()

        binding.root.setOnTouchListener(object : SwipeTouchListener(context) {
            override fun onSwipeDown() {
                super.onSwipeDown()
                UniUtils().expandNotificationPanel(requireContext())
            }
            override fun onDoubleClick() {
                super.onDoubleClick()
                UniUtils().lockMethod(
                    requireContext().getSharedPreferences(Constants().SHARED_PREFS_SETTINGS, MODE_PRIVATE)
                        .getInt(Constants().SHARED_PREF_LOCK, 0), requireContext(), fragmentActivity)
            }
        })
    }

    private fun setupInitialView() {
        packageManager = fragmentActivity.packageManager
        packageNamesArrayList = ArrayList()
        appsAdapter = ArrayAdapter(requireContext(), R.layout.apps_child, R.id.child_textview, ArrayList())
        // left search columns
        binding.leftSearchList.adapter =
            ArrayAdapter(requireContext(), R.layout.apps_child, R.id.child_textview, leftSearchArray)
        binding.leftSearchListII.adapter =
            ArrayAdapter(requireContext(), R.layout.apps_child, R.id.child_textview, leftSearchArrayII)
        // right search columns
        binding.rightSearchList.adapter =
            ArrayAdapter(requireContext(), R.layout.apps_child, R.id.child_textview, rightSearchArray)
        binding.rightSearchListII.adapter =
            ArrayAdapter(requireContext(), R.layout.apps_child, R.id.child_textview, rightSearchArrayII)
    }

    // fetch all the installed apps and sort them
    private val appsList: Unit
        get() {
            // fetch all the installed apps
            packageList = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                packageManager.queryIntentActivities(
                    Intent(Intent.ACTION_MAIN, null).addCategory(Intent.CATEGORY_LAUNCHER), PackageManager.ResolveInfoFlags.of(0))
            } else {
                @Suppress("DEPRECATION") packageManager.queryIntentActivities(
                    Intent(Intent.ACTION_MAIN, null).addCategory(Intent.CATEGORY_LAUNCHER), 0)
            }
            // sort the app list
            (packageList as MutableList<ResolveInfo>).sortWith(ResolveInfo.DisplayNameComparator(packageManager))
        }

    private fun fetchApps() {
        appsList
        // clear the list before repopulating
        appsAdapter.clear()
        packageNamesArrayList.clear()
        // add the apps names to the adapter, and the package names to the array list
        for (resolver in packageList) {
            val appName = resolver.loadLabel(packageManager).toString()
            appsAdapter.add(appName)
            packageNamesArrayList.add(resolver.activityInfo.packageName)
        }

        if (appsAdapter.count < 1) {
            return
        } else {
            showApps()
        }
    }

    private fun showApps() {
        // show the apps list and total count
        binding.appsList.adapter = appsAdapter
        binding.appsCount.text = appsAdapter.count.toString()
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

    private fun controlOnSearchActions() {
        // left column 1
        binding.leftSearchList.onItemClickListener =
            OnItemClickListener { adapterView: AdapterView<*>, _: View?, i: Int, _: Long ->
                searchClickHelper(adapterView, i)
            }
        // left column 2
        binding.leftSearchListII.onItemClickListener =
            OnItemClickListener { adapterView: AdapterView<*>, _: View?, i: Int, _: Long ->
                when (i) {
                    leftSearchArrayII.size - 1 -> binding.appsList.setSelection(appsAdapter.count - 1)
                    else -> searchClickHelper(adapterView, i)
                }
            }
        // right column 1
        binding.rightSearchList.onItemClickListener =
            OnItemClickListener { adapterView: AdapterView<*>, _: View?, i: Int, _: Long ->
                when (i) {
                    rightSearchArray.size - 1 -> binding.appsList.setSelection(0)
                    else -> searchClickHelper(adapterView, i)
                }
            }
        // right column 2
        binding.rightSearchListII.onItemClickListener =
            OnItemClickListener { adapterView: AdapterView<*>, _: View?, i: Int, _: Long ->
                searchClickHelper(adapterView, i)
            }
    }

    private fun searchClickHelper(adapterView: AdapterView<*>, i: Int) {
        if (appsAdapter.count < 2) return
        binding.searchLayout.visibility = View.VISIBLE
        val string = binding.searchInput.text.toString() + adapterView.getItemAtPosition(i).toString()
        binding.searchInput.text = SpannableStringBuilder(string)

        val sharedPreferences = requireContext().getSharedPreferences(Constants().SHARED_PREFS_SETTINGS, MODE_PRIVATE)
        if (sharedPreferences.getBoolean(Constants().SHARED_PREF_AUTO_KEYBOARD, false)) {
            binding.searchInput.requestFocus()
            val inputMethodManager = fragmentActivity.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            inputMethodManager.showSoftInput(binding.searchInput, InputMethodManager.SHOW_IMPLICIT)
        }

        searchStringChangeListener()
    }

    private fun searchStringRemover() {
        binding.backspace.setOnClickListener {
            if (binding.searchInput.text.toString().isNotEmpty()) {
                val string = binding.searchInput.text.toString().substring(0, binding.searchInput.text.toString().length - 1)
                binding.searchInput.text = SpannableStringBuilder(string)
                searchStringChangeListener()
                if (binding.searchInput.text.toString().isEmpty()) {
                    binding.searchLayout.visibility = View.GONE
                }
            }
        }
        binding.backspace.setOnLongClickListener {
            closeSearch()
            true
        }
    }

    private fun searchStringChangeListener() {
        binding.searchInput.doAfterTextChanged {
            binding.searchInput.setSelection(binding.searchInput.text.toString().length)
            val string = binding.searchInput.text.toString()
            filterAppsList(string)
        }
    }

    private fun filterAppsList(searchString: String) {
        // return if the search string is empty
        if (searchString == "") {
            fetchApps()
            return
        }

        // clear the current lists
        appsAdapter.clear()
        packageNamesArrayList.clear()

        /* check each package name and add only the ones
            that match the search string */
        for (resolver in packageList) {
            val appNm = resolver.loadLabel(packageManager) as String
            if (appNm.replace("[^a-zA-Z0-9]".toRegex(), "").lowercase(Locale.getDefault())
                    .contains(searchString)) {
                appsAdapter.add(appNm)
                packageNamesArrayList.add(resolver.activityInfo.packageName)
            }
        }

        // if only one app contains the search string, then launch it
        if (appsAdapter.count == 1) {
            startActivity(packageManager.getLaunchIntentForPackage(packageNamesArrayList[0]))
        } else if (appsAdapter.count < 1) {
            binding.appsCount.text = appsAdapter.count.toString()
        } else {
            showApps()
        }
    }

    private fun closeSearch() {
        binding.searchInput.text?.clear()
        binding.searchInput.let { view ->
            val inputMethodManager = fragmentActivity.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            inputMethodManager.hideSoftInputFromWindow(view.windowToken, 0)
        }
        binding.searchLayout.visibility = View.GONE
    }

    override fun onResume() {
        super.onResume()
        closeSearch()
        setupInitialView()
        fetchApps()
    }
}