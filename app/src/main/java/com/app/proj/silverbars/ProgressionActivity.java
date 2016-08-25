package com.app.proj.silverbars;

import android.content.Context;
import android.content.SharedPreferences;
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
            getSupportActionBar().setTitle(getResources().getString(R.string.progression_));
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

        getBodyView();

    }

    private void getBodyView(){
        // ACCEDER A LA URL DEL HTML GUARDADO EN EL PHONE
        SharedPreferences sharedPref = this.getSharedPreferences("Mis preferencias", Context.MODE_PRIVATE);
        String default_url = getResources().getString(R.string.muscle_path);
        String muscle_url = sharedPref.getString(getString(R.string.muscle_path),default_url);
        if (muscle_url.equals("/")){
            webview.loadUrl("https://s3-ap-northeast-1.amazonaws.com/silverbarsmedias3/html/index.html");
        }else {
            String fileurl = "file://"+muscle_url;
            webview.loadUrl(fileurl);
        }
    }

    private void injectJS() {
        try {
            String partes = "";
            webview.loadUrl("javascript: ("+ "window.onload = function () {"+

                    "partes = Snap.selectAll('"+partes+"');"+
                    "partes.forEach( function(elem,i) {"+
                    "elem.attr({fill: '#602C8D',stroke: '#602C8D',});"+
                    "});"+ "}"+  ")()");
            //Log.v("MAIN ACTIVITY","HA EJECUTADO EL JAVASCRIPT");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
