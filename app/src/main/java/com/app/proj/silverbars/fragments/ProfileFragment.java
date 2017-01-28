package com.app.proj.silverbars.fragments;


import android.content.Context;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.app.proj.silverbars.MainService;
import com.app.proj.silverbars.R;
import com.app.proj.silverbars.utils.Utilities;

import butterknife.BindView;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;


public class ProfileFragment extends Fragment {

    private static final String TAG = ProfileFragment.class.getSimpleName();
   
    private RecyclerView list;
    
    private RecyclerView.Adapter adapter;


    @BindView(R.id.myTeam)RelativeLayout myTeam;
    @BindView(R.id.Progression)RelativeLayout Progression;
    @BindView(R.id.skillsProgression)RelativeLayout skillsProgression;
    @BindView(R.id.sharedWorkouts)RelativeLayout sharedWorkouts;
    @BindView(R.id.history)RelativeLayout history;

    @BindView(R.id.Profile_name) TextView mProfileName;
    @BindView(R.id.profile_image) ImageView profile_image;

    private Utilities utilities;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_profile, container, false);

    }
    
    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);


    }


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

                        boolean writtenToDisk = utilities.saveWorkoutImgInDevice(context,response.body(),imgName);
                        if(writtenToDisk){bitmap = utilities.loadWorkoutImageFromDevice(context,imgName);}
                        profile_image.setImageBitmap(bitmap);


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
