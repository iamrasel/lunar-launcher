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

import android.os.SystemClock
import java.io.RandomAccessFile
import java.lang.Exception
import java.util.concurrent.TimeUnit
import kotlin.math.roundToInt


internal class SysInfoUtils {

	/* generate device's uptime string */
    val deviceUptime: String get() {
        var seconds = (SystemClock.uptimeMillis().toDouble() / 1000).roundToInt()
        val hours = TimeUnit.SECONDS.toHours(seconds.toLong())
        if (hours > 0) seconds -= TimeUnit.HOURS.toSeconds(hours).toInt()
        val minutes = if (seconds > 0) TimeUnit.SECONDS.toMinutes(seconds.toLong()) else 0
        if (minutes > 0) seconds -= TimeUnit.MINUTES.toSeconds(minutes).toInt()
        return if (hours > 0) String.format("%02d:%02d:%02d", hours, minutes, seconds)
            else String.format("%02d:%02d", minutes, seconds)
    }

    /* cpu usage percentage */
    val cpuUsage: String get() {
        val percentage: Int = if (maxCpuFrequency != 0) {
            100 * frequencyOfCore / maxCpuFrequency
        } else {
            30
        }
        return "$percentage%"
    }

    /* cpu frequency */
    val cpuFreq: String get() {
        val min = String.format("%.02f", minCpuFrequency.toFloat() / 1000)
        val max = String.format("%.02f", maxCpuFrequency.toFloat() / 1000)
        return "$min - $max GHz"
    }

    /* frequency of core */
    private val frequencyOfCore: Int get() {
        var currentFReq = 0
        try {
            val currentFreq: Double
            val readerCurFreq =
                RandomAccessFile("/sys/devices/system/cpu/cpu" + 0 + "/cpufreq/scaling_cur_freq", "r")
            val curFreq = readerCurFreq.readLine()
            currentFreq = curFreq.toDouble() / 1000
            readerCurFreq.close()
            currentFReq = currentFreq.toInt()
            println("$currentFReq----------------------------------------------------")
        } catch (ex: Exception) {
            ex.printStackTrace()
        }
        return currentFReq
    }

    /* minimum cpu frequency */
    private val minCpuFrequency: Int get() {
        var minFreq = -1
        try {
            val randomAccessFile =
                RandomAccessFile("/sys/devices/system/cpu/cpu" + 0 + "/cpufreq/cpuinfo_min_freq", "r")
            while (true) {
                val line = randomAccessFile.readLine() ?: break
                val timeInState = line.toInt()
                if (timeInState > 0) {
                    val freq = timeInState / 1000
                    if (freq > minFreq) {
                        minFreq = freq
                    }
                }
            }
        } catch (exception: Exception) {
            exception.printStackTrace()
        }
        return minFreq
    }

    /* maximum cpu frequency */
    private val maxCpuFrequency: Int get() {
        var currentFReq = 0
        try {
            val currentFreq: Double
            val readerCurFreq =
                RandomAccessFile("/sys/devices/system/cpu/cpu" + 0 + "/cpufreq/cpuinfo_max_freq", "r")
            val curFreq = readerCurFreq.readLine()
            currentFreq = curFreq.toDouble() / 1000
            readerCurFreq.close()
            currentFReq = currentFreq.toInt()
        } catch (exception: Exception) {
            exception.printStackTrace()
        }
        return currentFReq
    }

}
