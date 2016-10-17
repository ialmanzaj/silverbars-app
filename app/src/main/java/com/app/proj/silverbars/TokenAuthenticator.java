package com.app.proj.silverbars;

import android.content.Context;
import android.util.Log;

import com.google.gson.Gson;

import java.io.IOException;

import okhttp3.Authenticator;
import okhttp3.Request;
import okhttp3.Route;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by isaacalmanza on 09/23/16.
 */

public class TokenAuthenticator implements Authenticator{

    private static final String TAG = "TokenAuthenticator";
    private static final String CONSUMER_KEY = "KHeJV3Sg8ShguiYyvDf9t6i3WPpMpDWlBLN93mgz";
    private static final String CONSUMER_SECRET = "1krO5gdrzs08Ej5WoGpLrQifbuDRNFxEnRqLKyHFJIFG2fPpGPE3t1J8nCS7K9NoSidUCibUUi985ipRiipjM0YV6PoUDMcXw08A4M8R7yfzECFGDHnxVBYgQfgjfc2e";

    private Context context;
    private Gson gson;

    private AuthPreferences authPreferences;
    public TokenAuthenticator(Context context){
        this.authPreferences = new AuthPreferences(context);
        gson = new Gson();
    }



    public void setRefreshToken(String token){
        Log.v(TAG,"refreshToken: called");

        LoginService loginService = ServiceGenerator.createService(LoginService.class);
        Call<AccessToken> call = loginService.getRefreshAccessToken("refresh_token",CONSUMER_KEY,CONSUMER_SECRET,token);

        try {

            AccessToken refresh_token = call.execute().body();

            String new_token = gson.toJson(refresh_token,AccessToken.class);
            authPreferences.setAccessToken(new_token);

        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public String getToken(){
        String token = "";
        if (authPreferences.getAccessToken() != null){
            AccessToken accessToken = gson.fromJson(authPreferences.getAccessToken(),AccessToken.class);
            token = accessToken.getAccess_token();
        }

        return token;
    }


    @Override
    public Request authenticate(Route route, okhttp3.Response response) throws IOException {
        return null;
    }
}
