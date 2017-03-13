package com.app.app.silverbarsapp.interactors;

import android.util.Log;

import com.app.app.silverbarsapp.LoginService;
import com.app.app.silverbarsapp.callbacks.LoginCallback;
import com.app.app.silverbarsapp.database_models.ProfileFacebook;
import com.app.app.silverbarsapp.models.AccessToken;
import com.app.app.silverbarsapp.utils.DatabaseHelper;

import java.sql.SQLException;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.app.app.silverbarsapp.Constants.CONSUMER_KEY;
import static com.app.app.silverbarsapp.Constants.CONSUMER_SECRET;

/**
 * Created by isaacalmanza on 01/07/17.
 */

public class LoginInteractor {

    private static final String TAG = LoginInteractor.class.getSimpleName();

    private DatabaseHelper helper;
    private LoginService loginService;

    public LoginInteractor(LoginService loginService,DatabaseHelper helper){
        this.loginService = loginService;
        this.helper = helper;
    }


    public void getAccessToken(LoginCallback callback, String facebook_token){
        loginService.getAccessToken("convert_token",CONSUMER_KEY,CONSUMER_SECRET,"facebook",facebook_token).enqueue(new Callback<AccessToken>() {
            @Override
            public void onResponse(Call<AccessToken> call, Response<AccessToken> response) {
                if (response.isSuccessful()) {

                    callback.onToken(response.body());

                } else{
                    Log.e(TAG, "statusCode:" + response.code());
                    callback.onServerError();
                }
            }
            @Override
            public void onFailure(Call<AccessToken> call, Throwable t) {
                Log.e(TAG, "initial token, onFailure", t);
                callback.onServerError();
            }
        });
    }

    public void saveProfile(ProfileFacebook profile,LoginCallback callback){
        try {

            helper.getProfileFacebook().create(profile);

            callback.onProfileCreated(true);


        } catch (SQLException e) {
            callback.onProfileCreated(false);
            e.printStackTrace();
        }

    }


}




