package com.app.proj.silverbars;

import android.content.Context;
import android.support.multidex.MultiDexApplication;
import android.util.Log;

import com.app.proj.silverbars.components.DaggerSilverbarsComponent;
import com.app.proj.silverbars.components.SilverbarsComponent;
import com.app.proj.silverbars.modules.SilverbarsModule;
import com.crashlytics.android.Crashlytics;
import com.facebook.drawee.backends.pipeline.Fresco;

import io.fabric.sdk.android.Fabric;

/**
 * Created by isaacalmanza on 10/04/16.
 */

public class SilverbarsApp extends MultiDexApplication {


    private SilverbarsComponent component;


    /** A flag to show how easily you can switch from standard SQLite to the encrypted SQLCipher. */
    public static final boolean ENCRYPTED = true;

    //private DaoSession daoSession;


    @Override
    public void onCreate() {
        super.onCreate();

        setupGraph();

      /*  // do this once, for example in your Application class

        DevOpenHelper helper = new DevOpenHelper(this, ENCRYPTED ? "notes-db-encrypted" : "notes-db");
        Database db = ENCRYPTED ? helper.getEncryptedWritableDb("super-secret") : helper.getWritableDb();
        daoSession = new DaoMaster(db).newSession();*/



        if (!Fresco.hasBeenInitialized()){
            Fresco.initialize(this);
        }

        if (!BuildConfig.DEBUG) {
            Log.d("SilverbarsApp", "Release mode. Crashlytics enable");
            Fabric.with(this, new Crashlytics());
        } else {
            Log.d("SilverbarsApp", "Debug mode. Crashlytics disable");
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
