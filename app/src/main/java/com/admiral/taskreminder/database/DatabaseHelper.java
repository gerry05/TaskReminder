package com.admiral.taskreminder.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHelper extends SQLiteOpenHelper {
    public static final String DATABASE_NAME = "tasks.db";
    public static final String TABLE_NAME = "tasks_table";
    public static final String id_col = "ID";
    public static final String description_col = "DESCRIPTION";
    public static final String lat_col = "destination_lat";
    public static final String lon_col = "destination_lon";
    public static final String start_date_col = "start_date";
    public static final String end_date_col = "end_date";
    public static final String timestamp_col = "timestamp";

    public DatabaseHelper( Context context) {
        super(context, DATABASE_NAME, null ,1);

    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("create table " + TABLE_NAME + "(ID INTEGER PRIMARY KEY AUTOINCREMENT,DESCRIPTION TEXT,destination_lat DOUBLE,destination_lon DOUBLE,start_date INTEGER,end_date INTEGER,timestamp INTEGER)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(db);
    }

    public boolean insertData(String description, double lat,double lon,long start_date,long end_date,int timestamp){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(description_col,description);
        contentValues.put(timestamp_col,timestamp);
        contentValues.put(lat_col,lat);
        contentValues.put(lon_col,lon);
        contentValues.put(start_date_col,start_date);
        contentValues.put(end_date_col,end_date);
        long result = db.insert(TABLE_NAME,null,contentValues);
        if(result == -1)
            return false;
        else
            return true;
    }
    public boolean updateRecord(String id,String notes,double lat,double lon,long start_date,long end_date,int timestamp){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(description_col,notes);
        contentValues.put(timestamp_col,timestamp);
        contentValues.put(lat_col,lat);
        contentValues.put(lon_col,lon);
        contentValues.put(start_date_col,start_date);
        contentValues.put(end_date_col,end_date);
        db.update(TABLE_NAME,contentValues,"id ="+id,null);
        return true;
    }
    public boolean deleteRecord(int id){
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_NAME, id_col + "=" + id,null);
        return true;
    }

    public Cursor getAllData(){
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor res = db.rawQuery("select * from "+ TABLE_NAME + " ORDER BY " + id_col + " DESC",null);
        return res;
    }
    public Cursor getLastRecord(){
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor res = db.rawQuery("select * from "+ TABLE_NAME + " ORDER BY " + id_col + " DESC LIMIT 1",null);
        return res;
    }



}
