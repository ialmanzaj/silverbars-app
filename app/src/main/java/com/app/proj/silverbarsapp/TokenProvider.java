package com.app.proj.silverbarsapp;

import android.accounts.Account;
import android.util.Log;

import com.andretietz.retroauth.AndroidToken;
import com.andretietz.retroauth.AndroidTokenType;
import com.andretietz.retroauth.Provider;
import com.andretietz.retroauth.TokenStorage;
import com.app.proj.silverbarsapp.models.AccessToken;

import java.io.IOException;

import okhttp3.Request;
import retrofit2.Response;

import static com.app.proj.silverbarsapp.Constants.CONSUMER_KEY;
import static com.app.proj.silverbarsapp.Constants.CONSUMER_SECRET;

/**
 * Created by isaacalmanza on 09/23/16.
 */

public class TokenProvider implements Provider<Account, AndroidTokenType, AndroidToken>{

    private static final String TAG = "TokenProvider";

    @Override
    public Request authenticateRequest(Request request, AndroidToken androidToken) {
        Log.d(TAG,"token:"+androidToken.token);
        return request.newBuilder()
                .header("Authorization", "Bearer " + androidToken.token)
                .build();
    }

    @Override
    public boolean retryRequired(int i, okhttp3.Response response, TokenStorage<Account, AndroidTokenType, AndroidToken> tokenStorage, Account account, AndroidTokenType androidTokenType, AndroidToken androidToken) {
        if (!response.isSuccessful()) {
            Log.d(TAG,"code: "+response.code());
            if (response.code() == 401) {
                tokenStorage.removeToken(account, androidTokenType, androidToken);

                if (androidToken.refreshToken != null) {

                    LoginService loginService = ServiceGenerator.createService(LoginService.class);

                    try {

                        Response<AccessToken> refreshResponse = loginService.refreshAccessToken("refresh_token",CONSUMER_KEY,CONSUMER_SECRET,androidToken.refreshToken).execute();

                        if (refreshResponse.isSuccessful()){

                            AccessToken accessToken = refreshResponse.body();
                            Log.d(TAG,"accessToken: "+accessToken.getAccess_token());


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
