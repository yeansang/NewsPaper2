package com.example.nemus.newspaper2;

import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by nemus on 2016-06-30.
 */
public class GetGuardianNews extends AsyncTask<Void, Void, JSONArray> {

    private final String apiKey = "&api-key=7ef3d39f-2a09-483a-876d-6d9f39720195";
    private final String url = "http://content.guardianapis.com/search?";
    private String msg = "q=debate&order-by=newest";

    public void makeMsg(String msg){
        this.msg = msg;
    }

    public String[] getNewsByStringArray(){
        String[] out = new String[25];
        try {
            JSONArray jsa = get();
            for(int i=0;i<jsa.length();i++) {
                out[i] = jsa.getJSONObject(i).getString("webTitle");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return out;
    }

    public String getNews(){
        try {
            URL address = new URL(url+msg+apiKey);
            HttpURLConnection conn = (HttpURLConnection) address.openConnection();

            conn.setConnectTimeout(10000);

            int rescode = conn.getResponseCode();

            if(rescode == HttpURLConnection.HTTP_OK) {
                InputStream in = conn.getInputStream();
                BufferedReader bufreader = new BufferedReader(new InputStreamReader(in,"utf-8"));
                String recive="";
                while((recive = bufreader.readLine())!=null){ }

                return null;
            }else{
                return null;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    protected JSONArray doInBackground(Void... params) {
        try {

                URL address = new URL(url + msg + apiKey);
                HttpURLConnection conn = (HttpURLConnection) address.openConnection();

                conn.setConnectTimeout(10000);

                int rescode = conn.getResponseCode();

                if (rescode == HttpURLConnection.HTTP_OK) {
                    InputStream in = conn.getInputStream();
                    InputStreamReader inReader = new InputStreamReader(in);
                    BufferedReader bufreader = new BufferedReader(new InputStreamReader(in, "utf-8"));
                    String recive = "";
                    String save = "";
                    while ((recive = bufreader.readLine()) != null) {
                        save += recive;
                        //Log.d("recive",recive);
                    }
                    Log.d("save", save);

                    JSONArray ja = null;

                    try {
                        JSONObject js = new JSONObject(save);
                        ja = js.getJSONObject("response").getJSONArray("results");
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    return ja;
                } else {
                    return null;
                }

        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}
