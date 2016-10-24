package com.app.proj.silverbars;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Arrays;



import static com.app.proj.silverbars.Utilities.convertStringToArray;
import static com.app.proj.silverbars.Utilities.convertStringToExercises;
import static com.app.proj.silverbars.Utilities.getUrlReady;
/**
 * Created by isaacalmanza on 10/04/16.
 */

public class MySQLiteHelper extends SQLiteOpenHelper {

    private static final String TAG = "MySQLiteHelper";
    private static String strSeparator = "__,__";
    // Database Version
    public static final int DATABASE_VERSION = 1;
    // Database Name
    public static final String DATABASE_NAME = "SilverbarsData";


    //     Database tables name
    public static final String TABLE_USERS = "users";
    public static final String TABLE_PLAYLISTS = "playlists";
    public static final String TABLE_WORKOUTS = "workouts";
    public static final String TABLE_USER_WORKOUTS = "user_workouts";
    public static final String TABLE_EXERCISES_REP = "exercises_reps";
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
    private static final String KEY_USERID = "user_id";


    //    Workouts table Columns names
    public static final String KEY_IDWORKOUTS = "id";
    public static final String KEY_WORKOUTNAME = "workout_name";
    public static final String KEY_WORKOUTIMG = "workout_image";
    public static final String KEY_SETS = "sets";
    public static final String KEY_LEVEL = "level";
    public static final String KEY_MAINMUSCLE = "main_muscle";
    public static final String KEY_EXERCISES = "exercises";
    public static final String KEY_LOCAL = "local";



    //    Exercise-rep table Columns names
    public static final String KEY_EXERCISEREPID = "exercise_rep_id";
    public static final String KEY_WORKOUTEX = "exercise";
    public static final String KEY_REPETITION = "repetition";
    public static final String KEY_SECONDS = "seconds";
    public static final String KEY_TEMPO_POSITIVE = "tempo_positive";
    public static final String KEY_TEMPO_ISOMETRIC = "tempo_isometric";
    public static final String KEY_TEMPO_NEGATIVE = "tempo_negative";



    //    Exercises table Column names
    public static final String KEY_IDEXERCISE = "id";
    public static final String KEY_EXERCISENAME = "exercise_name";
    public static final String KEY_EXERCISELEVEL = "level";
    public static final String KEY_TYPE_EXERCISE = "type_exercise";
    public static final String KEY_MUSCLE = "muscle";
    public static final String KEY_EXERCISE_AUDIO = "exercise_audio";
    public static final String KEY_EXERCISE_IMAGE = "exercise_image";


    //   USER Workouts table Columns names
    public static final String KEY_IDUSERWORKOUTS = "id";
    public static final String KEY_USER_WORKOUTNAME = "workout_name";
    public static final String KEY_USER_WORKOUTIMG = "workout_image";
    public static final String KEY_USER_SETS = "sets";
    public static final String KEY_USER_LEVEL = "level";
    public static final String KEY_USER_MAINMUSCLE = "main_muscle";


    private final Context myContext;

    public MySQLiteHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        myContext = context;
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

    private static final String CREATE_TABLE_EXERCISES = "CREATE TABLE IF NOT EXISTS "+
            TABLE_EXERCISES+"(" +
            KEY_IDEXERCISE+" INTEGER PRIMARY KEY NOT NULL," +
            KEY_EXERCISENAME+" TEXT, " +
            KEY_EXERCISELEVEL+" TEXT, " +
            KEY_TYPE_EXERCISE+" VARCHAR, " +
            KEY_MUSCLE+" varchar, " +
            KEY_EXERCISE_AUDIO + " varchar, " +
            KEY_EXERCISE_IMAGE+" VARCHAR)";


    private static final String CREATE_TABLE_EXERCISE_REP = "CREATE TABLE IF NOT EXISTS "+
            TABLE_EXERCISES_REP +"(" +
            KEY_EXERCISEREPID + " INTEGER PRIMARY KEY NOT NULL," +
            KEY_WORKOUTEX + " INTEGER REFERENCE"+TABLE_EXERCISES+", " +
            KEY_REPETITION + " integer, " +
            KEY_SECONDS + " integer)";


