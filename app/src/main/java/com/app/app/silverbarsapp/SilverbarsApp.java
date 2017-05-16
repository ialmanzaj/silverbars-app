package com.app.app.silverbarsapp;

import android.content.Context;
import android.support.multidex.MultiDexApplication;

import com.app.app.silverbarsapp.components.DaggerSilverbarsComponent;
import com.app.app.silverbarsapp.components.SilverbarsComponent;
import com.app.app.silverbarsapp.modules.SilverbarsModule;
import com.crashlytics.android.Crashlytics;
import com.facebook.drawee.backends.pipeline.Fresco;

import io.fabric.sdk.android.Fabric;

/**
 * Created by isaacalmanza on 10/04/16.
 */

public class SilverbarsApp extends MultiDexApplication {

    private SilverbarsComponent component;

    @Override
    public void onCreate() {
        super.onCreate();

        setupGraph();

        if (!Fresco.hasBeenInitialized()){
            Fresco.initialize(this);
        }

        if (!BuildConfig.DEBUG) {
            //Log.d("SilverbarsApp", "Release mode. Crashlytics enable");
            Fabric.with(this, new Crashlytics());
        } else {
            //Log.d("SilverbarsApp", "Debug mode. Crashlytics disable");
        }

    }

    /**
     * The object graph contains all the instances of the objects
     * that resolve a dependency
     * */
    private void setupGraph() {
        component = DaggerSilverbarsComponent.builder()
                    .silverbarsModule(new SilverbarsModule(this))
                    .build();
    }

    public SilverbarsComponent getComponent() {
        return component;
    }

    public static SilverbarsApp getApp(Context context) {
        return (SilverbarsApp) context.getApplicationContext();
    }


}
