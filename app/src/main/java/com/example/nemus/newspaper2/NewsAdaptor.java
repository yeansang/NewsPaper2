package com.example.nemus.newspaper2;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by nemus on 2016-07-04.
 */
public class NewsAdaptor extends ArrayAdapter<JSONObject> {

    private ArrayList<JSONObject> item;

    public NewsAdaptor(Context context, int resource, ArrayList<JSONObject> objects) {
        super(context, resource, objects);
        this.item = objects;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent){
        View out = convertView;
        if(out == null){
            LayoutInflater vi = (LayoutInflater)getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            out = vi.inflate(R.layout.list_row, null);
        }
        JSONObject j = item.get(position);
        if(j!=null) {
            TextView top = (TextView) out.findViewById(R.id.toptext);
            TextView bott = (TextView) out.findViewById(R.id.bottomtext);
            try {
                if(top!=null) {
                    top.setText(j.getString("webTitle"));
                }
                if(bott!=null) {
                    bott.setText(j.getString("webUrl"));
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return out;
    }
}
