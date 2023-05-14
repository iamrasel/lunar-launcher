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
import android.graphics.Canvas
import android.graphics.Paint
import android.util.AttributeSet
import android.util.TypedValue
import android.view.MotionEvent
import android.view.View
import androidx.core.content.ContextCompat
import rasel.lunar.launcher.apps.AppDrawer.Companion.alphabet
import rasel.lunar.launcher.apps.AppDrawer.Companion.listenScroll


internal class AlphabetScrollbar : View {

    private var paint: Paint? = null
    private var selectedIndex = -1

    constructor(context: Context?) : super(context) {
        init()
    }

    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs) {
        init()
    }

    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        init()
    }

    @SuppressLint("ResourceType")
    private fun init() {
        paint = Paint(Paint.ANTI_ALIAS_FLAG)
        paint!!.color = defaultTextColor
        paint!!.textSize = 16f
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        val width = width
        val height = height
        val letterHeight: Int = height / alphabet.count()
        for (i in 0 until alphabet.count()) {
            val x = width / 2f - paint!!.measureText(alphabet[i]) / 2f
            val y = i * letterHeight + letterHeight / 2f
            if (i == selectedIndex) {
                paint!!.textSize = 28f
            } else {
                paint!!.textSize = 16f
            }
            canvas.drawText(alphabet[i], x, y, paint!!)
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onTouchEvent(event: MotionEvent): Boolean {
        when (event.action) {
            MotionEvent.ACTION_DOWN, MotionEvent.ACTION_MOVE -> {
                val y = event.y
                val index = (y / height * alphabet.count()).toInt()
                if (index != selectedIndex) {
                    selectedIndex = index
                    invalidate()
                }
            }

            MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> {
                try { listenScroll(alphabet[selectedIndex]) }
                catch (e: Exception) { e.printStackTrace() }
                selectedIndex = -1
                invalidate()
            }
        }
        return true
    }

    private val defaultTextColor: Int get() {
        val resolvedAttr = TypedValue()
        context.theme.resolveAttribute(android.R.attr.textColorPrimary, resolvedAttr, true)
        val colorRes = resolvedAttr.run { if (resourceId != 0) resourceId else data }
        return ContextCompat.getColor(context, colorRes)
    }
}