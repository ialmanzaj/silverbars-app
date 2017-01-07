package com.app.proj.silverbars.interactors;

import android.util.Log;

import com.app.proj.silverbars.LoginService;
import com.app.proj.silverbars.models.AccessToken;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.app.proj.silverbars.Constants.CONSUMER_KEY;
import static com.app.proj.silverbars.Constants.CONSUMER_SECRET;

/**
 * Created by isaacalmanza on 01/07/17.
 */

public class LoginInteractor {

    private static final String TAG = LoginInteractor.class.getSimpleName();

    private LoginService loginService;




    public void getAccessToken(String facebook_token){
        loginService.getAccessToken("convert_token",CONSUMER_KEY,CONSUMER_SECRET,"facebook",facebook_token).enqueue(new Callback<AccessToken>() {
            @Override
            public void onResponse(Call<AccessToken> call, Response<AccessToken> response) {
                if (response.isSuccessful()) {



                } else {
                    Log.e(TAG, "statusCode:" + response.code());
                }
            }
            @Override
            public void onFailure(Call<AccessToken> call, Throwable t) {
                Log.e(TAG, "initial token, onFailure", t);
            }
        });
    }


}




