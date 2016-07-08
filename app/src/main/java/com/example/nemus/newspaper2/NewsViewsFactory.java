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

import java.util.ArrayList;

/**
 * Created by nemus on 2016-07-05.
 */
public class NewsViewsFactory implements RemoteViewsService.RemoteViewsFactory {

    private static final String WIDGET_URI = "content://com.example.nemus.newspaper2.myContentProvider/widget";
    private static ArrayList<String> items;
    private static ArrayList<String> url;
    private Context ctxt=null;
    private int appWidgetId;
    private  Intent intent;

    public NewsViewsFactory(Context ctxt, Intent intent, ArrayList<String> items,ArrayList<String> url) {
        this.intent = intent;
        this.ctxt=ctxt;
        appWidgetId=intent.getIntExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, AppWidgetManager.INVALID_APPWIDGET_ID);
        this.items = items;
        this.url = url;
    }

    @Override
    public void onCreate() {


    }

    @Override
    public void onDestroy() {
        // no-op
    }

    @Override
    public int getCount() {
        return(items.size());
    }

    @Override
    public RemoteViews getViewAt(int position) {
        RemoteViews row=new RemoteViews(ctxt.getPackageName(), R.layout.widget_list_row);

        row.setTextViewText(android.R.id.text1, items.get(position));

        Intent i=new Intent();
        Bundle extras=new Bundle();

        extras.putString(NewAppWidget.EXTRA_WORD, items.get(position));
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
        //ctxt.grantUriPermission("com.example.nemus.newspaper2",Uri.parse(WIDGET_URI),Intent.FLAG_GRANT_READ_URI_PERMISSION);
        ContentResolver cr = ctxt.getContentResolver();
        Cursor newsData = cr.query(Uri.parse(WIDGET_URI),null,null,null,null);
        //addNewsToDB();
        items.clear();
        url.clear();
        if(newsData.moveToNext()) {
            int i = 1;
            do {
                items.add(newsData.getString(1));
                url.add(newsData.getString(2));
                i++;
                if (i > 10) {
                    break;
                }
            } while (newsData.moveToNext());
        }else{
            items.add(0,"No data");
        }
        newsData.close();
    }
}
