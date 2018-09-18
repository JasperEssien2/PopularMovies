package com.example.android.popularmovies;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.example.android.popularmovies.Constants.ApiConstant;

public class SettingsSharedPreference {

    private static SharedPreferences mSharedPreferences;
    private static Context mContext;
    public final static String THEME_SETTINGS_KEY = "theme_settings";
    public final static String THEME_SETTINGS_LIGHT = "light";
    public final static String THEME_SETTINGS_DARK = "dark";
    public final static String SORT_BY_SETTINGS_KEY = "sort_by";
    public final static String SORT_BY_SETTINGS_POPULARITY = ApiConstant.SORT_MOST_POPULAR;
    public final static String SORT_BY_SETTINGS_RATING = ApiConstant.SORT_HIGHEST_RATED;
    public final static String SORT_BY_SETTINGS_FAVOURITES = "favourites";

    public SettingsSharedPreference(Context context){
        mSharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        mContext = context;
    }

    public static void initSettingsSharedPrefernce(Context context){
        mSharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        mContext = context;
    }

    public static void setThemeSettings(String val){
        SharedPreferences.Editor editor = mSharedPreferences.edit();
        editor.putString(THEME_SETTINGS_KEY, val);
        editor.apply();
    }

    public static String getThemeSettings(){
        return mSharedPreferences.getString(THEME_SETTINGS_KEY, THEME_SETTINGS_DARK);
    }

    public static void setSortBySettings(String val){
        SharedPreferences.Editor editor = mSharedPreferences.edit();
        editor.putString(SORT_BY_SETTINGS_KEY, val);
        editor.apply();
    }

    public static String getSortBySettings(){
        return mSharedPreferences.getString(SORT_BY_SETTINGS_KEY, SORT_BY_SETTINGS_POPULARITY);
    }
}
