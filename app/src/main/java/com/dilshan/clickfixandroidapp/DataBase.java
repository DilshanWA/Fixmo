package com.dilshan.clickfixandroidapp;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

public class DataBase extends SQLiteOpenHelper {

    public static final String DATABASE_NAME = "clickfix.db";
    public static final String TABLE_NAME = "user_accounts";
    public static final String COL_1 = "ID";
    public static final String COL_2 = "Name";
    public static final String COL_3 = "Email";
    public static  final String COL_4 = "Password";
    public static  final String COL_5 = "Mobile";
    public DataBase(@Nullable Context context) {
        super(context, DATABASE_NAME, null, 1);
        SQLiteDatabase db  = this.getWritableDatabase();
    }


    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("create table " + TABLE_NAME
                + "(ID INTEGER PRIMARY KEY AUTOINCREMENT, "+" Name String, Email TEXT," +
                "Password String,Mobile INTIGER )" );

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(db);
    }

    public boolean insertData(String Name, String Email, String Password, String Mobile){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COL_2,Name);
        contentValues.put(COL_3,Email);
        contentValues.put(COL_4,Password);
        contentValues.put(COL_5,Mobile);
        long result = db.insert(TABLE_NAME,null,contentValues);
        if (result ==-1)
            return false;
        else
            return true;
    }

    public boolean validateUser(String email, String password) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " +
                TABLE_NAME + " WHERE Email=? AND Password=?", new String[]{email, password});
        boolean isValid = cursor.getCount() > 0;
        cursor.close();
        return isValid;
    }


}
