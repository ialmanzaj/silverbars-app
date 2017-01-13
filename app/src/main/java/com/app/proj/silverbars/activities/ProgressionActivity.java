package com.app.proj.silverbars.activities;

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

import com.app.proj.silverbars.CustomDateView;
import com.app.proj.silverbars.R;
import com.app.proj.silverbars.models.MuscleProgression;
import com.app.proj.silverbars.utils.Utilities;
import com.app.proj.silverbars.viewsets.ProgressionActivityView;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import butterknife.BindView;


/**
 * Created by isaacalmanza on 10/04/16.
 */
public class ProgressionActivity extends AppCompatActivity implements ProgressionActivityView{

    private static final String TAG = ProgressionActivity.class.getSimpleName();


    @BindView(R.id.webview) WebView mMusclesWebView;
    @BindView(R.id.content) LinearLayout progression_content_layout;
    @BindView(R.id.date) CustomDateView customDateView;
    @BindView(R.id.toolbar) Toolbar toolbar;
    @BindView(R.id.appbar) AppBarLayout appBarLayout;

    @BindView(R.id.main_content) NestedScrollView nestedScrollView;

    private Utilities mUtilities;

    String partes = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_progression);


        setupToolbar();



        appBarLayout.setExpanded(false, true);

        customDateView.setNameDay("Sun");
        customDateView.setNumberDay("2");


        //getProgressionAPI();
        
    }

    private void setupToolbar(){
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(getResources().getString(R.string.progression_));
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {finish();}
        });
    }
    
    
    
    @Override
    public void getMusclesProgresions(List<MuscleProgression> progressions) {

        for (MuscleProgression progressionMuscle: progressions){
            //Log.d(TAG,"progressionMuscle: "+progressionMuscle.getDate());
            //Log.d(TAG,"progressionMuscle: "+progressionMuscle.getLevel());

            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");

            try {

                Date date = dateFormat.parse(progressionMuscle.getDate());
                System.out.println(date);
                System.out.println(dateFormat.format(date));

            } catch (ParseException e) {
                e.printStackTrace();
            }
        }

        getMusclePorcentaje(progressions);
    }

    @Override
    public void displayNetworkError() {

    }

    @Override
    public void displayServerError() {

    }

    private void getMusclePorcentaje(List<MuscleProgression> muscles){
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

        mUtilities.getBodyView(this,mMusclesWebView);
    }




}
