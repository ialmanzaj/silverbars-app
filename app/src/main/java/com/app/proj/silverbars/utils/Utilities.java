package com.app.proj.silverbars.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.PorterDuff;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Display;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.webkit.WebView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.app.proj.silverbars.MainService;
import com.app.proj.silverbars.R;
import com.app.proj.silverbars.models.Muscle;
import com.google.gson.Gson;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

import static com.app.proj.silverbars.Constants.BODY_URL;

/**
 * Created by isaacalmanza on 08/10/16.
 */
public class Utilities {

    private static final String TAG = "Utilities";
    private   String strSeparator = "__,__";

    public Utilities (){}

    public void toast(Context context,String text){
        Toast.makeText(context.getApplicationContext(),text,Toast.LENGTH_SHORT).show();
    }

    public void getBodyView(Context context,WebView webView){
        SharedPreferences sharedPref = context.getSharedPreferences("Mis preferencias",Context.MODE_PRIVATE);
        String default_url = context.getResources().getString(R.string.muscle_path);
        String muscle_url = sharedPref.getString(context.getString(R.string.muscle_path),default_url);
        if (muscle_url.equals("/")){
            webView.loadUrl(BODY_URL);
        }else {
            String fileurl = "file://"+muscle_url;
            webView.loadUrl(fileurl);
        }
    }

    public  int containerDimensions(Context context) {
        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        Display display = wm.getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        int width = size.x;
        return width;
    }


