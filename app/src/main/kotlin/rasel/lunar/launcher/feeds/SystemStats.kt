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
import android.os.*
import android.text.Html
import android.view.LayoutInflater
import android.view.View
import androidx.appcompat.widget.LinearLayoutCompat
import androidx.core.content.ContextCompat
import com.google.android.material.textview.MaterialTextView
import rasel.lunar.launcher.LauncherActivity.Companion.lActivity
import rasel.lunar.launcher.R
import rasel.lunar.launcher.databinding.ChildSysInfoBinding
import rasel.lunar.launcher.helpers.Constants.Companion.KEY_TEMP_UNIT
import rasel.lunar.launcher.helpers.Constants.Companion.PREFS_SETTINGS
import rasel.lunar.launcher.helpers.UniUtils.Companion.isNetworkAvailable
import java.io.BufferedReader
import java.io.File
import java.io.InputStreamReader
import java.io.RandomAccessFile
import java.net.InetAddress
import java.net.NetworkInterface
import java.util.*
import java.util.concurrent.TimeUnit
import kotlin.math.roundToInt


internal class SystemStats {

    private val toGb = 1.07374182E9f
    private fun string(id: Int) : String { return lActivity!!.getString(id) }
    private val inflater : LayoutInflater get() { return lActivity!!.layoutInflater }

    /* ram info */
    fun ram(ramParent: LinearLayoutCompat) {
        ramParent.removeAllViews()
        val binding = ChildSysInfoBinding.inflate(inflater)
        ramParent.addView(binding.root)

        val totalMem = memoryInfo.totalMem / toGb
        val availMem = memoryInfo.availMem / toGb
        val usedMem = totalMem - availMem

        binding.indicator.progress = (usedMem * 100 / totalMem).toInt()
        binding.textView.text = Html.fromHtml(
            "<b>${string(R.string.ram)}</b><br>" +
                    "${string(R.string.total)}: ${String.format("%.03f", totalMem)} GB | " +
                    "${string(R.string.used)}: ${String.format("%.03f", usedMem)} GB | " +
                    "${string(R.string.free)}: ${String.format("%.03f", availMem)} GB",
            Html.FROM_HTML_MODE_COMPACT)
    }


    /* cpu and battery info */
    fun cpu(cpuParent: LinearLayoutCompat) {
        cpuParent.removeAllViews()
        val binding = ChildSysInfoBinding.inflate(inflater)
        cpuParent.addView(binding.root)

        var cpuTemp = 0.0f
        try {
            val cpuTempProcess = Runtime.getRuntime().exec("cat sys/class/thermal/thermal_zone0/temp")
            cpuTempProcess.waitFor()
            val cpuTempReader = BufferedReader(InputStreamReader(cpuTempProcess.inputStream))
            cpuTemp = cpuTempReader.readLine().toFloat() / 1000.0f
        } catch (exception: Exception) {
            exception.printStackTrace()
        }

        val finalCpuTemp = when (tempUnit) {
            0 -> "$cpuTemp ºC"
            1 -> "${String.format("%.01f", cpuTemp * 1.8 + 32)} ºF"
            else -> "$cpuTemp ºC"
        }

        val cpuFreq = "${String.format("%.02f", minCpuFrequency.toFloat() / 1000)} - " +
                "${String.format("%.02f", maxCpuFrequency.toFloat() / 1000)} GHz"

        binding.indicator.progress = when (maxCpuFrequency) {
            0 -> 30
            else -> frequencyOfCore * 100 / maxCpuFrequency
        }
        binding.textView.text = Html.fromHtml(
            "<b>${string(R.string.cpu)}</b><br>" +
                    "${string(R.string.temperature)}: $finalCpuTemp | " +
                    "${string(R.string.frequency)}: $cpuFreq",
            Html.FROM_HTML_MODE_COMPACT)
    }


    /* internal storage */
    fun intStorage(intParent: LinearLayoutCompat) {
        intParent.removeAllViews()
        val binding = ChildSysInfoBinding.inflate(inflater)
        intParent.addView(binding.root)

        val intPath = Environment.getExternalStorageDirectory().absolutePath
        val statFs = StatFs(intPath)
        val totalStorage = statFs.blockCountLong * statFs.blockSizeLong / toGb
        val availStorage = statFs.availableBlocksLong * statFs.blockSizeLong / toGb
        val usedStorage = totalStorage - availStorage

        binding.indicator.progress = (usedStorage * 100 / totalStorage).toInt()
        binding.textView.text = Html.fromHtml(
            "<b>${intPath + File.separator}</b><br>" +
                    "${string(R.string.total)}: ${String.format("%.03f", totalStorage)} GB | " +
                    "${string(R.string.used)}: ${String.format("%.03f", usedStorage)} GB | " +
                    "${string(R.string.free)}: ${String.format("%.03f", availStorage)} GB",
            Html.FROM_HTML_MODE_COMPACT)
    }


