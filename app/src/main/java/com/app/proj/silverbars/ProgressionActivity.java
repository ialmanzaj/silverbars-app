package com.app.proj.silverbars;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;


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

    private Utilities mUtilities;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_progression);

        mUtilities = new Utilities();


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

        service.getProgression().enqueue(new Callback<List<MuscleProgression>>() {
            @Override
            public void onResponse(Call<List<MuscleProgression>> call, retrofit2.Response<List<MuscleProgression>> response) {
                if (response.isSuccessful()) {

                    List<MuscleProgression> mMusclesProgress = response.body();


                    for (MuscleProgression progressionMuscle: mMusclesProgress){
                        Log.d(TAG,"progressionMuscle: "+progressionMuscle.getDate());
                        Log.d(TAG,"progressionMuscle: "+progressionMuscle.getLevel());

                        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");

                        try {

                            Date date = dateFormat.parse(progressionMuscle.getDate());
                            System.out.println(date);
                            System.out.println(dateFormat.format(date));

                        } catch (ParseException e) {
                            e.printStackTrace();
                        }

                      /*  Calendar c = Calendar.getInstance();
                        c.setTime();
                        int dayOfWeek = c.get(Calendar.DAY_OF_WEEK);*/
                    }



                    getMusclePorcentaje(mMusclesProgress);
                }else {
                    Log.e(TAG,"code: "+response.code());

                }
            }
            @Override
            public void onFailure(Call<List<MuscleProgression>> call, Throwable t) {
                Log.e(TAG,"progrssio, onFailure",t);
            }
        });
    }


    private void getMusclePorcentaje(List<MuscleProgression> muscles){
        Log.v(TAG,"getMusclePorcentaje");

        List<MuscleProgression> mMusclesProgress = new ArrayList <>();
        List<String> muscles_names = new ArrayList <>();

        int progress;

        for (MuscleProgression user_progress : muscles) {

            if (!muscles_names.contains(user_progress.getMuscle())) {

                mMusclesProgress.add(user_progress);
                muscles_names.add(user_progress.getMuscle());

            } else {

                int index = muscles_names.indexOf(user_progress.getMuscle());
                int level = user_progress.getLevel();
                progress = mMusclesProgress.get(index).getMuscle_activation() + user_progress.getMuscle_activation();
               

                if (progress >= 100) {

                    Log.v(TAG,"next level: yes");
                    progress = 100 - progress;
                    level++;

                    String real_progress = String.valueOf(progress);
                    real_progress = real_progress.split("-")[1];

                    mMusclesProgress.get(index).setLevel(level);
                    Log.v(TAG,"level: "+mMusclesProgress.get(index).getLevel());
                    mMusclesProgress.get(index).setMuscle_activation_progress(Integer.parseInt(real_progress));

                } else {

                    mMusclesProgress.get(index).setLevel(user_progress.getLevel());
                    mMusclesProgress.get(index).setMuscle_activation_progress(progress);
                }


            }
        }

        setMusclesToView(mMusclesProgress);
    }




    private void setMusclesToView(List<MuscleProgression> musculos) {
        if (musculos.size() > 0) {

            for (int a = 0; a < musculos.size(); a++) {
                partes += "#" + musculos.get(a).getMuscle() + ",";

                RelativeLayout relativeLayout = mUtilities.createProgressionView(this,musculos.get(a).getMuscle(), String.valueOf(musculos.get(a).getLevel()),musculos.get(a).getMuscle_activation());
                progression_content_layout.addView(relativeLayout);
            }
        }


        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mMusclesWebView.setWebViewClient(new WebViewClient() {
                    @Override
                    public void onPageFinished(WebView view, String url) {
                        mUtilities.injectJS(partes,mMusclesWebView);
                        super.onPageFinished(view, url);
                    }

                });
            }
        });

        mMusclesWebView.getSettings().setJavaScriptEnabled(true);
        getBodyView();

    }

}
