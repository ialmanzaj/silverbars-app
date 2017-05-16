package com.app.app.silverbarsapp.interactors;

import android.util.Log;

import com.app.app.silverbarsapp.MainService;
import com.app.app.silverbarsapp.callbacks.ProfileCallback;
import com.app.app.silverbarsapp.database_models.FbProfile;
import com.app.app.silverbarsapp.handlers.DatabaseHelper;

import java.sql.SQLException;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

/**
 * Created by isaacalmanza on 01/28/17.
 */

public class ProfileInteractor {

    private static final String TAG = ProfileInteractor.class.getSimpleName();

    private DatabaseHelper helper;

    public ProfileInteractor(DatabaseHelper helper){
        this.helper = helper;
    }


    public void getProfile(ProfileCallback callback){
        try {

            FbProfile fbProfile = helper.getProfileFacebook().queryForAll().get(0);

            callback.getProfileFacebook(fbProfile);

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void getProfileImage(ProfileCallback callback) throws SQLException {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://graph.facebook.com/")
                .build();

        String url_complete =  helper.getProfileFacebook().queryForAll().get(0).getId() + "/picture?type=large";

        Log.d(TAG,"url_complete: "+url_complete);

        MainService downloadService = retrofit.create(MainService.class);
        downloadService.downloadFile(url_complete).enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful()) {
                    Log.d(TAG,"response ");
                    callback.getProfileImg(response.body());

                } else {
                    Log.v(TAG,"State server contact failed");
                }
            }
            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Log.e(TAG,"onFailure",t);
            }
        });
    }






}
