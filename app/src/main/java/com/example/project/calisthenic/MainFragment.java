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


    private View rootView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_fmain, container, false);

        return rootView;

    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
//        AsyncTask task = new AsyncTaskParseJson().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
//        startProgress();
        Intent intent = this.getActivity().getIntent();
        email = intent.getStringExtra("Email");
        name = intent.getStringExtra("Name");
        Task();
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
                    Log.v("Size",String.valueOf(Workouts.length));
                    recyclerView = (TwoWayView) getView().findViewById(R.id.list);
                    recyclerView.setAdapter(new WorkoutAdapter(getActivity()));
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
//        service.getWorkouts(new Callback<List<JsonWorkout>>() {
//            @Override
//            public void success(List<JsonWorkout> jsonWorkouts, Response response) {
//                Workouts = jsonWorkouts;
//                Log.v("List",String.valueOf(jsonWorkouts));
//                Log.v("Response",String.valueOf(response));
//                recyclerView = (TwoWayView) getView().findViewById(R.id.list);
//                recyclerView.setAdapter(new WorkoutAdapter(getActivity()));
//            }
//
//            @Override
//            public void failure(RetrofitError error) {
//                Log.v("Error",error.toString());
//            }
//        });
//        Observable WorkoutObservable = Observable.create(new Observable.OnSubscribe() {
//            @Override
//            public void call(Object o) {
//                JsonParser JsonData = new JsonParser();
//                String array = null;
//                try {
//                    Workouts = JsonData.getWorkouts("http://api.silverbarsapp.com/workouts/?format=json");
//                    array = Arrays.toString(Workouts);
//                    Log.v("Array",Arrays.toString(Workouts));
//                } catch (Exception e) {
//                    e.printStackTrace();
//                }
//            }
//        });
//
//        Subscriber WorkoutSubscriber = new Subscriber() {
//            @Override
//            public void onCompleted() {
//                recyclerView = (TwoWayView) getView().findViewById(R.id.list);
//                recyclerView.setAdapter(new WorkoutAdapter(getActivity()));
//            }
//
//            @Override
//            public void onError(Throwable e) {
//
//            }
//
//            @Override
//            public void onNext(Object o) {
//
//            }
//        };
//        WorkoutObservable.subscribe(WorkoutSubscriber);
//        TinyTask.perform(new Something<String>() {
//            @Override
//            public String whichDoes() {
//                JsonParser JsonData = new JsonParser();
//                String array = null;
//                try {
//                    Workouts = JsonData.getWorkouts("http://api.silverbarsapp.com/workouts/?format=json");
//                    array = Arrays.toString(Workouts);
//                } catch (Exception e) {
//                    e.printStackTrace();
//                }
//                return array; // you write this method..
//            }
//
//        }).whenDone(new DoThis<String>() {
//            @Override
//            public void ifOK(String result) {
//                recyclerView = (TwoWayView) getView().findViewById(R.id.list);
//                recyclerView.setAdapter(new WorkoutAdapter(getActivity()));
//                Log.i("Result", result);
//            }
//
//            @Override
//            public void ifNotOK(Exception e) {
//                Log.i("Result", e.toString());
//            }
//        }).go();
    }

    public boolean CheckInternet(Context context){
        ConnectivityManager cm = (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        boolean isConnected = activeNetwork.isConnectedOrConnecting();
        return isConnected;
    }

}
