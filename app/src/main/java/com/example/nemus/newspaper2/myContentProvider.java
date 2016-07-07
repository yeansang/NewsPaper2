package com.example.nemus.newspaper2;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.support.annotation.Nullable;
import android.util.Log;

/**
 * Created by nemus on 2016-06-30.
 */
public class myContentProvider extends ContentProvider {
    DBConnect dbConnect;

    private static final UriMatcher sUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
    static{
        sUriMatcher.addURI("com.example.nemus.newspaper2.myContentProvider","fav",0);
        sUriMatcher.addURI("com.example.nemus.newspaper2.myContentProvider","rec",1);
        sUriMatcher.addURI("com.example.nemus.newspaper2.myContentProvider","news",2);
    }

    @Override
    public boolean onCreate() {
        dbConnect = new DBConnect(getContext(), "news.db",null,1);
        return false;
    }

    @Nullable
    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        SQLiteQueryBuilder queryBuilder = new SQLiteQueryBuilder();
        int tableNum = sUriMatcher.match(uri);
        //Log.d("db",sUriMatcher.);
        switch (tableNum){
            case 0:
                queryBuilder.setTables("fav");
                break;
            case 1:
                queryBuilder.setTables("rec");
                break;
            case 2:
                queryBuilder.setTables("news");
                break;
            default:
                throw new IllegalArgumentException("Unknown URI"+ uri);
        }

        SQLiteDatabase db = dbConnect.getWritableDatabase();
        Cursor cursor = queryBuilder.query(db, projection, selection, selectionArgs, null, null, sortOrder);

        cursor.setNotificationUri(getContext().getContentResolver(), uri);
        return cursor;
    }

    @Nullable
    @Override
    public String getType(Uri uri) {
        return null;
    }

    @Nullable
    @Override
    public Uri insert(Uri uri, ContentValues contentValues) {
        SQLiteDatabase db = dbConnect.getWritableDatabase();
        int tableNum = sUriMatcher.match(uri);
        long id =0;
        Cursor cursor =null;
        int maxnum = -1;
        switch (tableNum){
            case 0:
                cursor = db.rawQuery("SELECT * FROM FAV WHERE webTitle LIKE \""+contentValues.getAsString("webTitle")+"\"",null);
                if(cursor.moveToNext()){
                    db.execSQL("DELETE FROM FAV WHERE webTitle LIKE \""+contentValues.getAsString("webTitle")+"\"");
                    db.execSQL("UPDATE FAV SET pos=pos-1 WHERE pos>"+cursor.getString(3)+";");
                }
                cursor = db.rawQuery("SELECT MAX(pos) FROM FAV;",null);

                if(cursor.moveToNext()){
                    maxnum = cursor.getInt(0);
                }
                contentValues.put("pos",maxnum+1);
                id = db.insert("fav",null,contentValues);
                getContext().getContentResolver().notifyChange(uri,null);
                cursor.close();
                return Uri.parse("fav"+"/"+id);
            case 1:
                /*cursor = db.rawQuery("SELECT * FROM REC WHERE webTitle LIKE \""+contentValues.getAsString("webTitle")+"\"",null);
                if(cursor.moveToNext()){
                    db.execSQL("DELETE FROM REC WHERE webTitle LIKE \""+contentValues.getAsString("webTitle")+"\"");
                    db.execSQL("UPDATE REC SET pos=pos-1 WHERE pos>"+cursor.getString(3)+";");
                }
                */
                cursor = db.rawQuery("SELECT MAX(pos) FROM REC;",null);
                if(cursor.moveToNext()){
                    maxnum = cursor.getInt(0);
                    if(maxnum>=10){
                        db.execSQL("UPDATE REC SET pos=pos-1");
                        db.execSQL("DELETE FROM REC WHERE pos<0;");
                    }
                }
                contentValues.put("pos",maxnum+1);
                id = db.insert("rec",null,contentValues);
                getContext().getContentResolver().notifyChange(uri,null);
                cursor.close();
                return Uri.parse("rec"+"/"+id);
            case 2:
                //db.execSQL("DELETE FROM NEWS");
                id = db.insert("news",null,contentValues);
                Log.d("dbt",contentValues.getAsString("webTitle"));
                getContext().getContentResolver().notifyChange(uri,null);
                return Uri.parse("news"+"/"+id);
            default:
                throw new IllegalArgumentException("Unknown URI");
        }

    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        int uriType = sUriMatcher.match(uri);
        SQLiteDatabase db = dbConnect.getReadableDatabase();
        Cursor cr=null;
        int rowDelete =0;
        switch (uriType){
            case 0:
                cr = db.rawQuery("SELECT * FROM FAV WHERE "+selectionArgs[0]+" LIKE "+selection+"",null);
                if(!cr.moveToNext()){
                    break;
                }else{
                    db.execSQL("DELETE FROM FAV WHERE "+selectionArgs[0]+" LIKE "+selection+";");
                    db.execSQL("UPDATE FAV SET pos=pos-1 WHERE pos>"+cr.getString(3)+";");
                    break;
                }
            case 1:
                cr = db.rawQuery("SELECT * FROM REC WHERE "+selectionArgs[0]+" LIKE "+selection+"",null);
                if(!cr.moveToNext()){
                    break;
                }else{
                    db.execSQL("DELETE FROM REC WHERE "+selectionArgs[0]+" LIKE "+selection+";");
                    db.execSQL("UPDATE REC SET pos=pos-1 WHERE pos>"+cr.getString(3)+";");
                    break;
                }
            case 2:
                cr=db.rawQuery("SELECT * FROM REC",null);
                db.execSQL("DELETE FROM NEWS;");
                break;
            default:
                throw new IllegalArgumentException("Unknown URI: " + uri);
        }
        getContext().getContentResolver().notifyChange(uri, null);
        db.close();
        cr.close();
        return rowDelete;
    }

    @Override
    public int update(Uri uri, ContentValues contentValues, String s, String[] strings) {
        return 0;
    }
}
