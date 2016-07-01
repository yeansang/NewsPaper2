package com.example.nemus.newspaper2;

import android.annotation.SuppressLint;
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
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import org.json.JSONException;

import java.util.ArrayList;

/**
 * Created by nemus on 2016-06-30.
 */
public class ListedFrog extends Fragment {

    ListView screen = null;
    ArrayAdapter<String> adapter;
    ArrayList<String> saveWord;
    ContentResolver cr;

    private ContentObserver observer = new ContentObserver(new Handler()) {
        @Override
        public void onChange(boolean selfChange, Uri uri) {
            super.onChange(selfChange, uri);
            refresh();
            Log.d("change", "brand new " + getArguments().getString(ARG_TABNAME));
        }
    };

    Uri DBURI=null;
    private static final String ARG_LAYOUT = "arg_layout";
    private static final String ARG_TABNAME = "tab_name";
    private static final String ARG_LISTNAME = "list_name";

    public ListedFrog() {
    }

    public void refresh(){
        adapter.clear();
        //cr = getActivity().getContentResolver();
        cr.registerContentObserver(DBURI, true, observer);
        Cursor wordData = cr.query(DBURI,null,null,null,null);

        while(wordData.moveToNext()){
            saveWord.add(wordData.getString(1));
        }
        if(saveWord.size()<=0){
            saveWord.add("Data not found");
        }
        adapter = new ArrayAdapter<String>(getActivity(),android.R.layout.simple_expandable_list_item_1,saveWord);
        screen.setAdapter(adapter);
        wordData.close();
    }


    public static Fragment newInstance(int layout, int listId, String tabName) {
        ListedFrog fragment = new ListedFrog();
        Bundle args = new Bundle();
        args.putInt(ARG_LAYOUT, layout);
        args.putInt(ARG_LISTNAME, listId);

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
        View view = inflater.inflate(getArguments().getInt(ARG_LAYOUT), container, false);
        screen = (ListView) view.findViewById(getArguments().getInt(ARG_LISTNAME));
        saveWord = new ArrayList<String>();
        adapter = new ArrayAdapter<String>(getActivity(),android.R.layout.simple_expandable_list_item_1);

        DBURI = Uri.parse("content://com.example.nemus.newspaper2.myContentProvider/"+getArguments().getString(ARG_TABNAME).toLowerCase());

        cr = getActivity().getContentResolver();
        cr.registerContentObserver(DBURI, true, observer);
        final Cursor wordData = cr.query(DBURI,null,null,null,null);

        refresh();

        Log.d("tabname", getArguments().getString(ARG_TABNAME));

        screen.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                Intent i = new Intent(Intent.ACTION_VIEW);
                wordData.moveToPosition(position);
                Uri u = Uri.parse(wordData.getString(2));
                i.setData(u);
                startActivity(i);

            }
        });

        screen.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, final int position, long id) {
                PopupMenu pop = new PopupMenu(parent.getContext(), view);
                pop.getMenuInflater().inflate(R.menu.del_menu_pop,pop.getMenu());

                final int index = position;
                //팝업메뉴 리스너 설정
                pop.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        if(item.getItemId() == R.id.delete){
                            wordData.moveToPosition(index);
                            adapter.remove(wordData.getString(index));
                            getActivity().getContentResolver().delete(DBURI,""+(index+1),new String[]{"pos"});
                            adapter.notifyDataSetChanged();
                        }
                        return false;
                    }
                });
                pop.show();
                return false;
            }
        });

        return view;
    }
}
