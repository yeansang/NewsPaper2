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
        switch (tableNum){
            case 0:
                id = db.insert("fav",null,contentValues);
                getContext().getContentResolver().notifyChange(uri,null);
                return Uri.parse("fav"+"/"+id);
            case 1:
                id = db.insert("rec",null, contentValues);
                getContext().getContentResolver().notifyChange(uri,null);
                return Uri.parse("rec"+"/"+id);
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
