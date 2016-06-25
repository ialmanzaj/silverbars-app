package com.example.project.calisthenic;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.tuesda.walker.circlerefresh.CircleRefreshLayout;

import org.lucasr.twowayview.widget.TwoWayView;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;


public class MainFragment extends Fragment {

    private String email;
    private String name;
    private TwoWayView recyclerView;
    private Button songs;
    public static JsonWorkout[] Workouts;
    private CircleRefreshLayout refreshLayout;


    private View rootView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_fmain, container, false);
        return rootView;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        refreshLayout = (CircleRefreshLayout) rootView.findViewById(R.id.refresh_layout);
        recyclerView = (TwoWayView) getView().findViewById(R.id.list);
        Intent intent = this.getActivity().getIntent();
        email = intent.getStringExtra("Email");
        name = intent.getStringExtra("Name");
        if (CheckInternet(getContext())){
            Task();
        }
        else{
            toast("Please check your internet conection.");
        }

        refreshLayout.setOnRefreshListener(new CircleRefreshLayout.OnCircleRefreshListener() {
            @Override
            public void completeRefresh() {

            }

            @Override
            public void refreshing() {
                if (CheckInternet(getContext())){
                    Task();
                }
                else{
                    toast("Please check your internet connection.");
                    this.completeRefresh();
                }
            }
        });
    }

    public void Task(){

        OkHttpClient.Builder httpClient = new OkHttpClient.Builder();
        httpClient.addInterceptor(new Interceptor() {
            @Override
            public okhttp3.Response intercept(Chain chain) throws IOException {
                Request original = chain.request();

                // Customize the request
                Request request = original.newBuilder()
                        .header("Accept", "application/json")
                        .header("Authorization", "auth-token")
                        .method(original.method(), original.body())
                        .build();

                okhttp3.Response response = chain.proceed(request);
                Log.v("Response",response.toString());
                // Customize or return the response
                return response;
            }
        });

        OkHttpClient client = httpClient.build();
        Retrofit retrofit = new Retrofit.Builder().baseUrl("http://api.silverbarsapp.com")
                .addConverterFactory(GsonConverterFactory.create())
                .client(client)
                .build();
        SilverbarsService service = retrofit.create(SilverbarsService.class);
        Call<JsonWorkout[]> call = service.getWorkouts();
        call.enqueue(new Callback<JsonWorkout[]>() {
            @Override
            public void onResponse(Call<JsonWorkout[]> call, Response<JsonWorkout[]> response) {
                if (response.isSuccessful()) {
                    Workouts = response.body();
                    recyclerView.setAdapter(new WorkoutAdapter(getActivity()));
                    refreshLayout.finishRefreshing();
                } else {
                    int statusCode = response.code();
                    // handle request errors yourself
                    ResponseBody errorBody = response.errorBody();
                    Log.v("Error",errorBody.toString());

                }
            }

            @Override
            public void onFailure(Call<JsonWorkout[]> call, Throwable t) {
                Log.v("Exception",t.toString());
            }
        });
    }

    public boolean CheckInternet(Context context){
        ConnectivityManager cm = (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo i = cm.getActiveNetworkInfo();
        if (i == null)
            return false;
        if (!i.isConnected())
            return false;
        if (!i.isAvailable())
            return false;
        return true;
    }

    public void toast(String text){
        Toast.makeText(this.getActivity(),text,Toast.LENGTH_SHORT).show();
    }

}
