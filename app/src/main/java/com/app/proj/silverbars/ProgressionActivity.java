package com.app.proj.silverbars;

import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;

public class ProgressionActivity extends AppCompatActivity {
    private static final String TAG = "PROGRESSION ACTIVITY";
    private WebView webview;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_progression);

        Toolbar toolbar;
        toolbar = (Toolbar) findViewById(R.id.toolbar_);
        setSupportActionBar(toolbar);

        if (toolbar != null){
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("My Progression ");
            toolbar.setNavigationOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    finish();
                }
            });
        }


       webview = (WebView) findViewById(R.id.WebView_progression);
        webview.setWebViewClient(new WebViewClient(){
            @Override
            public void onPageFinished(WebView view, String url) {
                injectJS();
                super.onPageFinished(view, url);
            }
        });
        webview.getSettings().setJavaScriptEnabled(true);

        String fileUrl = "file://" + Environment.getExternalStorageDirectory() + "/html/" + "index.html";
        Log.v(TAG, fileUrl);
        webview.loadUrl(fileUrl);


    }
    private void injectJS() {
        try {

            String partes = "";

            webview.loadUrl("javascript: ("+ "window.onload = function () {"+

                    "partes = Snap.selectAll('"+partes+"');"+
                    "partes.forEach( function(elem,i) {"+
                    "elem.attr({fill: '#F5515F',stroke: '#F5515F',});"+
                    "});"+ "}"+  ")()");

            //Log.v("MAIN ACTIVITY","HA EJECUTADO EL JAVASCRIPT");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }




}
