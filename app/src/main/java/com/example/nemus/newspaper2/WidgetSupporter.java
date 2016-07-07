package com.example.nemus.newspaper2;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;


/**
 * Created by nemus on 2016-07-07.
 */
public class WidgetSupporter extends Activity {

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
