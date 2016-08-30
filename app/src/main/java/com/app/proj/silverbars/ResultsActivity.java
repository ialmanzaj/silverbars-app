package com.app.proj.silverbars;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TabHost;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static com.app.proj.silverbars.Utilities.deleteCopiesofList;
import static com.app.proj.silverbars.Utilities.removeLastChar;

public class ResultsActivity extends AppCompatActivity {

    private static final String TAG = "ResultsActivity";
    WebView webView;
    String partes = "";
    Button save;

    LinearLayout muscles_content_layout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_results);

        Intent i = getIntent();
        Bundle b = i.getExtras();
        List<String> muscles1 = b.getStringArrayList("muscles");


        Toolbar myToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(myToolbar);

        if (myToolbar != null) {
            getSupportActionBar().setTitle(getResources().getString(R.string.results));
        }

        webView = (WebView) findViewById(R.id.webview);
        muscles_content_layout = (LinearLayout) findViewById(R.id.content);


        TabHost Tab_layout = (TabHost) findViewById(R.id.tabHost2);
        Tab_layout.setup();

        TabHost.TabSpec skills = Tab_layout.newTabSpec(getResources().getString(R.string.skills));
        TabHost.TabSpec muscles = Tab_layout.newTabSpec(getResources().getString(R.string.tab_muscles));

        muscles.setIndicator(getResources().getString(R.string.tab_muscles));
        muscles.setContent(R.id.muscles);

        skills.setIndicator(getResources().getString(R.string.skills));
        skills.setContent(R.id.skills);

        Tab_layout.addTab(muscles);
        Tab_layout.addTab(skills);


        save = (Button) findViewById(R.id.save);
        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }

        });


        ScrollView scrollView = (ScrollView) findViewById(R.id.muscles);
        if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.KITKAT) {
            scrollView.setFillViewport(true);
        }

        setMusclesToView(muscles1);

    }


    private void setMusclesToView(List<String> musculos){
        if (musculos.size() > 0){
            List<String> musculos_oficial;
            musculos_oficial = deleteCopiesofList(musculos);

            for (int a = 0;a<musculos_oficial.size();a++) {

                final TextView MuscleTextView = new TextView(this);

                partes += "#"+ musculos_oficial.get(a) + ",";
                MuscleTextView.setText(musculos_oficial.get(a));

                MuscleTextView.setGravity(Gravity.START);

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    MuscleTextView.setTextColor(getResources().getColor(R.color.gray_active_icon,null));
                }else {
                    MuscleTextView.setTextColor(getResources().getColor(R.color.gray_active_icon));
                }


                RelativeLayout relativeLayout = new RelativeLayout(this);
                RelativeLayout.LayoutParams match_parent = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
                relativeLayout.setLayoutParams(match_parent);
                relativeLayout.setPadding(15,15,15,15);
                relativeLayout.setMinimumHeight(45);

               /* if (a%2 == 0){
                    secundary_ColumnMuscle.addView(MuscleTextView);
                }else {
                    primary_ColumnMuscle.addView(MuscleTextView);

                }*/
                relativeLayout.addView(MuscleTextView);
                muscles_content_layout.addView(relativeLayout);
            }
        }

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
        SharedPreferences sharedPref = this.getSharedPreferences("Mis preferencias",Context.MODE_PRIVATE);
        String default_url = getResources().getString(R.string.muscle_path);
        String muscle_url = sharedPref.getString(getString(R.string.muscle_path),default_url);

        if (muscle_url.equals("/")){
            webView.loadUrl("https://s3-ap-northeast-1.amazonaws.com/silverbarsmedias3/html/index.html");
        }else {
            String fileurl = "file://"+muscle_url;
            webView.loadUrl(fileurl);
        }
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
