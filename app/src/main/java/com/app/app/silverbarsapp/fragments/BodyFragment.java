package com.app.app.silverbarsapp.fragments;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.View;
import android.widget.LinearLayout;

import com.app.app.silverbarsapp.R;
import com.app.app.silverbarsapp.handlers.MusclesWebviewHandler;
import com.app.app.silverbarsapp.models.ExerciseProgression;
import com.app.app.silverbarsapp.models.MuscleExercise;
import com.app.app.silverbarsapp.presenters.BasePresenter;
import com.app.app.silverbarsapp.utils.MuscleListener;
import com.app.app.silverbarsapp.utils.Utilities;
import com.app.app.silverbarsapp.utils.WebAppInterface;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.OnClick;
import im.delight.android.webview.AdvancedWebView;

/**
 * A simple {@link Fragment} subclass.
 */
public class BodyFragment extends BaseFragment implements MuscleListener {

    @BindView(R.id.webview) AdvancedWebView webView;
    @BindView(R.id.modal_overlay) LinearLayout mModal;

    private Utilities mUtilities = new Utilities();
    private MusclesWebviewHandler mMusclesWebviewHandler = new MusclesWebviewHandler();

    private String mMuscleParts = " ";
    private MuscleListener muscleListener;

    @Override
    protected int getFragmentLayout() {
        return R.layout.fragment_body;
    }

    @Override
    protected BasePresenter getPresenter() {
        return null;
    }

    public void addListener(MuscleListener listener){
        this.muscleListener = listener;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if (this.isAdded()) {
            setupWebview();

            ArrayList<ExerciseProgression> progressions_filtered = getArguments().getParcelableArrayList("progressions");
            updateBodyMuscleWebView(progressions_filtered);
        }
    }


    private void updateBodyMuscleWebView(ArrayList<ExerciseProgression> exerciseProgressions){
        for (ExerciseProgression exercise_progress: exerciseProgressions){
            for (MuscleExercise muscle: exercise_progress.getExercise().getMuscles()) {
                insertMuscleToWebview(muscle.getMuscle());
            }
        }
    }

    private void insertMuscleToWebview(String muscle_name){
        mMuscleParts += mMusclesWebviewHandler.getMuscleReadyForWebview(muscle_name);
        mMusclesWebviewHandler.addWebviewClientOnClickPaint(webView,mMuscleParts);
    }

    private void reloadWebview(){
        webView.reload();
    }

    private void setupWebview() {
        webView.getSettings().setJavaScriptEnabled(true);
        webView.addJavascriptInterface(new WebAppInterface(CONTEXT, this), "Android");
        mUtilities.loadBodyFromUrl(CONTEXT, webView);
    }

    @Override
    public void onMuscleSelected(String muscle) {
        muscleListener.onMuscleSelected(muscle);
    }

    @OnClick(R.id.info)
    public void infoButton(){
        mModal.setVisibility(View.VISIBLE);
    }

    @OnClick(R.id.okay)
    public void okayButton(){
        mModal.setVisibility(View.GONE);
    }

}