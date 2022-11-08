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

import androidx.recyclerview.widget.RecyclerView
import android.view.ViewGroup
import android.view.LayoutInflater
import android.annotation.SuppressLint
import android.content.Context
import android.view.Gravity
import androidx.core.content.ContextCompat
import android.graphics.Typeface
import android.util.TypedValue
import android.content.Intent
import android.content.res.ColorStateList
import android.net.Uri
import rasel.lunar.launcher.databinding.ListItemBinding
import rasel.lunar.launcher.helpers.UniUtils


internal class RssAdapter(private val items: List<Rss>, private val context: Context) :
    RecyclerView.Adapter<RssAdapter.RssViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RssViewHolder {
        val binding = ListItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return RssViewHolder(binding)
    }

    override fun getItemCount(): Int = items.size

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: RssViewHolder, position: Int) {
        /* customize the first item */
        if (position == 0) {
            holder.view.itemText.apply {
                text = "\u22B6  " + items[position].title + "  \u22B7"
                gravity = Gravity.CENTER
                setTextColor(ContextCompat.getColor(context,
                    UniUtils().getColorResId(context, com.google.android.material.R.attr.colorPrimary)))
                setTypeface(null, Typeface.BOLD)
                textSize = 18f
            }
        /* reset customization for rest */
        } else {
            holder.view.itemText.apply {
                text = items[position].title
                gravity = holder.gravity
                setTextColor(holder.color)
                typeface = holder.typeface
                setTextSize(TypedValue.COMPLEX_UNIT_PX, holder.size)
            }
        }

        /* on click - open in browser */
        holder.view.itemText.setOnClickListener {
            context.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(items[position].link)))
        }
    }

    inner class RssViewHolder(var view: ListItemBinding) : RecyclerView.ViewHolder(view.root) {
        /* store previous styles for resetting */
        var gravity: Int = view.itemText.gravity
        var color: ColorStateList = view.itemText.textColors
        var typeface: Typeface = view.itemText.typeface
        var size: Float = view.itemText.textSize
    }

}
