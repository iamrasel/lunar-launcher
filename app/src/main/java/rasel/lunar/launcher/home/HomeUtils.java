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

package rasel.lunar.launcher.home;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.text.format.DateFormat;
import android.view.View;

import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;

import java.util.Calendar;

import rasel.lunar.launcher.R;
import rasel.lunar.launcher.helpers.Constants;
import rasel.lunar.launcher.helpers.SwipeTouchListener;
import rasel.lunar.launcher.helpers.TypewriterTexts;
import rasel.lunar.launcher.helpers.UniUtils;
import rasel.lunar.launcher.qactions.QuickActions;
import rasel.lunar.launcher.settings.SettingsActivity;
import rasel.lunar.launcher.todos.TodoManager;

public class HomeUtils {

    Constants constants = new Constants();
    UniUtils uniUtils = new UniUtils();

    // Gets time format
    protected String getTimeFormat(SharedPreferences sharedPreferences, Context context) {
        int timeFormatValue = sharedPreferences.getInt(constants.SHARED_PREF_TIME_FORMAT, 0);
        String timeFormat = null;
        switch (timeFormatValue) {
            case 0:
                if(DateFormat.is24HourFormat(context)) {
                    timeFormat = "kk:mm";
                } else {
                    timeFormat = "h:mm a";
                }
                break;
            case 1: timeFormat = "h:mm a";
                break;
            case 2: timeFormat = "kk:mm";
                break;
        }
        return timeFormat;
    }

    // Gets number suffixes
    private String getDateNumberSuffix(){
        Calendar calendar = Calendar.getInstance();
        int date = calendar.get(Calendar.DAY_OF_MONTH);
        switch (date) {
            case 1:     case 21:    case 31:
                return "ˢᵗ";
            case 2:     case 22:
                return "ⁿᵈ";
            case 3:     case 23:
                return "ʳᵈ";
            default: return "ᵗʰ";
        }
    }

    // Gets date format
    protected String getDateFormat(SharedPreferences sharedPreferences) {
        int dateFormatValue = sharedPreferences.getInt(constants.SHARED_PREF_SHOW_YEAR, 1);
        String dateFormat = null;
        switch (dateFormatValue) {
            case 0: dateFormat = "EEE',' d" + getDateNumberSuffix() + " MMMM";
                break;
            case 1: dateFormat = "EEE',' d" + getDateNumberSuffix() + " MMM',' yyyy";
                break;
        }
        return dateFormat;
    }
	
	// Shows the 99 names of Allah
	protected void showNames(SharedPreferences sharedPreferences, TypewriterTexts typewriterTexts, FragmentActivity fragmentActivity) {
		int namesModeValue = sharedPreferences.getInt(constants.SHARED_PREF_NAMES99, 0);
		String[] arrays = new String[0];
        final String[] randomString = new String[1];
        final int[] iterator = {0};
        switch(namesModeValue) {
            case 0: typewriterTexts.setVisibility(View.INVISIBLE); break;
            case 1: arrays = fragmentActivity.getResources().getStringArray(R.array.arabic); break;
            case 2: arrays = fragmentActivity.getResources().getStringArray(R.array.english); break;
            case 3: arrays = fragmentActivity.getResources().getStringArray(R.array.english_meaning); break;
        }
        for (String array : arrays) {
            randomString[0] = array;
        }
        typewriterTexts.animateText(fragmentActivity.getString(R.string.bismillah));
        String[] finalArrays = arrays;
        typewriterTexts.setOnClickListener(v -> {
            randomString[0] = finalArrays[iterator[0]];
            typewriterTexts.animateText(randomString[0]);
            iterator[0] = iterator[0] +1;
            if(iterator[0] >= finalArrays.length) {
                iterator[0] = 0;
            }
        });
	}

    // Gestures on root view
    protected void rootViewGestures(View view, Context context, FragmentManager fragmentManager, FragmentActivity fragmentActivity, int lockMethodValue) {
        view.setOnTouchListener(new SwipeTouchListener(context) {
            @Override
            public void onSwipeUp() {
                super.onSwipeUp();
                (new QuickActions()).show(fragmentManager, constants.MODAL_BOTTOM_SHEET_TAG);
            }
            @Override
            public void onSwipeDown() {
                super.onSwipeDown();
                uniUtils.expandNotificationPanel(context, fragmentActivity);
            }
            @Override
            public void onDoubleClick() {
                super.onDoubleClick();
                lockMethod(lockMethodValue, context, fragmentActivity);
            }
        });
    }

    // Gestures on battery progress indicator
    protected void batteryProgressGestures(View view, Context context, FragmentActivity fragmentActivity, int lockMethodValue) {
        view.setOnTouchListener(new SwipeTouchListener(context) {
            @Override
            public void onLongClick() {
                super.onLongClick();
                fragmentActivity.startActivity(new Intent(context, SettingsActivity.class));
            }
            @Override
            public void onSwipeDown() {
                super.onSwipeDown();
                uniUtils.expandNotificationPanel(context, fragmentActivity);
            }
            @Override
            public void onDoubleClick() {
                super.onDoubleClick();
                lockMethod(lockMethodValue, context, fragmentActivity);
            }
        });
    }

    // Gestures on todos section
    protected void todosGestures(View view, Context context, FragmentManager fragmentManager, FragmentActivity fragmentActivity, int lockMethodValue) {
        view.setOnTouchListener(new SwipeTouchListener(context) {
            @Override
            public void onLongClick() {
                super.onLongClick();
                (fragmentManager.beginTransaction()).replace(R.id.main_fragments_container,
                        new TodoManager()).addToBackStack("").commit();
            }
            @Override
            public void onSwipeUp() {
                super.onSwipeUp();
                (new QuickActions()).show(fragmentManager, constants.MODAL_BOTTOM_SHEET_TAG);
            }
            @Override
            public void onSwipeDown() {
                super.onSwipeDown();
                uniUtils.expandNotificationPanel(context, fragmentActivity);
            }
            @Override
            public void onDoubleClick() {
                super.onDoubleClick();
                lockMethod(lockMethodValue, context, fragmentActivity);
            }
        });
    }

    private void lockMethod(int lockMethodValue, Context context, FragmentActivity fragmentActivity) {
        if(lockMethodValue == 1) {
            uniUtils.lockAccessibility(fragmentActivity);
        } else if(lockMethodValue == 2) {
            uniUtils.lockDeviceAdmin(context, fragmentActivity);
        } else if(lockMethodValue == 3) {
            uniUtils.lockRoot(fragmentActivity);
        }
    }
}
