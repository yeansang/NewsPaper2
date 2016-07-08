package com.example.nemus.newspaper2;

import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.util.Log;
import android.widget.RemoteViewsService;

import java.util.ArrayList;

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
        ArrayList<String> items=new ArrayList<String>();
        ArrayList<String> urls=new ArrayList<String>();

        if(newsData.moveToNext()) {
            int i = 0;
            while (newsData.moveToNext()){
                items.add(i,newsData.getString(1));
                urls.add(i++,newsData.getString(2));
                if(i>10){
                    break;
                }
            }
        }else{
            items.add(0,"No data");
        }
        newsData.close();
        Log.d("widget","call");
        NewsViewsFactory out = new NewsViewsFactory(this.getApplicationContext(), intent, items, urls);
        Log.d("widget",out.toString());
        return out;
    }

}
