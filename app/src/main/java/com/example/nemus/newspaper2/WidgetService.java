package com.example.nemus.newspaper2;

import android.content.Intent;
import android.util.Log;
import android.widget.RemoteViewsService;

/**
 * Created by nemus on 2016-07-05.
 */
public class WidgetService extends RemoteViewsService {
    @Override
    public RemoteViewsFactory onGetViewFactory(Intent intent){
        Log.d("widget","call");
        return new NewsViewsFactory(this.getApplicationContext(),intent);
    }
}
