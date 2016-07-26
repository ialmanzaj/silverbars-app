package com.app.project.silverbars;

import android.os.AsyncTask;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Arrays;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ProgressionActivity extends AppCompatActivity {
    private static final String TAG = "PROGRESSION ACTIVITY";
    private WebView webview;

    private String fileUrl = "";
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

        fileUrl ="file://"+Environment.getExternalStorageDirectory()+"/html/"+"index.html";
        Log.v(TAG,fileUrl);
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
