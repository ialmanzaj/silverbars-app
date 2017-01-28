package com.app.proj.silverbars.activities;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TabHost;

import com.app.proj.silverbars.R;
import com.app.proj.silverbars.models.ExerciseRep;
import com.app.proj.silverbars.models.Muscle;
import com.app.proj.silverbars.utils.Utilities;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

import butterknife.BindView;


/**
 * Created by isaacalmanza on 10/04/16.
 */
public class ResultsActivity extends AppCompatActivity {

    private static final String TAG = ResultsActivity.class.getSimpleName();

    @BindView(R.id.toolbar) Toolbar toolbar;

    @BindView(R.id.save) Button mSaveResultsButton;
    @BindView(R.id.webview) WebView webView;

    @BindView(R.id.content)LinearLayout mContentLayout;
    @BindView(R.id.skills)LinearLayout mSkillsLayout;


    private  List<Muscle> mMuscles = new ArrayList<>();
    private  List<String> mTypeExercises = new ArrayList<>();
    private ArrayList<ExerciseRep> mExercises = new ArrayList<>();

    private Utilities mUtilities = new Utilities();

    private String partes = "";



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_results);

        Bundle extras =  getIntent().getExtras();
        mExercises = extras.getParcelableArrayList("exercises");



        setupToolbar();
        setupTabs();



        mSaveResultsButton.setOnClickListener(view -> {


            //mainService.saveMyProgression()

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

    private void setupToolbar(){
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(getResources().getString(R.string.results));
    }
    
    
    private void setupTabs(){
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




}
