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

package rasel.lunar.launcher.feeds;

import android.annotation.SuppressLint;
import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.BatteryManager;
import android.os.Environment;
import android.os.StatFs;
import android.text.Html;

import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;

import com.google.android.material.textview.MaterialTextView;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;

public class FeedsUtils {

    private final FragmentActivity fragmentActivity;
    SysInfoUtils sysInfoUtils = new SysInfoUtils();
    private final float toGb = 1073741824f;

    protected FeedsUtils(FragmentActivity fragmentActivity) {
        this.fragmentActivity = fragmentActivity;
    }

    @SuppressLint("DefaultLocale")
    protected void ram(MaterialTextView ram) {
        ActivityManager.MemoryInfo memoryInfo = new ActivityManager.MemoryInfo();
        ActivityManager activityManager = (ActivityManager) fragmentActivity.getSystemService(Context.ACTIVITY_SERVICE);
        activityManager.getMemoryInfo(memoryInfo);
        float totalMem = memoryInfo.totalMem / toGb;
        float availMem = memoryInfo.availMem / toGb;
        float thresholdMem = memoryInfo.threshold / toGb;
        float usedMem = totalMem - availMem;
        ram.setText(Html.fromHtml("<h5>RAM</h5><br>" +
                                        "Total: " + String.format("%.03f", totalMem) + " GB<br>" +
                                        "Used: " + String.format("%.03f", usedMem) + " GB<br>" +
                                        "Available: " + String.format("%.03f", availMem) + " GB<br>" +
                                        "Threshold: " + String.format("%.03f", thresholdMem) + " GB<br>" +
                                        "System Uptime: " + sysInfoUtils.deviceUptime(), Html.FROM_HTML_MODE_COMPACT));
    }

    @SuppressLint("DefaultLocale")
    protected void intStorage(MaterialTextView intStorage) {
        StatFs statFs = new StatFs(Environment.getDataDirectory().getPath());
        long blockSize = statFs.getBlockSizeLong();
        float totalIntStorage = (statFs.getBlockCountLong() * blockSize) / toGb;
        float availIntStorage = (statFs.getAvailableBlocksLong() * blockSize) / toGb;
        float usedIntStorage = totalIntStorage - availIntStorage;
        float totalRootStorage = (new StatFs((Environment.getRootDirectory()).getPath()).getBlockCountLong() *
                new StatFs((Environment.getRootDirectory()).getPath()).getBlockSizeLong()) / toGb;
        intStorage.setText(Html.fromHtml("<h5>ROM</h5><br>" +
                "Total: " + String.format("%.03f", totalIntStorage) + " GB<br>" +
                "Used: " + String.format("%.03f", usedIntStorage) + " GB<br>" +
                "Free: " + String.format("%.03f", availIntStorage) + " GB<br>" +
                "Root: " + String.format("%.03f", totalRootStorage) + " GB", Html.FROM_HTML_MODE_COMPACT));
    }

    protected void cpuBattery(MaterialTextView cpuBattery) {
        float cpuTemp = 0.0f;
        try {
            Process cpuTempProcess = Runtime.getRuntime().exec("cat sys/class/thermal/thermal_zone0/temp");
            cpuTempProcess.waitFor();
            BufferedReader cpuTempReader = new BufferedReader(new InputStreamReader(cpuTempProcess.getInputStream()));
            cpuTemp = Float.parseFloat(cpuTempReader.readLine()) / 1000.0f;
        } catch(Exception exception) {
            exception.printStackTrace();
        }
        Intent intent = fragmentActivity.registerReceiver(null, new IntentFilter(Intent.ACTION_BATTERY_CHANGED));
        float  batteryTemp   = ((float) intent.getIntExtra(BatteryManager.EXTRA_TEMPERATURE,0)) / 10;
        float voltage = ((float) intent.getIntExtra(BatteryManager.EXTRA_VOLTAGE, 0)) / 1000;
        cpuBattery.setText(Html.fromHtml("<h5>CPU & Battery</h5><br>" +
                "Cpu Temp: " + cpuTemp + " ºC<br>" +
                "Usage: " + sysInfoUtils.cpuUsage() + "<br>" +
                "Freq: " + sysInfoUtils.cpuFreq() + "<br>" +
                "Battery Temp: " + batteryTemp + " ºC<br>" +
                "Voltage: " + voltage + " V", Html.FROM_HTML_MODE_COMPACT));
    }

    @SuppressLint("DefaultLocale")
    protected void extStorage(MaterialTextView extStorage) {
        File[] storages = ContextCompat.getExternalFilesDirs(fragmentActivity, null);
        if(storages.length > 1 && storages[1] != null) {
            StatFs statFs = new StatFs(storages[1].getPath());
            long blockSize = statFs.getBlockSizeLong();
            float totalExtStorage = (statFs.getBlockCountLong() * blockSize) / toGb;
            float availExtStorage = (statFs.getAvailableBlocksLong() * blockSize) / toGb;
            float usedExtStorage = totalExtStorage - availExtStorage;
            String[] sdcardPaths = storages[1].getPath().split(File.separator);
            String sdcardPath = File.separator + sdcardPaths[1] + File.separator + sdcardPaths[2] + File.separator;
            extStorage.setText(Html.fromHtml("<h5>SD Card</h5><br>" +
                    "Total: " + String.format("%.03f", totalExtStorage) + " GB<br>" +
                    "Used: " + String.format("%.03f", usedExtStorage) + " GB<br>" +
                    "Free: " + String.format("%.03f", availExtStorage) + " GB<br>" +
                    "Path: " + sdcardPath, Html.FROM_HTML_MODE_COMPACT));

            extStorage.setOnClickListener(v -> {
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setDataAndType(Uri.parse(sdcardPath), "resource/folder");
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                try {
                    fragmentActivity.startActivity(intent);
                } catch(Exception exception) {
                    exception.printStackTrace();
                }
            });
        } else {
            extStorage.setText(Html.fromHtml("<h5>SD Card</h5><br>" + "Couldn't find", Html.FROM_HTML_MODE_COMPACT));
        }
    }
}
