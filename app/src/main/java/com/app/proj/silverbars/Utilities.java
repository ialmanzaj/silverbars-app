package com.app.proj.silverbars;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.os.Environment;
import android.util.Log;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import okhttp3.ResponseBody;

/**
 * Created by isaacalmanza on 08/10/16.
 */
public class Utilities {

    private static final String TAG = "Utilities";

    /*public static int containerDimensions(Context context) {
        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        Display display = wm.getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        int width = size.x;
        return width;
    }
*/


    /* Checks if external storage is available for read and write */
    public static boolean isExternalStorageWritable() {
        String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state)) {
            return true;
        }
        return false;
    }

    public static boolean CheckExternalStorage(){

        boolean mExternalStorageAvailable = false;
        boolean mExternalStorageWriteable = false;
        String state = Environment.getExternalStorageState();

        if (Environment.MEDIA_MOUNTED.equals(state)) {
            // We can read and write the media
            mExternalStorageAvailable = mExternalStorageWriteable = true;
        } else if (Environment.MEDIA_MOUNTED_READ_ONLY.equals(state)) {
            // We can only read the media
            mExternalStorageAvailable = true;
            mExternalStorageWriteable = false;
        } else {
            // Something else is wrong. It may be one of many other states, but all we need
            //  to know is we can neither read nor write
            mExternalStorageAvailable = mExternalStorageWriteable = false;
        }

        return mExternalStorageAvailable && mExternalStorageWriteable;
    }


    public static Bitmap loadImageFromCache(Context context, String imageURI) {

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

    public static boolean writeResponseBodyToDisk(Context context,ResponseBody body, String imgName) {

        try {

            File futureStudioIconFile = getFileReady(context,"/SilverbarsImg/"+imgName);

            InputStream inputStream = null;
            OutputStream outputStream = null;

            try {

                byte[] fileReader = new byte[4096];
                long fileSize = body.contentLength();
                long fileSizeDownloaded = 0;
                inputStream = body.byteStream();
                outputStream = new FileOutputStream(futureStudioIconFile);
                while (true) {
                    int read = inputStream.read(fileReader);
                    if (read == -1) {break;}
                    outputStream.write(fileReader, 0, read);
                    fileSizeDownloaded += read;
                    Log.v(TAG, "writeResponseBodyToDisk: Download, file download: " + fileSizeDownloaded + " of " + fileSize);
                }
                outputStream.flush();
                return true;

            } catch (IOException e) {
                return false;
            } finally {
                if (inputStream != null) {inputStream.close();}
                if (outputStream != null) {outputStream.close();}
            }
        } catch (IOException e) {return false;}
    }

    public static Bitmap loadWorkoutImageFromInternalMemory(Context context, String imageURI) {
        Log.v(TAG,"loadWorkoutImageFromInternalMemory:");


        Bitmap bitmap = null;
        File file = getFileReady(context,"/SilverbarsImg/"+imageURI);

        if (file.exists()){
            bitmap = BitmapFactory.decodeFile(file.getAbsolutePath());
        }
        return bitmap;
    }

    public static boolean saveWorkoutImgInDevice(Context context, ResponseBody body, String imgName) {

        Log.v(TAG,"saveWorkoutImgInDevice:");

        try {

            File Folder = getFileReady(context,"/SilverbarsImg/");
            File futureStudioIconFile = getFileReady(context,"/SilverbarsImg/"+imgName);


            InputStream inputStream = null;
            OutputStream outputStream = null;
            boolean success = true;

            if (!Folder.isDirectory()) {
                Log.v(TAG,"Creating Dir");
                success = Folder.mkdir();
            }
            if (success) {
                try {
                    byte[] fileReader = new byte[4096];
                    long fileSize = body.contentLength();
                    long fileSizeDownloaded = 0;
                    inputStream = body.byteStream();
                    outputStream = new FileOutputStream(futureStudioIconFile);
                    while (true) {
                        int read = inputStream.read(fileReader);
                        if (read == -1) {break;}
                        outputStream.write(fileReader, 0, read);
                        fileSizeDownloaded += read;
                        Log.v("Download", "file download: " + fileSizeDownloaded + " of " + fileSize);
                    }
                    outputStream.flush();
                    return true;

                } catch (IOException e) {
                    return false;
                } finally {
                    if (inputStream != null) {inputStream.close();}
                    if (outputStream != null) {outputStream.close();}
                }
            } else {
                Log.e(TAG,"Error while creating dir");

            }

        } catch (IOException e) {

            return false;

        }
        return false;
    }


    public static boolean writeProfileImageinDisk(Context context,ResponseBody body, String imgName) {

        try {

            File Folder;
            File futureStudioIconFile;
            if (isExternalStorageWritable()){
                Folder = new File(Environment.getExternalStorageDirectory()+"/SilverbarsProfileImage/");
                futureStudioIconFile = new File(Environment.getExternalStorageDirectory()+"/SilverbarsProfileImage/"+imgName);
            }else{
                Folder = new File(context.getFilesDir()+"/SilverbarsProfileImage/");
                futureStudioIconFile = new File(context.getFilesDir()+"/SilverbarsProfileImage/"+imgName);

            }

            InputStream inputStream = null;
            OutputStream outputStream = null;

            boolean success = true;
            if (!Folder.isDirectory()) {
                Log.v(TAG,"Creating Dir");
                success = Folder.mkdir();
            }

            if (success) {
                try {
                    byte[] fileReader = new byte[4096];
                    long fileSize = body.contentLength();
                    long fileSizeDownloaded = 0;
                    inputStream = body.byteStream();
                    outputStream = new FileOutputStream(futureStudioIconFile);
                    while (true) {
                        int read = inputStream.read(fileReader);
                        if (read == -1) {break;}
                        outputStream.write(fileReader, 0, read);
                        fileSizeDownloaded += read;
                        Log.v("Download", "file download: " + fileSizeDownloaded + " of " + fileSize);
                    }
                    outputStream.flush();
                    return true;

                } catch (IOException e) {
                    return false;
                } finally {
                    if (inputStream != null) {inputStream.close();}
                    if (outputStream != null) {outputStream.close();}
                }
            } else {
                Log.e(TAG,"Error while creating dir");
            }

        } catch (IOException e) {return false;}
        return false;
    }

    public static Bitmap loadProfileImageFromCache(Context context, String imageURI) {

        Bitmap bitmap = null;
        String[] imageDir = imageURI.split("SilverbarsImg");

        if (imageDir.length < 2){

            File file;
            if (isExternalStorageWritable()){
                file = new File(Environment.getExternalStorageDirectory()+"/SilverbarsImg/"+imageURI);
            }else {
                file = new File(context.getFilesDir()+"/SilverbarsImg/"+imageURI);
            }
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

    public static String strSeparator = "__,__";
    public static String convertArrayToString(String[] array){
        String str = "";
        for (int i = 0;i<array.length; i++) {
            str = str+array[i];

            if(i<array.length-1){
                str = str+strSeparator;
            }
        }
        return str;
    }


    public static String getUrlReady(Context context,String url){

        String dir;
        if (isExternalStorageWritable()){
            dir = Environment.getExternalStorageDirectory()+url;
        }else {
            dir = context.getFilesDir()+url;
        }
        return dir;
    }

    public static File getFileReady(Context context,String url){

        File Dir;
        if(isExternalStorageWritable()){
            Dir = new File(Environment.getExternalStorageDirectory() + url);
        }else {
            Dir = new File(context.getFilesDir()+url);
        }
        return Dir;
    }

    public static String removeLastChar(String str) {return str.substring(0,str.length()-1);}



    public static String SongArtist(Context context,File file){
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

    public static String SongDuration(Context context,File file){
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


    public static String SongName(Context context,File file){
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


    public static String quitarMp3(String song){
        if(song.contains(".mp3")){
            song =  song.replace(".mp3","");
        }if (song.contains("-")){
            song = song.replace("-"," ");
        }
        return song;
    }

}
