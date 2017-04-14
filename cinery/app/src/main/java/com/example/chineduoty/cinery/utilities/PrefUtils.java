package com.example.chineduoty.cinery.utilities;

import android.content.Context;
import android.content.SharedPreferences;

import com.example.chineduoty.cinery.R;

/**
 * Created by chineduoty on 4/14/17.
 */

public class PrefUtils {
    private final Context context;
    private final SharedPreferences sharedPref;

    public PrefUtils(Context context){
        this.context = context;
        sharedPref = context.getSharedPreferences(
                context.getString(R.string.cinery_preference), Context.MODE_PRIVATE);
    }

    public void savePref(String key, String value){
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString(key, value);
        editor.commit();
    }

    public String readPrefString(String key,String defaultValue){
        return sharedPref.getString(key,defaultValue);
    }

    public boolean readPrefBoolean(String key,boolean defaultValue){
        return sharedPref.getBoolean(key,defaultValue);
    }

    public int readPrefInt(String key,int defaultValue){
        return sharedPref.getInt(key,defaultValue);
    }
}
