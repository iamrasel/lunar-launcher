package rasel.lunar.launcher.helpers;

import android.app.admin.DeviceAdminReceiver;
import android.content.Context;
import android.content.Intent;

import androidx.localbroadcastmanager.content.LocalBroadcastManager;

public class AdminReceiver extends DeviceAdminReceiver {
    public static final String ACTION_DISABLED = "device_admin_action_disabled";
    public static final String ACTION_ENABLED = "device_admin_action_enabled";

    @Override
    public void onDisabled(Context context, Intent intent) {
        super.onDisabled(context, intent);
        LocalBroadcastManager.getInstance(context).sendBroadcast(
                new Intent(ACTION_DISABLED));
    }
    @Override
    public void onEnabled(Context context, Intent intent) {
        super.onEnabled(context, intent);
        LocalBroadcastManager.getInstance(context).sendBroadcast(
                new Intent(ACTION_ENABLED));
    }
}
