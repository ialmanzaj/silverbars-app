package com.app.app.silverbarsapp;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

/**
 * Created by isaacalmanza on 05/11/17.
 */

public class NetworkReceiver extends BroadcastReceiver {
    private static final String TAG = NetworkReceiver.class.getSimpleName();

    @Override
    public void onReceive(Context context, Intent intent) {
        debugIntent(intent, TAG);
    }
    private void debugIntent(Intent intent, String tag) {
        Log.v(tag, "action: " + intent.getAction());
        Log.v(tag, "component: " + intent.getComponent());
        Bundle extras = intent.getExtras();
        if (extras != null) {
            for (String key: extras.keySet()) {
                Log.v(tag, "key [" + key + "]: " +
                        extras.get(key));
            }
        }
        else {
            Log.v(tag, "no extras");
        }
    }

}