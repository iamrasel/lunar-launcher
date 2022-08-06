package rasel.lunar.launcher.helpers;

import static android.content.Context.CLIPBOARD_SERVICE;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.graphics.Insets;
import android.os.Build;
import android.util.DisplayMetrics;
import android.view.WindowInsets;
import android.view.WindowMetrics;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;

import rasel.lunar.launcher.R;

public class UniUtils {

    public int getScreenWidth(@NonNull Activity activity) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            WindowMetrics windowMetrics = activity.getWindowManager().getCurrentWindowMetrics();
            Insets insets = windowMetrics.getWindowInsets()
                    .getInsetsIgnoringVisibility(WindowInsets.Type.systemBars());
            return windowMetrics.getBounds().width() - insets.left - insets.right;
        } else {
            DisplayMetrics displayMetrics = new DisplayMetrics();
            activity.getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
            return displayMetrics.widthPixels;
        }
    }

    public int getScreenHeight(@NonNull Activity activity) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            WindowMetrics windowMetrics = activity.getWindowManager().getCurrentWindowMetrics();
            Insets insets = windowMetrics.getWindowInsets()
                    .getInsetsIgnoringVisibility(WindowInsets.Type.systemBars());
            return windowMetrics.getBounds().height() - insets.top - insets.bottom;
        } else {
            DisplayMetrics displayMetrics = new DisplayMetrics();
            activity.getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
            return displayMetrics.heightPixels;
        }
    }

    // Copies texts to clipboard
    public void copyToClipboard(FragmentActivity fragmentActivity, Context context, String copiedString) {
        ClipboardManager clipBoard = (ClipboardManager) fragmentActivity.getSystemService(CLIPBOARD_SERVICE);
        ClipData clipData = ClipData.newPlainText(null, copiedString);
        clipBoard.setPrimaryClip(clipData);
        Toast.makeText(context, context.getString(R.string.copied_message), Toast.LENGTH_SHORT).show();
    }

    // Expands notification panel
    @SuppressLint("WrongConstant")
    public void expandNotificationPanel(Context context) {
        try {
            ((Class.forName("android.app.StatusBarManager"))
                    .getMethod("expandNotificationsPanel"))
                    .invoke(context.getSystemService("statusbar"));
        } catch (Exception ignored) {}
    }
}
