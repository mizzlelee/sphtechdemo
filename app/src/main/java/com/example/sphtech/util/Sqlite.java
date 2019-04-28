package com.example.sphtech.util;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class Sqlite extends SQLiteOpenHelper {
    private static final String DATABASE_CREATE  = "create table quarterdata (volume_of_mobile_data text, year text, quarter text, id text);";
    public Sqlite (Context context) {
        super(context, "SPHTech", null, 3);
    }

    @Override
    public void onCreate (SQLiteDatabase db) {
        db.execSQL(DATABASE_CREATE);
    }

    @Override
    public void onUpgrade (SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS quarterdata");
        onCreate(db);
    }

    public boolean insertdata (String vloume,String year,String quarter,Integer id) {
        SQLiteDatabase db = this.getWritableDatabase();
        if(!isDataExist()) {
            try {
                ContentValues insertValues = new ContentValues();
                insertValues.put("volume_of_mobile_data", vloume);
                insertValues.put("year", year);
                insertValues.put("quarter", quarter);
                insertValues.put("id", id);
                if (db.insert("quarterdata", null, insertValues) != -1) {
                    Log.e("sqlite", "insert success");
                    return true;
                } else {
                    Log.e("sqlite", "insert fail");
                    return false;
                }
            } catch (Exception e) {
                Log.e("sqlite", "Exception: " + e.getLocalizedMessage());
                e.printStackTrace();
                return false;
            } finally {
                db.close();
            }
        }else{
            return false;
        }
    }

    public List<Map<String, String>> getdata () {
        List<Map<String, String>> yearData = new ArrayList<Map<String, String>>();
        SQLiteDatabase db = this.getReadableDatabase();
        try {
            //Cursor cursor = db.query("quarterdata", new String[]{"year", "quarter", "volume_of_mobile_data"}, null, null, null, null, null);
            Cursor cursor = db.rawQuery("SELECT year,SUM(volume_of_mobile_data) FROM quarterdata GROUP BY year",null);
            cursor.moveToFirst();
            do {
                Map<String, String> yeardata = new HashMap<String, String>();
                String aaa = cursor.getString(cursor.getColumnIndex("year"));
                String ccc = cursor.getString(cursor.getColumnIndex("SUM(volume_of_mobile_data)"));
                Log.e("sqlite","get: " + aaa);
                Log.e("sqlite","get3: " + ccc);
                yeardata.put("year",aaa);
                yeardata.put("value",ccc);
                yearData.add(yeardata);
            }
            while (cursor.moveToNext());
            cursor.close();
        } catch (Exception e) {
            // Do nothing
        }finally {
            db.close();
        }
        return yearData;
    }

    public boolean isDataExist (){
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query("quarterdata", new String[]{"id"}, "id=?", new String[]{"55"}, null, null, null);
        boolean result = (cursor.getCount() > 0);
        cursor.close();
        return result;
    }
}
