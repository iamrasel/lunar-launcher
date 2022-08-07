package rasel.lunar.launcher.home;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.BatteryManager;
import android.view.animation.AnimationUtils;
import android.widget.ProgressBar;

import rasel.lunar.launcher.R;

public class BatteryReceiver extends BroadcastReceiver {
    ProgressBar progressBar;

    protected BatteryReceiver(ProgressBar progressBar) {
        this.progressBar = progressBar;
    }

    // Gets battery percentage
    private int getBatteryPercentage(Intent intent) {
        int scale = intent.getIntExtra(BatteryManager.EXTRA_SCALE, -1);
        int level = intent.getIntExtra(BatteryManager.EXTRA_LEVEL, -1);
        float percentage = level / (float) scale;
        return (int) ((percentage) * 100);
    }

    // Gets charging status
    private int getChargingStatus(Intent intent) {
        return intent.getIntExtra(BatteryManager.EXTRA_STATUS, -1);
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        progressBar.setProgress(getBatteryPercentage(intent));
        if(getChargingStatus(intent) == BatteryManager.BATTERY_STATUS_CHARGING ||
                getChargingStatus(intent) == BatteryManager.BATTERY_STATUS_FULL) {
            progressBar.startAnimation(AnimationUtils.loadAnimation(context, R.anim.rotate_clockwise));
        } else if(getChargingStatus(intent) == BatteryManager.BATTERY_STATUS_DISCHARGING) {
            progressBar.clearAnimation();
        }
    }
}
