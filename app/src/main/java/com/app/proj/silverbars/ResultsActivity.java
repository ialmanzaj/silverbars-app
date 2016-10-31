package com.app.proj.silverbars;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TabHost;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

import static com.app.proj.silverbars.Utilities.CreateNewView;
import static com.app.proj.silverbars.Utilities.CreateNewViewProgression;
import static com.app.proj.silverbars.Utilities.deleteCopiesofList;
import static com.app.proj.silverbars.Utilities.injectJS;

/**
 * Created by isaacalmanza on 10/04/16.
 */
public class ResultsActivity extends AppCompatActivity {

    private static final String TAG = "ResultsActivity";
    WebView webView;
    String partes = "";
    Button mSaveResultsButton;
    LinearLayout muscles_content_layout,skills_layout;
    private  List<String> MusclesArray = new ArrayList<>();
    private  List<String> TypeExercises = new ArrayList<>();

    ArrayList<ExerciseRep> exercises = new ArrayList<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_results);

        Intent i = getIntent();
        Bundle b = i.getExtras();

        exercises = b.getParcelableArrayList("exercises");
        Log.v(TAG,"exercises: "+ exercises);


        Toolbar myToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(myToolbar);
        getSupportActionBar().setTitle(getResources().getString(R.string.results));


        
        webView = (WebView) findViewById(R.id.webview);
        muscles_content_layout = (LinearLayout) findViewById(R.id.content);
        skills_layout = (LinearLayout) findViewById(R.id.skills);

        TabHost Tab_layout = (TabHost) findViewById(R.id.tabHost2);
        Tab_layout.setup();

        TabHost.TabSpec skills = Tab_layout.newTabSpec(getResources().getString(R.string.skills));
        TabHost.TabSpec muscles = Tab_layout.newTabSpec(getResources().getString(R.string.tab_muscles));

        muscles.setIndicator(getResources().getString(R.string.tab_muscles));
        muscles.setContent(R.id.muscles);

        skills.setIndicator(getResources().getString(R.string.skills));
        skills.setContent(R.id.skills);

        Tab_layout.addTab(muscles);
        Tab_layout.addTab(skills);


        mSaveResultsButton = (Button) findViewById(R.id.save);
        mSaveResultsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }

        });


        ScrollView scrollView = (ScrollView) findViewById(R.id.muscles);
        if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.KITKAT) {
            scrollView.setFillViewport(true);
        }

        if (exercises != null) {

            int porcentaje[] = new int[exercises.size()];

            for(int a = 0; a < exercises.size(); a++){

                for (Muscle muscle: exercises.get(a).getExercise().getMuscles()){
                    Collections.addAll(MusclesArray, muscle.getMuscleName());
                }
                Collections.addAll(TypeExercises, exercises.get(a).getExercise().getTypes_exercise());

                if (Objects.equals(exercises.get(a).getExercise().getLevel(),"NORMAL")){
                    porcentaje[a] = 3;
                }else if (Objects.equals(exercises.get(a).getExercise().getLevel(),"EASY")){
                    porcentaje[a] = 1;
                }else if (Objects.equals(exercises.get(a).getExercise().getLevel(),"HARD")){
                    porcentaje[a] = 5;
                }else if (Objects.equals(exercises.get(a).getExercise().getLevel(),"CHALLENGING")){
                    porcentaje[a] = 8;
                }

                porcentaje[a] = porcentaje[a] * exercises.get(a).getRepetition();

                Log.v(TAG,"exercise"+exercises.get(a).getExercise().getExercise_name()+"porcentaje: "+porcentaje[a]);
            }

            setMusclesToView(MusclesArray);
            setTypes(TypeExercises);
        }
    }

    private void getCountTimes(List<String> muscles){
        List<String> muscles_ = new ArrayList<>();
    }

    private void setTypes(List<String> types){
        List<String> types_oficial = deleteCopiesofList(types);

        for (int a =0; a<types_oficial.size();a++){

            RelativeLayout relativeLayout = CreateNewView(this,types_oficial.get(a),100);
            skills_layout.addView(relativeLayout);

        }
    }


    @SuppressLint("SetJavaScriptEnabled")
    private void setMusclesToView(List<String> musculos){

        if (musculos.size() > 0){
            List<String> musculos_oficial;
            musculos_oficial = deleteCopiesofList(musculos);

            for (int a = 0;a<musculos_oficial.size();a++) {
                partes += "#"+ musculos_oficial.get(a) + ",";
                RelativeLayout relativeLayout = CreateNewViewProgression(this,musculos_oficial.get(a),10);
                muscles_content_layout.addView(relativeLayout);
            }
        }

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                webView.setWebViewClient(new WebViewClient(){
                    @Override
                    public void onPageFinished(WebView view, String url) {
                        injectJS(partes,webView);
                        super.onPageFinished(view, url);
                    }

                });
            }
        });


        webView.getSettings().setJavaScriptEnabled(true);
        getBodyView();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    private void getBodyView(){

        SharedPreferences sharedPref = this.getSharedPreferences("Mis preferencias",Context.MODE_PRIVATE);
        String default_url = getResources().getString(R.string.muscle_path);
        String muscle_url = sharedPref.getString(getString(R.string.muscle_path),default_url);
        if (muscle_url.equals("/")){
            webView.loadUrl("https://s3-ap-northeast-1.amazonaws.com/silverbarsmedias3/html/index.html");
        }else {
            String fileurl = "file://"+muscle_url;
            webView.loadUrl(fileurl);
        }
    }


}
