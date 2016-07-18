package com.example.nemus.newspaper2;

import android.content.ClipData;
import android.content.ClipDescription;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.ContentObserver;
import android.database.Cursor;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.DragEvent;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.example.nemus.newspaper2.DragandDrop.DragController;
import com.example.nemus.newspaper2.DragandDrop.DragSource;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by nemus on 2016-07-01.
 */
public class NewsFrog extends Fragment{

    public ListView screen;
    public NewsAdaptor adapter=null;
    private JSONArray newsArray =null;
    private ArrayList<JSONObject> saveWord = new ArrayList<JSONObject>();
    private ContentResolver cr = null;
    private boolean manualRefresh = false;

    public DragController mDragController;

    private SharedPreferences sharedPreferences;

    private ContentObserver observer = new ContentObserver(new Handler()) {
        @Override
        public void onChange(boolean selfChange, Uri uri) {
            super.onChange(selfChange, uri);
            //바뀐 신호가 들어오면 새로고침
            listRefresh();
            //Log.d("change", "brand new " + getArguments().getString(ARG_TABNAME));
        }
    };

    private Handler handler = new Handler();
    private Runnable timedTask = new Runnable(){
        @Override
        public void run() {
            handler.postDelayed(timedTask, delayMillSec);

            if(manualRefresh||(time+delayMillSec < System.currentTimeMillis())) {
                refresh();
                Log.d("refresh","time refreshed");
                manualRefresh = false;
            }

            Log.d("refreshTime", time+"");
        }};

    private long time = 0;
    private final long delayMillSec = 60*1000;

    public int pos=0;

    private final Uri REC_URI = Uri.parse("content://com.example.nemus.newspaper2.myContentProvider/rec");
    private final Uri FAV_URI = Uri.parse("content://com.example.nemus.newspaper2.myContentProvider/fav");
    private final Uri NEWS_URI = Uri.parse("content://com.example.nemus.newspaper2.myContentProvider/news");

    public NewsFrog() {
    }

    public NewsFrog(DragController dragController){
        mDragController = dragController;
    }

    public static NewsFrog newInstance() {
        NewsFrog fragment = new NewsFrog();
        return fragment;
    }

    public void listRefresh(){
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
        wordData.close();
    }

    public void manualPost(){
        handler.removeCallbacks(timedTask);
        manualRefresh = true;
        handler.post(timedTask);
        Toast.makeText(getActivity(),"Refreshed",Toast.LENGTH_SHORT).show();
    }

    public void refresh(){
        ContentValues cv = new ContentValues();
        SharedPreferences.Editor editor = sharedPreferences.edit();
        cr.delete(NEWS_URI,"'%'",new String[]{"pos"});
        try {
            //newsArray = new JSONArray();
            newsArray = new GetGuardianNews().execute().get();
        } catch (Exception e) {
            e.printStackTrace();
        }
        if(newsArray!=null){
            try {
            for(int i=0;i<newsArray.length();i++){
                    JSONObject in = newsArray.getJSONObject(i);
                    cv.put("webTitle",in.getString("webTitle"));
                    cv.put("webUrl",in.getString("webUrl"));
                    cv.put("pos",i);
                    cr.insert(NEWS_URI,cv);
            }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        editor.putLong("PREFERENCE_TIME",System.currentTimeMillis());
        editor.apply();

        listRefresh();
        Log.d("refresh","ok");
    }

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        cr = getActivity().getContentResolver();

        //mDragController = new DragController(getActivity());

        sharedPreferences = getActivity().getSharedPreferences(getString(R.string.preference_file_key),Context.MODE_PRIVATE);
        time = sharedPreferences.getLong(getString(R.string.preference_time),0);
        //뉴스 데이터 불러오기. 뉴스 데이터는 만들어질때 1번만 불러온다.
        adapter= new NewsAdaptor(getActivity(), android.R.layout.simple_expandable_list_item_1,saveWord);
        handler.post(timedTask);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        //초기화
        final View rootView = inflater.inflate(R.layout.fragment_news, container, false);
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
                /*if (mLongClickStartsDrag) {

                    //trace ("onLongClick in view: " + v + " touchMode: " + v.isInTouchMode ());

                    // Make sure the drag was started by a long press as opposed to a long click.
                    // (Note: I got this from the Workspace object in the Android Launcher code.
                    //  I think it is here to ensure that the device is still in touch mode as we start the drag operation.)
                    if (!view.isInTouchMode()) {
                        return false;
                    }
                    DragSource dragSource = (DragSource) view;
                    // We are starting a drag. Let the DragController handle it.
                    mDragController.startDrag (view, dragSource, dragSource, DragController.DRAG_ACTION_MOVE);
                    mLongClickStartsDrag = false;
                    return true;
                }*/
                if (!view.isInTouchMode()) {
                    return false;
                }
                DragSource dragSource = (DragSource) view;
                // We are starting a drag. Let the DragController handle it.
                mDragController.startDrag (view, dragSource, adapter.getItem(position), DragController.DRAG_ACTION_MOVE);


                // If we get here, return false to indicate that we have not taken care of the event.
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
