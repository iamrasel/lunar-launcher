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

package rasel.lunar.launcher.home

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.BatteryManager
import android.view.animation.AnimationUtils
import com.google.android.material.progressindicator.CircularProgressIndicator
import rasel.lunar.launcher.R

internal class BatteryReceiver(private val progressBar: CircularProgressIndicator) : BroadcastReceiver() {

    private fun getBatteryPercentage(intent: Intent): Int {
        val scale = intent.getIntExtra(BatteryManager.EXTRA_SCALE, -1)
        val level = intent.getIntExtra(BatteryManager.EXTRA_LEVEL, -1)
        val percentage = level / scale.toFloat()
        return (percentage * 100).toInt()
    }

    private fun getChargingStatus(intent: Intent): Int {
        return intent.getIntExtra(BatteryManager.EXTRA_STATUS, -1)
    }

    override fun onReceive(context: Context?, intent: Intent?) {
        progressBar.progress = getBatteryPercentage(intent!!)
        if (getChargingStatus(intent) == BatteryManager.BATTERY_STATUS_CHARGING ||
            getChargingStatus(intent) == BatteryManager.BATTERY_STATUS_FULL) {
            progressBar.startAnimation(
                AnimationUtils.loadAnimation(context, R.anim.rotate_clockwise)
            )
        } else if (getChargingStatus(intent) == BatteryManager.BATTERY_STATUS_DISCHARGING) {
            progressBar.clearAnimation()
        }
    }
}