    /* Checks if external storage is available for read and write */
    public  boolean isExternalStorageWritable() {
        String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state)) {
            return true;
        }
        return false;
    }


    public  Bitmap loadExerciseImageFromDevice(Context context, String imageURI) {
        Bitmap bitmap = null;
        String[] imageDir = imageURI.split("SilverbarsImg");
        if (imageDir.length < 2){
            File file = getFileReady(context,"/SilverbarsImg/"+imageURI);
            if (file.exists()){
                bitmap = BitmapFactory.decodeFile(file.getAbsolutePath());
            }
        }else{
            File file = new File(imageURI);
            if (file.exists()){
                bitmap = BitmapFactory.decodeFile(file.getAbsolutePath());
            }
        }
        return bitmap;
    }


    public  boolean saveExerciseImageInDevice(Context context, ResponseBody body, String imgName) {
        try {
            File futureStudioIconFile = getFileReady(context,"/SilverbarsImg/"+imgName);
            InputStream input = null;
            OutputStream outputStream = null;
            try {
                input = body.byteStream();
                outputStream = new FileOutputStream(futureStudioIconFile);
                int size = input.available();
                
                byte[] fileReader = new byte[size];
                long fileSize = body.contentLength();
                long fileSizeDownloaded = 0;

                while (true) {
                    int read = input.read(fileReader);
                    if (read == -1) {break;}
                    outputStream.write(fileReader, 0, read);
                    fileSizeDownloaded += read;
                    Log.v(TAG, "Download, file download from saveExerciseImageInDevice: " + fileSizeDownloaded + " of " + fileSize);
                }
                outputStream.flush();
                return true;
            } catch (IOException e) {
                return false;
            } finally {
                if (input != null) {input.close();}
                if (outputStream != null) {outputStream.close();}
            }
        } catch (IOException e) {return false;}
    }


    public  Bitmap loadWorkoutImageFromDevice(Context context, String imageURI) {
        Bitmap bitmap = null;
        File file = getFileReady(context,"/SilverbarsImg/"+imageURI);
        if (file.exists()){
            bitmap = BitmapFactory.decodeFile(file.getAbsolutePath());
        }
        return bitmap;
    }


    public String getWorkoutImage(String url){
        String[] workoutImgDir = url.split("workouts");
        String Parsedurl = "workouts"+workoutImgDir[1];
        String[] imagesName = Parsedurl.split("/");
        String workoutImgName = imagesName[2];
        Log.v(TAG,"Image Name: "+workoutImgName);
        return workoutImgName;
    }


    public String convertMusclesToString(Muscle[] muscles) {
        Gson gson = new Gson();
        String jsonInString = gson.toJson(muscles);
        Log.v(TAG, jsonInString);

        return jsonInString;
    }



    public Muscle[] convertMusclesToString(String jsonInString) {
        Gson gson = new Gson();
        Muscle[] muscles = gson.fromJson(jsonInString, Muscle[].class);
        return muscles;
    }


    public String getExerciseAudioName(String audio_url){
        String[] audioDir = audio_url.split("exercises");
        String Parsedurl = "exercises" + audioDir[1];
        String[] splitName = Parsedurl.split("/");
        return splitName[2];
    }


    public static String getExerciseImageName(String url){
        String[] imageDir = url.split("exercises");
        String Parsedurl = "exercises" + imageDir[1];
        String[] imagesName = Parsedurl.split("/");
        return imagesName[2];
    }


    public void createExerciseAudio(Context context, Boolean download, String mp3_url){
        String url_dir_device = "exercises" + mp3_url;
        File Dir = getFileReady(context,"/SilverbarsMp3/");

        if (Dir.isDirectory()) {
            File file = getFileReady(context,"/SilverbarsMp3/"+getExerciseAudioName(mp3_url));
            if (!file.exists()) {
                if (download){
                    downloadAudio(context,url_dir_device, getExerciseAudioName(mp3_url));
                }
            }
        } else {
            boolean success = Dir.mkdir();
            if (success) {
                if (download){
                    downloadAudio(context,url_dir_device, getExerciseAudioName(mp3_url));
                }
            }else
                Log.e(TAG, "Error creating dir");
        }
    }

    public boolean saveAudioInDevice(Context context,ResponseBody body, String audioName) {

        try {
            File Folder = getFileReady(context,"/SilverbarsMp3/");
            File mp3file = new File(context.getFilesDir()+"/SilverbarsMp3/"+audioName);

            InputStream input = null;
            OutputStream outputStream = null;
            boolean success = true;

            if (!Folder.isDirectory()) {
                Log.v(TAG,"Creating Dir");
                success = Folder.mkdir();
            }if (success) {
                try {
                    input = body.byteStream();
                    outputStream = new FileOutputStream(mp3file);
                    int size = input.available();

                    byte[] fileReader = new byte[size];
                    long fileSize = body.contentLength();
                    long fileSizeDownloaded = 0;
                    while (true) {
                        int read = input.read(fileReader);
                        if (read == -1) {break;}
                        outputStream.write(fileReader, 0, read);
                        fileSizeDownloaded += read;
                        Log.v("Download", "file download from saveAudioInDevice: " + fileSizeDownloaded + " of " + fileSize);
                    }
                    outputStream.flush();
                    return true;

                } catch (IOException e) {
                    return false;
                } finally {
                    if (input != null) {input.close();}
                    if (outputStream != null) {outputStream.close();}
                }
            } else {
                Log.e(TAG,"Error while creating dir: saveWorkoutImgInDevice");
            }


        } catch (IOException e) {return false;}

        return false;
    }

    public boolean saveWorkoutImgInDevice(Context context, ResponseBody body, String workout_img) {

        try {
            File Folder = getFileReady(context,"/SilverbarsImg/");
            File futureStudioIconFile = getFileReady(context,"/SilverbarsImg/"+workout_img);

            InputStream input = null;
            OutputStream outputStream = null;

            boolean success = true;
            if (!Folder.isDirectory()) {
                Log.v(TAG,"Creating Dir");
                success = Folder.mkdir();
            }
            if (success) {
                try {
                    input = body.byteStream();
                    outputStream = new FileOutputStream(futureStudioIconFile);
                    int size = input.available();

                    byte[] fileReader = new byte[size];
                    long fileSize = body.contentLength();
                    long fileSizeDownloaded = 0;
                    while (true) {
                        int read = input.read(fileReader);
                        if (read == -1) {break;}
                        outputStream.write(fileReader, 0, read);
                        fileSizeDownloaded += read;
                        Log.v("Download", "file download from saveWorkoutImgInDevice: " + fileSizeDownloaded + " of " + fileSize);
                    }
                    outputStream.flush();
                    return true;

                } catch (IOException e) {
                    return false;
                } finally {
                    if (input != null) {input.close();}
                    if (outputStream != null) {outputStream.close();}
                }
            } else {
                Log.e(TAG,"Error while creating dir: saveWorkoutImgInDevice");
            }
        } catch (IOException e) {
            return false;
        }
        return false;
    }

    public Bitmap loadProfileImageFromCache(Context context, String imageURI) {
        Bitmap bitmap = null;
        String[] imageDir = imageURI.split("SilverbarsImg");

        if (imageDir.length < 2){
            File file = getFileReady(context,"/SilverbarsImg/"+imageURI);
            if (file.exists()){
                bitmap = BitmapFactory.decodeFile(file.getAbsolutePath());
            }
        }else{
            File file = new File(imageURI);
            if (file.exists()){
                bitmap = BitmapFactory.decodeFile(file.getAbsolutePath());
            }
        }
        return bitmap;
    }




    public  String convertArrayToString(String[] array){
        String str = "";
        for (int i = 0;i<array.length; i++) {
            str = str+array[i];

            if(i<array.length-1){
                str = str+strSeparator;
            }
        }
        return str;
    }

    public  String[] convertStringToArray(String str){
        return str.split(strSeparator);
    }


    public  String getUrlReady(Context context,String url){
        return context.getFilesDir()+url;
    }

    public  File getFileReady(Context context,String url){
        return new File(context.getFilesDir()+url);
    }


    public  String removeLastChar(String str) {return str.substring(0,str.length()-1);}


    public  String SongArtist(Context context,File file){
        String artist = null;
        try{
            MediaMetadataRetriever mediaMetadataRetriever = new MediaMetadataRetriever();
            Uri uri = Uri.fromFile(file);
            mediaMetadataRetriever.setDataSource(context, uri);
            artist = mediaMetadataRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_ARTIST);
        }catch(Exception e){
            Log.e(TAG,"Exception: ",e);
        }
        return artist;
    }

    public  String getSongDuration(Context context, File file){
        String duration = null;
        try{MediaMetadataRetriever mediaMetadataRetriever = new MediaMetadataRetriever();
            Uri uri = Uri.fromFile(file);
            mediaMetadataRetriever.setDataSource(context, uri);
            duration = mediaMetadataRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION);
        }catch (Exception e){
            Log.e(TAG,"Exception: ",e);
        }
        return duration;
    }


    public  String getSongName(Context context, File file){
        String title = null;
        try{
            if (file.getPath().contains("/storage/emulated/0/Download/")){
                title = file.getPath().split("/storage/emulated/0/Download/")[1];
                Log.v(TAG,"title: "+title);
            }else {
                MediaMetadataRetriever mediaMetadataRetriever = new MediaMetadataRetriever();
                Uri uri = Uri.fromFile(file);
                mediaMetadataRetriever.setDataSource(context, uri);
                title = mediaMetadataRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_TITLE);
            }
        } catch(Exception e){
            Log.e(TAG,"Exception: ",e);
        }
        return title;
    }



    public  void DownloadImage(final Context context, String url, final String imgName){

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://s3-ap-northeast-1.amazonaws.com/silverbarsmedias3/")
                .build();

        MainService downloadService = retrofit.create(MainService.class);
        downloadService.downloadFile(url).enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful()) {
                    saveWorkoutImgInDevice(context,response.body(),imgName);
                }
            }
            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {Log.e(TAG,"DownloadImage: onFAILURE",t);}
        });
    }


    public  void downloadAudio(final Context context, String audio_url, final String getAudioName) {

        Retrofit retrofit = new Retrofit.Builder().baseUrl("https://s3-ap-northeast-1.amazonaws.com/silverbarsmedias3/")
                .build();

        MainService downloadService = retrofit.create(MainService.class);
        downloadService.downloadFile(audio_url).enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful()) {
                    saveAudioInDevice(context,response.body(),getAudioName);
                } else
                    Log.e(TAG, "downloadAudio, Download server failed:");

            }
            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {Log.e(TAG, "downloadAudio: onFailure",t);}
        });
    }

    public String removeLastMp3(String song){
        if(song.contains(".mp3")){
            song =  song.replace(".mp3","");
        }if (song.contains("-")){
            song = song.replace("-"," ");
        }
        return song;
    }

    public  List<String> deleteCopiesofList(List<String> list){
        List<String> real_list = new ArrayList<>();

        for (int a = 0; a<list.size();a++) {
            if (!real_list.contains(list.get(a))) {
                real_list.add(list.get(a));
            }
        }
        return real_list;
    }



    public  static class WrappingLinearLayoutManager extends LinearLayoutManager {

        public WrappingLinearLayoutManager(Context context) {
            super(context);
        }

        private int[] mMeasuredDimension = new int[2];

        @Override
        public boolean canScrollVertically() {
            return false;
        }

        @Override
        public void onMeasure(RecyclerView.Recycler recycler, RecyclerView.State state,
                              int widthSpec, int heightSpec) {
            final int widthMode = View.MeasureSpec.getMode(widthSpec);
            final int heightMode = View.MeasureSpec.getMode(heightSpec);

            final int widthSize = View.MeasureSpec.getSize(widthSpec);
            final int heightSize = View.MeasureSpec.getSize(heightSpec);

            int width = 0;
            int height = 0;
            for (int i = 0; i < getItemCount(); i++) {
                if (getOrientation() == HORIZONTAL) {
                    measureScrapChild(recycler, i,
                            View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED),
                            heightSpec,
                            mMeasuredDimension);

                    width = width + mMeasuredDimension[0];
                    if (i == 0) {
                        height = mMeasuredDimension[1];
                    }
                } else {
                    measureScrapChild(recycler, i,
                            widthSpec,
                            View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED),
                            mMeasuredDimension);

                    height = height + mMeasuredDimension[1];
                    if (i == 0) {
                        width = mMeasuredDimension[0];
                    }
                }
            }

            switch (widthMode) {
                case View.MeasureSpec.EXACTLY:
                    width = widthSize;
                case View.MeasureSpec.AT_MOST:
                case View.MeasureSpec.UNSPECIFIED:
            }

            switch (heightMode) {
                case View.MeasureSpec.EXACTLY:
                    height = heightSize;
                case View.MeasureSpec.AT_MOST:
                case View.MeasureSpec.UNSPECIFIED:
            }

            setMeasuredDimension(width, height);
        }

        private void measureScrapChild(RecyclerView.Recycler recycler, int position, int widthSpec,
                                       int heightSpec, int[] measuredDimension) {

            View view = recycler.getViewForPosition(position);
            if (view.getVisibility() == View.GONE) {
                measuredDimension[0] = 0;
                measuredDimension[1] = 0;
                return;
            }
            // For adding Item Decor Insets to view
            super.measureChildWithMargins(view, 0, 0);
            RecyclerView.LayoutParams p = (RecyclerView.LayoutParams) view.getLayoutParams();
            int childWidthSpec = ViewGroup.getChildMeasureSpec(
                    widthSpec,
                    getPaddingLeft() + getPaddingRight() + getDecoratedLeft(view) + getDecoratedRight(view),
                    p.width);
            int childHeightSpec = ViewGroup.getChildMeasureSpec(
                    heightSpec,
                    getPaddingTop() + getPaddingBottom() + getDecoratedTop(view) + getDecoratedBottom(view),
                    p.height);
            view.measure(childWidthSpec, childHeightSpec);

            // Get decorated measurements
            measuredDimension[0] = getDecoratedMeasuredWidth(view) + p.leftMargin + p.rightMargin;
            measuredDimension[1] = getDecoratedMeasuredHeight(view) + p.bottomMargin + p.topMargin;
            recycler.recycleView(view);
        }
    }



    public  boolean saveHtmInDevice(Context context,ResponseBody body, String name) {

        try {
            File file = getFileReady(context,"/html/"+name);

            InputStream input = null;
            OutputStream output = null;

            try {
                input = body.byteStream();
                output = new FileOutputStream(file);
                int size = input.available();
                byte[] buffer = new byte[size];

                long fileSize = body.contentLength();
                long fileSizeDownloaded = 0;

                while (true) {
                    int read = input.read(buffer);
                    if (read == -1) {break;}
                    output.write(buffer, 0, read);
                    fileSizeDownloaded += read;
                    Log.d(TAG, "Download file from saveHtmInDevice: " + fileSizeDownloaded + " of " + fileSize);
                }
                output.flush();
                return true;

            } catch (IOException e) {
                Log.e(TAG,"IOEXEPTION",e);
                return false;
            } finally {
                if (input != null) {input.close();}
                if (output != null) {output.close();}
            }
        } catch (IOException e) { return false;}
    }


    public void injectJS(String partes, WebView webView) {
        try {
            if (!Objects.equals(partes, "")){
                partes = removeLastChar(partes);
                webView.loadUrl("javascript: ("+ "window.onload = function () {"+
                        "partes = Snap.selectAll('"+partes+"');"+
                        "partes.forEach( function(elem,i) {"+
                        "elem.attr({stroke:'#602C8D',fill:'#602C8D'});"+
                        "});"+ "}"+  ")()");
            }
        } catch (Exception e) {
            Log.e(TAG,"JAVASCRIPT Exception",e);
        }
    }


    public RelativeLayout createRelativeProgress(Context context, String type_exercise, int progress){

        TextView textView = new TextView(context);

        // TEXT OF TYPE OF EXERCISES
        RelativeLayout.LayoutParams layoutParams_of_textView = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        textView.setGravity(Gravity.START);
        textView.setLayoutParams(layoutParams_of_textView);
        textView.setTextColor(context.getResources().getColor(R.color.black));
        textView.setText(type_exercise);


        // Progress
        ProgressBar progressBar = new ProgressBar(context,null ,android.R.attr.progressBarStyleHorizontal);
        RelativeLayout.LayoutParams layoutParams_Progress = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        layoutParams_Progress.addRule(RelativeLayout.ALIGN_PARENT_END);
        progressBar.setLayoutParams(layoutParams_Progress);
        progressBar.getLayoutParams().width = containerDimensions(context) / 2;
        progressBar.setMax(100);


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            progressBar.setProgressTintList(ColorStateList.valueOf(Color.RED));
            progressBar.setBackgroundTintList((ColorStateList.valueOf(Color.RED)));
        }else {
            progressBar.getProgressDrawable().setColorFilter(Color.RED, PorterDuff.Mode.SRC_IN);
        }


        progressBar.setProgress(progress);

        RelativeLayout relativeLayout = new RelativeLayout(context);
        RelativeLayout.LayoutParams match_parent = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        relativeLayout.setLayoutParams(match_parent);
        relativeLayout.setPadding(15,15,15,15);
        relativeLayout.setMinimumHeight(45);

        relativeLayout.addView(textView);
        relativeLayout.addView(progressBar);

        return relativeLayout;
    }

    public RelativeLayout createViewProgression(Context context, String type_exercise, int progress){

        TextView textView = new TextView(context);

        // TEXT OF TYPE OF EXERCISES
        RelativeLayout.LayoutParams layoutParams_of_textView = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        textView.setGravity(Gravity.START);
        textView.setLayoutParams(layoutParams_of_textView);
        textView.setTextColor(context.getResources().getColor(R.color.black));
        textView.setText(type_exercise);

        TextView progressPorcentaje = new TextView(context);
        progressPorcentaje.setTextColor(context.getResources().getColor(R.color.black));
        String progression = progress+"%";
        progressPorcentaje.setText(progression);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {progressPorcentaje.setTextColor(context.getResources().getColor(R.color.colorSecondary,null));}else {
            progressPorcentaje.setTextColor(context.getResources().getColor(R.color.colorSecondary));}


        LinearLayout linearLayout = new LinearLayout(context);
        RelativeLayout.LayoutParams layoutParam = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        layoutParam.addRule(RelativeLayout.ALIGN_PARENT_END);
        linearLayout.setLayoutParams(layoutParam);


        linearLayout.addView(progressPorcentaje);

        RelativeLayout relativeLayout = new RelativeLayout(context);
        RelativeLayout.LayoutParams match_parent = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        relativeLayout.setLayoutParams(match_parent);
        relativeLayout.setPadding(15,15,15,15);
        relativeLayout.setMinimumHeight(45);


        relativeLayout.addView(textView);
        relativeLayout.addView(linearLayout);

        return relativeLayout;
    }

    public  RelativeLayout createProgressionView(Context context, String exercise, String level, int progress){

        TextView text_exercise = new TextView(context);

        RelativeLayout.LayoutParams layoutParams_of_textView = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        text_exercise.setGravity(Gravity.START);
        text_exercise.setLayoutParams(layoutParams_of_textView);
        text_exercise.setTextColor(context.getResources().getColor(R.color.black));
        text_exercise.setText(exercise);


        TextView text_level = new TextView(context);
        LinearLayout.LayoutParams layout_of_textView = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        layout_of_textView.setMarginEnd(30);
        text_level.setLayoutParams(layout_of_textView);

        text_level.setTextColor(context.getResources().getColor(R.color.gray_active_icon));
        String level_ = context.getResources().getString(R.string.level)+": "+level;
        text_level.setText(level_);

        TextView progressPorcentaje = new TextView(context);
        progressPorcentaje.setTextColor(context.getResources().getColor(R.color.black));
        String progression = progress+"%";
        progressPorcentaje.setText(progression);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {progressPorcentaje.setTextColor(context.getResources().getColor(R.color.colorSecondary,null));}else {progressPorcentaje.setTextColor(context.getResources().getColor(R.color.colorSecondary));}


        LinearLayout linearLayout = new LinearLayout(context);
        linearLayout.setOrientation(LinearLayout.HORIZONTAL);
        RelativeLayout.LayoutParams layoutParam = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        layoutParam.addRule(RelativeLayout.ALIGN_PARENT_END);
        linearLayout.setLayoutParams(layoutParam);

        linearLayout.addView(text_level);
        linearLayout.addView(progressPorcentaje);

        RelativeLayout relativeLayout = new RelativeLayout(context);
        RelativeLayout.LayoutParams match_parent = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        relativeLayout.setLayoutParams(match_parent);
        relativeLayout.setPadding(15,15,15,15);
        relativeLayout.setMinimumHeight(45);


        relativeLayout.addView(text_exercise);
        relativeLayout.addView(linearLayout);

        return relativeLayout;
    }
    
   




}
