package com.app.proj.silverbars.fragments;


import android.content.Context;
import android.content.Intent;
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
import com.app.proj.silverbars.activities.ProgressionActivity;
import com.app.proj.silverbars.utils.Utilities;

import butterknife.BindView;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

import static com.facebook.Profile.getCurrentProfile;


public class ProfileFragment extends Fragment {

    private static final String TAG = ProfileFragment.class.getSimpleName();
   
    private RecyclerView list;
    
    private RecyclerView.Adapter adapter;
    private RecyclerView.LayoutManager lManager;


    @BindView()RelativeLayout myTeam;
    @BindView()RelativeLayout Progression;
    @BindView()RelativeLayout skillsProgression;
    @BindView()RelativeLayout sharedWorkouts;
    @BindView()RelativeLayout history;

    @BindView() TextView mProfileName;
    @BindView() ImageView profile_image;

    private Utilities utilities;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_profile, container, false);

    }
    
    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);


        //profiles settings
        mProfileName = (TextView) view.findViewById(R.id.Profile_name);
        profile_image = (ImageView) view.findViewById(R.id.profile_image);
        myTeam = (RelativeLayout) view.findViewById(R.id.myTeam);
        Progression = (RelativeLayout) view.findViewById(R.id.Progression);
        skillsProgression = (RelativeLayout) view.findViewById(R.id.skillsProgression);
        sharedWorkouts = (RelativeLayout) view.findViewById(R.id.sharedWorkouts);
        history = (RelativeLayout) view.findViewById(R.id.history);
        

        String name = getCurrentProfile().getFirstName()+" "+ getCurrentProfile().getLastName();

        mProfileName.setText(name);


        Bitmap bmp = utilities.loadProfileImageFromCache(getActivity(),getCurrentProfile().getId());

        if (bmp != null){
            profile_image.setImageBitmap(bmp);
        }else {
            DownloadProfileImage(getActivity(),getCurrentProfile().getId(),getCurrentProfile().getId());
        }


        // buttons of options
        float scale = getResources().getDisplayMetrics().density;
        final int dpAsPixels = (int) (7*scale + 0.5f);

        myTeam.setClickable(true);



        Progression.setClickable(true);
        Progression.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getActivity(),ProgressionActivity.class));
            }
        });



        skillsProgression.setClickable(true);
        sharedWorkouts.setClickable(true);
        history.setClickable(true);
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
