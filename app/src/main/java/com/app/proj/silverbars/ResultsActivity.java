package com.app.proj.silverbars;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TabHost;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;


/**
 * Created by isaacalmanza on 10/04/16.
 */
public class ResultsActivity extends AppCompatActivity {

    private static final String TAG = "ResultsActivity";


    private WebView webView;
    private String partes = "";

    private Button mSaveResultsButton;
    LinearLayout mContentLayout,mSkillsLayout;


    private  List<Muscle> mMuscles = new ArrayList<>();
    private  List<String> mTypeExercises = new ArrayList<>();


    private ArrayList<ExerciseRep> mExercises = new ArrayList<>();

    MainService mainService;

    private Utilities mUtilities;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_results);

        mainService = ServiceGenerator.createService(MainService.class);
        mUtilities = new Utilities();

        Intent i = getIntent();
        Bundle b = i.getExtras();
        mExercises = b.getParcelableArrayList("exercises");


        Toolbar myToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(myToolbar);
        getSupportActionBar().setTitle(getResources().getString(R.string.results));
        webView = (WebView) findViewById(R.id.webview);
        mContentLayout = (LinearLayout) findViewById(R.id.content);
        mSkillsLayout = (LinearLayout) findViewById(R.id.skills);
        mSaveResultsButton = (Button) findViewById(R.id.save);

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



        mSaveResultsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                //mainService.saveMyProgression()

            }

        });


        if (mExercises != null) {

            int porcentaje[] = new int[mExercises.size()];

            for(int a = 0; a < mExercises.size(); a++){

                Collections.addAll(mTypeExercises, mExercises.get(a).getExercise().getTypes_exercise());


                if (Objects.equals(mExercises.get(a).getExercise().getLevel(),"NORMAL")){
                    porcentaje[a] = 3;
                }else if (Objects.equals(mExercises.get(a).getExercise().getLevel(),"EASY")){
                    porcentaje[a] = 1;
                }else if (Objects.equals(mExercises.get(a).getExercise().getLevel(),"HARD")){
                    porcentaje[a] = 5;
                }else if (Objects.equals(mExercises.get(a).getExercise().getLevel(),"CHALLENGING")){
                    porcentaje[a] = 8;
                }

                porcentaje[a] = porcentaje[a] * mExercises.get(a).getRepetition();


                for (Muscle muscle: mExercises.get(a).getExercise().getMuscles()){
                    muscle.setMuscle_activation(porcentaje[a]);

                    Collections.addAll(mMuscles, muscle);

                    //Log.d(TAG,"muscle: "+muscle.getMuscle_activation());

                }


                //Log.v(TAG,"exercise"+mExercises.get(a).getExercise().getExercise_name()+"porcentaje: "+porcentaje[a]);
            }

            //setMusclesToView(mMuscles);
            setTypes(mTypeExercises);
        }
    }

    private void getCountTimes(List<String> muscles){


    }

    private void setTypes(List<String> types){
        List<String> types_oficial = mUtilities.deleteCopiesofList(types);

        for (int a = 0; a<types_oficial.size();a++){

            RelativeLayout relativeLayout = mUtilities.createViewProgression(this,types_oficial.get(a),100);
            mSkillsLayout.addView(relativeLayout);
        }
    }
    
   /* private List<Muscle> getMuscleProgression(List<Muscle> muscles){
        List<MuscleProgression> mMusclesProgression = new ArrayList<>();
        List<String> names_muscles = new ArrayList<>();


        for (Muscle muscle: muscles){

            //Log.d(TAG,"muscle: "+muscle.getMuscleName());

            if (!names_muscles.contains(muscle.getMuscleName())){

                MuscleProgression muscleProgression = new MuscleProgression(muscle,)

                mMusclesProgression.add();
                names_muscles.add(muscle.getMuscleName());

            }else {
                //Log.d(TAG,"index: "+names_muscles.indexOf(muscle.getMuscleName()));

                int index  = names_muscles.indexOf(muscle.getMuscleName());

                //Muscle muscleSelected = mMusclesProgression.get(index);

                int progression = muscleSelected.getMuscle_activation() + muscle.getMuscle_activation();
              

                if (progression > 100){
                    progression = progression - 100;

                    muscleSelected.setProgression_level(muscleSelected.getProgression_level()+1);

                }
                

                muscleSelected.setMuscle_activation(progression);

               // mMusclesProgression.set(index,muscleSelected);

                Log.d(TAG,"size: "+mMusclesProgression.size());

            }
        }
        return mMusclesProgression;
    }
*/


/*
    @SuppressLint("SetJavaScriptEnabled")
    private void setMusclesToView(List<Muscle> musculos){

        if (musculos.size() > 0){

            List<Muscle> mMuscles = getMuscleProgression(musculos);


            for (int a = 0;a<mMuscles.size();a++) {

                String muscleName = mMuscles.get(a).getMuscleName();
                int progression = mMuscles.get(a).getMuscle_activation();
                int level = mMuscles.get(a).getProgression_level();

                // for webview
                partes += "#"+ muscleName + ",";


                RelativeLayout relativeLayout = createProgressionView(this,muscleName,String.valueOf(level),progression);
                mContentLayout.addView(relativeLayout);
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
    }*/



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
