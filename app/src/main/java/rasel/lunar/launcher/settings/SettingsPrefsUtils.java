package rasel.lunar.launcher.settings;

import android.content.Context;
import android.content.SharedPreferences;

import rasel.lunar.launcher.helpers.Constants;

public class SettingsPrefsUtils {

    private final Constants constants = new Constants();

    protected void saveTimeFormat(Context context, int timeFormatValue) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(constants.SHARED_PREFS_SETTINGS, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt(constants.SHARED_PREF_TIME_FORMAT, timeFormatValue);
        editor.apply();
    }

    protected void showYear(Context context, int showYear) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(constants.SHARED_PREFS_SETTINGS, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt(constants.SHARED_PREF_SHOW_YEAR, showYear);
        editor.apply();
    }

    protected void saveCityName(Context context, String cityName) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(constants.SHARED_PREFS_SETTINGS, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(constants.SHARED_PREF_CITY_NAME, cityName);
        editor.apply();
    }

    protected void saveOwmKey(Context context, String owmKey) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(constants.SHARED_PREFS_SETTINGS, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(constants.SHARED_PREF_OWM_KEY, owmKey);
        editor.apply();
    }

    protected void saveTempUnit(Context context, int tempUnit) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(constants.SHARED_PREFS_SETTINGS, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt(constants.SHARED_PREF_TEMP_UNIT, tempUnit);
        editor.apply();
    }

    protected void showCity(Context context, int showCity) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(constants.SHARED_PREFS_SETTINGS, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt(constants.SHARED_PREF_SHOW_CITY, showCity);
        editor.apply();
    }

    protected void showTodos(Context context, int showTodos) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(constants.SHARED_PREFS_SETTINGS, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt(constants.SHARED_PREF_SHOW_TODOS, showTodos);
        editor.apply();
    }

    protected void saveFeedUrl(Context context, String feedUrl) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(constants.SHARED_PREFS_SETTINGS, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(constants.SHARED_PREF_FEED_URL, feedUrl);
        editor.apply();
    }

    protected void saveLockMode(Context context, int lockMode) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(constants.SHARED_PREFS_SETTINGS, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt(constants.SHARED_PREF_LOCK, lockMode);
        editor.apply();
    }

    protected void saveTheme(Context context, int themeValue) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(constants.SHARED_PREFS_SETTINGS, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt(constants.SHARED_PREF_THEME, themeValue);
        editor.apply();
    }
}
