package com.example.nemus.newspaper2;

import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;
import android.widget.RemoteViews;


/**
 * Implementation of App Widget functionality.
 */
public class NewAppWidget extends AppWidgetProvider {

    public static final String EXTRA_WORD = "com.example.nemus.newspaper2.WORD";


    @Override
    public void onUpdate(Context ctxt, AppWidgetManager appWidgetManager, int[] appWidgetIds) {

        for(int i=0; i<appWidgetIds.length; i++) {
            Log.d("widget", "update");
            Intent svcIntent = new Intent(ctxt, com.example.nemus.newspaper2.WidgetService.class);

            svcIntent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID,appWidgetIds[i]);
            svcIntent.setData(Uri.parse(svcIntent.toUri(Intent.URI_INTENT_SCHEME)));

            RemoteViews widget = new RemoteViews(ctxt.getPackageName(), R.layout.new_app_widget);

            widget.setRemoteAdapter(R.id.words,svcIntent);
            appWidgetManager.updateAppWidget(appWidgetIds[i], widget);
        }
        Log.d("widget","updated");


        super.onUpdate(ctxt, appWidgetManager, appWidgetIds);
    }

    @Override
    public void onEnabled(Context context) {
        // Enter relevant functionality for when the first widget is created
        super.onEnabled(context);
    }

    @Override
    public void onDisabled(Context context) {
        // Enter relevant functionality for when the last widget is disabled
        super.onDisabled(context);
    }
}

