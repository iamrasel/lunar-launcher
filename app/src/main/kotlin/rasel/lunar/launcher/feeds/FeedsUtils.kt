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

import android.annotation.SuppressLint
import android.app.ActivityManager
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.net.Uri
import android.os.BatteryManager
import android.os.Environment
import android.os.StatFs
import android.text.Html
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentActivity
import com.google.android.material.textview.MaterialTextView
import java.io.BufferedReader
import java.io.File
import java.io.InputStreamReader


internal class FeedsUtils(private val fragmentActivity: FragmentActivity) {

    private var sysInfoUtils = SysInfoUtils()
    private val toGb = 1.07374182E9f

    @SuppressLint("DefaultLocale")
    fun ram(ram: MaterialTextView) {
        val memoryInfo = ActivityManager.MemoryInfo()
        val activityManager = fragmentActivity.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
        activityManager.getMemoryInfo(memoryInfo)
        val totalMem = memoryInfo.totalMem / toGb
        val availMem = memoryInfo.availMem / toGb
        val thresholdMem = memoryInfo.threshold / toGb
        val usedMem = totalMem - availMem
        ram.text = Html.fromHtml(
            "<h5>RAM</h5><br>" +
                    "Total: " + String.format("%.03f", totalMem) + " GB<br>" +
                    "Used: " + String.format("%.03f", usedMem) + " GB<br>" +
                    "Available: " + String.format("%.03f", availMem) + " GB<br>" +
                    "Threshold: " + String.format("%.03f", thresholdMem) + " GB<br>" +
                    "System Uptime: " + sysInfoUtils.deviceUptime(), Html.FROM_HTML_MODE_COMPACT
        )
    }

    @SuppressLint("DefaultLocale")
    fun intStorage(intStorage: MaterialTextView) {
        val statFs = StatFs(Environment.getDataDirectory().path)
        val blockSize = statFs.blockSizeLong
        val totalIntStorage = statFs.blockCountLong * blockSize / toGb
        val availIntStorage = statFs.availableBlocksLong * blockSize / toGb
        val usedIntStorage = totalIntStorage - availIntStorage
        val totalRootStorage = StatFs(Environment.getRootDirectory().path).blockCountLong *
                StatFs(Environment.getRootDirectory().path).blockSizeLong / toGb
        intStorage.text = Html.fromHtml(
            "<h5>ROM</h5><br>" +
                    "Total: " + String.format("%.03f", totalIntStorage) + " GB<br>" +
                    "Used: " + String.format("%.03f", usedIntStorage) + " GB<br>" +
                    "Free: " + String.format("%.03f", availIntStorage) + " GB<br>" +
                    "Root: " + String.format("%.03f", totalRootStorage) + " GB", Html.FROM_HTML_MODE_COMPACT
        )
    }

    fun cpuBattery(cpuBattery: MaterialTextView) {
        var cpuTemp = 0.0f
        try {
            val cpuTempProcess = Runtime.getRuntime().exec("cat sys/class/thermal/thermal_zone0/temp")
            cpuTempProcess.waitFor()
            val cpuTempReader = BufferedReader(InputStreamReader(cpuTempProcess.inputStream))
            cpuTemp = cpuTempReader.readLine().toFloat() / 1000.0f
        } catch (exception: Exception) {
            exception.printStackTrace()
        }
        val intent = fragmentActivity.registerReceiver(null, IntentFilter(Intent.ACTION_BATTERY_CHANGED))
        val batteryTemp = intent!!.getIntExtra(BatteryManager.EXTRA_TEMPERATURE, 0).toFloat() / 10
        val voltage = intent.getIntExtra(BatteryManager.EXTRA_VOLTAGE, 0).toFloat() / 1000
        cpuBattery.text = Html.fromHtml(
            "<h5>CPU & Battery</h5><br>" +
                    "Cpu Temp: " + cpuTemp + " ºC<br>" +
                    "Usage: " + sysInfoUtils.cpuUsage() + "<br>" +
                    "Freq: " + sysInfoUtils.cpuFreq() + "<br>" +
                    "Battery Temp: " + batteryTemp + " ºC<br>" +
                    "Voltage: " + voltage + " V", Html.FROM_HTML_MODE_COMPACT
        )
    }

    @SuppressLint("DefaultLocale")
    fun extStorage(extStorage: MaterialTextView) {
        val storages = ContextCompat.getExternalFilesDirs(fragmentActivity, null)
        if (storages.size > 1 && storages[1] != null) {
            val statFs = StatFs(storages[1]!!.path)
            val blockSize = statFs.blockSizeLong
            val totalExtStorage = statFs.blockCountLong * blockSize / toGb
            val availExtStorage = statFs.availableBlocksLong * blockSize / toGb
            val usedExtStorage = totalExtStorage - availExtStorage
            val sdcardPaths = storages[1].path.split(File.separator).toTypedArray()
            val sdcardPath = File.separator + sdcardPaths[1] + File.separator + sdcardPaths[2] + File.separator
            extStorage.text = Html.fromHtml(
                "<h5>SD Card</h5><br>" +
                        "Total: " + String.format("%.03f", totalExtStorage) + " GB<br>" +
                        "Used: " + String.format("%.03f", usedExtStorage) + " GB<br>" +
                        "Free: " + String.format("%.03f", availExtStorage) + " GB<br>" +
                        "Path: " + sdcardPath, Html.FROM_HTML_MODE_COMPACT
            )
            extStorage.setOnClickListener {
                val intent = Intent(Intent.ACTION_VIEW)
                intent.setDataAndType(Uri.parse(sdcardPath), "resource/folder")
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
                try {
                    fragmentActivity.startActivity(intent)
                } catch (exception: Exception) {
                    exception.printStackTrace()
                }
            }
        } else {
            extStorage.text = Html.fromHtml("<h5>SD Card</h5><br>" + "Couldn't find", Html.FROM_HTML_MODE_COMPACT)
        }
    }
}