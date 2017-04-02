package com.app.app.silverbarsapp.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.PorterDuff;
import android.media.MediaMetadataRetriever;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.util.Log;
import android.view.Display;
import android.view.Gravity;
import android.view.WindowManager;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.app.app.silverbarsapp.MainService;
import com.app.app.silverbarsapp.R;
import com.app.app.silverbarsapp.models.Exercise;
import com.app.app.silverbarsapp.models.ExerciseRep;
import com.app.app.silverbarsapp.models.MuscleExercise;
import com.app.app.silverbarsapp.models.Workout;
import com.spotify.sdk.android.player.Connectivity;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

import static com.app.app.silverbarsapp.Constants.BODY_URL;

/**
 * Created by isaacalmanza on 08/10/16.
 */
public class Utilities {

    private static final String TAG = "Utilities";

    private   String strSeparator = "__,__";

    public Utilities (){}

    /**
     * Method to format normal number to decimal and Rounding.
     */
    public String formaterDecimal(String originalString){
        String results = "";
        Double doubleval;
        try {
            if (originalString.contains(",")) {originalString = originalString.replaceAll(",", "");}
            doubleval = Double.parseDouble(originalString);
            //Log.v(TAG,"doubleval "+doubleval);
            DecimalFormat formatter = (DecimalFormat) NumberFormat.getInstance(Locale.US);
            formatter.applyPattern("#,###,###,###.#");
            results = formatter.format(doubleval);
        } catch (NumberFormatException nfe) {
            nfe.printStackTrace();
        }
        //Log.v(TAG,"results "+results);
        return results;
    }


    public String formatHMS(int totalSecs){
        int hours = totalSecs / 3600;
        int minutes = (totalSecs % 3600) / 60;
        int seconds = totalSecs % 60;
        return String.format(Locale.US,"%02d:%02d:%02d", hours, minutes, seconds);
    }

    public boolean checkIfRep(ExerciseRep exercise){
        //repetitions  or seconds
        return exercise.getRepetition() > 0;
    }

    public List<Exercise> getExercisesById(List<Exercise> all_exercises_list, List<Integer> exercises_id){
        List<Exercise> exerciseList = new ArrayList<>();
        for (Integer exercise_id: exercises_id){
            exerciseList.add(getExerciseById(all_exercises_list,exercise_id));
        }
        return exerciseList;
    }

    public ArrayList<ExerciseRep> returnExercisesRep(List<Exercise> exercises){
        ArrayList<ExerciseRep> exerciseReps = new ArrayList<>();
        for (Exercise exercise: exercises){
            ExerciseRep exerciseRep = new ExerciseRep();
            exerciseRep.setExercise(exercise);
            exerciseRep.setNumber(0);
            exerciseReps.add(exerciseRep);
        }
        return exerciseReps;
    }


    public List<String> getMusclesFromExercises(List<Exercise> exercises){
        List<String> muscles_names = new ArrayList<>();
        for(Exercise exercise:exercises){
            //add muscles array
            for (MuscleExercise muscle: exercise.getMuscles()){
                muscles_names.add(muscle.getMuscle());
            }
        }
        return muscles_names;
    }

    public ArrayList<Integer> getExercisesIds(ArrayList<Exercise> exercises){
        ArrayList<Integer> exercises_ids = new ArrayList<>();

        for (int a = 0;a<exercises.size();a++){
            exercises_ids.add(exercises.get(a).getId());
        }
        return exercises_ids;
    }

    public Workout getWorkoutById(List<Workout> workouts, int workout_id){
        for (Workout workout: workouts){
            if (workout.getId() == workout_id){
                return workout;
            }
        }
        return null;
    }


    public Exercise getExerciseById(List<Exercise> exercises, int exercise_id){
        for (Exercise exercise: exercises){
            if (exercise.getId() == exercise_id){
                return exercise;
            }
        }
        return null;
    }


    public ExerciseRep getExerciseRepById(List<ExerciseRep> exercises, int exercise_id){
        for (ExerciseRep exercise: exercises){
            if (exercise.getExercise().getId() == exercise_id){
                return exercise;
            }
        }
        return null;
    }


