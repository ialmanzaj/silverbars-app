package com.app.proj.silverbars;

import android.accounts.Account;
import android.content.Context;
import android.util.Log;

import com.andretietz.retroauth.AndroidToken;
import com.andretietz.retroauth.AndroidTokenType;
import com.andretietz.retroauth.Provider;
import com.andretietz.retroauth.TokenStorage;
import com.google.gson.Gson;

import java.io.IOException;

import okhttp3.Authenticator;
import okhttp3.Request;
import okhttp3.Route;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

/**
 * Created by isaacalmanza on 09/23/16.
 */

public class TokenProvider implements Provider<Account, AndroidTokenType, AndroidToken>{

    private static final String TAG = "TokenProvider";
    private static final String CONSUMER_KEY = "KHeJV3Sg8ShguiYyvDf9t6i3WPpMpDWlBLN93mgz";
    private static final String CONSUMER_SECRET = "1krO5gdrzs08Ej5WoGpLrQifbuDRNFxEnRqLKyHFJIFG2fPpGPE3t1J8nCS7K9NoSidUCibUUi985ipRiipjM0YV6PoUDMcXw08A4M8R7yfzECFGDHnxVBYgQfgjfc2e";

    @Override
    public Request authenticateRequest(Request request, AndroidToken androidToken) {
        return request.newBuilder()
                .header("Authorization", "Bearer " + androidToken.token)
                .build();
    }

    @Override
    public boolean retryRequired(int i, okhttp3.Response response, TokenStorage<Account, AndroidTokenType, AndroidToken> tokenStorage, Account account, AndroidTokenType androidTokenType, AndroidToken androidToken) {
        Log.v(TAG,"retryRequired");

        if (!response.isSuccessful()) {
            Log.v(TAG,"code: "+response.code());
            if (response.code() == 401) {
                tokenStorage.removeToken(account, androidTokenType, androidToken);

                if (androidToken.refreshToken != null) {

                    LoginService loginService = ServiceGenerator.createService(LoginService.class);

                    try {

                        Response<AccessToken> refreshResponse = loginService.getRefreshAccessToken("refresh_token",CONSUMER_KEY,CONSUMER_SECRET,androidToken.refreshToken).execute();

                        if (refreshResponse.isSuccessful()){

                            AccessToken accessToken = refreshResponse.body();
                            Log.v(TAG,"accessToken:"+accessToken.getAccess_token());


                            tokenStorage.storeToken(account, androidTokenType,
                                    new AndroidToken(accessToken.getAccess_token(), accessToken.getRefresh_token()));

                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                return true;
            }
        }
        return false;
    }
}
