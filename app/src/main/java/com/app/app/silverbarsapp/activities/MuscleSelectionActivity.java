package com.app.app.silverbarsapp.activities;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.app.app.silverbarsapp.R;
import com.app.app.silverbarsapp.SilverbarsApp;
import com.app.app.silverbarsapp.components.DaggerMuscleSelectionComponent;
import com.app.app.silverbarsapp.handlers.MusclesWebviewHandler;
import com.app.app.silverbarsapp.models.Muscle;
import com.app.app.silverbarsapp.modules.MuscleSelectionModule;
import com.app.app.silverbarsapp.presenters.BasePresenter;
import com.app.app.silverbarsapp.presenters.MuscleSelectionPresenter;
import com.app.app.silverbarsapp.utils.MuscleListener;
import com.app.app.silverbarsapp.utils.Utilities;
import com.app.app.silverbarsapp.utils.WebAppInterface;
import com.app.app.silverbarsapp.viewsets.MuscleSelectionView;
import com.mixpanel.android.mpmetrics.MixpanelAPI;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.OnClick;
import im.delight.android.webview.AdvancedWebView;

import static com.app.app.silverbarsapp.Constants.MIX_PANEL_TOKEN;

public class MuscleSelectionActivity extends BaseActivity implements MuscleSelectionView,MuscleListener,AdvancedWebView.Listener {

    private static final String TAG = MuscleSelectionActivity.class.getSimpleName();

    @Inject
    MuscleSelectionPresenter mMuscleSelectionPresenter;

    @BindView(R.id.toolbar) Toolbar toolbar;
    @BindView(R.id.webview) AdvancedWebView myWebView;
    @BindView(R.id.search_exercises) Button mLookExercises;
    @BindView(R.id.muscles_selected)TextView mMusclesTextSelected;
    @BindView(R.id.scroll_text) HorizontalScrollView mScrollText;
    @BindView(R.id.loading) LinearLayout mLoadingView;
    @BindView(R.id.error_view) LinearLayout mErrorView;
    @BindView(R.id.modal_overlay) LinearLayout mModal;
    @BindView(R.id.info) ImageView mInfo;


    private Utilities utilities = new Utilities();
    MusclesWebviewHandler mMusclesWebviewHandler = new MusclesWebviewHandler();

    List<String> muscles_names = new ArrayList<>();
    private ArrayList<String> muscles_selected = new ArrayList<>();


    private MixpanelAPI mMixpanel;

    @Override
    protected int getLayout() {
        return R.layout.activity_muscle_selection;
    }

    @Override
    public void injectDependencies() {
        super.injectDependencies();
        DaggerMuscleSelectionComponent.builder()
                .silverbarsComponent(SilverbarsApp.getApp(this).getComponent())
                .muscleSelectionModule(new MuscleSelectionModule(this))
                .build().inject(this);
    }

    @Nullable
    @Override
    protected BasePresenter getPresenter() {
        return mMuscleSelectionPresenter;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setupToolbar();
        mMuscleSelectionPresenter.getMuscles();

        //mix panel events

        mMixpanel = MixpanelAPI.getInstance(this, MIX_PANEL_TOKEN);
        mixPanelEventMuscleSelection();
    }




    /**
     *
     *
     *
     *
     *
     *    Click listeners
     *<p>
     *
     *
     *
     *
     *
     *
     *
     */

    @OnClick(R.id.info)
    public void infoButton(){
        mModal.setVisibility(View.VISIBLE);
    }

    @OnClick(R.id.okay)
    public void okayButton(){
        mModal.setVisibility(View.GONE);
    }

    @OnClick(R.id.reload)
    public void reload(){
        onErrorViewOff();
        onLoadingViewOn();
        mMuscleSelectionPresenter.getMuscles();
    }

    @OnClick(R.id.search_exercises)
    public void searchExercisesButton(){

        if (muscles_selected.size() < 1){
            utilities.toast(this,getString(R.string.activity_muscle_selection_selection));
            return;
        }

        //mix panel events
        mixPanelEventMuscleSelectionCompleted();


        //return muscle selected
        Intent return_intent = new Intent();
        return_intent.putExtra("muscles",muscles_selected);
        setResult(RESULT_OK, return_intent);
        finish();
    }


