package com.app.app.silverbarsapp.fragments;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.app.app.silverbarsapp.R;
import com.app.app.silverbarsapp.utils.Utilities;

import butterknife.BindView;
import butterknife.ButterKnife;


public class MyProgressFragment extends Fragment {

    private static final String TAG = MyProgressFragment.class.getSimpleName();

    @BindView(R.id.webview) WebView webView;

    private Utilities mUtilities = new Utilities();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup parent,Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_my_progress, parent, false);
    }


    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ButterKnife.bind(this,view);


        if (this.isAdded()){
            setupWebview();
        }
    }


    private void onChange(){}


    private void setupWebview(){
        webView.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
            }
        });
        webView.getSettings().setJavaScriptEnabled(true);
        mUtilities.loadUrlOfMuscleBody(getActivity(),webView);
    }


}
