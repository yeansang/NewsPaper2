package com.example.nemus.newspaper2;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by nemus on 2016-06-30.
 */
public class DBConnect extends SQLiteOpenHelper {

    final static String fav = "FAV";
    final static String rec = "REC";

    public DBConnect(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE FAV( _ID INTEGER PRIMARY KEY AUTOINCREMENT, webTitle TEXT, webUrl TEXT,pos INTEGER);");
        db.execSQL("CREATE TABLE REC( _ID INTEGER PRIMARY KEY AUTOINCREMENT, webTitle TEXT, webUrl TEXT,pos INTEGER);");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DELETE FROM FAV;");
        db.execSQL("DELETE FROM REC;");
    }

    public void dropTable(){
        SQLiteDatabase db = getWritableDatabase();
        db.execSQL("DELETE FROM FAV;");
        db.execSQL("DELETE FROM REC;");
    }

    public boolean input(String table, String title,String url, int pos){
        SQLiteDatabase db = getWritableDatabase();
        db.execSQL("INSERT INTO "+table+" (webTitle, webUrl, pos) VALUES (\""+title+"\",\""+url+"\","+pos+");");
        db.close();
        return true;
    }

    public boolean inputAll(String table, JSONArray js){
        SQLiteDatabase db = getWritableDatabase();
        for(int i=0;i<js.length();i++) {
            try {
                db.execSQL("INSERT INTO " + table + " (webTitle, webUrl, pos) VALUES (\"" + js.getJSONObject(i).getString("webTitle") + "\" ,\""+js.getJSONObject(i).getString("webUrl")+"\" ," + i + ");");
            }catch (Exception e){
                e.printStackTrace();
            }
        }
        db.close();
        return true;
    }

    public JSONArray getAll(String table){
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM "+table+" ORDER BY pos DESC",null);
        JSONArray out = new JSONArray();

        if(cursor.isAfterLast()){
            return null;
        }
        while(cursor.moveToNext()){
            try {
                JSONObject jo = new JSONObject("{\"webTitle\":\""+cursor.getString(1)+"\",\"webUrl\":\""+cursor.getString(2)+"\",\"pos\":"+cursor.getString(3)+"}");
                out.put(jo);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        cursor.close();
        db.close();
        return out;
    }

    public boolean remove(String table, int pos){
        SQLiteDatabase db = getWritableDatabase();
        db.execSQL("DELETE FROM "+table+" WHERE pos like "+pos+";");
        db.execSQL("UPDATE "+table+" SET pos=pos-1 WHERE pos>"+pos+";");
        db.close();
        return true;
    }

    public boolean removeOld(String table, String webTitle){
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM "+table+" WHERE webTitle LIKE \""+webTitle+"\"",null);
        if(!cursor.moveToNext()){
            return false;
        }else{
            db.execSQL("DELETE FROM "+table+" WHERE webTitle LIKE \""+webTitle+"\";");
            return true;
        }
    }

    public boolean removeAll(String table){
        SQLiteDatabase db = getWritableDatabase();
        db.execSQL("DELETE FROM "+table+";");
        db.close();
        return true;
    }

    public int getLastPos(String table){
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT MAX(pos) FROM "+table,null);
        cursor.moveToNext();
        if(cursor.isNull(0)) return -1;
        db.close();
        return cursor.getInt(0);
    }

}
