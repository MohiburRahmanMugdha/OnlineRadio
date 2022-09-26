package com.mrapps.onlineradio.utils;

import static android.content.Context.MODE_PRIVATE;

import android.content.Context;
import android.content.SharedPreferences;

public class FirstLaunch {

    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;
    String PREF= "first_launch_utils";
    String PREF_NAME = "is_first_time_launch";


    public FirstLaunch(Context context) {
           sharedPreferences = context.getSharedPreferences(PREF, MODE_PRIVATE);
            editor = sharedPreferences.edit();

    }

    public boolean isFirstTimeLaunch() {
        return sharedPreferences.getBoolean(PREF_NAME, true);
    }

    public void setFirstTimeLaunch() {
        editor.putBoolean(PREF_NAME, false).apply();
    }


}
