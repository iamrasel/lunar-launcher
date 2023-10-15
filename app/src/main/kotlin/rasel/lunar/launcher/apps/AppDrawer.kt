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

package rasel.lunar.launcher.apps

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.content.pm.ResolveInfo
import android.graphics.Rect
import android.os.Build
import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import androidx.core.view.updateLayoutParams
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.textview.MaterialTextView
import rasel.lunar.launcher.BuildConfig
import rasel.lunar.launcher.LauncherActivity.Companion.lActivity
import rasel.lunar.launcher.R
import rasel.lunar.launcher.databinding.AppDrawerBinding
import rasel.lunar.launcher.helpers.Constants.Companion.DEFAULT_GRID_COLUMNS
import rasel.lunar.launcher.helpers.Constants.Companion.DEFAULT_SCROLLBAR_HEIGHT
import rasel.lunar.launcher.helpers.Constants.Companion.KEY_APPS_COUNT
import rasel.lunar.launcher.helpers.Constants.Companion.KEY_APPS_LAYOUT
import rasel.lunar.launcher.helpers.Constants.Companion.KEY_DRAW_ALIGN
import rasel.lunar.launcher.helpers.Constants.Companion.KEY_GRID_COLUMNS
import rasel.lunar.launcher.helpers.Constants.Companion.KEY_KEYBOARD_SEARCH
import rasel.lunar.launcher.helpers.Constants.Companion.KEY_QUICK_LAUNCH
import rasel.lunar.launcher.helpers.Constants.Companion.KEY_SCROLLBAR_HEIGHT
import rasel.lunar.launcher.helpers.Constants.Companion.KEY_STATUS_BAR
import rasel.lunar.launcher.helpers.Constants.Companion.PREFS_SETTINGS
import java.text.Normalizer
import java.util.*
import java.util.regex.Pattern


internal class AppDrawer : Fragment() {

    private lateinit var binding: AppDrawerBinding
    private var layoutType: Int = 0
    private var isSearchShown: Boolean = false
    private var isKeyboardShowing: Boolean = false

    companion object {
        private var packageManager: PackageManager? = null
        private var appsAdapter: AppsAdapter? = null
        private var packageInfoList: MutableList<ResolveInfo> = mutableListOf()
        private var packageList = mutableListOf<Packages>()
        private val numberPattern = Pattern.compile("[0-9]")
        private val alphabetPattern = Pattern.compile("[A-Z]")
        @JvmStatic var settingsPrefs: SharedPreferences? = null
        @JvmStatic var alphabetList = mutableListOf<String>()
        @JvmStatic var letterPreview: MaterialTextView? = null

        fun listenScroll(letter: String) {
            packageList.clear()
            for (resolver in packageInfoList) {
                val appName = resolver.loadLabel(packageManager).toString()
                when {
                    letter == "#" -> {
                        if (numberPattern.matcher(appName.first().uppercase()).matches()) {
                            packageList.add(Packages(resolver.activityInfo.packageName, appName))
                        }
                    }
                    alphabetPattern.matcher(letter).matches() -> {
                        if (appName.first().uppercase() == letter) {
                            packageList.add(Packages(resolver.activityInfo.packageName, appName))
                        }
                    }
                    letter == "⠶" -> {
                        if (!numberPattern.matcher(appName.first().uppercase()).matches() &&
                            !alphabetPattern.matcher(appName.first().uppercase()).matches()) {
                            packageList.add(Packages(resolver.activityInfo.packageName, appName))
                        }
                    }
                }
            }
            appsAdapter?.updateData(packageList)
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = AppDrawerBinding.inflate(inflater, container, false)

        settingsPrefs = requireContext().getSharedPreferences(PREFS_SETTINGS, 0)
        layoutType = settingsPrefs!!.getInt(KEY_APPS_LAYOUT, 0)
        packageManager = lActivity?.packageManager
        appsAdapter = AppsAdapter(layoutType, packageManager!!, childFragmentManager, binding.appsCount)
        letterPreview = binding.appsCount

        binding.appsCount.visibility = if (settingsPrefs!!.getBoolean(KEY_APPS_COUNT, true)) VISIBLE else GONE

        setLayout()
        fetchApps()
        getAlphabetItems()
        setKeyboardPadding()

        return binding.root
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.reset.setOnClickListener { onResume() }

        binding.moveDown.setOnClickListener {
            binding.appsList.smoothScrollToPosition(packageList.size - 1)
        }

        binding.moveUp.setOnClickListener {
            binding.appsList.smoothScrollToPosition(0)
        }

        binding.search.setOnClickListener {
            when (isSearchShown) {
                true -> closeSearch()
                false -> openSearch()
            }
        }

        binding.searchInput.doOnTextChanged { inputText, _, _, _ ->
            binding.searchInput.text?.let { binding.searchInput.setSelection(it.length) }
            filterAppsList(inputText.toString())
        }
    }

    override fun onResume() {
        super.onResume()
        fetchApps()
        getAlphabetItems()

        binding.appsCount.visibility = if (settingsPrefs!!.getBoolean(KEY_APPS_COUNT, true)) VISIBLE else GONE

        if (settingsPrefs!!.getInt(KEY_APPS_LAYOUT, 0) in 0..1) {
            appsAdapter?.updateGravity(settingsPrefs!!.getInt(KEY_DRAW_ALIGN, Gravity.CENTER))
        }

        /* pop up the keyboard */
        if (settingsPrefs!!.getBoolean(KEY_KEYBOARD_SEARCH, false)) openSearch()
    }

    override fun onPause() {
        super.onPause()
        closeSearch()
    }

    private fun setLayout() {
        when (layoutType) {
            0, 1 -> {
                binding.appsList.layoutManager = LinearLayoutManager(requireContext())
                appsAdapter!!.updateGravity(settingsPrefs!!.getInt(KEY_DRAW_ALIGN, Gravity.CENTER))
            }
            2 -> binding.appsList.layoutManager = GridLayoutManager(requireContext(), settingsPrefs!!.getInt(KEY_GRID_COLUMNS, DEFAULT_GRID_COLUMNS))
        }

        /* initialize apps list adapter */
        binding.appsList.adapter = appsAdapter
    }

    /* update app list with app and package name */
    private fun fetchApps() {
        packageInfoList = (if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            packageManager?.queryIntentActivities(
                Intent(Intent.ACTION_MAIN, null).addCategory(Intent.CATEGORY_LAUNCHER),
                PackageManager.ResolveInfoFlags.of(0)
            )
        } else {
            @Suppress("DEPRECATION")
            (packageManager?.queryIntentActivities(
                Intent(Intent.ACTION_MAIN, null).addCategory(Intent.CATEGORY_LAUNCHER), 0))
        })?.apply {
            removeIf { it.activityInfo.packageName.equals(BuildConfig.APPLICATION_ID) }
            sortWith(ResolveInfo.DisplayNameComparator(packageManager))
        }!!

        /* add package and app names to the list */
        packageList.clear()
        for (resolver in packageInfoList) {
            packageList.add(Packages(resolver.activityInfo.packageName, resolver.loadLabel(packageManager).toString()))
        }

        when {
            packageList.size < 1 -> return
            else -> appsAdapter?.updateData(packageList)
        }
    }

