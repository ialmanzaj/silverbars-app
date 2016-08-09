package com.app.proj.silverbars;


import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;

import org.lucasr.twowayview.widget.TwoWayView;

import java.io.IOException;
import java.util.Objects;

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

    private static final String TAG ="MAIN SCREEN FRAGMENT";
    public TwoWayView recyclerView;
    public static JsonWorkout[] Workouts;
    private SwipeRefreshLayout swipeContainer;
    public String muscleData = "ALL";
    private boolean opened;
    LinearLayout noInternetConnectionLayout,failedServerLayout;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_fmain, container, false);
    }

    public boolean isOpened() {
        return opened;
    }

    public void setOpened(boolean opened) {
        this.opened = opened;
    }

    public String getMuscleData() {
        return muscleData;
    }

    public void setMuscleData(String muscleData) {
        this.muscleData = muscleData;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        Intent intent = this.getActivity().getIntent();
        String email = intent.getStringExtra("Email");
        String name = intent.getStringExtra("Name");

        muscleData = getArguments().getString("Muscle");
        opened = getArguments().getBoolean("Opened");


        recyclerView = (TwoWayView) getView().findViewById(R.id.list);

        noInternetConnectionLayout = (LinearLayout) getView().findViewById(R.id.noInternetConnection_layout);
        failedServerLayout = (LinearLayout) getView().findViewById(R.id.failed_conection_layout);
        swipeContainer = (SwipeRefreshLayout) getView().findViewById(R.id.swipeContainer);



        Button button_reload_no_internet = (Button) getView().findViewById(R.id.button_reload_no_internet);
        button_reload_no_internet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (CheckInternet(getActivity().getApplicationContext())){
                    swipeContainer.setVisibility(View.VISIBLE);
                    Task(muscleData);
                    noInternetConnectionLayout.setVisibility(View.GONE);
                }else {
                    noInternetConnectionLayout.setVisibility(View.VISIBLE);
                }

            }
        });
        Button button_failed_server = (Button) getView().findViewById(R.id.button_reload_failed_server);
        button_failed_server.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (CheckInternet(getActivity().getApplicationContext())){
                    swipeContainer.setVisibility(View.VISIBLE);
                    Task(muscleData);
                    failedServerLayout.setVisibility(View.GONE);
                }else {
                    failedServerLayout.setVisibility(View.VISIBLE);
                }

            }
        });

        if (!opened){

            opened = true;
            if (CheckInternet(getActivity().getApplicationContext())){
                Task(muscleData);
                swipeContainer.setVisibility(View.VISIBLE);
                noInternetConnectionLayout.setVisibility(View.GONE);
            }
            else{
                swipeContainer.setVisibility(View.GONE);
                noInternetConnectionLayout.setVisibility(View.VISIBLE);

            }
        }
        else{
            if (CheckInternet(getActivity().getApplicationContext())){
                Task(muscleData);
                swipeContainer.setVisibility(View.VISIBLE);
                noInternetConnectionLayout.setVisibility(View.GONE);
            }
            else{
                swipeContainer.setVisibility(View.GONE);
                noInternetConnectionLayout.setVisibility(View.VISIBLE);

            }
        }

//        final Spinner spinner = (Spinner) rootView.findViewById(R.id.spinner);
//
//        spinnerArray.add("ALL");
//        spinnerArray.add("ABS/CORE");
//        spinnerArray.add("UPPER BODY");
//        spinnerArray.add("LOWER BODY");
//
//        final ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(getActivity(), R.layout.spinner_item_secondary,spinnerArray);
//
//        arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
//        spinner.setAdapter(arrayAdapter);
//
//        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
//            @Override
//            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
//                recyclerView.setAdapter(null);
//                String element = spinner.getItemAtPosition(position).toString();
//                muscle = element;
//                Task(muscle);
//            }
//
//            @Override
//            public void onNothingSelected(AdapterView<?> parent) {
//
//            }
//        });



        // Setup refresh listener which triggers new data loading
        swipeContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                // Your code to refresh the list here.
                // Make sure you call swipeContainer.setRefreshing(false)
                // once the network request has completed successfully.
                if (CheckInternet(getActivity().getApplicationContext())){
                    Task(muscleData);
                }
                else{
                    swipeContainer.setRefreshing(false);
                }
            }
        });
        // Configure the refreshing colors
        swipeContainer.setColorSchemeResources(android.R.color.holo_blue_bright,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light);


    }

    public void Task(final String muscle){

        muscleData = muscle;


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
                Log.v(TAG,response.toString());
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
//                    Workouts = null;
                    recyclerView.removeAllViews();
                    recyclerView.removeAllViewsInLayout();
                    recyclerView.setAdapter(null);
                    if (Objects.equals(muscle,"ALL")){
                        Workouts = response.body();
                    }
                    else{
                        JsonWorkout[] auxWorkout = response.body();
                        JsonWorkout[] workoutFlag = null;
                        int x = 0;
                        for (JsonWorkout anAuxWorkout : auxWorkout) {
                            String muscleData = anAuxWorkout.getMain_muscle();
                            if (Objects.equals(muscle, muscleData)) {
                                x++;
                            }
                        }
                        Workouts = new JsonWorkout[x];
                        int y = 0;
                        for (JsonWorkout anAuxWorkout : auxWorkout) {
                            String muscleData = anAuxWorkout.getMain_muscle();
                            if (Objects.equals(muscle, muscleData)) {
                                Workouts[y] = anAuxWorkout;
                                //Log.v("Workout", Workouts[y].getWorkout_name());
                                y++;
                            }
                        }
                    }
                    recyclerView.setAdapter(new WorkoutAdapter(getActivity()));
                    swipeContainer.setRefreshing(false);
                } else {
                    int statusCode = response.code();
                    // handle request errors yourself
                    ResponseBody errorBody = response.errorBody();
                    Log.e(TAG,errorBody.toString());

                }
            }
            @Override
            protected void finalize() throws Throwable {
                super.finalize();
                //Log.v(TAG,"ha finalizado TASK() ");

            }

            @Override
            public void onFailure(Call<JsonWorkout[]> call, Throwable t) {
                Log.e(TAG,"Task, onFailure",t);
                swipeContainer.setVisibility(View.GONE);
                failedServerLayout.setVisibility(View.VISIBLE);

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



}
