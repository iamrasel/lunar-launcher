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

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.net.Uri;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import rasel.lunar.launcher.R;
import rasel.lunar.launcher.databinding.ListItemBinding;

public class RssAdapter extends RecyclerView.Adapter<RssAdapter.ViewHolder> {

    private final List<RSS> items;
    private final Context context;

    public RssAdapter(List<RSS> items, Context context) {
        this.items = items;
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ListItemBinding binding = ListItemBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new ViewHolder(binding);
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, @SuppressLint("RecyclerView") final int position) {
        holder.view.itemText.setSingleLine(false);
        holder.view.itemText.setText(items.get(position).getTitle());

        if(position == 0) {
            holder.view.itemText.setText("❚  " + items.get(position).getTitle() + "  ❚");
            holder.view.itemText.setGravity(Gravity.CENTER);
            holder.view.itemText.setTextColor(ContextCompat.getColor(context, R.color.not_primary));
            holder.view.itemText.setTypeface(null, Typeface.BOLD);
            holder.view.itemText.setTextSize(18);
        }

        holder.view.itemText.setOnClickListener(v -> {
            Uri uri = Uri.parse(items.get(position).getLink());
            Intent intent = new Intent(Intent.ACTION_VIEW, uri);
            context.startActivity(intent);
        });
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        ListItemBinding view;
        ViewHolder(ListItemBinding v) {
            super(v.getRoot());
            view = v;
        }
    }
}
