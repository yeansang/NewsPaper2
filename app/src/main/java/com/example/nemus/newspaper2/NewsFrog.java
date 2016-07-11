package com.example.nemus.newspaper2;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v7.widget.PopupMenu;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by nemus on 2016-07-01.
 */
public class NewsFrog extends Fragment{

    static ListView screen;
    private static NewsAdaptor adapter=null;
    static JSONArray newsArray =null;
    static ArrayList<JSONObject> saveWord = new ArrayList<JSONObject>();
    private static ContentResolver cr = null;
    public static boolean manualRefresh = false;

    private static SharedPreferences sharedPreferences;

    static Handler handler = new Handler();
    static Runnable timedTask = new Runnable(){
        @Override
        public void run() {
            handler.postDelayed(timedTask, delaySec*1000);

            if(manualRefresh||(time+(delaySec*1000) < System.currentTimeMillis())) {
                refresh();
                Log.d("refresh","time refreshed");
                manualRefresh = false;
            }
            Log.d("refreshTime", time+"");
        }};


    private static long time = 0;
    private static int delaySec = 60;

    private static final Uri REC_URI = Uri.parse("content://com.example.nemus.newspaper2.myContentProvider/rec");
    private static final Uri FAV_URI = Uri.parse("content://com.example.nemus.newspaper2.myContentProvider/fav");
    private static final Uri NEWS_URI = Uri.parse("content://com.example.nemus.newspaper2.myContentProvider/news");

    public NewsFrog() {
    }

    public static NewsFrog newInstance() {
        NewsFrog fragment = new NewsFrog();
        return fragment;
    }

    public static void listRefresh(){
        adapter.clear();
        saveWord.clear();
        //cr = getActivity().getContentResolver();
        //db에서 불러와서 새로 고침
        Cursor wordData = cr.query(NEWS_URI,null,null,null,null);
        try {
            while (wordData.moveToNext()) {
                saveWord.add(new JSONObject("{\"webTitle\":\""+wordData.getString(1)+"\",\"webUrl\":\""+wordData.getString(2)+"\"}"));
            }
            wordData.close();
        }catch (Exception e){
            e.printStackTrace();
        }
        screen.setAdapter(adapter);

    }

    public static void refresh(){
        ContentValues cv = new ContentValues();

        SharedPreferences.Editor editor = sharedPreferences.edit();

        cr.delete(NEWS_URI,null,null);
        try {
            newsArray = new GetGuardianNews().execute().get();
        } catch (Exception e) {
            e.printStackTrace();
        }
        if(newsArray!=null){
            for(int i=0;i<newsArray.length();i++){
                try {
                    JSONObject in = newsArray.getJSONObject(i);
                    cv.put("webTitle",in.getString("webTitle"));
                    cv.put("webUrl",in.getString("webUrl"));
                    cv.put("pos",i);
                    cr.insert(NEWS_URI,cv);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }

        editor.putLong("PREFERENCE_TIME",System.currentTimeMillis());
        editor.apply();
        editor.clear();
        listRefresh();
        Log.d("refresh","ok");
    }

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        cr = getActivity().getContentResolver();
        sharedPreferences = getActivity().getSharedPreferences(getString(R.string.preference_file_key),Context.MODE_PRIVATE);
        long defaulttime = 10000;
        time = sharedPreferences.getLong(getString(R.string.preference_time),defaulttime);
        //뉴스 데이터 불러오기. 뉴스 데이터는 만들어질때 1번만 불러온다.
        adapter= new NewsAdaptor(getActivity(), android.R.layout.simple_expandable_list_item_1,saveWord);
        handler.post(timedTask);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        //초기화
        View rootView = inflater.inflate(R.layout.fragment_news, container, false);
        screen = (ListView) rootView.findViewById(R.id.news_listView);
        screen.setAdapter(adapter);

        screen.setEmptyView(MainActivity.emptyView);

        final JSONArray urlCatch = newsArray;

        screen.setEmptyView(rootView.findViewById(R.id.empty));

        listRefresh();
        //짧은 클릭 리스너 설정
        screen.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Toast toast = null;
                ContentValues cv = new ContentValues();

                try {
                    //외부 연결부분. 기본 인터넷으로 연결한다.
                    Intent i = new Intent(Intent.ACTION_VIEW);
                    JSONObject input = adapter.getItem(position);
                    Uri u = Uri.parse(input.getString("webUrl"));
                    i.setData(u);
                    startActivity(i);
                    //팝업 설정
                    toast = Toast.makeText(getActivity(),input.getString("webUrl"), Toast.LENGTH_LONG);
                    toast.setGravity(Gravity.CENTER, 0, 0);
                    toast.show();
                    //저장용 데이터
                    cv.put("webTitle",input.getString("webTitle"));
                    cv.put("webUrl",input.getString("webUrl"));
                    //최근글 db에 집어넣기
                    cr.insert(REC_URI,cv);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                cv.clear();
            }
        });

        //긴 클릭 리스너 설정
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
                        ContentValues cv = new ContentValues();
                        try {
                            JSONObject input = urlCatch.getJSONObject(index);
                            cv.put("webTitle",input.getString("webTitle"));
                            cv.put("webUrl",input.getString("webUrl"));
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        //즐겨찾기 db에 집어넣기
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
