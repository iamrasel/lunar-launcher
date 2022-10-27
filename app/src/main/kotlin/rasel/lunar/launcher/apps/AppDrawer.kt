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
    private val constants = Constants()
    private val uniUtils = UniUtils()

    /* items for search columns */
    private val leftSearchArray = arrayOf("a", "b", "c", "d", "e", "f", "g", "h", "i", "j", "k", "l", "m")
    private val leftSearchArrayII = arrayOf("0", "1", "2", "3", "4", "\u290B")
    private val rightSearchArray = arrayOf("9", "8", "7", "6", "5", "\u290A")
    private val rightSearchArrayII = arrayOf("z", "y", "x", "w", "v", "u", "t", "s", "r", "q", "p", "o", "n")

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = AppDrawerBinding.inflate(inflater, container, false)

        /* set up insets and search columns */
        setInsets()
        setupSearchColumns()

        fragmentActivity = if (isAdded) {
            requireActivity()
        } else {
            LauncherActivity()
        }

        packageManager = fragmentActivity.packageManager
        appsAdapter = AppsAdapter(packageManager, childFragmentManager, binding.appsCount)

        /* initialize apps list adapter */
        binding.appsList.adapter = appsAdapter.also { adapter ->
                adapter.updateData(packagesList)
        }

        return binding.root
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        /* listen search item and string remover clicks */
        controlOnSearchActions()
        searchStringRemover()

        /* gestures */
        binding.root.setOnTouchListener(object : SwipeTouchListener(context) {
            /* expand notification panel on swipe down */
            override fun onSwipeDown() {
                super.onSwipeDown()
                uniUtils.expandNotificationPanel(requireContext())
            }
            /* lock screen on double tap */
            override fun onDoubleClick() {
                super.onDoubleClick()
                uniUtils.lockMethod(
                    requireContext().getSharedPreferences(constants.PREFS_SETTINGS, 0)
                        .getInt(constants.KEY_LOCK_METHOD, 0), requireContext(), fragmentActivity)
            }
        })
    }

    override fun onResume() {
        super.onResume()
        closeSearch()
        fetchApps()
    }

    /* insets */
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

    /* search column adapters */
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

    /* get all the installed apps and sort them */
    private val getAppsList: Unit get() {
        packageInfoList = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            packageManager.queryIntentActivities(
                Intent(Intent.ACTION_MAIN, null).addCategory(Intent.CATEGORY_LAUNCHER),
                PackageManager.ResolveInfoFlags.of(0)
            )
        } else {
            @Suppress("DEPRECATION")
            packageManager.queryIntentActivities(
                Intent(Intent.ACTION_MAIN, null).addCategory(Intent.CATEGORY_LAUNCHER), 0)
        }

        /* sort the list */
        (packageInfoList as MutableList<ResolveInfo>).sortWith(ResolveInfo.DisplayNameComparator(packageManager))
    }

    /* update app list with app and package name */
    private fun fetchApps() {
        getAppsList
        /* add package and app names to the list */
        packagesList.clear()
        for (resolver in packageInfoList) {
            val packages = Packages(resolver.activityInfo.packageName, resolver.loadLabel(packageManager).toString())
            packagesList.add(packages)
        }

        if (packagesList.size < 1) {
            return
        } else {
            /* update the list */
            appsAdapter.updateData(packagesList)
        }
    }

    /* listen search button clicks */
    private fun controlOnSearchActions() {
        /* left column 1 */
        binding.leftSearchList.onItemClickListener =
            OnItemClickListener { adapterView: AdapterView<*>, _: View?, i: Int, _: Long ->
                searchClickHelper(adapterView, i)
            }
        /* left column 2 */
        binding.leftSearchListII.onItemClickListener =
            OnItemClickListener { adapterView: AdapterView<*>, _: View?, i: Int, _: Long ->
                when (i) {
                    /* go bottom */
                    leftSearchArrayII.size - 1 -> binding.appsList.smoothScrollToPosition(packagesList.size - 1)
                    else -> searchClickHelper(adapterView, i)
                }
            }
        /* right column 1 */
        binding.rightSearchList.onItemClickListener =
            OnItemClickListener { adapterView: AdapterView<*>, _: View?, i: Int, _: Long ->
                when (i) {
                    /* go top */
                    rightSearchArray.size - 1 -> binding.appsList.smoothScrollToPosition(0)
                    else -> searchClickHelper(adapterView, i)
                }
            }
        /* right column 2 */
        binding.rightSearchListII.onItemClickListener =
            OnItemClickListener { adapterView: AdapterView<*>, _: View?, i: Int, _: Long ->
                searchClickHelper(adapterView, i)
            }
    }

    private fun searchClickHelper(adapterView: AdapterView<*>, i: Int) {
        if (packagesList.size < 2) return

        /* show search box and build search string */
        binding.searchLayout.visibility = View.VISIBLE
        val string = binding.searchInput.text.toString() + adapterView.getItemAtPosition(i).toString()
        searchStringChangeListener(string)

        /* pop up the keyboard */
        val sharedPreferences = requireContext().getSharedPreferences(constants.PREFS_SETTINGS, 0)
        if (sharedPreferences.getBoolean(constants.KEY_KEYBOARD_SEARCH, false)) {
            binding.searchInput.requestFocus()
            val inputMethodManager = fragmentActivity.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            inputMethodManager.showSoftInput(binding.searchInput, InputMethodManager.SHOW_IMPLICIT)
        }
    }

    private fun searchStringRemover() {
        binding.backspace.setOnClickListener {
            if (binding.searchInput.text.toString().isNotEmpty()) {
                /* remove search string one by one */
                val string = binding.searchInput.text.toString().substring(0, binding.searchInput.text.toString().length - 1)
                searchStringChangeListener(string)

                /* hide search box when there's nothing left */
                if (binding.searchInput.text.toString().isEmpty()) {
                    binding.searchLayout.visibility = View.GONE
                }
            }
        }

        binding.close.setOnClickListener { closeSearch() }
    }

    /* add search string to the search box and filter list accordingly */
    private fun searchStringChangeListener(string: String) {
        binding.searchInput.text = SpannableStringBuilder(string)
        binding.searchInput.doOnTextChanged { inputText, _, _, _ ->
            binding.searchInput.setSelection(binding.searchInput.text.toString().length)
            filterAppsList(inputText.toString())
        }
    }

    private fun filterAppsList(searchString: String) {
        /* check each app name and add if it matches the search string */
        packagesList.clear()
        for (resolver in packageInfoList) {
            val appName = resolver.loadLabel(packageManager).toString()
            /* ignore symbols except a-z, A-Z or 0-9 */
            if (appName.replace("[^a-zA-Z0-9]".toRegex(), "").lowercase(Locale.getDefault())
                    .contains(searchString)) {
                val packages = Packages(resolver.activityInfo.packageName, appName)
                packagesList.add(packages)
            }
        }

        if (packagesList.size == 1) {
            /* if only one app found, then launch it */
            startActivity(packageManager.getLaunchIntentForPackage(packagesList[0].packageName))
        } else {
            /* update the app list with filtered result */
            appsAdapter.updateData(packagesList)
        }
    }

    /* clear search string, hide keyboard and search box */
    private fun closeSearch() {
        binding.searchInput.text?.clear()
        binding.searchInput.let { view ->
            val inputMethodManager = fragmentActivity.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            inputMethodManager.hideSoftInputFromWindow(view.windowToken, 0)
        }
        binding.searchLayout.visibility = View.GONE
    }

}
