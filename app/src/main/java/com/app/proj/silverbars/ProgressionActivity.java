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
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import static com.app.proj.silverbars.Utilities.CreateProgression;
import static com.app.proj.silverbars.Utilities.injectJS;
import static com.app.proj.silverbars.Utilities.removeLastChar;

public class ProgressionActivity extends AppCompatActivity {
    private static final String TAG = "PROGRESSION ACTIVITY";
    private WebView webview;
    String partes = "";
    AuthPreferences authPreferences;
    LinearLayout progression_content_layout;

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
                public void onClick(View v) {finish();}
            });
        }


        progression_content_layout = (LinearLayout) findViewById(R.id.content);

        authPreferences = new AuthPreferences(this);
        getProgressionAPI(authPreferences.getToken());

        ScrollView scrollView = (ScrollView) findViewById(R.id.muscles);
        webview = (WebView) findViewById(R.id.WebView_progression);
        if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.KITKAT) {scrollView.setFillViewport(true);}
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

    private void getProgressionAPI(final String token){

        OkHttpClient.Builder httpClient = new OkHttpClient.Builder();
        httpClient.addInterceptor(new Interceptor() {
            @Override
            public Response intercept(Chain chain) throws IOException {

                Request request = new Request.Builder()
                        .header("Authorization", "Bearer " + token)
                        .url("https://api.silverbarsapp.com/v1/progression/me/")
                        .build();

                okhttp3.Response response = chain.proceed(request);
                Log.v(TAG,response.toString());
                return response;
            }
        });

        OkHttpClient client = httpClient.build();
        Retrofit retrofit = new Retrofit.Builder().baseUrl("https://api.silverbarsapp.com/")
                .addConverterFactory(GsonConverterFactory.create())
                .client(client)
                .build();

        SilverbarsService service = retrofit.create(SilverbarsService.class);
        retrofit2.Call<User.ProgressionMuscle[]> call = service.getProgression();

        call.enqueue(new Callback<User.ProgressionMuscle[]>() {
            @Override
            public void onResponse(Call<User.ProgressionMuscle[]> call, retrofit2.Response<User.ProgressionMuscle[]> response) {
                if (response.isSuccessful()) {

                    User.ProgressionMuscle[] progression = response.body();
                    List<User.ProgressionMuscle> muscles_to_View = new ArrayList <>();

                    Collections.addAll(muscles_to_View,progression);
                    getMusclePorcentaje(muscles_to_View);

                }
            }
            @Override
            public void onFailure(Call<User.ProgressionMuscle[]> call, Throwable t) {
                Log.e(TAG,"progrssion from server, onFailure",t);
            }
        });
    }


    private void getMusclePorcentaje(List<User.ProgressionMuscle> muscles){
        Log.v(TAG,"getMusclePorcentaje");

        List<User.ProgressionMuscle> muscles_to_View = new ArrayList <>();
        List<String> muscles_ids = new ArrayList <>();
        int progress;

        for (User.ProgressionMuscle user_progress : muscles) {

            if (!muscles_ids.contains(user_progress.getMuscle())) {

                muscles_to_View.add(user_progress);
                muscles_ids.add(user_progress.getMuscle());

            } else {

                int index = muscles_ids.indexOf(user_progress.getMuscle());
                int level = 1;
                progress = muscles_to_View.get(index).getMuscle_activation() + user_progress.getMuscle_activation();
                Log.v(TAG,"progress: "+progress);

                if (progress >= 100) {

                    Log.v(TAG,"next level: yes");
                    progress = 100 - progress;
                    level++;

                    String real_progress = String.valueOf(progress);
                    real_progress = real_progress.split("-")[1];

                    muscles_to_View.get(index).setLevel(level);
                    Log.v(TAG,"level: "+muscles_to_View.get(index).getLevel());
                    muscles_to_View.get(index).setMuscle_activation_progress(Integer.parseInt(real_progress));
                } else {

                    muscles_to_View.get(index).setLevel(user_progress.getLevel());
                    muscles_to_View.get(index).setMuscle_activation_progress(progress);
                }


            }
        }

        setMusclesToView(muscles_to_View);
    }



    private void setMusclesToView(List<User.ProgressionMuscle> musculos) {
        if (musculos.size() > 0) {

            for (int a = 0; a < musculos.size(); a++) {
                partes += "#" + musculos.get(a).getMuscle() + ",";

                RelativeLayout relativeLayout = CreateProgression(this,musculos.get(a).getMuscle(), String.valueOf(musculos.get(a).getLevel()),musculos.get(a).getMuscle_activation());
                progression_content_layout.addView(relativeLayout);
            }
        }


        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                webview.setWebViewClient(new WebViewClient() {
                    @Override
                    public void onPageFinished(WebView view, String url) {
                        injectJS(partes,webview);
                        super.onPageFinished(view, url);
                    }

                });
            }
        });

        webview.getSettings().setJavaScriptEnabled(true);
        getBodyView();

    }

}
