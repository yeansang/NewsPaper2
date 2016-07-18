package com.example.nemus.newspaper2;

import android.animation.Animator;
import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.Rect;
import android.net.Uri;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

import com.example.nemus.newspaper2.DragandDrop.DragSource;
import com.example.nemus.newspaper2.DragandDrop.DragView;
import com.example.nemus.newspaper2.DragandDrop.DropTarget;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by nemus on 2016-07-15.
 */
public class DropPager extends ViewPager implements DropTarget{
    Context mContext;
    private final Uri NEWS_URI = Uri.parse("content://com.example.nemus.newspaper2.myContentProvider/news");

    public DropPager(Context context) {
        super(context);
        mContext = context;
    }

    public DropPager(Context context, AttributeSet attr){
        super(context,attr);
        mContext = context;
    }

    @Override
    public void onDrop(DragSource source, final int x, final int y, int xOffset, int yOffset, final DragView dragView, Object dragInfo) {
        ContentResolver cr = mContext.getContentResolver();
        JSONObject jo = (JSONObject)dragInfo;
        try {
            cr.delete(NEWS_URI, "\""+jo.getString("webTitle")+"\"", new String[]{"webTitle"});
        }catch (JSONException e){
            e.printStackTrace();
        }

        Log.d("drag","drop pager");
        Log.d("drag",dragView.getParent().getClass().getSimpleName());
        dragView.setBackgroundColor(Color.WHITE);
        dragView.goFaraway();
        /*dragView.animate().translationY(1800).setDuration(1000);
        dragView.animate().setListener(new Animator.AnimatorListener() {
            int yi = y;
            @Override
            public void onAnimationStart(Animator animator) {}
            @Override
            public void onAnimationEnd(Animator animator) {
                dragView.remove();
            }
            @Override
            public void onAnimationCancel(Animator animator){}
            @Override
            public void onAnimationRepeat(Animator animator){
//                Log.d("drag","repeat");
//                dragView.move(x,y);
}
        });*/

        //dragView.remove();

    }
    @Override
    public void onDragEnter(DragSource source, int x, int y, int xOffset, int yOffset, DragView dragView, Object dragInfo) {

    }
    @Override
    public void onDragOver(DragSource source, int x, int y, int xOffset, int yOffset, DragView dragView, Object dragInfo) {

    }
    @Override
    public void onDragExit(DragSource source, int x, int y, int xOffset, int yOffset, DragView dragView, Object dragInfo) {

    }
    @Override
    public boolean acceptDrop(DragSource source, int x, int y, int xOffset, int yOffset, DragView dragView, Object dragInfo) {
        return true;
    }
    @Override
    public Rect estimateDropLocation(DragSource source, int x, int y, int xOffset, int yOffset, DragView dragView, Object dragInfo, Rect recycle) {
        return null;
    }
}
