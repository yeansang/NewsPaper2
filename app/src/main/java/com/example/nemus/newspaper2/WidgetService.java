package com.example.nemus.newspaper2;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.util.Log;
import android.widget.RemoteViewsService;

import org.json.JSONArray;
import org.json.JSONException;

/**
 * Created by nemus on 2016-07-05.
 */
public class WidgetService extends RemoteViewsService {

    private static final String NEWS_URI = "content://com.example.nemus.newspaper2.myContentProvider/news";



    @Override
    public RemoteViewsFactory onGetViewFactory(Intent intent){
        ContentResolver cr = getContentResolver();
        Cursor newsData = cr.query(Uri.parse(NEWS_URI),null,null,null,null);
        //addNewsToDB();
        String[] items;
        String[] urls;
        if(newsData.moveToNext()) {
            items = new String[10];
            urls = new String[10];
            int i = 0;
            while (newsData.moveToNext()){
                items[i] = newsData.getString(1);
                urls[i++] = newsData.getString(2);
                if(i>9){
                    break;
                }
            }
        }else{
            items = new String[]{"No Data"};
        }
        newsData.close();
        Log.d("widget","call");
        NewsViewsFactory out = new NewsViewsFactory(this.getApplicationContext(), intent, items);
        Log.d("widget",out.toString());
        return out;
    }
}
