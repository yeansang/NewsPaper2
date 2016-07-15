package com.example.nemus.newspaper2;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.graphics.Color;
import android.net.Uri;
import android.support.design.widget.TabLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.util.Log;
import android.view.DragEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.TextView;
import android.widget.Toast;

import com.example.nemus.newspaper2.DragandDrop.DragController;
import com.example.nemus.newspaper2.DragandDrop.DragLayer;

import org.json.JSONException;
import org.json.JSONObject;

public class MainActivity extends AppCompatActivity {

    /**
     * The {@link android.support.v4.view.PagerAdapter} that will provide
     * fragments for each of the sections. We use a
     * {@link FragmentPagerAdapter} derivative, which will keep every
     * loaded fragment in memory. If this becomes too memory intensive, it
     * may be best to switch to a
     * {@link android.support.v4.app.FragmentStatePagerAdapter}.
     */
    private SectionsPagerAdapter mSectionsPagerAdapter;

    /**
     * The {@link ViewPager} that will host the section contents.
     */
    private ViewPager mViewPager;
    public static TextView emptyView;
    private NewsFrog newsFrog;

       // Object that handles a drag-drop sequence. It intersacts with DragSource and DropTarget objects.
    private DragLayer mDragLayer;

    private void deleteAnimation(View view){
        Animation ani = AnimationUtils.loadAnimation(getBaseContext(),R.anim.delete);
        view.startAnimation(ani);
    }

    private void addAnimation(final View view){
        Animation ani = AnimationUtils.loadAnimation(getBaseContext(),R.anim.add);
        view.startAnimation(ani);
        view.setBackgroundColor(Color.WHITE);
        /*view.animate().scaleX(0.4f).scaleY(0.4f).translationX(view.getX()).translationY(0.0f - view.getY()).setDuration(1000);
        view.animate().cancel();*/
       /* Animation ani = AnimationUtils.loadAnimation(getBaseContext(),R.anim.add);
        view.startAnimation(ani);*/
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        newsFrog = NewsFrog.newInstance();

        newsFrog.mDragController = new DragController(this);
        mDragLayer = (DragLayer)findViewById(R.id.drag_layer);
        mDragLayer.setDragController(newsFrog.mDragController);
        newsFrog.mDragController.setDragListener(mDragLayer);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        // Set up the ViewPager with the sections adapter.
        mViewPager = (DropPager) findViewById(R.id.container);
        mViewPager.setAdapter(mSectionsPagerAdapter);

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(mViewPager);

    }


    @Override
    public boolean onCreateOptionsMenu(final Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        MenuInflater setting = getMenuInflater();
        setting.inflate(R.menu.menu_main,menu);
        menu.getItem(0).setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {
                if(menuItem.getItemId() == R.id.action_settings) {
                    newsFrog.manualPost();
                }
                return true;
            }
        });

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }



    /**
     * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
     * one of the sections/tabs/pages.
     */
    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            // getItem is called to instantiate the fragment for the given page.
            // Return a PlaceholderFragment (defined as a static inner class below).
            switch(position){
                case 0:
                    return newsFrog;//뉴스 페이지 보여주기
                case 1:
                    return ListedFrog.newInstance(R.layout.fragment_fav, R.id.fav_listView,"fav");//즐겨찾기 보여주기
                case 2:
                    return ListedFrog.newInstance(R.layout.fragment_rec, R.id.rec_listView,"rec");//최근 본 뉴스 보여주기
                default:
                    return null;
            }
        }

        @Override
        public int getCount() {
            // Show 3 total pages.
            return 3;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            switch (position) {
                case 0:
                    return "News";
                case 1:
                    return "Favorite";
                case 2:
                    return "Recent";
            }
            return null;
        }
    }
}
