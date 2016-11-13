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
import static com.app.proj.silverbars.Utilities.CreateProgression;
import static com.app.proj.silverbars.Utilities.deleteCopiesofList;
import static com.app.proj.silverbars.Utilities.injectJS;

/**
 * Created by isaacalmanza on 10/04/16.
 */
public class ResultsActivity extends AppCompatActivity {


    private static final String TAG = "ResultsActivity";
    private WebView webView;
    private String partes = "";
    private Button mSaveResultsButton;
    LinearLayout muscles_content_layout,skills_layout;
    private  List<Muscle> MusclesArray = new ArrayList<>();
    private  List<String> TypeExercises = new ArrayList<>();

    private ArrayList<ExerciseRep> exercises = new ArrayList<>();


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

                MainService mainService = ServiceGenerator.createService(MainService.class);


                //mainService.saveMyProgression()
                finish();
            }

        });


        if (exercises != null) {

            int porcentaje[] = new int[exercises.size()];

            for(int a = 0; a < exercises.size(); a++){

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


                for (Muscle muscle: exercises.get(a).getExercise().getMuscles()){
                    muscle.setMuscle_activation(porcentaje[a]);

                    Collections.addAll(MusclesArray, muscle);

                    //Log.d(TAG,"muscle: "+muscle.getMuscle_activation());

                }


                //Log.v(TAG,"exercise"+exercises.get(a).getExercise().getExercise_name()+"porcentaje: "+porcentaje[a]);
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





    private List<Muscle> deleteCopiesofMuscle(List<Muscle> muscles){
        List<Muscle> muscle_to_return = new ArrayList<>();
        List<String> names_muscles = new ArrayList<>();


        for (Muscle muscle: muscles){

            //Log.d(TAG,"muscle: "+muscle.getMuscleName());

            if (!names_muscles.contains(muscle.getMuscleName())){

                muscle_to_return.add(muscle);
                names_muscles.add(muscle.getMuscleName());

            }else {
                //Log.d(TAG,"index: "+names_muscles.indexOf(muscle.getMuscleName()));

                int index  = names_muscles.indexOf(muscle.getMuscleName());

                Muscle muscleSelected = muscle_to_return.get(index);

                int progression = muscleSelected.getMuscle_activation() + muscle.getMuscle_activation();
                Log.d(TAG,"progression before: "+progression);

                if (progression > 100){
                    progression = progression - 100;

                    muscleSelected.setProgression_level(muscleSelected.getProgression_level()+1);

                }

                Log.d(TAG,"progression after: "+progression);

                muscleSelected.setMuscle_activation(progression);

                muscle_to_return.set(index,muscleSelected);

                Log.d(TAG,"size: "+muscle_to_return.size());

            }
        }
        return muscle_to_return;
    }




    @SuppressLint("SetJavaScriptEnabled")
    private void setMusclesToView(List<Muscle> musculos){

        if (musculos.size() > 0){

            List<Muscle> musculos_oficial = deleteCopiesofMuscle(musculos);


            for (int a = 0;a<musculos_oficial.size();a++) {

                String muscleName = musculos_oficial.get(a).getMuscleName();
                int progression = musculos_oficial.get(a).getMuscle_activation();
                int level = musculos_oficial.get(a).getProgression_level();

                // for webview
                partes += "#"+ muscleName + ",";


                RelativeLayout relativeLayout = CreateProgression(this,muscleName,String.valueOf(level),progression);
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
