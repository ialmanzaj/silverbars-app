package com.app.proj.silverbars;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ScrollView;

import java.util.Objects;

import static com.app.proj.silverbars.Utilities.removeLastChar;

public class ResultsActivity extends AppCompatActivity {

    private static final String TAG = "ResultsActivity";
    WebView webView;
    String partes;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_results);

        Toolbar myToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(myToolbar);


        ScrollView scrollView = (ScrollView) findViewById(R.id.muscles);

        if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.KITKAT) {
            scrollView.setFillViewport(true);
        }


        if (myToolbar != null) {
            getSupportActionBar().setTitle("Results");
        }


        webView = (WebView) findViewById(R.id.webview);
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                webView.setWebViewClient(new WebViewClient(){
                    @Override
                    public void onPageFinished(WebView view, String url) {
                        injectJS();
                        super.onPageFinished(view, url);
                    }
                });
            }
        });
        webView.getSettings().setJavaScriptEnabled(true);

        // ACCEDER A LA URL DEL HTML GUARDADO EN EL PHONE
        SharedPreferences sharedPref = this.getSharedPreferences("Mis preferencias", Context.MODE_PRIVATE);
        String default_url = getResources().getString(R.string.muscle_path);
        String muscle_url = sharedPref.getString(getString(R.string.muscle_path),default_url);
        String fileurl = "file://"+muscle_url;
        webView.loadUrl(fileurl);
    }

    private void injectJS() {
        try {
            if (!Objects.equals(partes, "")){
                partes = removeLastChar(partes);
                webView.loadUrl("javascript: ("+ "window.onload = function () {"+
                        "partes = Snap.selectAll('"+partes+"');"+
                        "partes.forEach( function(elem,i) {"+
                        "elem.attr({fill: '#602C8D',stroke: '#602C8D',});"+
                        "});"+ "}"+  ")()");
            }
        } catch (Exception e) {
            Log.e(TAG,"JAVASCRIPT Exception",e);
        }
    }
}
