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
import rasel.lunar.launcher.helpers.Constants.Companion.KEY_RSS_URL
import rasel.lunar.launcher.helpers.Constants.Companion.PREFS_SETTINGS
import rasel.lunar.launcher.helpers.Constants.Companion.RSS_ITEMS
import rasel.lunar.launcher.helpers.Constants.Companion.RSS_RECEIVER
import java.io.IOException
import java.io.InputStream
import java.io.Serializable
import java.net.URL


internal class RssService : JobIntentService() {

    override fun onHandleWork(intent: Intent) {
        val settingsPrefs = getSharedPreferences(PREFS_SETTINGS, 0)
        val rssUrl = settingsPrefs.getString(KEY_RSS_URL, "")
        var rssItems: List<Rss?>? = null

        try {
            val parser = RssParser()
            rssItems = getInputStream(rssUrl)?.let { parser.parse(it) }
        } catch (exception: Exception) {
            exception.printStackTrace()
        }

        val bundle = Bundle()
        bundle.putSerializable(RSS_ITEMS, rssItems as? Serializable)

        val receiver = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            intent.getParcelableExtra(RSS_RECEIVER, ResultReceiver::class.java)
        } else {
            @Suppress("DEPRECATION") intent.getParcelableExtra(RSS_RECEIVER)
        }

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
