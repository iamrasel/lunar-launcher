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

package rasel.lunar.launcher.feeds;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.ResultReceiver;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import java.util.List;

import dev.chrisbanes.insetter.Insetter;
import rasel.lunar.launcher.LauncherActivity;
import rasel.lunar.launcher.databinding.FeedsBinding;
import rasel.lunar.launcher.feeds.rss.RSS;
import rasel.lunar.launcher.feeds.rss.RssAdapter;
import rasel.lunar.launcher.feeds.rss.RssService;
import rasel.lunar.launcher.helpers.Constants;
import rasel.lunar.launcher.helpers.UniUtils;

public class Feeds extends Fragment {

    private FeedsBinding binding;
    private FragmentActivity activity;
    private final Constants constants = new Constants();
    private FeedsUtils feedsUtils;
    private Handler handler;
    private Runnable runnable;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FeedsBinding.inflate(inflater, container, false);
        Insetter.builder()
                .padding(WindowInsetsCompat.Type.systemBars())
                .applyToView(binding.getRoot());

        if(isAdded()) {
            activity = requireActivity();
        } else {
            activity = new LauncherActivity();
        }

        feedsUtils = new FeedsUtils(activity);

        return binding.getRoot();
    }

    private void startService() {
        String rssUrl = (activity.getSharedPreferences(constants.SHARED_PREFS_SETTINGS, Context.MODE_PRIVATE))
                .getString(constants.SHARED_PREF_FEED_URL, "");

        if(new UniUtils().isNetworkAvailable(activity) && !rssUrl.isEmpty()) {
            Intent intent = new Intent(activity, RssService.class);
            intent.putExtra(constants.RSS_RECEIVER, resultReceiver);
            activity.startService(intent);
        } else {
            resumeService();
        }
    }

    private void resumeService() {
        binding.rss.setVisibility(View.GONE);
        binding.loadingProgress.setVisibility(View.GONE);
        binding.dataFetchingFailed.setVisibility(View.VISIBLE);
        binding.dataFetchingFailed.setOnClickListener(v -> startService());
    }

    private final ResultReceiver resultReceiver = new ResultReceiver(new Handler()) {
        @SuppressWarnings("unchecked")
        @Override
        protected void onReceiveResult(int resultCode, Bundle resultData) {
            List<RSS> items = (List<RSS>) resultData.getSerializable(constants.RSS_ITEMS);
            if(items != null) {
                binding.rss.setLayoutManager(new LinearLayoutManager(getContext()));
                binding.rss.setAdapter(new RssAdapter(items, getContext()));
                binding.dataFetchingFailed.setVisibility(View.GONE);
                binding.loadingProgress.setVisibility(View.GONE);
                binding.rss.setVisibility(View.VISIBLE);
            } else {
                resumeService();
            }
        }
    };

    @Override
    public void onResume() {
        super.onResume();
        startService();
        feedsUtils.ram(binding.ram);
        feedsUtils.cpuBattery(binding.cpu);
        feedsUtils.intStorage(binding.intStorage);
        feedsUtils.extStorage(binding.extStorage);

        handler = new Handler();
        runnable = new Runnable() {
            public void run() {
                feedsUtils.ram(binding.ram);
                feedsUtils.cpuBattery(binding.cpu);
                handler.postDelayed(this, 1000);
            }
        };
        handler.post(runnable);
    }

    @Override
    public void onPause() {
        super.onPause();
        handler.removeCallbacks(runnable);
    }
}
