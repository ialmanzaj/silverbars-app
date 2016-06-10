package com.example.project.calisthenic;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import android.widget.Toast;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.DuplicateFormatFlagsException;

/**
 * Created by andre_000 on 5/18/2016.
 */

public class MySQLiteHelper extends SQLiteOpenHelper {

    // Database Version
    public static final int DATABASE_VERSION = 1;
    // Database Name
    public static final String DATABASE_NAME = "SilverbarsData";
    //     Database tables name
    public static final String TABLE_USERS = "users";
    public static final String TABLE_PLAYLISTS = "playlists";
    public static final String TABLE_WORKOUTS = "workouts";
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
    public static final String KEY_IDWORKOUT = "id";
    //    Exercises table Column names
    public static final String KEY_IDEXERCISE = "id";

    public MySQLiteHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    private static final String CREATE_TABLE_USERS = "CREATE TABLE "+
            TABLE_USERS+"(" +
            KEY_IDUSER+"INTEGER PRIMARY KEY," +
            KEY_NAME+" TEXT, " +
            KEY_EMAIL+" varchar, " +
            KEY_ACTIVE+" integer)";
    private static final String CREATE_TABLE_PLAYLISTS = "CREATE TABLE "+
            TABLE_PLAYLISTS+"(" +
            KEY_IDPLIST+" INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL," +
            KEY_PNAME+" TEXT, " +
            KEY_SONGNAME+" varchar, " +
            KEY_USERID+" integer)";
//            "FOREIGN KEY("+KEY_USERID+") REFERENCES "+TABLE_USERS+"("+KEY_IDUSER+")";

    @Override
    public void onCreate(SQLiteDatabase database) {
        database.execSQL(CREATE_TABLE_USERS);
        database.execSQL(CREATE_TABLE_PLAYLISTS);
    }


    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion){
        Log.w(MySQLiteHelper.class.getName(),
                "Upgrading database from version " + oldVersion + " to "
                        + newVersion + ", which will destroy all old data");
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_USERS +" AND "+TABLE_PLAYLISTS);
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
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor row = db.rawQuery("SELECT * FROM "+TABLE_PLAYLISTS+" WHERE "+KEY_IDPLIST+" = "+id,null);
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
        return results;
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

}