	/* external storage */
    fun extStorage(extParent: LinearLayoutCompat) {
        val extStorages = ContextCompat.getExternalFilesDirs(lActivity!!, null)
        /* sd card is available */
        if (extStorages.size > 1) {
            extParent.removeAllViews()
            for (i in 1 until extStorages.size) {
                if (extStorages[i] != null) {
                    val binding = ChildSysInfoBinding.inflate(inflater)
                    extParent.addView(binding.root)

                    val statFs = StatFs(extStorages[i]!!.path)
                    val blockSize = statFs.blockSizeLong
                    val totalStorage = statFs.blockCountLong * blockSize / toGb
                    val availStorage = statFs.availableBlocksLong * blockSize / toGb
                    val usedStorage = totalStorage - availStorage

                    val sdcardPaths = extStorages[i]!!.path.split(File.separator).toTypedArray()
                    val sdPath = File.separator + sdcardPaths[1] + File.separator + sdcardPaths[2] + File.separator

                    binding.indicator.progress = (usedStorage * 100 / totalStorage).toInt()
                    binding.textView.text = Html.fromHtml(
                        "<b>$sdPath</b><br>" +
                                "${string(R.string.total)}: ${String.format("%.03f", totalStorage)} GB | " +
                                "${string(R.string.used)}: ${String.format("%.03f", usedStorage)} GB | " +
                                "${string(R.string.free)}: ${String.format("%.03f", availStorage)} GB",
                        Html.FROM_HTML_MODE_COMPACT)
                }
            }
        } else {
            extParent.visibility = View.GONE
        }
    }


    @SuppressLint("SetTextI18n")
    fun misc(misc: MaterialTextView) {
        val totalRootStorage = StatFs(Environment.getRootDirectory().path).blockCountLong *
                StatFs(Environment.getRootDirectory().path).blockSizeLong / toGb

        val batteryIntent = lActivity!!.registerReceiver(null, IntentFilter(Intent.ACTION_BATTERY_CHANGED))
        val batteryTemp = batteryIntent!!.getIntExtra(BatteryManager.EXTRA_TEMPERATURE, 0).toFloat() / 10
        val voltage = batteryIntent.getIntExtra(BatteryManager.EXTRA_VOLTAGE, 0).toFloat() / 1000

        val finalBatteryTemp = when (tempUnit) {
            0 -> "$batteryTemp ºC"
            1 -> "${String.format("%.01f", batteryTemp * 1.8 + 32)} ºF"
            else -> "$batteryTemp ºC"
        }

        misc.text =
            "${longToString(SystemClock.elapsedRealtime())}\n" +
            "${longToString(SystemClock.uptimeMillis())}\n" +
            "${String.format("%.02f", memoryInfo.threshold / 1048576f)} MB\n" +
            "$finalBatteryTemp\n" +
            "$voltage V\n" +
            "${String.format("%.03f", totalRootStorage)} GB\n" +
            "${getIpAddress(true)}\n" +
            "${getIpAddress(false)}"
    }


    private val memoryInfo: ActivityManager.MemoryInfo get() {
        val memoryInfo = ActivityManager.MemoryInfo()
        val activityManager = lActivity!!.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
        activityManager.getMemoryInfo(memoryInfo)
        return memoryInfo
    }

    private val tempUnit: Int get() =
        lActivity!!.getSharedPreferences(PREFS_SETTINGS, 0).getInt(KEY_TEMP_UNIT, 0)

    private fun longToString(long: Long) : String {
        var seconds = (long.toDouble() / 1000).roundToInt()
        val hours = TimeUnit.SECONDS.toHours(seconds.toLong())
        if (hours > 0) seconds -= TimeUnit.HOURS.toSeconds(hours).toInt()
        val minutes = if (seconds > 0) TimeUnit.SECONDS.toMinutes(seconds.toLong()) else 0
        if (minutes > 0) seconds -= TimeUnit.MINUTES.toSeconds(minutes).toInt()
        return if (hours > 0) String.format("%02d:%02d:%02d", hours, minutes, seconds)
        else String.format("%02d:%02d", minutes, seconds)
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
        } catch (ex: java.lang.Exception) {
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
        } catch (exception: java.lang.Exception) {
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
        } catch (exception: java.lang.Exception) {
            exception.printStackTrace()
        }
        return currentFReq
    }

    private fun getIpAddress(getIPv4: Boolean): String {
        try {
            val interfaces: List<NetworkInterface> = Collections.list(NetworkInterface.getNetworkInterfaces())
            for (interFace in interfaces) {
                val addresses: List<InetAddress> = Collections.list(interFace.inetAddresses)
                for (address in addresses) {
                    if (!address.isLoopbackAddress) {
                        val addressStr = address.hostAddress
                        val isIPv4 = addressStr!!.indexOf(':') < 0
                        if (getIPv4) {
                            if (isIPv4) return addressStr
                        } else {
                            if (!isIPv4 && isNetworkAvailable()) {
                                val endIndex = addressStr.indexOf('%')
                                return if (endIndex < 0) addressStr
                                else addressStr.substring(0, endIndex)
                            }
                        }
                    }
                }
            }
        } catch (e: java.lang.Exception) { e.printStackTrace() }
        return string(R.string.na)
    }

}
