package com.example.shaunakbasu.capstone;

import android.support.multidex.MultiDexApplication;

/**
 * Created by shaunak basu on 21-11-2016.
 */
public class FontApp extends MultiDexApplication {

    @Override
    public void onCreate() {
        super.onCreate();
        TypefaceUtil.overrideFont(getApplicationContext(), "SERIF", "fonts/RobotoCondensed-Light.ttf");
    }
}
