package com.app.app.silverbarsapp.interactors;

import com.app.app.silverbarsapp.LoginService;
import com.app.app.silverbarsapp.callbacks.LoginCallback;
import com.app.app.silverbarsapp.database_models.FbProfile;
import com.app.app.silverbarsapp.handlers.DatabaseHelper;
import com.app.app.silverbarsapp.handlers.DatabaseQueries;
import com.app.app.silverbarsapp.models.AccessToken;

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

    private LoginService loginService;
    private DatabaseQueries databaseQueries;

    public LoginInteractor(LoginService loginService,DatabaseHelper helper){
        this.loginService = loginService;
        this.databaseQueries = new DatabaseQueries(helper);
    }

    public void saveProfile(FbProfile profile, LoginCallback callback){
        try {

            FbProfile profile_saved = databaseQueries.saveFaceProfile(profile);
            callback.onProfileSaved(profile_saved);

        } catch (SQLException e) {
           // Log.e(TAG,"SQLException",e);
        }
    }

    public void getAccessToken(LoginCallback callback, String fbAccesstoken){
        loginService.getAccessToken("convert_token",CONSUMER_KEY,CONSUMER_SECRET,"facebook",fbAccesstoken)
                .enqueue(new Callback<AccessToken>() {
            @Override
            public void onResponse(Call<AccessToken> call, Response<AccessToken> response) {
                if (response.isSuccessful()) {

                    callback.onToken(response.body());

                } else{
                    callback.onServerError();
                }
            }
            @Override
            public void onFailure(Call<AccessToken> call, Throwable t) {
                callback.onNetworkError();
            }
        });
    }


}




