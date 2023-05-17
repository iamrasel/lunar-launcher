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

import android.content.pm.PackageManager
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.fragment.app.FragmentManager
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.textview.MaterialTextView
import rasel.lunar.launcher.databinding.AppsChildBinding


internal class AppsAdapter(
    private val packageManager: PackageManager,
    private val fragmentManager: FragmentManager,
    private val appsCount: MaterialTextView) : RecyclerView.Adapter<AppsAdapter.AppsViewHolder>() {

    private var oldList = mutableListOf<Packages>()

    companion object {
        @JvmStatic var appsSize: Int? = null
    }

    override fun onCreateViewHolder(viewGroup: ViewGroup, i: Int): AppsViewHolder =
        AppsViewHolder(AppsChildBinding.inflate(LayoutInflater.from(viewGroup.context), viewGroup, false))

    override fun onBindViewHolder(holder: AppsViewHolder, i: Int) {
        val item = oldList[i]
        holder.view.childTextview.apply {
            /* show app name */
            text = item.appName

            /* on click - open app */
            setOnClickListener {
                context.startActivity(packageManager.getLaunchIntentForPackage(item.packageName))
            }

            /* on long click - open app menu */
            setOnLongClickListener {
                AppMenu().show(fragmentManager, item.packageName)
                true
            }
        }
    }

    override fun getItemCount(): Int = oldList.size

    inner class AppsViewHolder(var view: AppsChildBinding) : RecyclerView.ViewHolder(view.root)

    /* update app list */
    fun updateData(newList: List<Packages>) {
        val diffUtil = AppsDiffUtil(oldList, newList)
        val diffUtilResult = DiffUtil.calculateDiff(diffUtil)

        oldList.clear()
        oldList.addAll(newList)
        diffUtilResult.dispatchUpdatesTo(this)

        appsCount.text = newList.size.toString()
        appsSize = newList.size
    }
}

internal data class Packages (
    val packageName: String,
    val appName: String
)

internal class AppsDiffUtil(
    private val oldList: List<Packages>, private val newList: List<Packages>
) : DiffUtil.Callback() {

    override fun getOldListSize(): Int = oldList.size
    override fun getNewListSize(): Int = newList.size

    override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean =
        oldList[oldItemPosition].packageName == newList[newItemPosition].packageName

    override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean =
        oldList[oldItemPosition] == newList[newItemPosition]
}
