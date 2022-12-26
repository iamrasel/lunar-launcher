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

package rasel.lunar.launcher.feeds

import android.appwidget.AppWidgetHostView
import android.content.Context
import android.view.MotionEvent


internal class WidgetHostView(context: Context?) : AppWidgetHostView(context) {

    private var longClick: OnLongClickListener? = null
    private var down: Long = 0

    override fun setOnLongClickListener(l: OnLongClickListener?) {
        longClick = l
    }

    override fun onInterceptTouchEvent(motionEvent: MotionEvent): Boolean {
        when (motionEvent.actionMasked) {
            MotionEvent.ACTION_DOWN -> down = System.currentTimeMillis()
            MotionEvent.ACTION_MOVE -> {
                val upVal = System.currentTimeMillis() - down
                if (upVal > 100L) {
                    longClick!!.onLongClick(this@WidgetHostView)
                    return true
                }
            }
        }
        return false
    }

}
