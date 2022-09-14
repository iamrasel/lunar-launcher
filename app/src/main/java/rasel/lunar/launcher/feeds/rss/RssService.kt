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

package rasel.lunar.launcher.feeds.rss

import android.app.IntentService
import android.content.Intent
import android.os.Bundle
import android.os.ResultReceiver
import rasel.lunar.launcher.helpers.Constants
import java.io.IOException
import java.io.InputStream
import java.io.Serializable
import java.net.URL

internal class RssService : IntentService("RssService") {   // Todo: deprecated

    override fun onHandleIntent(intent: Intent?) {
        val settingsPrefs = getSharedPreferences(Constants().SHARED_PREFS_SETTINGS, MODE_PRIVATE)
        val rssUrl = settingsPrefs.getString(Constants().SHARED_PREF_FEED_URL, "")
        var rssItems: List<RSS?>? = null
        try {
            val parser = RssParser()
            rssItems = getInputStream(rssUrl)?.let { parser.parse(it) }
        } catch (exception: Exception) {
            exception.printStackTrace()
        }
        val bundle = Bundle()
        bundle.putSerializable(Constants().RSS_ITEMS, rssItems as Serializable)
        val receiver = intent?.getParcelableExtra<ResultReceiver>(Constants().RSS_RECEIVER)
        receiver?.send(0, bundle)
    }

    private fun getInputStream(link: String?): InputStream? {
        return try {
            val url = URL(link)
            url.openConnection().getInputStream()
        } catch (ioException: IOException) {
            ioException.printStackTrace()
            null
        }
    }
}