package com.app.app.silverbarsapp.activities;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.AppBarLayout;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.app.app.silverbarsapp.CustomDateView;
import com.app.app.silverbarsapp.R;
import com.app.app.silverbarsapp.SilverbarsApp;
import com.app.app.silverbarsapp.components.DaggerProgressionComponent;
import com.app.app.silverbarsapp.models.Muscle;
import com.app.app.silverbarsapp.models.MuscleProgression;
import com.app.app.silverbarsapp.modules.ProgressionModule;
import com.app.app.silverbarsapp.presenters.BasePresenter;
import com.app.app.silverbarsapp.presenters.ProgressionPresenter;
import com.app.app.silverbarsapp.utils.Utilities;
import com.app.app.silverbarsapp.viewsets.ProgressionView;

import java.io.IOException;
import java.io.InputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import javax.inject.Inject;

import butterknife.BindView;


/**
 * Created by isaacalmanza on 10/04/16.
 */
public class ProgressionActivity extends BaseActivity implements ProgressionView {

    private static final String TAG = ProgressionActivity.class.getSimpleName();

    @Inject
    ProgressionPresenter mProgressionPresenter;

    @BindView(R.id.toolbar) Toolbar toolbar;
    @BindView(R.id.appbar) AppBarLayout appBarLayout;

    @BindView(R.id.webview) WebView mMusclesWebView;
    @BindView(R.id.content) LinearLayout mProgresionContent;
    @BindView(R.id.date) CustomDateView customDateView;
    
    @BindView(R.id.main_content) NestedScrollView nestedScrollView;

    @BindView(R.id.loading) LinearLayout mLoadingView;

    @BindView(R.id.reload) Button mReload;
    @BindView(R.id.error_view) LinearLayout mErrorView;

    private Utilities mUtilities = new Utilities();

    private String mMuscleParts = "";
    List<String> sMuscles_names = new ArrayList<>();
    List<MuscleProgression> muscleProgressions;

    @Override
    protected int getLayout() {
        return R.layout.activity_progression;
    }

    @Nullable
    @Override
    protected BasePresenter getPresenter() {
        return mProgressionPresenter;
    }

    @Override
    public void injectDependencies() {
        super.injectDependencies();

        DaggerProgressionComponent.builder()
                .silverbarsComponent(SilverbarsApp.getApp(this).getComponent())
                .progressionModule(new ProgressionModule(this))
                .build().inject(this);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setupToolbar();
        setupWebview();

        appBarLayout.setExpanded(false, true);

        customDateView.setNameDay("Sun");
        customDateView.setNumberDay("2");

       /* onLoadingViewOff();
        List<MuscleProgression> progressions = new Gson().fromJson(getJson(),new TypeToken<ArrayList<MuscleProgression>>(){}.getType());
        setMusclesToView(progressions);*/

        mProgressionPresenter.getProgression();
    }


    public void setupToolbar(){
        setSupportActionBar(toolbar);
        if (toolbar != null){
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle(getResources().getString(R.string.progression_));
        }
    }

    private void setupWebview(){
        mMusclesWebView.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
            }
        });
        mMusclesWebView.getSettings().setJavaScriptEnabled(true);
    }

    @Override
    public void displayProgressions(List<MuscleProgression> progressions) {
        Collections.reverse(progressions);
        muscleProgressions = progressions;
        Log.v(TAG,"progressions: "+progressions);
        onLoadingViewOff();

        for (MuscleProgression progressionMuscle: progressions){

            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");

            try {

                Date date = dateFormat.parse(progressionMuscle.getDate());
                System.out.println(date);
                System.out.println(dateFormat.format(date));

            } catch (ParseException e) {
                e.printStackTrace();
            }
        }

        getMuscles();
        mUtilities.setBodyInWebView(this,mMusclesWebView);
        //getMusclePorcentaje(progressions);
    }

    @Override
    public void onMuscle(Muscle muscle) {
        sMuscles_names.add(muscle.getMuscle_name());
        mMuscleParts += "#" + muscle.getMuscle_name() + ",";
        insertMuscleContent(muscle);
        mUtilities.injectJS(mMuscleParts,mMusclesWebView);
    }

    private void insertMuscleContent(Muscle muscle){
        if (sMuscles_names.size() > 0){
            RelativeLayout relativeLayout = mUtilities.createProgressionView(
                    this,
                    muscle.getMuscle_name(),
                    String.valueOf(muscleProgressions.get(sMuscles_names.size()-1).getLevel()),
                    muscleProgressions.get(sMuscles_names.size()-1).getMuscle_activation_progress());

            mProgresionContent.addView(relativeLayout);
        }
    }

    private void getMuscles(){
        for (MuscleProgression muscleProgression: muscleProgressions){
            mProgressionPresenter.getMuscle(muscleProgression.getMuscle_id());
        }
    }

    @Override
    public void displayNetworkError() {
        onErrorViewOn();
    }

    @Override
    public void displayServerError() {
        onErrorViewOn();
    }


    private void getMusclePorcentaje(List<MuscleProgression> muscleProgressions){

       /* List<MuscleProgression> mMusclesProgress = new ArrayList <>();
        List<String> muscles_names = new ArrayList <>();

        int progress;

        for (MuscleProgression muscle_progress : muscleProgressions) {

            if (!muscles_names.contains(muscle_progress.getMuscle())) {

                mMusclesProgress.add(muscle_progress);
                muscles_names.add(muscle_progress.getMuscle());

            } else {

                int index = muscles_names.indexOf(muscle_progress.getMuscle());
                int level = muscle_progress.getLevel();
                progress = mMusclesProgress.get(index).getMuscle_activation() + muscle_progress.getMuscle_activation();


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

                    mMusclesProgress.get(index).setLevel(muscle_progress.getLevel());
                    mMusclesProgress.get(index).setMuscle_activation_progress(progress);
                }

            }
        }*/
    }


    private void setMusclesToView(List<MuscleProgression> musculos) {
        if (musculos.size() > 0) {

            for (int a = 0; a < musculos.size(); a++) {


            }
        }


    }




    private String getJson(){
        String json = null;
        try {
            InputStream is = this.getAssets().open("progression.json");
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();
            json = new String(buffer, "UTF-8");
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        return json;
    }

    private void onLoadingViewOn(){
        mLoadingView.setVisibility(View.VISIBLE);
    }

    private void onLoadingViewOff(){
        mLoadingView.setVisibility(View.GONE);
    }

    private void onErrorViewOn(){
        mErrorView.setVisibility(View.VISIBLE);
    }

    private void onErrorViewOff(){
        mErrorView.setVisibility(View.GONE);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == android.R.id.home) {
            Log.d(TAG, "action bar clicked");
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

}
