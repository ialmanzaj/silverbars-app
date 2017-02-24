package com.app.proj.silverbarsapp.interactors;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.Log;

import com.app.proj.silverbarsapp.MainService;

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

    private void DownloadProfileImage(final Context context, String url, final String imgName){


        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://graph.facebook.com/")
                .build();
        String url_complete = url + "/picture?type=large";

        MainService downloadService = retrofit.create(MainService.class);

        downloadService.downloadFile(url_complete).enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful()) {

                    Bitmap bitmap = null;

                    if (response.isSuccessful()) {

                       /* boolean writtenToDisk = utilities.saveWorkoutImgInDevice(context,response.body(),imgName);
                        if(writtenToDisk){bitmap = utilities.loadWorkoutImageFromDevice(context,imgName);}
                        profile_image.setImageBitmap(bitmap);*/


                    }
                    else {
                        Log.v(TAG, "Download server contact failed");
                    }



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
