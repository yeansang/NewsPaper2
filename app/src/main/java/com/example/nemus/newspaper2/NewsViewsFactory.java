package com.example.nemus.newspaper2;

import android.appwidget.AppWidgetManager;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;

import org.json.JSONArray;
import org.json.JSONException;

/**
 * Created by nemus on 2016-07-05.
 */
public class NewsViewsFactory implements RemoteViewsService.RemoteViewsFactory {

    private static final String NEWS_URI = "content://com.example.nemus.newspaper2.myContentProvider/news";
    private static String[] items;
    private static String[] url;
    private Context ctxt=null;
    private int appWidgetId;

    public NewsViewsFactory(Context ctxt, Intent intent, String[] items) {
        this.ctxt=ctxt;
        appWidgetId=intent.getIntExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, AppWidgetManager.INVALID_APPWIDGET_ID);
        this.items = items;
    }

    @Override
    public void onCreate() {
        // no-op
    }

    @Override
    public void onDestroy() {
        // no-op
    }

    @Override
    public int getCount() {
        return(items.length-1);
    }

    @Override
    public RemoteViews getViewAt(int position) {
        RemoteViews row=new RemoteViews(ctxt.getPackageName(), R.layout.widget_list_row);

        row.setTextViewText(android.R.id.text1, items[position]);

        Intent i=new Intent();
        Bundle extras=new Bundle();

        extras.putString(NewAppWidget.EXTRA_WORD, items[position]);
        i.putExtras(extras);
        row.setOnClickFillInIntent(android.R.id.text1, i);

        return(row);
    }

    @Override
    public RemoteViews getLoadingView() {
        return(null);
    }

    @Override
    public int getViewTypeCount() {
        return(1);
    }

    @Override
    public long getItemId(int position) {
        return(position);
    }

    @Override
    public boolean hasStableIds() {
        return(true);
    }

    @Override
    public void onDataSetChanged() {
        ContentResolver cr = ctxt.getContentResolver();
        Cursor newsData = cr.query(Uri.parse(NEWS_URI),null,null,null,null);
        //addNewsToDB();
        if(newsData.moveToNext()) {
            items = new String[10];
            url = new String[10];
            int i = 0;
            while (newsData.moveToNext()){
                items[i] = newsData.getString(1);
                url[i++] = newsData.getString(2);
                if(i>10){
                    break;
                }
            }
        }else{
            items = new String[]{"No Data"};
        }
        newsData.close();
    }
}
