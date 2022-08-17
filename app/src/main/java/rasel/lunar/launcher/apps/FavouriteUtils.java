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

package rasel.lunar.launcher.apps;

import android.content.Context;
import android.content.SharedPreferences;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.button.MaterialButtonToggleGroup;

import rasel.lunar.launcher.helpers.Constants;

public class FavouriteUtils {

    private final Constants constants = new Constants();
    public String packageOne, packageTwo, packageThree, packageFour, packageFive, packageSix;

    public void saveFavApps(Context context, int position, String packageName) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(constants.SHARED_PREFS_FAV_APPS, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(constants.FAV_APP_ + position, packageName);
        editor.apply();
    }

    protected void saveFavPosition(MaterialButtonToggleGroup buttonToggleGroup, MaterialButton button1, MaterialButton button2, MaterialButton button3, MaterialButton button4, MaterialButton button5, MaterialButton button6, Context context, String packageName) {
        buttonToggleGroup.addOnButtonCheckedListener((group, checkedId, isChecked) -> {
            if(isChecked) {
                if(checkedId == button1.getId()) {
                    saveFavApps(context, 1, packageName);
                } else if(checkedId == button2.getId()) {
                    saveFavApps(context, 2, packageName);
                } else if(checkedId == button3.getId()) {
                    saveFavApps(context, 3, packageName);
                } else if(checkedId == button4.getId()) {
                    saveFavApps(context, 4, packageName);
                } else if(checkedId == button5.getId()) {
                    saveFavApps(context, 5, packageName);
                } else if(checkedId == button6.getId()) {
                    saveFavApps(context, 6, packageName);
                }
            } else {
                if(checkedId == button1.getId()) {
                    saveFavApps(context, 1, null);
                } else if(checkedId == button2.getId()) {
                    saveFavApps(context, 2, null);
                } else if(checkedId == button3.getId()) {
                    saveFavApps(context, 3, null);
                } else if(checkedId == button4.getId()) {
                    saveFavApps(context, 4, null);
                } else if(checkedId == button5.getId()) {
                    saveFavApps(context, 5, null);
                } else if(checkedId == button6.getId()) {
                    saveFavApps(context, 6, null);
                }
            }
        });
    }

    public void initialize(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(constants.SHARED_PREFS_FAV_APPS, Context.MODE_PRIVATE);
        packageOne = sharedPreferences.getString(constants.FAV_APP_ + 1, null);
        packageTwo = sharedPreferences.getString(constants.FAV_APP_ + 2, null);
        packageThree = sharedPreferences.getString(constants.FAV_APP_ + 3, null);
        packageFour = sharedPreferences.getString(constants.FAV_APP_ + 4, null);
        packageFive = sharedPreferences.getString(constants.FAV_APP_ + 5, null);
        packageSix = sharedPreferences.getString(constants.FAV_APP_ + 6, null);
    }

    protected void setPreview(Context context, String packageName, MaterialButton button1, MaterialButton button2, MaterialButton button3, MaterialButton button4, MaterialButton button5, MaterialButton button6) {
        initialize(context);
        if(packageName.equals(packageOne)) {
            button1.setChecked(true);
        } else if(packageName.equals(packageTwo)) {
            button2.setChecked(true);
        } else if(packageName.equals(packageThree)) {
            button3.setChecked(true);
        } else if(packageName.equals(packageFour)) {
            button4.setChecked(true);
        } else if(packageName.equals(packageFive)) {
            button5.setChecked(true);
        } else if(packageName.equals(packageSix)) {
            button6.setChecked(true);
        }
    }
}
