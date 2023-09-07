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
import android.content.pm.PackageManager
import android.content.res.Resources
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.util.Log
import androidx.core.content.res.ResourcesCompat
import org.xmlpull.v1.XmlPullParser
import org.xmlpull.v1.XmlPullParserException
import org.xmlpull.v1.XmlPullParserFactory
import rasel.lunar.launcher.LauncherActivity.Companion.lActivity
import rasel.lunar.launcher.helpers.Constants.Companion.DEFAULT_ICON_PACK
import rasel.lunar.launcher.helpers.Constants.Companion.KEY_ICON_PACK
import rasel.lunar.launcher.helpers.Constants.Companion.PREFS_SETTINGS
import java.io.IOException
import java.util.Locale


internal class IconPackManager {

    @SuppressLint("DiscouragedApi")
    companion object {

        private val settingsPrefs = lActivity!!.getSharedPreferences(PREFS_SETTINGS, 0)
        private val packageName = settingsPrefs.getString(KEY_ICON_PACK, DEFAULT_ICON_PACK)
        private var loaded = false
        private val packagesDrawables = HashMap<String?, String?>()
        private val backImages: MutableList<Bitmap> = ArrayList()
        private var maskImage: Bitmap? = null
        private var frontImage: Bitmap? = null
        private var factor = 1.0f
        private var totalIcons = 0
        private var iconPackRes: Resources? = null

        private fun load() {
            /* load appfilter.xml from the icon pack package */
            try {
                var xpp: XmlPullParser? = null
                iconPackRes = lActivity!!.packageManager.getResourcesForApplication(packageName!!)
                val appFilterId = iconPackRes!!.getIdentifier("appfilter", "xml", packageName)
                if (appFilterId > 0) {
                    xpp = iconPackRes!!.getXml(appFilterId)
                } else {
                    /* no resource found, try to open it from assets folder */
                    try {
                        xpp = XmlPullParserFactory.newInstance().apply { isNamespaceAware = true }
                            .newPullParser().apply { setInput(iconPackRes!!.assets.open("appfilter.xml"), "utf-8") }
                    } catch (e: IOException) {
                        e.printStackTrace()
                        Log.w("", "Couldn't find the appfilter.xml file")
                    }
                }
                if (xpp != null) {
                    var eventType = xpp.eventType
                    while (eventType != XmlPullParser.END_DOCUMENT) {
                        if (eventType == XmlPullParser.START_TAG) {
                            when (xpp.name) {
                                "iconback" -> {
                                    for (i in 0 until xpp.attributeCount) {
                                        if (xpp.getAttributeName(i).startsWith("img")) {
                                            loadBitmap(xpp.getAttributeValue(i))?.let { backImages.add(it) }
                                        }
                                    }
                                }
                                "iconmask" -> {
                                    if (xpp.attributeCount > 0 && xpp.getAttributeName(0) == "img1") {
                                        maskImage = loadBitmap(xpp.getAttributeValue(0))
                                    }
                                }
                                "iconupon" -> {
                                    if (xpp.attributeCount > 0 && xpp.getAttributeName(0) == "img1") {
                                        frontImage = loadBitmap(xpp.getAttributeValue(0))
                                    }
                                }
                                "scale" -> {
                                    if (xpp.attributeCount > 0 && xpp.getAttributeName(0) == "factor") {
                                        factor = java.lang.Float.valueOf(xpp.getAttributeValue(0))
                                    }
                                }
                                "item" -> {
                                    var componentName: String? = null
                                    var drawableName: String? = null
                                    for (i in 0 until xpp.attributeCount) {
                                        when (xpp.getAttributeName(i)) {
                                            "component" -> componentName = xpp.getAttributeValue(i)
                                            "drawable" -> drawableName = xpp.getAttributeValue(i)
                                        }
                                    }
                                    if (!packagesDrawables.containsKey(componentName)) {
                                        packagesDrawables[componentName] = drawableName
                                        totalIcons += 1
                                    }
                                }
                            }
                        }
                        eventType = xpp.next()
                    }
                }
                loaded = true
            } catch (e: PackageManager.NameNotFoundException) {
                Log.w("", "Failed to load the icon pack")
            } catch (e: XmlPullParserException) {
                Log.w("", "Failed to parse the appfilter.xml file")
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }

        private fun loadBitmap(drawableName: String): Bitmap? {
            iconPackRes!!.getIdentifier(drawableName, "drawable", packageName).let { id ->
                if (id > 0) {
                    ResourcesCompat.getDrawable(iconPackRes!!, id, null).let {
                        if (it is BitmapDrawable) return it.bitmap
                    }
                }
            }
            return null
        }

        private fun loadDrawable(drawableName: String): Drawable? {
            iconPackRes!!.getIdentifier(drawableName, "drawable", packageName).let {
                return if (it > 0) ResourcesCompat.getDrawable(iconPackRes!!, it, null)
                else null
            }
        }

        fun getDrawableIconForPackage(appPackageName: String?, defaultDrawable: Drawable?): Drawable? {
            when (packageName) {
                DEFAULT_ICON_PACK -> return defaultDrawable
                else -> {
                    if (!loaded) load()
                    var componentName: String? = null
                    if (lActivity!!.packageManager.getLaunchIntentForPackage(appPackageName!!) != null) {
                        componentName = lActivity!!.packageManager.getLaunchIntentForPackage(appPackageName)!!.component.toString()
                    }
                    var drawable = packagesDrawables[componentName]
                    if (!drawable.isNullOrEmpty()) return loadDrawable(drawable)
                    else {
                        /* try to get a resource with the component filename */
                        if (!componentName.isNullOrEmpty()) {
                            val start = componentName.indexOf("{") + 1
                            val end = componentName.indexOf("}", start)
                            if (end > start) {
                                drawable = componentName.substring(start, end).lowercase(Locale.getDefault()).replace(".", "_").replace("/", "_")
                                try {
                                    if (iconPackRes!!.getIdentifier(drawable, "drawable", packageName) > 0) return loadDrawable(drawable)
                                } catch (e: NullPointerException) {
                                    settingsPrefs.edit().putString(KEY_ICON_PACK, DEFAULT_ICON_PACK).apply()
                                }
                            }
                        }
                    }
                    return defaultDrawable
                }
            }
        }

    }
}
