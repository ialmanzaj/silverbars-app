package com.app.project.silverbars;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Environment;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.Arrays;

/**
 * Created by andre_000 on 5/18/2016.
 */

public class MySQLiteHelper extends SQLiteOpenHelper {

    private static String strSeparator = "__,__";
    // Database Version
    public static final int DATABASE_VERSION = 1;
    // Database Name
    public static final String DATABASE_NAME = "SilverbarsData";
    //     Database tables name
    public static final String TABLE_USERS = "users";
    public static final String TABLE_PLAYLISTS = "playlists";
    public static final String TABLE_WORKOUTS = "workouts";
    public static final String TABLE_WORKOUT = "workout";
    public static final String TABLE_EXERCISES = "exercises";

    //     Users Table Columns names
    public static final String KEY_IDUSER = "id";
    public static final String KEY_EMAIL = "email";
    public static final String KEY_NAME = "name";
    public static final String KEY_ACTIVE = "active";
    //    Playlists Table Columns names
    public static final String KEY_IDPLIST = "id";
    public static final String KEY_PNAME = "name";
    public static final String KEY_SONGNAME = "songname";
    public static final String KEY_USERID = "user_id";
    //    Workouts table Columns names
    public static final String KEY_IDWORKOUTS = "id";
    public static final String KEY_WORKOUTNAME = "workout_name";
    public static final String KEY_WORKOUTIMG = "workout_image";
    public static final String KEY_SETS = "sets";
    public static final String KEY_LEVEL = "level";
    public static final String KEY_MAINMUSCLE = "main_muscle";
    public static final String KEY_EXERCISES = "exercises";
    public static final String KEY_LOCAL = "local";
    //    Workout table Columns names
    public static final String KEY_IDWORKOUT = "id";
    public static final String KEY_WORKOUTSID = "workout_id";
    public static final String KEY_WORKOUTEX = "exercise";
    public static final String KEY_REPETITION = "repetition";
    //    Exercises table Column names
    public static final String KEY_ID_EXERCISE_LOCAL = "idLocal";
    public static final String KEY_IDEXERCISE = "id";
    public static final String KEY_EXERCISENAME = "exercise_name";
    public static final String KEY_EXERCISELEVEL = "level";
    public static final String KEY_TYPE_EXERCISE = "type_exercise";
    public static final String KEY_MUSCLE = "muscle";
    public static final String KEY_EXERCISE_AUDIO = "exercise_audio";
    public static final String KEY_EXERCISE_IMAGE = "exercise_image";

    public MySQLiteHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    private static final String CREATE_TABLE_USERS = "CREATE TABLE IF NOT EXISTS "+
            TABLE_USERS+"(" +
            KEY_IDUSER+" INTEGER PRIMARY KEY, " +
            KEY_NAME+" TEXT, " +
            KEY_EMAIL+" varchar, " +
            KEY_ACTIVE+" integer)";
    private static final String CREATE_TABLE_PLAYLISTS = "CREATE TABLE IF NOT EXISTS "+
            TABLE_PLAYLISTS+"(" +
            KEY_IDPLIST+" INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL," +
            KEY_PNAME+" TEXT, " +
            KEY_SONGNAME+" varchar, " +
            KEY_USERID+" integer)";
    private static final String CREATE_TABLE_WORKOUTS = "CREATE TABLE IF NOT EXISTS "+
            TABLE_WORKOUTS+"(" +
            KEY_IDWORKOUTS+" INTEGER PRIMARY KEY NOT NULL," +
            KEY_WORKOUTNAME+" TEXT, " +
            KEY_WORKOUTIMG+" varchar, " +
            KEY_SETS+" INTEGER, " +
            KEY_LEVEL+" TEXT, " +
            KEY_MAINMUSCLE+" TEXT, " +
            KEY_USERID+" INTEGER, "+
            KEY_EXERCISES+" varchar, "+
            KEY_LOCAL+" TEXT )";
    private static final String CREATE_TABLE_WORKOUT = "CREATE TABLE IF NOT EXISTS "+
            TABLE_WORKOUT+"(" +
            KEY_IDWORKOUT+" INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL," +
            KEY_WORKOUTSID+" INTEGER REFERENCE"+TABLE_WORKOUTS+"," +
            KEY_WORKOUTEX+" INTEGER REFERENCE"+TABLE_EXERCISES+", " +
            KEY_REPETITION+" integer)";
    private static final String CREATE_TABLE_EXERCISES = "CREATE TABLE IF NOT EXISTS "+
            TABLE_EXERCISES+"(" +
            KEY_IDEXERCISE+" INTEGER PRIMARY KEY NOT NULL," +
            KEY_EXERCISENAME+" TEXT, " +
            KEY_EXERCISELEVEL+" TEXT, " +
            KEY_TYPE_EXERCISE+" VARCHAR, " +
            KEY_MUSCLE+" varchar, " +
            KEY_EXERCISE_AUDIO+" varchar, " +
            KEY_EXERCISE_IMAGE+" VARCHAR)";
//            "FOREIGN KEY("+KEY_USERID+") REFERENCES "+TABLE_USERS+"("+KEY_IDUSER+")";

