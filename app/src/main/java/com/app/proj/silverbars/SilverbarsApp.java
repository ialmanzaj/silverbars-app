package com.app.proj.silverbars;

import android.app.Application;
import android.support.multidex.MultiDexApplication;
import android.util.Log;

import com.crashlytics.android.Crashlytics;

import io.fabric.sdk.android.Fabric;
import uk.co.chrisjenx.calligraphy.CalligraphyConfig;
/**
 * Created by isaacalmanza on 10/04/16.
 */

public class SilverbarsApp extends MultiDexApplication {

    @Override
    public void onCreate() {
        super.onCreate();


        Fabric.with(this, new Crashlytics());

        if (!BuildConfig.DEBUG) {
            Log.d("Ez", "Release mode. Crashlytics enable");
            //Fabric.with(this, new Crashlytics());
        } else {
            Log.d("Ez", "Debug mode. Crashlytics disable");
        }

        CalligraphyConfig.initDefault(new CalligraphyConfig.Builder()
                .setDefaultFontPath("fonts/OpenSans-Regular.ttf")
                .setFontAttrId(R.attr.fontPath)
                .build()
        );
    }
}