    private fun getAlphabetItems() {
        settingsPrefs!!.getInt(KEY_SCROLLBAR_HEIGHT, DEFAULT_SCROLLBAR_HEIGHT).let { height: Int ->
            if (height == 0) { binding.alphabets.visibility = GONE }
            else {
                binding.alphabets.apply {
                    if (visibility == GONE) visibility = VISIBLE
                    updateLayoutParams { this.height = height }
                }
                alphabetList.clear()
                for (mPackage in packageList) {
                    mPackage.appName.first().uppercase().let { firstLetter: String ->
                        when {
                            numberPattern.matcher(firstLetter).matches() -> alphabetList.add(0, "#")
                            alphabetPattern.matcher(firstLetter).matches() -> alphabetList.add(firstLetter)
                            !numberPattern.matcher(firstLetter).matches() &&
                                    !alphabetPattern.matcher(firstLetter).matches() -> alphabetList.add(alphabetList.size,"⠶")
                            else -> {}
                        }
                    }
                }
                binding.alphabets.invalidate()
            }
        }
    }

    private fun filterAppsList(searchString: String) {
        /* check each app name and add if it matches the search string */
        packageList.clear()
        for (resolver in packageInfoList) {
            resolver.loadLabel(packageManager).toString().let {
                if (normalize(it).contains(searchString)) {
                    packageList.add(Packages(resolver.activityInfo.packageName, it))
                }
            }
        }

        if (packageList.size == 1 && settingsPrefs!!.getBoolean(KEY_QUICK_LAUNCH, true))
            startActivity(packageManager?.getLaunchIntentForPackage(packageList[0].packageName))
        else appsAdapter?.updateData(packageList)
    }

    private fun normalize(str: String): String {
        val normalizedString =
            Normalizer.normalize(str.replace("\\W".toRegex(), ""), Normalizer.Form.NFD)
        val pattern = Pattern.compile("\\p{InCombiningDiacriticalMarks}+")
        return pattern.matcher(normalizedString).replaceAll("").lowercase()
    }

    private fun openSearch() {
        isSearchShown = true
        binding.search.setImageResource(R.drawable.ic_close)
        binding.searchInput.apply {
            visibility = VISIBLE
            requestFocus()
            let {
                (lActivity!!.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager)
                    .showSoftInput(it, InputMethodManager.SHOW_IMPLICIT)
            }
        }
    }

    /* clear search string, hide keyboard and search box */
    private fun closeSearch() {
        isSearchShown = false
        binding.search.setImageResource(R.drawable.ic_search)
        binding.searchInput.apply {
            text?.clear()
            visibility = GONE
            let {
                (lActivity!!.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager)
                    .hideSoftInputFromWindow(it.windowToken, 0)
            }
        }
    }

    private fun setKeyboardPadding() {
        binding.root.viewTreeObserver.addOnGlobalLayoutListener {
            val rect = Rect()
            binding.root.getWindowVisibleDisplayFrame(rect)
            val screenHeight = binding.root.height
            val keyboardHeight = screenHeight - (rect.bottom - rect.top)

            when {
                keyboardHeight > screenHeight * 0.15 -> {
                    if (!isKeyboardShowing &&
                        !settingsPrefs!!.getBoolean(KEY_STATUS_BAR, false)) {
                        isKeyboardShowing = true
                        binding.root.setPadding(0, 0, 0, keyboardHeight)
                    }
                }
                else -> {
                    if (isKeyboardShowing) {
                        isKeyboardShowing = false
                        binding.root.setPadding(0, 0, 0, 0)
                    }
                }
            }
        }
    }

}
