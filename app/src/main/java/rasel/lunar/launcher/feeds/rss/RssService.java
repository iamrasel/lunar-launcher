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

package rasel.lunar.launcher.feeds.rss;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.ResultReceiver;

import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.net.URL;
import java.util.List;

import rasel.lunar.launcher.helpers.Constants;

public class RssService extends IntentService {

    private final Constants constants = new Constants();

    public RssService() {
        super("RssService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        SharedPreferences settingsPrefs = getSharedPreferences(constants.SHARED_PREFS_SETTINGS, Context.MODE_PRIVATE);
        String rssUrl = settingsPrefs.getString(constants.SHARED_PREF_FEED_URL, null);

        List<RSS> rssItems = null;
        try {
            RssParser parser = new RssParser();
            rssItems = parser.parse(getInputStream(rssUrl));
        } catch (Exception exception) {
            exception.printStackTrace();
        }
        Bundle bundle = new Bundle();
        bundle.putSerializable(constants.RSS_ITEMS, (Serializable) rssItems);
        ResultReceiver receiver = intent.getParcelableExtra(constants.RSS_RECEIVER);
        receiver.send(0, bundle);
    }

    private InputStream getInputStream(String link) {
        try {
            URL url = new URL(link);
            return url.openConnection().getInputStream();
        } catch (IOException ioException) {
            ioException.printStackTrace();
            return null;
        }
    }
}
