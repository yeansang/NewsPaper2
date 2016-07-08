package com.example.nemus.newspaper2;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.util.Log;


/**
 * Created by nemus on 2016-07-07.
 */
public class WidgetSupporter extends Activity {


    public WidgetSupporter(){
    }

    @Override
    public void onCreate(Bundle state){
        super.onCreate(state);

        String url = getIntent().getStringExtra(NewAppWidget.EXTRA_WORD);
        Log.d("widget",url);
        Intent i = new Intent(Intent.ACTION_VIEW);
        Uri u = Uri.parse(url);
        i.setData(u);
        startActivity(i);
        finish();
    }
}
