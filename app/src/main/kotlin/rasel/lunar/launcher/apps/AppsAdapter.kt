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
import android.content.pm.PackageManager
import android.util.TypedValue
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.updatePadding
import androidx.fragment.app.FragmentManager
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.textview.MaterialTextView
import rasel.lunar.launcher.LauncherActivity.Companion.lActivity
import rasel.lunar.launcher.R
import rasel.lunar.launcher.apps.IconPackManager.Companion.getDrawableIconForPackage
import rasel.lunar.launcher.databinding.AppsChildBinding


internal class AppsAdapter(
    private val layoutType: Int,
    private val packageManager: PackageManager,
    private val fragmentManager: FragmentManager,
    private val appsCount: MaterialTextView) : RecyclerView.Adapter<AppsAdapter.AppsViewHolder>() {

    private var oldList = mutableListOf<Packages>()
    private var appGravity: Int = Gravity.CENTER

    companion object {
        @JvmStatic var appsSize: Int? = null
    }

    override fun onCreateViewHolder(viewGroup: ViewGroup, i: Int): AppsViewHolder =
        AppsViewHolder(AppsChildBinding.inflate(LayoutInflater.from(viewGroup.context), viewGroup, false))

    override fun onBindViewHolder(holder: AppsViewHolder, i: Int) {
        val item = oldList[i]
        val fourDp = dpToPx(lActivity!!, R.dimen.four)
        val eightDp = dpToPx(lActivity!!, R.dimen.eight)
        val twelveDp = dpToPx(lActivity!!, R.dimen.twelve)
        val sixteenDp = dpToPx(lActivity!!, R.dimen.sixteen)

        holder.view.apply {
            childTextview.text = item.appName

            when (layoutType) {
                0 -> {
                    appIcon.visibility = View.GONE
                    appIconTwo.visibility = View.GONE
                    childTextview.apply {
                        gravity = appGravity
                        setTextSize(TypedValue.COMPLEX_UNIT_PX, lActivity!!.resources.getDimension(R.dimen.twentyTwo))
                    }
                    root.setPadding(sixteenDp, fourDp, sixteenDp, fourDp)
                }
                1 -> {
                    appIcon.visibility = View.GONE
                    appIconTwo.setImageDrawable(getDrawableIconForPackage(item.packageName, packageManager.getApplicationIcon(item.packageName)))
                    childTextview.apply {
                        gravity = appGravity or Gravity.CENTER_VERTICAL
                        setTextSize(TypedValue.COMPLEX_UNIT_PX, lActivity!!.resources.getDimension(R.dimen.twenty))
                        updatePadding(left = twelveDp)
                    }
                    root.setPadding(sixteenDp, eightDp, sixteenDp, eightDp)
                }
                2 -> {
                    appIconTwo.visibility = View.GONE
                    appIcon.setImageDrawable(getDrawableIconForPackage(item.packageName, packageManager.getApplicationIcon(item.packageName)))
                    childTextview.apply {
                        gravity = Gravity.CENTER
                        setTextSize(TypedValue.COMPLEX_UNIT_PX, lActivity!!.resources.getDimension(R.dimen.twelve))
                    }
                    root.setPadding(eightDp, eightDp, eightDp, eightDp)
                }
            }
        }

        holder.view.root.apply {
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

        newList.size.let {
            appsCount.text = it.toString()
            appsSize = it
        }
    }

    /* update text gravity (alignment) */
    @SuppressLint("RtlHardcoded", "NotifyDataSetChanged")
    fun updateGravity(gravity: Int){
        /* the first check is to avoid calling notifyDataSetChanged() everytime */
        if (gravity != appGravity &&
            (gravity == Gravity.LEFT || gravity == Gravity.CENTER || gravity == Gravity.RIGHT)) {
            appGravity = gravity
            notifyDataSetChanged()
        }
    }

    private fun dpToPx(id: Int) : Int {
        val valueInDP = lActivity!!.resources.getDimension(id)
        return (valueInDP * lActivity!!.resources.displayMetrics.density).toInt()
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
