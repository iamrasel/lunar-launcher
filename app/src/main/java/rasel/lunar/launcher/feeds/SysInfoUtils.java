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
import android.os.SystemClock;

import java.io.RandomAccessFile;
import java.util.concurrent.TimeUnit;

public class SysInfoUtils {

    @SuppressLint("DefaultLocale")
    protected String deviceUptime() {
        long seconds = Math.round((double) SystemClock.uptimeMillis() / 1000);
        long hours = TimeUnit.SECONDS.toHours(seconds);
        if(hours > 0)
            seconds -= TimeUnit.HOURS.toSeconds(hours);
        long minutes = seconds > 0 ? TimeUnit.SECONDS.toMinutes(seconds) : 0;
        if(minutes > 0)
            seconds -= TimeUnit.MINUTES.toSeconds(minutes);
        return hours > 0 ? String.format("%02d:%02d:%02d", hours, minutes, seconds) : String.format("%02d:%02d", minutes, seconds);
    }

    protected String cpuUsage() {
        int percentage;
        if(getMaxCpuFrequency() != 0) {
            percentage = (100 * getFrequencyOfCore()) / getMaxCpuFrequency();
        } else {
            percentage = 30;
        }
        return percentage + "%";
    }

    protected String cpuFreq() {
        @SuppressLint("DefaultLocale") String min = String.format("%.02f", (float) getMinCpuFrequency() / 1000);
        @SuppressLint("DefaultLocale") String max = String.format("%.02f", (float) getMaxCpuFrequency() / 1000);
        return min + " - " + max + " GHz";
    }

    private int getFrequencyOfCore() {
        int currentFReq = 0;
        try {
            double currentFreq;
            RandomAccessFile readerCurFreq;
            readerCurFreq = new RandomAccessFile("/sys/devices/system/cpu/cpu" + 0 + "/cpufreq/scaling_cur_freq", "r");
            String curfreg = readerCurFreq.readLine();
            currentFreq = Double.parseDouble(curfreg) / 1000;
            readerCurFreq.close();
            currentFReq = (int) currentFreq;
            System.out.println(currentFReq+"----------------------------------------------------");
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return currentFReq;
    }

    private int getMinCpuFrequency() {
        int minFreq = -1;
        try {
            RandomAccessFile randomAccessFile = new RandomAccessFile("/sys/devices/system/cpu/cpu" + 0 + "/cpufreq/cpuinfo_min_freq", "r");
            while (true) {
                String line = randomAccessFile.readLine();
                if (null == line) {
                    break;
                }
                int timeInState = Integer.parseInt(line);
                if (timeInState > 0) {
                    int freq = timeInState / 1000;
                    if (freq > minFreq) {
                        minFreq = freq;
                    }
                }
            }
        } catch (Exception exception) {
            exception.printStackTrace();
        }
        return minFreq;
    }

    private int getMaxCpuFrequency() {
        int currentFReq = 0;
        try {
            double currentFreq;
            RandomAccessFile readerCurFreq;
            readerCurFreq = new RandomAccessFile("/sys/devices/system/cpu/cpu" + 0 + "/cpufreq/cpuinfo_max_freq", "r");
            String curfreg = readerCurFreq.readLine();
            currentFreq = Double.parseDouble(curfreg) / 1000;
            readerCurFreq.close();
            currentFReq = (int) currentFreq;
        } catch (Exception exception) {
            exception.printStackTrace();
        }
        return currentFReq;
    }
}
