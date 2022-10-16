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
import android.widget.ArrayAdapter
import androidx.core.widget.doOnTextChanged
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
import kotlin.collections.ArrayList


internal class AppDrawer : Fragment() {

    private lateinit var binding: AppDrawerBinding
    private lateinit var fragmentActivity: FragmentActivity
    private lateinit var packageInfoList: List<ResolveInfo>
    private var packagesList: ArrayList<Packages> = ArrayList()
    private lateinit var packageManager: PackageManager
    private lateinit var appsAdapter: AppsAdapter

    private val leftSearchArray = arrayOf("a", "b", "c", "d", "e", "f", "g", "h", "i", "j", "k", "l", "m")
    private val leftSearchArrayII = arrayOf("0", "1", "2", "3", "4", "\u290B")
    private val rightSearchArray = arrayOf("9", "8", "7", "6", "5", "\u290A")
    private val rightSearchArrayII = arrayOf("z", "y", "x", "w", "v", "u", "t", "s", "r", "q", "p", "o", "n")

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = AppDrawerBinding.inflate(inflater, container, false)

        setInsets()
        setupSearchColumns()

        fragmentActivity = if (isAdded) {
            requireActivity()
        } else {
            LauncherActivity()
        }

        packageManager = fragmentActivity.packageManager
        appsAdapter = AppsAdapter(fragmentActivity, binding.appsCount)
        binding.appsList.adapter = appsAdapter.also { adapter ->
                adapter.updateData(packagesList)
        }

        return binding.root
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

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
                    requireContext().getSharedPreferences(Constants().PREFS_SETTINGS, MODE_PRIVATE)
                        .getInt(Constants().KEY_LOCK_METHOD, 0), requireContext(), fragmentActivity)
            }
        })
    }

    override fun onResume() {
        super.onResume()
        closeSearch()
        fetchApps()
    }

    private fun setInsets() {
        Insetter.builder()
            .paddingTop(windowInsetTypesOf(statusBars = true))
            .paddingBottom(windowInsetTypesOf(navigationBars = true))
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
            .marginBottom(windowInsetTypesOf(navigationBars = true, ime = true))
            .applyToView(binding.searchLayout)
    }

    private fun setupSearchColumns() {
        binding.leftSearchList.adapter =
            ArrayAdapter(requireContext(), R.layout.apps_child, R.id.child_textview, leftSearchArray)
        binding.leftSearchListII.adapter =
            ArrayAdapter(requireContext(), R.layout.apps_child, R.id.child_textview, leftSearchArrayII)
        binding.rightSearchList.adapter =
            ArrayAdapter(requireContext(), R.layout.apps_child, R.id.child_textview, rightSearchArray)
        binding.rightSearchListII.adapter =
            ArrayAdapter(requireContext(), R.layout.apps_child, R.id.child_textview, rightSearchArrayII)
    }

    // fetch all the installed apps and sort them
    private val getAppsList: Unit
        get() {
            // fetch all the installed apps
            packageInfoList = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                packageManager.queryIntentActivities(
                    Intent(Intent.ACTION_MAIN, null).addCategory(Intent.CATEGORY_LAUNCHER), PackageManager.ResolveInfoFlags.of(0))
            } else {
                @Suppress("DEPRECATION") packageManager.queryIntentActivities(
                    Intent(Intent.ACTION_MAIN, null).addCategory(Intent.CATEGORY_LAUNCHER), 0)
            }
            // sort the list
            (packageInfoList as MutableList<ResolveInfo>).sortWith(ResolveInfo.DisplayNameComparator(packageManager))
        }

    private fun fetchApps() {
        getAppsList
        // add package and app names to the list
        packagesList.clear()
        for (resolver in packageInfoList) {
            val packages = Packages(resolver.activityInfo.packageName, resolver.loadLabel(packageManager).toString())
            packagesList.add(packages)
        }

        if (packagesList.size < 1) {
            return
        } else {
            appsAdapter.updateData(packagesList)
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
                    leftSearchArrayII.size - 1 -> binding.appsList.smoothScrollToPosition(packagesList.size - 1)
                    else -> searchClickHelper(adapterView, i)
                }
            }
        // right column 1
        binding.rightSearchList.onItemClickListener =
            OnItemClickListener { adapterView: AdapterView<*>, _: View?, i: Int, _: Long ->
                when (i) {
                    rightSearchArray.size - 1 -> binding.appsList.smoothScrollToPosition(0)
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
        if (packagesList.size < 2) return

        binding.searchLayout.visibility = View.VISIBLE
        val string = binding.searchInput.text.toString() + adapterView.getItemAtPosition(i).toString()
        searchStringChangeListener(string)

        val sharedPreferences = requireContext().getSharedPreferences(Constants().PREFS_SETTINGS, MODE_PRIVATE)
        if (sharedPreferences.getBoolean(Constants().KEY_KEYBOARD_SEARCH, false)) {
            binding.searchInput.requestFocus()
            val inputMethodManager = fragmentActivity.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            inputMethodManager.showSoftInput(binding.searchInput, InputMethodManager.SHOW_IMPLICIT)
        }
    }

    private fun searchStringRemover() {
        binding.backspace.setOnClickListener {
            if (binding.searchInput.text.toString().isNotEmpty()) {
                val string = binding.searchInput.text.toString().substring(0, binding.searchInput.text.toString().length - 1)
                searchStringChangeListener(string)

                if (binding.searchInput.text.toString().isEmpty()) {
                    binding.searchLayout.visibility = View.GONE
                }
            }
        }

        binding.close.setOnClickListener { closeSearch() }
    }

    private fun searchStringChangeListener(string: String) {
        binding.searchInput.text = SpannableStringBuilder(string)
        binding.searchInput.doOnTextChanged { inputText, _, _, _ ->
            binding.searchInput.setSelection(binding.searchInput.text.toString().length)
            filterAppsList(inputText.toString())
        }
    }

    private fun filterAppsList(searchString: String) {
        /* check each package name and add only the ones
            that match the search string */
        packagesList.clear()
        for (resolver in packageInfoList) {
            val appName = resolver.loadLabel(packageManager).toString()
            if (appName.replace("[^a-zA-Z0-9]".toRegex(), "").lowercase(Locale.getDefault())
                    .contains(searchString)) {
                val packages = Packages(resolver.activityInfo.packageName, appName)
                packagesList.add(packages)
            }
        }

        if (packagesList.size == 1) {
            startActivity(packageManager.getLaunchIntentForPackage(packagesList[0].packageName))
        } else {
            appsAdapter.updateData(packagesList)
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
}
