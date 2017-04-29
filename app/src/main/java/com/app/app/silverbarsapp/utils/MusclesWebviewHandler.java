package com.app.app.silverbarsapp.utils;

import android.util.Log;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import java.util.List;
import java.util.Objects;

/**
 * Created by isaacalmanza on 04/29/17.
 */

public class MusclesWebviewHandler {

    private static final String TAG = "MusclesWebviewHandler";

    private Utilities utilities = new Utilities();

    private String js_ready;

    public MusclesWebviewHandler(){}


    public String getMusclesReadyForWebview(List<String> muscles_names){
        String muscles_body = "";
        for (String muscle_name: muscles_names){
            muscles_body += "#"+ muscle_name + ",";
        }
        return muscles_body;
    }

    public String getMuscleReadyForWebview(String muscle_name){
        return  "#"+ muscle_name + ",";
    }

    public void paint(String muscles){
        if (!Objects.equals(muscles, "")) {
            js_ready =  "javascript: (" + "window.onload = function () {" +
                    "muscles = Snap.selectAll('" +  utilities.removeLastChar(muscles) + "');" +
                    "muscles.forEach( function(muscle,i) {" +
                    "muscle.attr({stroke:'#602C8D',fill:'#602C8D'});" +
                    "});" + "}" + ")()";
        }else
            js_ready=  "";
    }


    public void paintOnClick(String muscles){
        muscles = utilities.removeLastChar(muscles);
        //Log.d(TAG,"muscles:"+muscles);
        js_ready =  "javascript: (" + "window.onload = function () {" +
                "var muscles = Snap.selectAll('" + muscles + "');" +
                "muscles.forEach( function(muscle,i) {" +
                "muscle.node.onclick = function () {"+
                "muscle.attr({stroke:'#602C8D',fill:'#602C8D'});"+
                "Android.setMuscle(muscle.node.id);"+
                "};" + "});" + "}" + ")()";
    }



    public void removePaint(String muscle){
        js_ready =  "javascript: (" + "window.onload = function () {" +
                "muscle = Snap.select('" +  utilities.removeLastChar(muscle) + "');" +
                "muscle.attr({stroke:'#fff',fill:'#fff'});" +
                 "}" + ")()";
    }



    public void paintedAndSetClickListener(String muscles){
        muscles = utilities.removeLastChar(muscles);
        //Log.d(TAG,"muscles:"+muscles);
        js_ready =  "javascript: (" + "window.onload = function () {" +
                "var muscles = Snap.selectAll('" + muscles + "');" +
                "muscles.forEach( function(muscle,i) {" +
                "muscle.attr({stroke:'#602C8D',fill:'#602C8D'});"+
                "muscle.node.onclick = function () {"+
                "Android.setMuscle(muscle.node.id);"+
                "};" + "});" + "}" + ")()";
    }

    public void execute(WebView webView) {
        try {

            webView.loadUrl(js_ready);

        } catch (Exception e) {
            Log.e(TAG,"Exception",e);
        }
    }

    public void onWebviewReadyPaint(WebView webView, String muscles){
        webView.setWebViewClient(new WebViewClient(){
            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                paint(muscles);
                execute(webView);
            }
        });
    }


    public void onWebviewOnClickPain(WebView webView, String muscles){
        webView.setWebViewClient(new WebViewClient(){
            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                paintedAndSetClickListener(muscles);
                execute(webView);
            }
        });
    }


}
