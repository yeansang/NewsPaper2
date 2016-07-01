package com.example.nemus.newspaper2;

import android.app.Fragment;
import android.database.ContentObserver;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.ArrayList;

/**
 * Created by nemus on 2016-06-30.
 */
public class ListFragment extends Fragment {

    ListView screen =null;
    ArrayAdapter<String> adapter;
    ArrayList<String> saveWord;

    private ContentObserver observer = new ContentObserver(new Handler()) {
        @Override
        public void onChange(boolean selfChange, Uri uri) {
            super.onChange(selfChange, uri);
            Log.d("change", "brand new "+getArguments().getString(ARG_TABNAME));
        }
    };

    static final String DBURI = "content://com.example.nemus.newspaper2.myContentProvider/news";
    private static final String ARG_LAYOUT="arg_layout";
    private static final String ARG_TABNAME="tab_name";
    private static final String ARG_LISTNAME="list_name";

    public ListFragment(){}

    public static ListFragment newInstance(int layout,int listId, String tabName){
        ListFragment fragment = new ListFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_LAYOUT, layout);
        args.putInt(ARG_LISTNAME, listId);

        switch(tabName){
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
        Log.d("tabname", getArguments().getString(ARG_TABNAME));

    }
}
