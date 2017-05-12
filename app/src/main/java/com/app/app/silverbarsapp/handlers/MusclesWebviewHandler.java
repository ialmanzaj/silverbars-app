package com.app.app.silverbarsapp.handlers;

import android.util.Log;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.app.app.silverbarsapp.utils.Utilities;

import java.util.List;

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
        muscles = utilities.removeLastChar(muscles);
        js_ready = "javascript: (" + "window.onload = function () {" +
                "var muscles = Snap.selectAll('" +  muscles + "');" +
                "muscles.forEach( function(muscle,i) {" +
                "muscle.attr({stroke:'#602C8D',fill:'#602C8D'});" +
                "});" + "}" + ")()";
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
        muscle = utilities.removeLastChar(muscle);
        js_ready =  "javascript: (" + "window.onload = function () {" +
                "var muscle = Snap.select('" +  muscle  + "');" +
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

            Log.d(TAG,"execute");
            webView.loadUrl(js_ready);

        } catch (Exception e) {
            Log.e(TAG,"Exception",e);
        }
    }

    public void addWebviewClientPaint(WebView webView, String muscles){
        webView.setWebViewClient(new WebViewClient(){
            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                paint(muscles);
                execute(webView);
            }
        });
    }

    public void addWebviewClientOnClickPaint(WebView webView, String muscles){
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
