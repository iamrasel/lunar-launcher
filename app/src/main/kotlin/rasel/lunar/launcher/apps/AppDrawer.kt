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
import android.content.SharedPreferences
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
import rasel.lunar.launcher.BuildConfig
import rasel.lunar.launcher.LauncherActivity.Companion.lActivity
import rasel.lunar.launcher.R
import rasel.lunar.launcher.databinding.AppDrawerBinding
import rasel.lunar.launcher.helpers.Constants.Companion.KEY_KEYBOARD_SEARCH
import rasel.lunar.launcher.helpers.Constants.Companion.KEY_LOCK_METHOD
import rasel.lunar.launcher.helpers.Constants.Companion.KEY_QUICK_LAUNCH
import rasel.lunar.launcher.helpers.Constants.Companion.PREFS_SETTINGS
import rasel.lunar.launcher.helpers.SwipeTouchListener
import rasel.lunar.launcher.helpers.UniUtils.Companion.expandNotificationPanel
import rasel.lunar.launcher.helpers.UniUtils.Companion.lockMethod
import java.util.*


internal class AppDrawer : Fragment() {

    private lateinit var binding: AppDrawerBinding
    private lateinit var packageManager: PackageManager
    private lateinit var appsAdapter: AppsAdapter
    private lateinit var settingsPrefs: SharedPreferences
    private lateinit var packageInfoList: MutableList<ResolveInfo>
    private var packageList = mutableListOf<Packages>()

    /* items for search columns */
    private val leftSearchArray = arrayOf("a", "b", "c", "d", "e", "f", "g", "h", "i", "j", "k", "l", "m")
    private val leftSearchArrayII = arrayOf("0", "1", "2", "3", "4", "\u290B")
    private val rightSearchArray = arrayOf("9", "8", "7", "6", "5", "\u290A")
    private val rightSearchArrayII = arrayOf("z", "y", "x", "w", "v", "u", "t", "s", "r", "q", "p", "o", "n")

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = AppDrawerBinding.inflate(inflater, container, false)

        /* set up search columns */
        setupSearchColumns()

        packageManager = lActivity!!.packageManager
        appsAdapter = AppsAdapter(packageManager, childFragmentManager, binding.appsCount)
        settingsPrefs = requireContext().getSharedPreferences(PREFS_SETTINGS, 0)

        /* initialize apps list adapter */
        binding.appsList.adapter = appsAdapter
        fetchApps()

        return binding.root
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        /* listen search item and string remover clicks */
        controlOnSearchActions()
        searchStringRemover()
        binding.searchInput.doOnTextChanged { inputText, _, _, _ ->
            binding.searchInput.text?.let { binding.searchInput.setSelection(it.length) }
            filterAppsList(inputText.toString())
        }

