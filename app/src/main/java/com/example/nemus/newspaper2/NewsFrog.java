package com.example.nemus.newspaper2;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.PopupMenu;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;

/**
 * Created by nemus on 2016-07-01.
 */
public class NewsFrog extends Fragment{

    ListView screen;
    private ListAdapter adapter=null;
    JSONArray newsArray =null;
    ArrayList<String> saveWord = new ArrayList<String>();

    private static final Uri REC_URI = Uri.parse("content://com.example.nemus.newspaper2.myContentProvider/rec");
    private static final Uri FAV_URI = Uri.parse("content://com.example.nemus.newspaper2.myContentProvider/fav");

    public NewsFrog() {
    }

    public static NewsFrog newInstance() {
        NewsFrog fragment = new NewsFrog();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);

        try {
            newsArray = new GetGuardianNews().execute().get();

        } catch (Exception e) {
            e.printStackTrace();
        }
        if(newsArray!=null){
            for(int i=0;i<newsArray.length();i++){
                try {
                    saveWord.add(newsArray.getJSONObject(i).getString("webTitle"));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }else{
            saveWord.add("Fail News read");
        }

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_news, container, false);
        //final DBConnect dbConnect = new DBConnect(getActivity(), "news.db",null,1);
        screen = (ListView) rootView.findViewById(R.id.news_listView);

        /*ArrayList<String> saveWord = new ArrayList<String>();
        Log.d("tag", "news create");

        JSONArray newsArray =null;
        try {
            newsArray = new GetGuardianNews().execute().get();
        } catch (Exception e) {
            e.printStackTrace();
        }
        if(newsArray!=null){
            for(int i=0;i<newsArray.length();i++){
                try {
                    saveWord.add(newsArray.getJSONObject(i).getString("webTitle"));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }else{
            saveWord.add("Fail News read");
        }*/

        adapter= new ArrayAdapter<String>(getActivity(), android.R.layout.simple_expandable_list_item_1,saveWord);
        screen.setAdapter(adapter);

        final JSONArray urlCatch = newsArray;

        screen.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Toast toast = null;
                ContentResolver cr = getActivity().getContentResolver();
                ContentValues cv = new ContentValues();

                try {
                    Intent i = new Intent(Intent.ACTION_VIEW);
                    Uri u = Uri.parse(urlCatch.getJSONObject(position).getString("webUrl"));
                    i.setData(u);
                    startActivity(i);
                    toast = Toast.makeText(getActivity(),urlCatch.getJSONObject(position).getString("webUrl"), Toast.LENGTH_LONG);
                    /*cs = cr.query(REC_URI , null , urlCatch.getJSONObject(position).getString("webTitle") , new String[]{"webTitle"} ,null);
                    if(cs.moveToNext()){
                        cr.delete(REC_URI,urlCatch.getJSONObject(position).getString("webTitle"),new String[]{"webTitle"});
                    }*/
                    cv.put("webTitle",urlCatch.getJSONObject(position).getString("webTitle"));
                    cv.put("webUrl",urlCatch.getJSONObject(position).getString("webUrl"));

                } catch (JSONException e) {
                    e.printStackTrace();
                }
                toast.setGravity(Gravity.CENTER, 0, 0);
                toast.show();

                cr.insert(REC_URI,cv);
                cv.clear();

                //Cursor cs = cr.query(REC_URI,null,null,null,null);

            }
        });


        screen.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                PopupMenu pop = new PopupMenu(parent.getContext(), view);
                pop.getMenuInflater().inflate(R.menu.fav_menu_pop,pop.getMenu());

                final int index = position;
                //팝업메뉴 리스너 설정
                pop.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        //
                        ContentResolver cr = getActivity().getContentResolver();
                        ContentValues cv = new ContentValues();
                        try {
                            cv.put("webTitle",urlCatch.getJSONObject(index).getString("webTitle"));
                            cv.put("webUrl",urlCatch.getJSONObject(index).getString("webUrl"));
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        cr.insert(FAV_URI,cv);
                        cv.clear();
                        return false;
                    }
                });
                pop.show();
                return true;
            }
        });

        return rootView;
    }

    @Override
    public void onPause(){
        super.onPause();

    }
}
