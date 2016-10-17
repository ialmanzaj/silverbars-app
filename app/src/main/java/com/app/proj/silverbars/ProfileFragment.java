package com.app.proj.silverbars;


import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

import static com.app.proj.silverbars.Utilities.loadProfileImageFromCache;
import static com.app.proj.silverbars.Utilities.loadWorkoutImageFromDevice;
import static com.app.proj.silverbars.Utilities.saveWorkoutImgInDevice;
import static com.facebook.Profile.getCurrentProfile;
/**
 * Created by isaacalmanza on 10/04/16.
 */
public class ProfileFragment extends Fragment {

    private static final String TAG = "ProfileFragment";
    View rootView;
    private RecyclerView recycler;
    private RecyclerView.Adapter adapter;
    private RecyclerView.LayoutManager lManager;
    private RelativeLayout myTeam, Progression, skillsProgression, sharedWorkouts, history;
    private TextView profile_name;
    private ImageView profile_image;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        rootView = inflater.inflate(R.layout.fragment_profile, container, false);

        //profiles settings
        profile_name = (TextView) rootView.findViewById(R.id.Profile_name);
        String name = getCurrentProfile().getFirstName()+" "+getCurrentProfile().getLastName();
        profile_name.setText(name);
        profile_image = (ImageView) rootView.findViewById(R.id.profile_image);
        Bitmap bmp = loadProfileImageFromCache(getActivity(),getCurrentProfile().getId());
        if (bmp != null){
            profile_image.setImageBitmap(bmp);
        }else {
            DownloadProfileImage(getActivity(),getCurrentProfile().getId(),getCurrentProfile().getId());
        }


        // buttons of options
        float scale = getResources().getDisplayMetrics().density;
        final int dpAsPixels = (int) (7*scale + 0.5f);
        myTeam = (RelativeLayout) rootView.findViewById(R.id.myTeam);
        myTeam.setClickable(true);


        Progression = (RelativeLayout) rootView.findViewById(R.id.Progression);
        Progression.setClickable(true);
        Progression.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getActivity(),ProgressionActivity.class));
            }
        });


        skillsProgression = (RelativeLayout) rootView.findViewById(R.id.skillsProgression);
        skillsProgression.setClickable(true);

        sharedWorkouts = (RelativeLayout) rootView.findViewById(R.id.sharedWorkouts);
        sharedWorkouts.setClickable(true);

        history = (RelativeLayout) rootView.findViewById(R.id.history);
        history.setClickable(true);

        return rootView;
    }

    private void DownloadProfileImage(final Context context, String url, final String imgName){
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://graph.facebook.com/")
                .build();
        String url_complete = url + "/picture?type=large";
        SilverbarsService downloadService = retrofit.create(SilverbarsService.class);
        Call<ResponseBody> call = downloadService.downloadFile(url_complete);

        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful()) {
                    Bitmap bitmap = null;
                    if (response.isSuccessful()) {
                        boolean writtenToDisk = saveWorkoutImgInDevice(context,response.body(),imgName);
                        if(writtenToDisk){bitmap = loadWorkoutImageFromDevice(context,imgName);}
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
                Log.e(TAG,"onFAILURE",t);
            }
        });
    }

}