        /* gestures */
        binding.root.setOnTouchListener(object : SwipeTouchListener(context) {
            /* expand notification panel on swipe down */
            override fun onSwipeDown() {
                super.onSwipeDown()
                expandNotificationPanel(requireContext())
            }
            /* lock screen on double tap */
            override fun onDoubleClick() {
                super.onDoubleClick()
                lockMethod(settingsPrefs.getInt(KEY_LOCK_METHOD, 0), requireContext())
            }
        })
    }

    override fun onResume() {
        super.onResume()
        fetchApps()

        /* pop up the keyboard */
        if (settingsPrefs.getBoolean(KEY_KEYBOARD_SEARCH, false)) {
            binding.searchLayout.visibility = View.VISIBLE
            binding.searchInput.requestFocus()
            val inputMethodManager = lActivity!!.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            inputMethodManager.showSoftInput(binding.searchInput, InputMethodManager.SHOW_IMPLICIT)
        }
    }

    override fun onPause() {
        super.onPause()
        closeSearch()
    }

    /* search column adapters */
    private fun setupSearchColumns() {
        binding.leftSearchList.adapter =
            ArrayAdapter(requireContext(), R.layout.apps_child, R.id.childTextview, leftSearchArray)
        binding.leftSearchListII.adapter =
            ArrayAdapter(requireContext(), R.layout.apps_child, R.id.childTextview, leftSearchArrayII)
        binding.rightSearchList.adapter =
            ArrayAdapter(requireContext(), R.layout.apps_child, R.id.childTextview, rightSearchArray)
        binding.rightSearchListII.adapter =
            ArrayAdapter(requireContext(), R.layout.apps_child, R.id.childTextview, rightSearchArrayII)
    }

    /* update app list with app and package name */
    private fun fetchApps() {
        packageInfoList = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            packageManager.queryIntentActivities(
                Intent(Intent.ACTION_MAIN, null).addCategory(Intent.CATEGORY_LAUNCHER),
                PackageManager.ResolveInfoFlags.of(0)
            )
        } else {
            @Suppress("DEPRECATION")
            packageManager.queryIntentActivities(
                Intent(Intent.ACTION_MAIN, null).addCategory(Intent.CATEGORY_LAUNCHER), 0)
        }.apply {
            removeIf { it.activityInfo.packageName.equals(BuildConfig.APPLICATION_ID) }
            sortWith(ResolveInfo.DisplayNameComparator(packageManager))
        }

        /* add package and app names to the list */
        packageList.clear()
        for (resolver in packageInfoList) {
            packageList.add(Packages(resolver.activityInfo.packageName, resolver.loadLabel(packageManager).toString()))
        }

        if (packageList.size < 1) return
        else {
            /* update the list */
            binding.loading.visibility = View.GONE
            appsAdapter.updateData(packageList)
        }
    }

    /* listen search button clicks */
    private fun controlOnSearchActions() {
        /* left column 1 */
        binding.leftSearchList.onItemClickListener =
            OnItemClickListener { adapterView: AdapterView<*>, _: View?, i: Int, _: Long ->
                searchClickHelper(adapterView.getItemAtPosition(i))
            }
        /* left column 2 */
        binding.leftSearchListII.onItemClickListener =
            OnItemClickListener { adapterView: AdapterView<*>, _: View?, i: Int, _: Long ->
                when (i) {
                    /* go bottom */
                    leftSearchArrayII.size - 1 -> binding.appsList.smoothScrollToPosition(packageList.size - 1)
                    else -> searchClickHelper(adapterView.getItemAtPosition(i))
                }
            }
        /* right column 1 */
        binding.rightSearchList.onItemClickListener =
            OnItemClickListener { adapterView: AdapterView<*>, _: View?, i: Int, _: Long ->
                when (i) {
                    /* go top */
                    rightSearchArray.size - 1 -> binding.appsList.smoothScrollToPosition(0)
                    else -> searchClickHelper(adapterView.getItemAtPosition(i))
                }
            }
        /* right column 2 */
        binding.rightSearchListII.onItemClickListener =
            OnItemClickListener { adapterView: AdapterView<*>, _: View?, i: Int, _: Long ->
                searchClickHelper(adapterView.getItemAtPosition(i))
            }
    }

    private fun searchClickHelper(clickedItem: Any) {
        if (packageList.size < 2) return

        /* show search box and build search string */
        if (binding.searchLayout.visibility != View.VISIBLE) binding.searchLayout.visibility = View.VISIBLE
        binding.searchInput.text = SpannableStringBuilder(binding.searchInput.text.toString() + clickedItem)
    }

    private fun searchStringRemover() {
        binding.backspace.setOnClickListener {
            if (binding.searchInput.text.toString().isNotEmpty()) {
                /* remove search string one by one */
                binding.searchInput.text = SpannableStringBuilder(
                    binding.searchInput.text.toString().substring(0, binding.searchInput.text!!.length - 1))

                /* hide search box when there's nothing left */
                if (binding.searchInput.text.toString().isEmpty()) binding.searchLayout.visibility = View.GONE
            }
        }

        binding.close.setOnClickListener { closeSearch() }
    }

    private fun filterAppsList(searchString: String) {
        /* check each app name and add if it matches the search string */
        packageList.clear()
        for (resolver in packageInfoList) {
            val appName = resolver.loadLabel(packageManager).toString()
            if (appName.replace("\\W".toRegex(), "").lowercase(Locale.getDefault())
                    .contains(searchString)) {
                packageList.add(Packages(resolver.activityInfo.packageName, appName))
            }
        }

        if (packageList.size == 1 && settingsPrefs.getBoolean(KEY_QUICK_LAUNCH, true))
            startActivity(packageManager.getLaunchIntentForPackage(packageList[0].packageName))
        else
            appsAdapter.updateData(packageList)
    }

    /* clear search string, hide keyboard and search box */
    private fun closeSearch() {
        binding.searchInput.text?.clear()
        binding.searchInput.let { view ->
            (lActivity!!.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager)
                .hideSoftInputFromWindow(view.windowToken, 0)
        }
        binding.searchLayout.visibility = View.GONE
    }

}