    /**
     *
     *
     *
     *
     *
     *    Events listening
     *
     *

     *
     *
     *
     */
    @Override
    public void getMuscles(List<Muscle> muscles) {
        //Log.d(TAG,"muscles "+muscles);
        for (Muscle muscle: muscles){muscles_names.add(muscle.getMuscle_name());}

        setupWebview();
    }

    @Override
    public void onMuscleSelected(String muscle) {
        if (!muscles_selected.contains(muscle)){

            //add muscle to list selected
            muscles_selected.add(muscle);


            updateViewMusclesTxt();

        }else {

            String muscles_for_js = mMusclesWebviewHandler.getMuscleReadyForWebview(muscle);
            removePaintMuscles(muscles_for_js);

            //remove from list selected
            muscles_selected.remove(muscle);

            updateViewMusclesTxt();
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

    private String getMuscles(ArrayList<String> muscles_selected) {
        return muscles_selected.toString().replace("[","").replace("]","");
    }

    @Override
    public void onPageFinished(String url) {
        String muscles_for_js = mMusclesWebviewHandler.getMusclesReadyForWebview(muscles_names);
        setMusclesOnClick(muscles_for_js);

        //loading off
        onLoadingViewOff();
    }


    /**
     *
     *
     *
     *     Webview events
     *
     *
     *
     */

    @Override
    public void onPageStarted(String url, Bitmap favicon) {}
    @Override
    public void onPageError(int errorCode, String description, String failingUrl) {}
    @Override
    public void onDownloadRequested(String url, String suggestedFilename, String mimeType, long contentLength, String contentDisposition, String userAgent) {}
    @Override
    public void onExternalPageRequest(String url) {}



    /**
     *
     *
     *
     *
     *     UI events
     *
     *
     *
     *
     */


    public void setupToolbar(){
        if (toolbar != null) {
            setSupportActionBar(toolbar);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle(R.string.activity_muscle_selection_toolbar);
            toolbar.setNavigationIcon(R.drawable.ic_clear_white_24px);
        }
    }

    private void setupWebview(){
        myWebView.setListener(this, this);
        myWebView.getSettings().setJavaScriptEnabled(true);
        myWebView.addJavascriptInterface(new WebAppInterface(this,this), "Android");
        utilities.loadBodyFromLocal(this,myWebView);
    }

    private void setMusclesOnClick(String muscles_for_js){
        myWebView.post(() -> {
            mMusclesWebviewHandler.paintOnClick(muscles_for_js);
            mMusclesWebviewHandler.execute(myWebView);
            //mMusclesWebviewHandler.addWebviewClickListenerClientPaint(myWebView,muscles_for_js);
        });
    }

    private void removePaintMuscles(String muscles_for_js){
        myWebView.post(() -> {
            mMusclesWebviewHandler.removePaint(muscles_for_js);
            mMusclesWebviewHandler.execute(myWebView);
        });
    }

    private void updateViewMusclesTxt(){
        runOnUiThread(() -> {
            //stuff that updates ui
            mMusclesTextSelected.setText(getMuscles(muscles_selected));
        });

        mScrollText.postDelayed(() -> mScrollText.fullScroll(HorizontalScrollView.FOCUS_RIGHT), 100L);
    }


    private void onLoadingViewOn(){
        mLoadingView.setVisibility(View.VISIBLE);
    }

    private void onLoadingViewOff(){
        mLoadingView.setVisibility(View.GONE);
    }

    private void onErrorViewOn(){mErrorView.setVisibility(View.VISIBLE);}

    private void onErrorViewOff(){mErrorView.setVisibility(View.GONE);}


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == android.R.id.home) {
            //Log.d(TAG, "action bar clicked");
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     *
     *
     *
     *
     *
     *    Mix panel events
     *<p>
     *
     *
     *
     *
     *
     *
     *
     */

    private void mixPanelEventMuscleSelection(){mMixpanel.track("on Muscle Selection", utilities.getUserData(this));}

    private void mixPanelEventMuscleSelectionCompleted(){mMixpanel.track("Muscle Selection Completed", utilities.getUserData(this));}

}

