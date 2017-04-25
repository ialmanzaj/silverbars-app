package com.app.app.silverbarsapp.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Point;
import android.media.MediaMetadataRetriever;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Environment;
import android.util.Log;
import android.view.Display;
import android.view.WindowManager;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

import com.app.app.silverbarsapp.MainService;
import com.app.app.silverbarsapp.R;
import com.app.app.silverbarsapp.models.Exercise;
import com.app.app.silverbarsapp.models.ExerciseProgression;
import com.app.app.silverbarsapp.models.ExerciseRep;
import com.app.app.silverbarsapp.models.MuscleExercise;
import com.app.app.silverbarsapp.models.Skill;
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
import java.util.concurrent.TimeUnit;

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


    public List<Skill> getTypes(ArrayList<ExerciseProgression> exercises){
        List<Skill> types = new ArrayList<>();
        List<String> types_list = new ArrayList<>();
        for (ExerciseProgression exercise: exercises){
            for (String type: exercise.getExercise().getType_exercise()){
                types_list.add(type);
            }
        }
        for (String type : deleteCopiesofList(types_list)){
            double porcentaje = (counter(types_list,type)*100 / types_list.size());
            //Log.d(TAG,type +" " + porcentaje);
            types.add(new Skill(type, (int) porcentaje));
        }

        return types;
    }



    public List<Skill> getTypesExercise(ArrayList<ExerciseRep> exercises){
        List<Skill> types = new ArrayList<>();
        List<String> types_list = new ArrayList<>();
        for (ExerciseRep exercise: exercises){
            for (String type: exercise.getExercise().getType_exercise()){
                types_list.add(type);
            }
        }
        for (String type : deleteCopiesofList(types_list)){
            double porcentaje = (counter(types_list,type)*100 / types_list.size());
            //Log.d(TAG,type +" " + porcentaje);
            types.add(new Skill(type, (int) porcentaje));
        }

        return types;
    }

    public int counter(List<String> list,String s){
        int count = 0;
        for (String element: list){
            if (Objects.equals(element, s)){
                count = count+1;
            }
        }
        return count;
    }


    public static class HandlerMover{
        int position = 0;
        int size;
        public HandlerMover(int size){
            this.size = size;
        }

        public boolean allowToMove(int pos){
            return pos != -1;
        }

        public void setPosition(int position) {
            this.position = position;
        }

        public int moveNext(){
            if ( size > 1 && position + 1 < size) {
                position = (position + 1) % size;
                //Log.d(TAG,"next "+position);
                return position;
            }
            return -1;
        }

        public int getPosition(){
            return position;
        }

        public int movePreview(){
            if (size > 1 && (position-1) >= 0 ) {
                position = (position - 1) % size;
                //Log.d(TAG,"preview "+position);
                return position;
            }
           return -1;
        }
    }


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


    public String formatHMS(long millis){
        return String.format(Locale.US,"%02d:%02d:%02d",
                TimeUnit.MILLISECONDS.toHours(millis),
                TimeUnit.MILLISECONDS.toMinutes(millis) - TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(millis)),
                TimeUnit.MILLISECONDS.toSeconds(millis) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(millis)));
    }


    public boolean checkIfRep(ExerciseRep exercise){
        //repetitions  or seconds
        return exercise.getRepetition() > 0;
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

    public void toastLong(Context context,String text){
        Toast.makeText(context.getApplicationContext(),text,Toast.LENGTH_LONG).show();
    }


    public void loadUrlOfMuscleBody(Context context, WebView webView){
        SharedPreferences sharedPref = context.getSharedPreferences("Mis preferencias",Context.MODE_PRIVATE);
        String default_url = context.getResources().getString(R.string.muscle_path);
        String muscle_url = sharedPref.getString(context.getString(R.string.muscle_path),default_url);

        webView.setWebChromeClient(new WebChromeClient());

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

    public  List<String> deleteCopiesofMuscles(List<MuscleExercise> elements_with_copies){
        List<String> elements_with_no_copies = new ArrayList<>();
        for (MuscleExercise element : elements_with_copies) {
            if (!elements_with_no_copies.contains(element.getMuscle())) {
                elements_with_no_copies.add(element.getMuscle());
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


    private String getJsReady(String muscles){
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

    public void injectJS(WebView webView,String muscles) {
        Log.d(TAG,"muscles: "+muscles);
        try {

            webView.loadUrl(getJsReady(muscles));

        } catch (Exception e) {
            Log.e(TAG,"Exception",e);
        }
    }

    private String getJsOnClickReady(String muscles){
        muscles = removeLastChar(muscles);
        Log.d(TAG,"muscles:"+muscles);
        return "javascript: (" + "window.onload = function () {" +
                "var muscles = Snap.selectAll('" + muscles + "');" +
                "muscles.forEach( function(muscle,i) {" +
                    "muscle.node.onclick = function () {"+
                        "muscle.attr({stroke:'#602C8D',fill:'#602C8D'});"+
                        "Android.setMuscle(muscle.node.id);"+
                "};" + "});" + "}" + ")()";
    }

    private String getJsClickPaintedReady(String muscles){
        muscles = removeLastChar(muscles);
        Log.d(TAG,"muscles:"+muscles);
        return "javascript: (" + "window.onload = function () {" +
                "var muscles = Snap.selectAll('" + muscles + "');" +
                "muscles.forEach( function(muscle,i) {" +
                    "muscle.attr({stroke:'#602C8D',fill:'#602C8D'});"+
                    "muscle.node.onclick = function () {"+
                        "Android.setMuscle(muscle.node.id);"+
                "};" + "});" + "}" + ")()";
    }

    public void injectJSOnClick(WebView webView,String muscles) {

        try {

            webView.loadUrl(getJsOnClickReady(muscles));

        } catch (Exception e) {
            Log.e(TAG,"Exception",e);
        }
    }

    private void injectJSOnClickPainted(WebView webView,String muscles) {

        try {

            webView.loadUrl(getJsClickPaintedReady(muscles));

        } catch (Exception e) {
            Log.e(TAG,"Exception",e);
        }
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

    public void onWebviewClickReady(WebView webView,String muscles){
        webView.setWebViewClient(new WebViewClient(){
            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                injectJSOnClickPainted(webView,muscles);
            }
        });
    }

}
