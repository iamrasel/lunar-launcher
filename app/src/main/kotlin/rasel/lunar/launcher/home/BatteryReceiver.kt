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
import android.provider.Settings
import android.view.animation.AnimationUtils
import com.google.android.material.progressindicator.CircularProgressIndicator
import rasel.lunar.launcher.R


internal class BatteryReceiver(private val progressBar: CircularProgressIndicator) : BroadcastReceiver() {

    /* get current battery percentage */
    private fun batteryPercentage(intent: Intent): Int {
        val scale = intent.getIntExtra(BatteryManager.EXTRA_SCALE, -1)
        val level = intent.getIntExtra(BatteryManager.EXTRA_LEVEL, -1)
        val percentage = level / scale.toFloat()
        return (percentage * 100).toInt()
    }

    /* get current charging status */
    private fun chargingStatus(intent: Intent): Int {
        return intent.getIntExtra(BatteryManager.EXTRA_STATUS, -1)
    }

    override fun onReceive(context: Context?, intent: Intent?) {
        val animationDuration = try {
            Settings.Global.getFloat(context?.contentResolver, Settings.Global.ANIMATOR_DURATION_SCALE)
        } catch (e: Settings.SettingNotFoundException) {
            e.printStackTrace()
        }

        /* set battery percentage value to the circular progress bar */
        progressBar.progress = batteryPercentage(intent!!)

        /* progress bar animation */
        if (chargingStatus(intent) == BatteryManager.BATTERY_STATUS_CHARGING ||
            chargingStatus(intent) == BatteryManager.BATTERY_STATUS_FULL) {
            if (progressBar.animation == null && animationDuration != 0f) {
                progressBar.startAnimation(
                    AnimationUtils.loadAnimation(context, R.anim.rotate_clockwise)
                )
            }
        } else if (chargingStatus(intent) == BatteryManager.BATTERY_STATUS_DISCHARGING ||
                chargingStatus(intent) == BatteryManager.BATTERY_STATUS_NOT_CHARGING) {
            progressBar.clearAnimation()
        }
    }

}
