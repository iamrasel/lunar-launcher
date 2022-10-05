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
import android.content.pm.PackageManager
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.RecyclerView
import rasel.lunar.launcher.databinding.AppsChildBinding

internal class AppsAdapter(
    private val fragmentActivity: FragmentActivity,
    private val context: Context,
    private val appsList: ArrayList<String>,
    private val packageNameList: ArrayList<String>
) : RecyclerView.Adapter<AppsAdapter.ViewHolder>() {

    private var packageManager: PackageManager = fragmentActivity.packageManager

    override fun onCreateViewHolder(viewGroup: ViewGroup, i: Int): ViewHolder {
        val binding = AppsChildBinding.inflate(LayoutInflater.from(viewGroup.context), viewGroup, false)
        return ViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return appsList.size
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: ViewHolder, @SuppressLint("RecyclerView") position: Int) {
        holder.view.childTextview.text = appsList[position]

        holder.view.childTextview.setOnClickListener {
            context.startActivity(packageManager.getLaunchIntentForPackage(packageNameList[position]))
        }

        holder.view.childTextview.setOnLongClickListener {
            AppMenus().show(fragmentActivity.supportFragmentManager, packageNameList[position])
            true
        }
    }

    class ViewHolder(var view: AppsChildBinding) : RecyclerView.ViewHolder(view.root)
}
