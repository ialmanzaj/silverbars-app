package com.example.project.calisthenic;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by andre_000 on 5/18/2016.
 */

public class AdminSQLiteOpenHelper extends SQLiteOpenHelper {

    public AdminSQLiteOpenHelper(Context context, String nombre, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, nombre, factory, version);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("create table users(id integer primary key autoincrement, name text, email integer primary key, active integer)");
        db.execSQL("create table playlists(id integer primary key autoincrement, name text, position integer, user_id integer, FOREIGN KEY(user_id) REFERENCES users(id))");

    }


    @Override
    public void onUpgrade(SQLiteDatabase db, int versionAnte, int versionNue) {
        db.execSQL("drop table if exists users");
        db.execSQL("create table users(id integer primary key autoincrement, name text, lastname text ,email integer primary key)");
        db.execSQL("drop table if exists playlists");
        db.execSQL("create table playlists(id integer primary key autoincrement, name text, position integer, user_id integer, FOREIGN KEY(user_id) REFERENCES users(id))");

    }

}