    private static final String CREATE_TABLE_WORKOUTS = "CREATE TABLE IF NOT EXISTS "+
            TABLE_WORKOUTS+"(" +
            KEY_IDWORKOUTS+" INTEGER PRIMARY KEY NOT NULL," +
            KEY_WORKOUTNAME+" TEXT, " +
            KEY_WORKOUTIMG+" varchar, " +
            KEY_SETS+" INTEGER, " +
            KEY_LEVEL+" TEXT, " +
            KEY_MAINMUSCLE+" TEXT, " +
            KEY_EXERCISES + " INTEGER REFERENCE"+ TABLE_EXERCISES_REP + ", " +
            KEY_LOCAL+" TEXT )";


    private static final String CREATE_TABLE_USER_WORKOUTS = "CREATE TABLE IF NOT EXISTS "+
            TABLE_USER_WORKOUTS+"(" +
            KEY_IDUSERWORKOUTS+" INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL," +
            KEY_USER_WORKOUTNAME+" TEXT, " +
            KEY_USER_WORKOUTIMG+" varchar, " +
            KEY_USER_SETS+" INTEGER, " +
            KEY_USER_LEVEL+" TEXT, " +
            KEY_USER_MAINMUSCLE+" TEXT, " +
            KEY_USERID+" INTEGER, "+
            KEY_EXERCISES + " INTEGER REFERENCE"+ TABLE_EXERCISES_REP+" )";

//            "FOREIGN KEY("+KEY_USERID+") REFERENCES "+TABLE_USERS+"("+KEY_IDUSER+")";