    public Connectivity getNetworkConnectivity(Context context) {
        ConnectivityManager connectivityManager;
        connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = connectivityManager.getActiveNetworkInfo();
        if (activeNetwork != null && activeNetwork.isConnected()) {
            return Connectivity.fromNetworkType(activeNetwork.getType());
        } else {
            return Connectivity.OFFLINE;
        }
    }

    public ArrayList<File> findSongs(Context context,File root){
        ArrayList<File> songs = new ArrayList<File>();
        if (root.listFiles() != null){
            File[] files = root.listFiles();
            for(File singleFile : files){
                if (singleFile.isDirectory() && !singleFile.isHidden()){
                    songs.addAll(findSongs(context,singleFile));
                }
                else{
                    if (singleFile.getName().endsWith(".mp3") || singleFile.getName().endsWith(".wav")){
                        if (getSongDuration(context,singleFile)!=null && Long.valueOf(getSongDuration(context,singleFile))>150000)
                            songs.add(singleFile);
                    }
                }
            }
        }
        return songs;
    }

    public ArrayList<File> deleteVoiceNote(ArrayList<File> songs){
        ArrayList<String> canciones = new ArrayList<>();
        ArrayList<File>   songs_urls = new ArrayList<>();

        for (int a = 0;a<songs.size();a++){canciones.add(songs.get(a).getPath());}

        for (int b = 0;b<canciones.size();b++){
            canciones.get(b).split("/storage/emulated/0/");

            if (!canciones.get(b).contains("WhatsApp/Media/WhatsApp Audio/AUD-")){
                //Log.d(TAG,"otras canciones: "+songs.get(b));

                try {

                    songs_urls.add(songs.get(b));

                }catch (NullPointerException e){Log.e(TAG,"NullPointerException",e);}

            }
        }
        return songs_urls;
    }

    public void toast(Context context,String text){
        Toast.makeText(context.getApplicationContext(),text,Toast.LENGTH_SHORT).show();
    }

    public void loadUrlOfMuscleBody(Context context, WebView webView){
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

    public  int calculateContainerWidth(Context context) {
        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        Display display = wm.getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        return size.x;
    }

    public int calculateContainerHeight(Context context) {
        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        Display display = wm.getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        return size.y;
    }

    /* Checks if external storage is available for read and write */
    public  boolean isExternalStorageWritable() {
        String state = Environment.getExternalStorageState();
        return Environment.MEDIA_MOUNTED.equals(state);
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



    public  void downloadImage(Context context, String url, String imgName){

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

        Retrofit retrofit = new Retrofit.Builder().baseUrl("https://s3-ap-northeast-1.amazonaws.com/silverbarsmedias3/").build();

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

    public  List<String> deleteCopiesofList(List<String> elements_with_copies){
        List<String> elements_with_no_copies = new ArrayList<>();
        for (String element : elements_with_copies) {
            if (!elements_with_no_copies.contains(element)) {
                elements_with_no_copies.add(element);
            }
        }
        return elements_with_no_copies;
    }

    public boolean saveHtmInDevice(Context context,ResponseBody body, String name) {

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


    public void injectJS(WebView webView,String muscles) {
        Log.d(TAG,"muscles: "+muscles);
        try {

            webView.loadUrl(getJavascriptReady(muscles));

        } catch (Exception e) {
            Log.e(TAG,"Exception",e);
        }
    }


    private String getJavascriptReady(String muscles){
        if (!Objects.equals(muscles, "")) {
            final String muscles_ready = removeLastChar(muscles);
            Log.d(TAG,"muscles_ready: "+muscles_ready);

            return "javascript: (" + "window.onload = function () {" +
                    "muscles = Snap.selectAll('" + muscles_ready + "');" +
                    "muscles.forEach( function(muscle,i) {" +
                        "muscle.attr({stroke:'#602C8D',fill:'#602C8D'});" +
                    "});" + "}" + ")()";
        }
        return "";
    }


    public String getMusclesReadyForWebview(List<String> muscles_names){
        String muscles_body = "";
        for (String muscle_name: muscles_names){
            muscles_body += "#"+ muscle_name + ",";
        }
        return muscles_body;
    }


    public void onWebviewReady(WebView webView,String muscles){
        webView.setWebViewClient(new WebViewClient(){
            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                injectJS(webView,muscles);
            }
        });
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
        progressBar.getLayoutParams().width = calculateContainerWidth(context) / 2;
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


}
