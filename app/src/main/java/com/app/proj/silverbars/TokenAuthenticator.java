package com.app.proj.silverbars;

import android.content.Context;
import android.util.Log;

import com.google.gson.Gson;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by isaacalmanza on 09/23/16.
 */

public class TokenAuthenticator {


    private static final String TAG = "TokenAuthenticator";
    private static final String CONSUMER_KEY = "KHeJV3Sg8ShguiYyvDf9t6i3WPpMpDWlBLN93mgz";
    private static final String CONSUMER_SECRET = "1krO5gdrzs08Ej5WoGpLrQifbuDRNFxEnRqLKyHFJIFG2fPpGPE3t1J8nCS7K9NoSidUCibUUi985ipRiipjM0YV6PoUDMcXw08A4M8R7yfzECFGDHnxVBYgQfgjfc2e";

    private Context context;

    public TokenAuthenticator(Context context){
        this.context = context;
    }

/*

    public String refreshToken(){
        Log.v(TAG,"refreshToken: called");




        return preferences.getAccessToken();
    }
*/



}