    @Override
    public void onCreate(SQLiteDatabase database) {
        database.execSQL(CREATE_TABLE_USERS);
        database.execSQL(CREATE_TABLE_PLAYLISTS);
        database.execSQL(CREATE_TABLE_WORKOUTS);
        database.execSQL(CREATE_TABLE_EXERCISE_REP);
        database.execSQL(CREATE_TABLE_EXERCISES);
        database.execSQL(CREATE_TABLE_USER_WORKOUTS);
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
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_USERS +", "+TABLE_PLAYLISTS+", "+TABLE_WORKOUTS+", "+TABLE_EXERCISES_REP+", "+TABLE_EXERCISES+", "+TABLE_USER_WORKOUTS);
        onCreate(db);
    }

    public void addUser(String name, String email, int active){
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

    public String[] getUserByEmail(String email){
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

    public boolean checkUser(String id){
        boolean check;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor row = db.rawQuery("SELECT * FROM "+TABLE_USERS+" WHERE "+KEY_EMAIL+" = "+id,null);

        if (row.moveToFirst()){
            check = true;
            Log.v(TAG,"checkUser, yes we that user in database");
        }
        else{
            Log.e(TAG,"checkUser, Database error:  user not found in database");
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
        String[] playlist = new String[4] ;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor row = db.rawQuery("SELECT * FROM "+TABLE_PLAYLISTS+" WHERE "+KEY_IDPLIST+" = "+id,null);
        Log.v("Row Count",String.valueOf(row.getCount()));
        if (row.moveToFirst()){
            row.moveToFirst();
            playlist[0] = String.valueOf(row.getInt(0));
            playlist[1] = row.getString(1);
            playlist[2] = row.getString(2);
            playlist[3] = row.getString(3);
        }
        else
            Log.i(TAG,"GET Playlist: No results");
        db.close();
        try {
            BD_backup();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return playlist;
    }

    public String[] getUserPlaylists(int id){
        String songName;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor row = db.rawQuery("SELECT * FROM "+TABLE_PLAYLISTS+" WHERE "+KEY_USERID+" = "+id,null);
        String[] playlists = null;
        if (row.moveToFirst()){
            row.moveToFirst();
            playlists = new String[row.getCount()];
            int i = 0;
            songName = row.getString(row.getColumnIndex(KEY_PNAME));
            playlists[i] = songName;
            while(row.moveToNext()){
                i++;
                songName = row.getString(row.getColumnIndex(KEY_PNAME));
                playlists[i] = songName;
            }
        }
        else
            Log.i(TAG,"User Playlists: No results");

        db.close();
        try {
            BD_backup();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return playlists;
    }

    public void insertExercises(int id, String name, String level, String typeExercise, String muscles, String audio, String Image){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(KEY_IDEXERCISE,id);
        cv.put(KEY_EXERCISENAME,name);
        cv.put(KEY_EXERCISELEVEL,level);
        cv.put(KEY_TYPE_EXERCISE,typeExercise);
        cv.put(KEY_MUSCLE,muscles);
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

   public Exercise getExercise(int exerciseId) {
        Exercise exercise = null;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor row = db.rawQuery("SELECT * FROM " + TABLE_EXERCISES + " WHERE " + KEY_IDEXERCISE + " = " + exerciseId, null);

        if (row.moveToFirst()) {
            exercise = new Exercise(row.getInt(0), row.getString(1), row.getString(2), convertStringToArray(row.getString(3)), Utilities.convertMusclesToString(row.getString(4)), row.getString(5), row.getString(6));
        } else {
            Log.i(TAG, "No exercise found");
        }
        db.close();
        try {
            BD_backup();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return exercise;
    }

   public Exercise[] getAllExercises(){
        Exercise[] exercises = null;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor row = db.rawQuery("SELECT * FROM "+TABLE_EXERCISES+"",null);
         int i = 0;
         if (row.moveToFirst()){
             exercises = new Exercise[row.getCount()];

             while(row.moveToNext()){
                 i++;
                 exercises[i] = new Exercise(row.getInt(0),row.getString(1),row.getString(2),convertStringToArray(row.getString(3)), Utilities.convertMusclesToString(row.getString(4)),row.getString(5),row.getString(6));
            }
        } else {
            Log.i(TAG,"No exercises found");
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
        boolean check;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor row = db.rawQuery("SELECT * FROM "+TABLE_EXERCISES+" WHERE "+KEY_IDEXERCISE+" = "+id,null);

        if (row.moveToFirst()){
            check = true;
            Log.v(TAG,"Yes we found that exercise in database");
        } else{
            Log.i(TAG,"Exercise not found in database");
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

    public boolean checkExerciseRep(int workoutId){
        boolean check;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor row = db.rawQuery("SELECT"+ KEY_EXERCISES + "FROM "+ TABLE_WORKOUTS+" WHERE "+KEY_IDWORKOUTS+" = "+workoutId,null);
        if (row.moveToFirst()){
            check = true;
        } else{
            Log.i(TAG,"check ExerciseRep: No exercise rep found");
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


    public void insertExerciseRep(int exerciseId, int repetitions,int seconds){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(KEY_EXERCISEREPID,exerciseId);
        cv.put(KEY_WORKOUTEX,exerciseId);
        cv.put(KEY_REPETITION,repetitions);
        cv.put(KEY_SECONDS,seconds);
        db.insert(TABLE_EXERCISES_REP,null,cv);
        db.close();
        try {
            BD_backup();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public ExerciseRep getExerciseRep(int exerciseId) {
        ExerciseRep exercise = null;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor row = db.rawQuery("SELECT * FROM " + TABLE_EXERCISES_REP + " WHERE " + KEY_EXERCISEREPID + " = " + exerciseId, null);
        if (row.moveToFirst()) {
            exercise = new ExerciseRep(getExercise(row.getInt(0)), row.getInt(1), row.getInt(2));
        } else {
            Log.i(TAG, "No exercise found");
        }
        db.close();
        try {
            BD_backup();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return exercise;
    }

    private ExerciseRep[] getExercises(String[] exercises_ids){
        ExerciseRep[] exercises = new ExerciseRep[exercises_ids.length];

        for (int a = 0;a<exercises_ids.length;a++){
            exercises[a] = getExerciseRep(Integer.parseInt(exercises_ids[a]));
        }

        return exercises;
    }

    public void insertLocalWorkout(int id, String name, String imgFile, int sets, String level, String mainMuscle, String exercises, String local){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(KEY_IDWORKOUTS,id);
        cv.put(KEY_WORKOUTNAME,name);
        cv.put(KEY_WORKOUTIMG,imgFile);
        cv.put(KEY_SETS,sets);
        cv.put(KEY_LEVEL,level);
        cv.put(KEY_MAINMUSCLE,mainMuscle);
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

   public Workout[] getLocalWorkouts(){
        Workout[] workouts = null;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor row = db.rawQuery("SELECT * FROM "+TABLE_WORKOUTS+" WHERE "+KEY_LOCAL+"='true'",null);
        int i = 0;
        if (row.moveToFirst()){
            row.moveToFirst();
            workouts = new Workout[row.getCount()];

            workouts[i] = new Workout(row.getInt(0),row.getString(1),row.getString(2),row.getInt(3),row.getString(4),row.getString(5), getExercises( convertStringToArray(row.getString(6))) );
            Log.v(TAG,"LocalWorkouts Database: "+ Arrays.toString(workouts));
            while(row.moveToNext()){
                i++;
                workouts[i] = new Workout(row.getInt(0),row.getString(1),row.getString(2),row.getInt(3),row.getString(4),row.getString(5), getExercises( convertStringToArray(row.getString(6))) );
                Log.v(TAG,"Local Workouts, Database: "+ Arrays.toString(workouts));
            }
        } else {
            Log.i(TAG,"GET Local workouts: No saved workouts found");
        }
        db.close();
        try {
            BD_backup();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return workouts;
    }

    public boolean checkLocalWorkouts(int workout_id){
        boolean check;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor row = db.rawQuery("SELECT * FROM "+ TABLE_WORKOUTS +" WHERE "+ KEY_IDWORKOUTS +" = "+workout_id,null);
        if (row.moveToFirst()){
            check = true;
        } else{
            Log.i(TAG,"Local workouts: No saved workouts found");
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




    public void updateWorkoutLocal(int workout_id, String local){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(KEY_LOCAL,local);
        db.update(TABLE_WORKOUTS,cv,"id="+workout_id,null);
        db.close();
        try {
            BD_backup();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }



    public void addUserWorkout(String name, String imgFile, int sets, String level, String mainMuscle, String exercises, int userid){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(KEY_WORKOUTNAME,name);
        cv.put(KEY_WORKOUTIMG,imgFile);
        cv.put(KEY_SETS,sets);
        cv.put(KEY_LEVEL,level);
        cv.put(KEY_MAINMUSCLE,mainMuscle);
        cv.put(KEY_EXERCISES,exercises);
        cv.put(KEY_USERID,userid);
        db.insert(TABLE_USER_WORKOUTS,null,cv);
        db.close();
        try {
            BD_backup();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    /*public Workout[] getUserWorkout(int workoutId){
        Workout[] workouts = null;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor row = db.rawQuery("SELECT * FROM "+TABLE_USER_WORKOUTS+" WHERE "+ KEY_IDUSERWORKOUTS + " = "+workoutId,null);
        int i = 0;
        if (row.moveToFirst()){
            row.moveToFirst();
            workouts = new Workout[row.getCount()];
            workouts[i] = new Workout(row.getInt(0),row.getString(1),row.getString(2),row.getInt(3),row.getString(4),row.getString(5),(row.getString(6)));
            while(row.moveToNext()){
                i++;
                workouts[i] = new Workout(row.getInt(0),row.getString(1),row.getString(2),row.getInt(3),row.getString(4),row.getString(5),(row.getString(6)));
                Log.v(TAG,"getLocalWorkouts, Database workouts: "+ Arrays.toString(workouts));
            }
        }
        else {
            Log.i(TAG,"getUserWorkouts, Database Error: No user workouts found");
        }
        db.close();
        try {
            BD_backup();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return workouts;
    }
*/

    public boolean checkUserWorkouts(int workout_id){
        boolean check;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor row = db.rawQuery("SELECT * FROM "+TABLE_USER_WORKOUTS+" WHERE "+KEY_IDWORKOUTS+" = "+workout_id,null);
        if (row.moveToFirst()){
            check = true;
        } else {
            Log.e(TAG,"checkLocalWorkouts, Database Error: No user workouts found");
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




    public int getWorkoutId(int usersid, String workout_name){
        int workoutId = 0;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor row = db.rawQuery("SELECT * FROM "+TABLE_WORKOUTS+" WHERE "+KEY_USERID+" = "+usersid+" AND "+KEY_WORKOUTNAME+"="+workout_name,null);
        if (row.moveToFirst()){
            row.moveToFirst();
            workoutId = row.getInt(row.getColumnIndex(KEY_IDWORKOUTS));
        }
        else
            Log.e(TAG,"getWorkoutId, Database Error: No workout");

        db.close();
        try {
            BD_backup();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return workoutId;
    }




    public void BD_backup() throws IOException {

        String URL = "/data/data/com.app.proj.silverbars/databases/";

        final String inFileName = URL+DATABASE_NAME;
        File dbFile = new File(inFileName);
        FileInputStream fis;

        fis = new FileInputStream(dbFile);

        String directorio = getUrlReady(myContext,"/Database");

        File d = new File(directorio);
        if (!d.exists()) {
            d.mkdir();
            Log.v(TAG,"base de datos creada");
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






}
