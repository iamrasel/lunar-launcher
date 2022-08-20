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
import androidx.recyclerview.widget.LinearLayoutManager;

import java.util.List;

import dev.chrisbanes.insetter.Insetter;
import rasel.lunar.launcher.databinding.FeedsBinding;
import rasel.lunar.launcher.feeds.rss.RSS;
import rasel.lunar.launcher.feeds.rss.RssAdapter;
import rasel.lunar.launcher.feeds.rss.RssService;
import rasel.lunar.launcher.helpers.Constants;
import rasel.lunar.launcher.helpers.UniUtils;

public class Feeds extends Fragment {

    private FeedsBinding binding;
    private final Constants constants = new Constants();

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FeedsBinding.inflate(inflater, container, false);
        Insetter.builder()
                .padding(WindowInsetsCompat.Type.systemBars())
                .applyToView(binding.getRoot());

        String rssUrl = (requireActivity().getSharedPreferences(constants.SHARED_PREFS_SETTINGS, Context.MODE_PRIVATE))
                .getString(constants.SHARED_PREF_FEED_URL, null);

        if(new UniUtils().isNetworkAvailable(requireActivity()) && !rssUrl.isEmpty()) {
            startService();
        } else {
            binding.dataFetchingFailed.setVisibility(View.VISIBLE);
            binding.loadingProgress.setVisibility(View.GONE);
        }

        return binding.getRoot();
    }

    private void startService() {
        Intent intent = new Intent(getActivity(), RssService.class);
        intent.putExtra(constants.RSS_RECEIVER, resultReceiver);
        requireActivity().startService(intent);
    }

    private final ResultReceiver resultReceiver = new ResultReceiver(new Handler()) {
        @SuppressWarnings("unchecked")
        @Override
        protected void onReceiveResult(int resultCode, Bundle resultData) {
            List<RSS> items = (List<RSS>) resultData.getSerializable(constants.RSS_ITEMS);
            if(items != null) {
                binding.rss.setLayoutManager(new LinearLayoutManager(getContext()));
                binding.rss.setAdapter(new RssAdapter(items, getContext()));
                binding.loadingProgress.setVisibility(View.GONE);
                binding.rss.setVisibility(View.VISIBLE);
            } else {
                binding.loadingProgress.setVisibility(View.GONE);
                binding.dataFetchingFailed.setVisibility(View.VISIBLE);
            }
        }
    };
}
