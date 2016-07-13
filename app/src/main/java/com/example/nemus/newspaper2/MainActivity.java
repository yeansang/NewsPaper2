package com.example.nemus.newspaper2;

import android.animation.AnimatorInflater;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.ClipData;
import android.content.ClipDescription;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.graphics.Canvas;
import android.graphics.Color;
import android.net.Uri;
import android.support.design.widget.TabLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.Toolbar;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.util.Log;
import android.util.Property;
import android.view.DragEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.AnimationUtils;
import android.view.animation.ScaleAnimation;
import android.view.animation.TranslateAnimation;
import android.widget.AdapterView;
import android.widget.TextView;
import android.widget.Toast;

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

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.container);
        mViewPager.setAdapter(mSectionsPagerAdapter);
        mViewPager.setOnDragListener(new View.OnDragListener() {
            @Override
            public boolean onDrag(View view, DragEvent dragEvent) {
                Log.d("x",""+dragEvent.getX());
                Log.d("y",""+dragEvent.getY());
                if(dragEvent.getAction()==DragEvent.ACTION_DROP) {
                    Log.d("drag", "deleted");
                    ContentResolver cr = getContentResolver();
                    cr.delete(Uri.parse("content://com.example.nemus.newspaper2.myContentProvider/news"),(newsFrog.pos)+"",new String[]{"pos"});
                    deleteAnimation((View)dragEvent.getLocalState());
                    newsFrog.listRefresh();
                    newsFrog.adapter.notifyDataSetChanged();
                }
                return false;
            }
        });

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(mViewPager);
        tabLayout.setOnDragListener(new View.OnDragListener() {
            @Override
            public boolean onDrag(View view, DragEvent dragEvent) {
                if ((dragEvent.getAction() == DragEvent.ACTION_DROP) && (view == findViewById(R.id.tabs))) {
                    ContentValues cv = new ContentValues();
                    ContentResolver cr = getContentResolver();
                    try {
                        JSONObject input = (JSONObject) newsFrog.screen.getAdapter().getItem(newsFrog.pos);
                        cv.put("webTitle", input.getString("webTitle"));
                        cv.put("webUrl", input.getString("webUrl"));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    addAnimation((View)dragEvent.getLocalState());
                    //newsFrog.screen.getSelectedView().setBackgroundColor(0);
                    Toast.makeText(getApplicationContext(), "added", Toast.LENGTH_SHORT).show();
                    //즐겨찾기 db에 집어넣기
                    cr.insert(Uri.parse("content://com.example.nemus.newspaper2.myContentProvider/fav"), cv);
                    cv.clear();
                    view.setBackgroundColor(0);
                }else if(dragEvent.getAction()==DragEvent.ACTION_DRAG_ENTERED){
                    view.setBackgroundColor(Color.DKGRAY);
                }else if(dragEvent.getAction()==DragEvent.ACTION_DRAG_EXITED){
                    view.setBackgroundColor(0);
                }
                return true;
            }
        });
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
