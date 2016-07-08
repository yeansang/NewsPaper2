package com.example.nemus.newspaper2;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Handler;
import android.os.HandlerThread;
import android.util.Log;
import android.widget.RemoteViews;


/**
 * Implementation of App Widget functionality.
 */
public class NewAppWidget extends AppWidgetProvider {

    public static final String EXTRA_WORD = "com.example.nemus.newspaper2.WORD";
    public static final String ACTION_NEWS = "com.example.nemus.newspaper2.ACTION_NEWS";
    public static final String ACTION_FAV = "com.example.nemus.newspaper2.ACTION_FAV";
    public static final String ACTION_REC = "com.example.nemus.newspaper2.ACTION_REC";


    private static HandlerThread sWorkerThread;
    private static Handler sWorkerQueue;
    private static final Uri WIDGET_URI = Uri.parse("content://com.example.nemus.newspaper2.myContentProvider/widget");

    public NewAppWidget() {
        sWorkerThread = new HandlerThread("WeatherWidgetProvider-worker");
        sWorkerThread.start();
        sWorkerQueue = new Handler(sWorkerThread.getLooper());
    }

    @Override
    public void onReceive(Context ctx, Intent intent) {
        final String action = intent.getAction();
        final Context c = ctx;
        Log.d("widget", action);

        final AppWidgetManager mgr = AppWidgetManager.getInstance(c);
        final ComponentName cn = new ComponentName(c, NewAppWidget.class);

        if (NewAppWidget.ACTION_NEWS.equals(action)) {
            sWorkerQueue.removeMessages(0);
            sWorkerQueue.post(new Runnable() {
                @Override
                public void run() {
                    Log.d("widget","news click");
                    ContentResolver cr = c.getContentResolver();
                    ContentValues status =new ContentValues();
                    status.put("widgetStatus","news");
                    cr.insert(WIDGET_URI, status);
                    mgr.notifyAppWidgetViewDataChanged(mgr.getAppWidgetIds(cn), R.id.words);
                }
            });
        }else if(NewAppWidget.ACTION_FAV.equals(action)){
            sWorkerQueue.removeMessages(0);
            sWorkerQueue.post(new Runnable() {
                @Override
                public void run() {
                    Log.d("widget","fav click");
                    ContentResolver cr = c.getContentResolver();
                    ContentValues status =new ContentValues();
                    status.put("widgetStatus","fav");
                    cr.insert(WIDGET_URI, status);
                    mgr.notifyAppWidgetViewDataChanged(mgr.getAppWidgetIds(cn), R.id.words);
                }
            });
        }else if(NewAppWidget.ACTION_REC.equals(action)){
            sWorkerQueue.removeMessages(0);
            sWorkerQueue.post(new Runnable() {
                @Override
                public void run() {
                    Log.d("widget","rec click");
                    ContentResolver cr = c.getContentResolver();
                    ContentValues status =new ContentValues();
                    status.put("widgetStatus","rec");
                    cr.insert(WIDGET_URI, status);
                    mgr.notifyAppWidgetViewDataChanged(mgr.getAppWidgetIds(cn), R.id.words);
                }
            });
        }
        super.onReceive(ctx, intent);
    }

    @Override
    public void onUpdate(Context ctxt, AppWidgetManager appWidgetManager, int[] appWidgetIds) {

        for(int i=0; i<appWidgetIds.length; i++) {
            Log.d("widget", "update");
            Intent svcIntent = new Intent(ctxt, com.example.nemus.newspaper2.WidgetService.class);

            svcIntent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID,appWidgetIds[i]);
            svcIntent.setData(Uri.parse(svcIntent.toUri(Intent.URI_INTENT_SCHEME)));

            RemoteViews widget = new RemoteViews(ctxt.getPackageName(), R.layout.new_app_widget);

            widget.setRemoteAdapter(R.id.words,svcIntent);

            final Intent newsClickIntent = new Intent(ctxt, NewAppWidget.class);
            newsClickIntent.setAction(ACTION_NEWS);
            final PendingIntent refreshPendingIntent = PendingIntent.getBroadcast(ctxt, 0,
                    newsClickIntent, PendingIntent.FLAG_UPDATE_CURRENT);
            widget.setOnClickPendingIntent(R.id.news_button, refreshPendingIntent);

            final Intent favClickIntent = new Intent(ctxt, NewAppWidget.class);
            favClickIntent.setAction(ACTION_FAV);
            final PendingIntent favPendingIntent = PendingIntent.getBroadcast(ctxt, 0,
                    favClickIntent, PendingIntent.FLAG_UPDATE_CURRENT);
            widget.setOnClickPendingIntent(R.id.fav_button, favPendingIntent);

            final Intent recClickIntent = new Intent(ctxt, NewAppWidget.class);
            recClickIntent.setAction(ACTION_REC);
            final PendingIntent recPendingIntent = PendingIntent.getBroadcast(ctxt, 0,
                    recClickIntent, PendingIntent.FLAG_UPDATE_CURRENT);
            widget.setOnClickPendingIntent(R.id.rec_button, recPendingIntent);

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

