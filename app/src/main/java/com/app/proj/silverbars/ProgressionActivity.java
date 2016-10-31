package com.app.proj.silverbars;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.v4.view.NestedScrollingParentHelper;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.animation.AccelerateInterpolator;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import static com.app.proj.silverbars.Utilities.CreateProgression;
import static com.app.proj.silverbars.Utilities.injectJS;
/**
 * Created by isaacalmanza on 10/04/16.
 */
public class ProgressionActivity extends AppCompatActivity {

    private static final String TAG = "ProgressionActivity";

    private WebView mMusclesWebView;
    String partes = "";

    LinearLayout progression_content_layout;
    CustomDateView customDateView;
    private  Toolbar mToolbar;;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_progression);



        mToolbar = (Toolbar) findViewById(R.id.toolbar_);
        setSupportActionBar(mToolbar);


        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(getResources().getString(R.string.progression_));
        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {finish();}
            });


        AppBarLayout appBarLayout = (AppBarLayout)findViewById(R.id.appbar);
        appBarLayout.setExpanded(false, true);

        progression_content_layout = (LinearLayout) findViewById(R.id.content);

        customDateView = (CustomDateView) findViewById(R.id.date);
        customDateView.setNameDay("Sun");
        customDateView.setNumberDay("2");


        mMusclesWebView = (WebView) findViewById(R.id.webview);


        NestedScrollView nestedScrollView = (NestedScrollView) findViewById(R.id.main_content);



        getProgressionAPI();


    }


    private void getBodyView(){

        // ACCEDER A LA URL DEL HTML GUARDADO EN EL PHONE
        SharedPreferences sharedPref = this.getSharedPreferences("Mis preferencias", Context.MODE_PRIVATE);
        String default_url = getResources().getString(R.string.muscle_path);
        String muscle_url = sharedPref.getString(getString(R.string.muscle_path),default_url);
        if (muscle_url.equals("/")){
            mMusclesWebView.loadUrl("https://s3-ap-northeast-1.amazonaws.com/silverbarsmedias3/html/index.html");
        }else {
            String fileurl = "file://"+muscle_url;
            mMusclesWebView.loadUrl(fileurl);
        }
    }

    private void getProgressionAPI(){


        MainService service = ServiceGenerator.createService(MainService.class);

        service.getProgression().enqueue(new Callback<User.ProgressionMuscle[]>() {
            @Override
            public void onResponse(Call<User.ProgressionMuscle[]> call, retrofit2.Response<User.ProgressionMuscle[]> response) {
                if (response.isSuccessful()) {

                    User.ProgressionMuscle[] progression = response.body();
                    List<User.ProgressionMuscle> muscles_to_View = new ArrayList <>();

                    Collections.addAll(muscles_to_View,progression);
                    getMusclePorcentaje(muscles_to_View);

                }
            }
            @Override
            public void onFailure(Call<User.ProgressionMuscle[]> call, Throwable t) {
                Log.e(TAG,"progrssion from server, onFailure",t);
            }
        });
    }


    private void getMusclePorcentaje(List<User.ProgressionMuscle> muscles){
        Log.v(TAG,"getMusclePorcentaje");

        List<User.ProgressionMuscle> muscles_to_View = new ArrayList <>();
        List<String> muscles_ids = new ArrayList <>();
        int progress;

        for (User.ProgressionMuscle user_progress : muscles) {

            if (!muscles_ids.contains(user_progress.getMuscle())) {

                muscles_to_View.add(user_progress);
                muscles_ids.add(user_progress.getMuscle());

            } else {

                int index = muscles_ids.indexOf(user_progress.getMuscle());
                int level = 1;
                progress = muscles_to_View.get(index).getMuscle_activation() + user_progress.getMuscle_activation();
                Log.v(TAG,"progress: "+progress);

                if (progress >= 100) {

                    Log.v(TAG,"next level: yes");
                    progress = 100 - progress;
                    level++;

                    String real_progress = String.valueOf(progress);
                    real_progress = real_progress.split("-")[1];

                    muscles_to_View.get(index).setLevel(level);
                    Log.v(TAG,"level: "+muscles_to_View.get(index).getLevel());
                    muscles_to_View.get(index).setMuscle_activation_progress(Integer.parseInt(real_progress));
                } else {

                    muscles_to_View.get(index).setLevel(user_progress.getLevel());
                    muscles_to_View.get(index).setMuscle_activation_progress(progress);
                }


            }
        }

        setMusclesToView(muscles_to_View);
    }



    private void setMusclesToView(List<User.ProgressionMuscle> musculos) {
        if (musculos.size() > 0) {

            for (int a = 0; a < musculos.size(); a++) {
                partes += "#" + musculos.get(a).getMuscle() + ",";

                RelativeLayout relativeLayout = CreateProgression(this,musculos.get(a).getMuscle(), String.valueOf(musculos.get(a).getLevel()),musculos.get(a).getMuscle_activation());
                progression_content_layout.addView(relativeLayout);
            }
        }


        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mMusclesWebView.setWebViewClient(new WebViewClient() {
                    @Override
                    public void onPageFinished(WebView view, String url) {
                        injectJS(partes,mMusclesWebView);
                        super.onPageFinished(view, url);
                    }

                });
            }
        });

        mMusclesWebView.getSettings().setJavaScriptEnabled(true);
        getBodyView();

    }

}
