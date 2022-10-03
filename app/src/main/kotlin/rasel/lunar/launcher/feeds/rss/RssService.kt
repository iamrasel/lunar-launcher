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

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.os.ResultReceiver
import androidx.core.app.JobIntentService
import rasel.lunar.launcher.feeds.Feeds
import rasel.lunar.launcher.helpers.Constants
import java.io.IOException
import java.io.InputStream
import java.io.Serializable
import java.net.URL

internal class RssService : JobIntentService() {   // Todo: deprecated

    override fun onHandleWork(intent: Intent) {
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

        val receiver = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            intent.getParcelableExtra(Constants().RSS_RECEIVER, ResultReceiver::class.java)
        } else {
            @Suppress("DEPRECATION") intent.getParcelableExtra(Constants().RSS_RECEIVER)
        }
        try {
            receiver?.send(0, bundle)
        } catch (exception: Exception) {
            exception.printStackTrace()
        }
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