package com.example.nemus.newspaper2;

import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.support.v4.app.Fragment;
import android.database.ContentObserver;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.widget.PopupMenu;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by nemus on 2016-06-30.
 */
public class ListedFrog extends Fragment {

    ListView screen = null;
    NewsAdaptor adapter;
    ArrayList<JSONObject> saveWord;
    ContentResolver cr;
    Uri DBURI=null;

    private static final String ARG_LAYOUT = "arg_layout";
    private static final String ARG_TABNAME = "tab_name";
    private static final String ARG_LISTNAME = "list_name";

    //콘텐트 옵저버는 반드시 한번만 등록할것
    private ContentObserver observer = new ContentObserver(new Handler()) {
        @Override
        public void onChange(boolean selfChange, Uri uri) {
            super.onChange(selfChange, uri);
            //바뀐 신호가 들어오면 새로고침
            refresh();
            Log.d("change", "brand new " + getArguments().getString(ARG_TABNAME));
        }
    };



    public ListedFrog() {
    }

    //
    public void refresh(){
        //adapter.clear();
        saveWord.clear();
        //cr = getActivity().getContentResolver();
        //db에서 불러와서 새로 고침
        Cursor wordData = cr.query(DBURI,null,null,null,null);
        try {
            while (wordData.moveToNext()) {
                saveWord.add(new JSONObject("{\"webTitle\":\""+wordData.getString(1)+"\",\"webUrl\":\""+wordData.getString(2)+"\"}"));
            }
        }catch (JSONException e){
            e.printStackTrace();
        }
        screen.setAdapter(adapter);
        wordData.close();
    }


    public static Fragment newInstance(int layout, int listId, String tabName) {
        ListedFrog fragment = new ListedFrog();

        Bundle args = new Bundle();

        //변수 집어 넣기
        args.putInt(ARG_LAYOUT, layout);
        args.putInt(ARG_LISTNAME, listId);

        //tabname으로 역할 구분
        switch (tabName) {
            case "fav":
                args.putString(ARG_TABNAME, DBConnect.fav);
                break;
            case "rec":
                args.putString(ARG_TABNAME, DBConnect.rec);
                break;
            default:
        }

        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(final LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        //초기화
        View view = inflater.inflate(getArguments().getInt(ARG_LAYOUT), container, false);
        screen = (ListView) view.findViewById(getArguments().getInt(ARG_LISTNAME));
        saveWord = new ArrayList<JSONObject>();
        adapter = new NewsAdaptor(getActivity(),android.R.layout.simple_expandable_list_item_1,saveWord);

        DBURI = Uri.parse("content://com.example.nemus.newspaper2.myContentProvider/"+getArguments().getString(ARG_TABNAME).toLowerCase());

        cr = getActivity().getContentResolver();
        //콘텐트 옵저버는 반드시 한번만 등록할것
        cr.registerContentObserver(DBURI, true, observer);

        //리스트에 데이터 넣기
        refresh();

        Log.d("tabname", getArguments().getString(ARG_TABNAME));

        //짧은 터치 리스너 설정
        screen.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Cursor wordData = cr.query(DBURI,null,null,null,null);
                wordData.moveToPosition(position);
                //외부 연결
                Intent i = new Intent(Intent.ACTION_VIEW);
                Uri u = Uri.parse(wordData.getString(2));
                i.setData(u);
                startActivity(i);
                wordData.close();
            }
        });

        //긴 터치 리스너 설정
        screen.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                PopupMenu pop = new PopupMenu(parent.getContext(), view);
                pop.getMenuInflater().inflate(R.menu.del_menu_pop,pop.getMenu());

                final int index = position;
                //팝업메뉴 리스너 설정
                pop.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        if(item.getItemId() == R.id.delete){
                            Cursor wordData = cr.query(DBURI,null,null,null,null);
                            wordData.moveToPosition(index);
                            getActivity().getContentResolver().delete(DBURI,""+(index+1),new String[]{"pos"});
                            Log.d("data",wordData.getString(1));
                            adapter.notifyDataSetChanged();
                            wordData.close();
                        }
                        return false;
                    }
                });
                pop.show();
                return true;
            }
        });

        return view;
    }

    @Override
    public void onDestroyView(){
        super.onDestroyView();
        cr.unregisterContentObserver(observer);
    }
}
