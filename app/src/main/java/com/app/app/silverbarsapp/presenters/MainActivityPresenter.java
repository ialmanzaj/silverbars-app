package com.app.app.silverbarsapp.presenters;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.app.app.silverbarsapp.MainService;
import com.app.app.silverbarsapp.R;
import com.app.app.silverbarsapp.utils.Utilities;

import java.io.File;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

import static com.app.app.silverbarsapp.Constants.AMAZON_S3_URL;
import static com.app.app.silverbarsapp.Constants.MUSCLE_BODY_TEMPLATE;

/**
 * Created by isaacalmanza on 01/28/17.
 */

public class MainActivityPresenter {

    private static final String TAG = MainActivityPresenter.class.getSimpleName();

    private Utilities utilities;
    private Context context;

    public  MainActivityPresenter(Context context){

    }

    private void downloadHtmlMuscleTemplate(){

        Retrofit retrofit = new Retrofit.Builder().baseUrl(AMAZON_S3_URL).build();

        MainService mDownloadService = retrofit.create(MainService.class);

        mDownloadService.downloadFile(MUSCLE_BODY_TEMPLATE).enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful()) {
                    utilities.saveHtmInDevice(context,response.body(),"index.html");
                } else {Log.e(TAG, "Download server contact failed");}
            }
            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Log.e(TAG, "Download server contact failed",t);}
        });


    }

    private void saveBodyTemplate(){
        File Dir = utilities.getFileReady(context,"/html/");
        if (Dir.isDirectory()){
            Log.v(TAG,"EXISTE DIR"+Dir.getPath());
            File file = utilities.getFileReady(context,"/html/"+"index.html");
            if (!file.exists()){
                downloadHtmlMuscleTemplate();
                setMusclePath(file);
            }else
                setMusclePath(file);
        }else {
            boolean success = Dir.mkdir();
            if (success)
                downloadHtmlMuscleTemplate();
            else
                Log.e(TAG,"Error creating dir");
        }

    }

    private void setMusclePath(File file){
        SharedPreferences sharedPref = context.getSharedPreferences("Mis preferencias", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString(context.getString(R.string.muscle_path), file.getPath());
        editor.apply();
    }


}