    @Override
    public void onCreate(SQLiteDatabase database) {
        database.execSQL(CREATE_TABLE_USERS);
        database.execSQL(CREATE_TABLE_PLAYLISTS);
        database.execSQL(CREATE_TABLE_WORKOUTS);
        database.execSQL(CREATE_TABLE_WORKOUT);
        database.execSQL(CREATE_TABLE_EXERCISES);
        if (!database.isReadOnly()) {
            // Enable foreign key constraints
            database.execSQL("PRAGMA foreign_keys=ON;");
        }
    }


    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion){
        Log.w(MySQLiteHelper.class.getName(),
                "Upgrading database from version " + oldVersion + " to "
                        + newVersion + ", which will destroy all old data");
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_USERS +", "+TABLE_PLAYLISTS+", "+TABLE_WORKOUTS+", "+TABLE_WORKOUT+", "+TABLE_EXERCISES);
        onCreate(db);
    }

    public void insertUser(String name, String email, int active){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(KEY_NAME,name);
        cv.put(KEY_EMAIL,email);
        cv.put(KEY_ACTIVE,active);
        db.insert(TABLE_USERS,null,cv);
        db.close();
    }

    public int updateUser(String email, int active){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(KEY_ACTIVE,active);
        int i = db.update(TABLE_USERS,cv,KEY_EMAIL+"="+email,null);
        db.close();

        return i;
    }

    public String[] getUser(String email){
        String[] results = new String[4] ;
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor row = db.rawQuery("SELECT * FROM users WHERE "+KEY_EMAIL+" = "+email,null);
        if (row.moveToFirst()){
            row.moveToFirst();
            results[0] = String.valueOf(row.getInt(0));
            results[1] = row.getString(1);
            results[2] = row.getString(2);
            results[3] = row.getString(3);
        }
        db.close();
        try {
            BD_backup();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return results;
    }

    public String[] getActiveUser(){
        String[] results = new String[4] ;
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor row = db.rawQuery("SELECT * FROM users WHERE "+KEY_ACTIVE+" = "+1,null);
        if (row.moveToFirst()){
            row.moveToFirst();
            results[0] = String.valueOf(row.getInt(0));
            results[1] = row.getString(1);
            results[2] = row.getString(2);
            results[3] = row.getString(3);
        }
        db.close();
        try {
            BD_backup();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return results;
    }

    public void insertPlaylist(String name, String songname, int userid){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(KEY_PNAME,name);
        cv.put(KEY_SONGNAME,songname);
        cv.put(KEY_USERID,userid);
        db.insert(TABLE_PLAYLISTS,null,cv);
        db.close();
    }

    public String[] getPlaylist(int id){
        String[] results = new String[4] ;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor row = db.rawQuery("SELECT * FROM "+TABLE_PLAYLISTS+" WHERE "+KEY_IDPLIST+" = "+id,null);
        Log.v("Row Count",String.valueOf(row.getCount()));
        if (row.moveToFirst()){
            row.moveToFirst();
            results[0] = String.valueOf(row.getInt(0));
            results[1] = row.getString(1);
            results[2] = row.getString(2);
            results[3] = row.getString(3);
        }
        else
            Log.v("Database Error","No results");
        db.close();
        try {
            BD_backup();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return results;
    }

    public String[] getUserPlaylists(int id){
        String songName;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor row = db.rawQuery("SELECT * FROM "+TABLE_PLAYLISTS+" WHERE "+KEY_USERID+" = "+id,null);
        String[] results = null;
        if (row.moveToFirst()){
            row.moveToFirst();
            results = new String[row.getCount()];
            int i = 0;
            songName = row.getString(row.getColumnIndex(KEY_PNAME));
            results[i] = songName;
            while(row.moveToNext()){
                i++;
                songName = row.getString(row.getColumnIndex(KEY_PNAME));
                results[i] = songName;
            }
        }
        else
            Log.v("Database Error","No results");

        db.close();
        try {
            BD_backup();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return results;
    }

    public void insertExercises(int id, String name, String level, String typeExercise, String typeMuscle, String audio, String Image){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(KEY_IDEXERCISE,id);
        cv.put(KEY_EXERCISENAME,name);
        cv.put(KEY_EXERCISELEVEL,level);
        cv.put(KEY_TYPE_EXERCISE,typeExercise);
        cv.put(KEY_MUSCLE,typeMuscle);
        cv.put(KEY_EXERCISE_AUDIO,audio);
        cv.put(KEY_EXERCISE_IMAGE,Image);
        db.insert(TABLE_EXERCISES,null,cv);
        db.close();
        try {
            BD_backup();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public JsonExercise getExercise(int exerciseId){
        JsonExercise exercises = new JsonExercise();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor row = db.rawQuery("SELECT * FROM "+TABLE_EXERCISES+" WHERE "+KEY_IDEXERCISE+" = "+exerciseId,null);
        String[] results = null;
        if (row.moveToFirst()){
            if (convertStringToArray(row.getString(3)) == null){
                String[] type = new String[1];
                type[0] = row.getString(3);
                exercises.setType_exercise(type);
                exercises = new JsonExercise(row.getInt(0),row.getString(1),row.getString(2),type,convertStringToArray(row.getString(4)),row.getString(5),row.getString(6));
            }
            else{
                exercises = new JsonExercise(row.getInt(0),row.getString(1),row.getString(2),convertStringToArray(row.getString(3)),convertStringToArray(row.getString(4)),row.getString(5),row.getString(6));
            }
        }
        else{
            Log.v("Database Error","No results");
        }
        db.close();
        try {
            BD_backup();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return exercises;
    }

    public boolean checkExercise(int id){
        boolean check = false;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor row = db.rawQuery("SELECT * FROM "+TABLE_EXERCISES+" WHERE "+KEY_IDEXERCISE+" = "+id,null);
        String[] results = null;
        if (row.moveToFirst()){
            check = true;
        }
        else{
            Log.v("Database Error","No results");
            check = false;
        }
        db.close();
        try {
            BD_backup();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return check;
    }

    public void insertWorkouts(int id, String name, String imgFile, int sets, String level, String mainMuscle, String exercises, int usersid, String local){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(KEY_IDWORKOUTS,id);
        cv.put(KEY_WORKOUTNAME,name);
        cv.put(KEY_WORKOUTIMG,imgFile);
        cv.put(KEY_SETS,sets);
        cv.put(KEY_LEVEL,level);
        cv.put(KEY_MAINMUSCLE,mainMuscle);
        cv.put(KEY_USERID,usersid);
        cv.put(KEY_EXERCISES,exercises);
        cv.put(KEY_LOCAL,local);
        db.insert(TABLE_WORKOUTS,null,cv);
        db.close();
        try {
            BD_backup();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void updateLocal(int id, String local){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(KEY_LOCAL,local);
        db.update(TABLE_WORKOUTS,cv,"id="+id,null);
        db.close();
        try {
            BD_backup();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public boolean checkWorkouts(int id){
        boolean check = false;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor row = db.rawQuery("SELECT * FROM "+TABLE_WORKOUTS+" WHERE "+KEY_IDWORKOUTS+" = "+id,null);
        String[] results = null;
        if (row.moveToFirst()){
            check = true;
        }
        else{
            Log.v("Database Error","No results");
            check = false;
        }
        db.close();
        try {
            BD_backup();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return check;
    }

    public String checkLocal (int id){
        String check = "false";
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor row = db.rawQuery("SELECT * FROM "+TABLE_WORKOUTS+" WHERE "+KEY_IDWORKOUTS+" = "+id,null);
        String[] results = null;
        if (row.moveToFirst()){
            check = row.getString(8);
        }
        else{
            Log.v("Database Error","No results");
            check = "false";
        }
        db.close();
        try {
            BD_backup();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return check;
    }

    public void insertWorkout(int workoutId, int exercisesId, int repetitions){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(KEY_WORKOUTSID,workoutId);
        cv.put(KEY_WORKOUTEX,exercisesId);
        cv.put(KEY_REPETITION,repetitions);
        db.insert(TABLE_WORKOUT,null,cv);
        db.close();
        try {
            BD_backup();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public JsonReps[] getWorkout(int workoutId){
        JsonReps[] workout = null;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor row = db.rawQuery("SELECT * FROM "+TABLE_WORKOUT+" WHERE "+KEY_WORKOUTSID+" = "+workoutId,null);
        int i = 0;
        if (row.moveToFirst()){
            workout = new JsonReps[row.getCount()];
            row.moveToFirst();
            workout[i] = new JsonReps(row.getInt(0),String.valueOf(row.getInt(1)),String.valueOf(row.getInt(2)),row.getInt(3));
//            workout[i].setId(row.getInt(0));
//            workout[i].setWorkout_id(String.valueOf(row.getInt(1)));
//            workout[i].setExercise(String.valueOf(row.getInt(2)));
//            workout[i].setRepetition(row.getInt(3));
            while(row.moveToNext()){
                i++;
                workout[i] = new JsonReps(row.getInt(0),String.valueOf(row.getInt(1)),String.valueOf(row.getInt(2)),row.getInt(3));
//                workout[i].setId(row.getInt(0));
//                workout[i].setWorkout_id(String.valueOf(row.getInt(1)));
//                workout[i].setExercise(String.valueOf(row.getInt(2)));
//                workout[i].setRepetition(row.getInt(3));
            }
        }
        else{
            Log.v("Database Error","No results");
        }
        db.close();
        try {
            BD_backup();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return workout;
    }

    public boolean checkWorkout(int workoutId, int exerciseId){
        boolean check = false;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor row = db.rawQuery("SELECT * FROM "+TABLE_WORKOUT+" WHERE "+KEY_WORKOUTSID+" = "+workoutId+" AND "+KEY_WORKOUTEX+" = "+exerciseId,null);
        String[] results = null;
        if (row.moveToFirst()){
            check = true;
        }
        else{
            Log.v("Database Error","No results");
            check = false;
        }
        db.close();
        try {
            BD_backup();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return check;
    }

    public int getWorkoutId(int usersid, String workoutname){
        int workoutId = 0;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor row = db.rawQuery("SELECT * FROM "+TABLE_WORKOUTS+" WHERE "+KEY_USERID+" = "+usersid+" AND "+KEY_WORKOUTNAME+"="+workoutname,null);
        String[] results = null;
        if (row.moveToFirst()){
            row.moveToFirst();
            workoutId = row.getInt(row.getColumnIndex(KEY_IDWORKOUTS));
        }
        else
            Log.v("Database Error","No results");

        db.close();
        try {
            BD_backup();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return workoutId;
    }


//    public MySQLiteHelper(Context context, String nombre, SQLiteDatabase.CursorFactory factory, int version) {
//        super(context, nombre, factory, version);
//    }
//
//    @Override
//    public void onCreate(SQLiteDatabase db) {
//        db.execSQL("create table users(id integer primary key autoincrement not null, name text, email varchar, active integer)");
//        db.execSQL("create table playlists(id integer primary key autoincrement not null, name text, position integer, user_id integer, FOREIGN KEY(user_id) REFERENCES users(id))");
//
//    }
//
//
//    @Override
//    public void onUpgrade(SQLiteDatabase db, int versionAnte, int versionNue) {
//        db.execSQL("drop table if exists users");
//        db.execSQL("create table users(id integer primary key autoincrement, name text, lastname text ,email integer primary key)");
//        db.execSQL("drop table if exists playlists");
//        db.execSQL("create table playlists(id integer primary key autoincrement, name text, position integer, user_id integer, FOREIGN KEY(user_id) REFERENCES users(id))");
//
//    }


    public static void BD_backup() throws IOException {
//        String timeStamp = new SimpleDateFormat("ddMMyyyy_HHmmss").format(new Date());

        final String inFileName = "/data/data/com.app.project.silverbars/databases/"+DATABASE_NAME;
        File dbFile = new File(inFileName);
        FileInputStream fis = null;

        fis = new FileInputStream(dbFile);

        String directorio = Environment.getExternalStorageDirectory()+"/Database";
        File d = new File(directorio);
        if (!d.exists()) {
            d.mkdir();
        }
        String outFileName = directorio + "/"+DATABASE_NAME;

        OutputStream output = new FileOutputStream(outFileName);

        byte[] buffer = new byte[1024];
        int length;
        while ((length = fis.read(buffer)) > 0) {
            output.write(buffer, 0, length);
        }

        output.flush();
        output.close();
        fis.close();

    }

    public static String convertArrayToString(String[] array){
        String str = "";
        for (int i = 0;i<array.length; i++) {
            str = str+array[i];
            // Do not append comma at the end of last element
            if(i<array.length-1){
                str = str+strSeparator;
            }
        }
        return str;
    }

    public static String[] convertStringToArray(String str){
        return str.split(strSeparator);
    }


}
