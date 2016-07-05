package com.example.nemus.newspaper2;

import android.appwidget.AppWidgetManager;
import android.content.Context;
import android.content.Intent;
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

    private static JSONArray items;
    private Context context;
    private int appWidgetId;

    public NewsViewsFactory(Context context, Intent intent){
        try {
            items = new GetGuardianNews().get();
        }catch(Exception e){
            try {
                items.put(0,"{\"webTitle\": \"Data read error\",\"webUrl\":\"http://\"}");
            } catch (JSONException e1) {
                e1.printStackTrace();
                Log.d("epik ","fail");
            }
            e.printStackTrace();
        }
        this.context = context;
        appWidgetId = intent.getIntExtra(AppWidgetManager.EXTRA_APPWIDGET_ID,AppWidgetManager.INVALID_APPWIDGET_ID);
    }

    @Override
    public void onCreate() {

    }

    @Override
    public void onDataSetChanged() {

    }

    @Override
    public void onDestroy() {

    }

    @Override
    public int getCount() {
        return items.length();
    }

    @Override
    public RemoteViews getViewAt(int pos) {
        RemoteViews row = new RemoteViews(context.getPackageName(), R.layout.widget_list_row);
        String title="";
        try{
            title = items.getJSONObject(pos).getString("webTitle");
        }catch (JSONException e){
            e.printStackTrace();
        }
        row.setTextViewText(android.R.id.text1, title);

        Intent i = new Intent();
        Bundle extras = new Bundle();

        extras.putString(NewAppWidget.TITLE,title);
        i.putExtras(extras);
        row.setOnClickFillInIntent(android.R.id.text1, i);

        return row;
    }

    @Override
    public RemoteViews getLoadingView() {
        return null;
    }

    @Override
    public int getViewTypeCount() {
        return 1;
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public boolean hasStableIds() {
        return true;
    }
}
