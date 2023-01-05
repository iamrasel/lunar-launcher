/*
 * Copyright (C) 2009 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package rasel.lunar.launcher.feeds

import android.appwidget.AppWidgetHostView
import android.content.Context
import android.view.MotionEvent
import android.view.ViewConfiguration
import kotlin.math.abs


internal class WidgetHostView(context: Context) : AppWidgetHostView(context) {

    private var hasPerformedLongPress = false
    private var pendingCheckForLongPress: CheckForLongPress? = null
    private var xPos = 0f
    private var yPos = 0f

    override fun onInterceptTouchEvent(ev: MotionEvent): Boolean {
        // Consume any touch events for ourselves after longpress is triggered
        if (hasPerformedLongPress) {
            hasPerformedLongPress = false
            return true
        }

        // Watch for long press events at this level to make sure
        // users can always pick up this widget
        when (ev.action) {
            MotionEvent.ACTION_DOWN -> {
                postCheckForLongClick()
                xPos = ev.x
                yPos = ev.y
            }
            MotionEvent.ACTION_MOVE -> {
                if (abs(ev.x - xPos) > 5 || abs(ev.y - yPos) > 5) {
                    hasPerformedLongPress = false
                    if (pendingCheckForLongPress != null) removeCallbacks(pendingCheckForLongPress)
                }
            }
            MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> {
                hasPerformedLongPress = false
                if (pendingCheckForLongPress != null) removeCallbacks(pendingCheckForLongPress)
            }
        }

        // Otherwise continue letting touch events fall through to children
        return false
    }

    internal inner class CheckForLongPress : Runnable {
        private var originalWindowAttachCount = 0
        override fun run() {
            if (parent != null && hasWindowFocus()
                && originalWindowAttachCount == windowAttachCount && !hasPerformedLongPress
            ) {
                if (performLongClick()) hasPerformedLongPress = true
            }
        }

        fun rememberWindowAttachCount() { originalWindowAttachCount = windowAttachCount }
    }

    private fun postCheckForLongClick() {
        hasPerformedLongPress = false
        if (pendingCheckForLongPress == null) pendingCheckForLongPress = CheckForLongPress()
        pendingCheckForLongPress!!.rememberWindowAttachCount()
        postDelayed(pendingCheckForLongPress, ViewConfiguration.getLongPressTimeout().toLong())
    }

    override fun cancelLongPress() {
        super.cancelLongPress()
        hasPerformedLongPress = false
        if (pendingCheckForLongPress != null) removeCallbacks(pendingCheckForLongPress)
    }

    override fun getDescendantFocusability(): Int = FOCUS_BLOCK_DESCENDANTS

}
