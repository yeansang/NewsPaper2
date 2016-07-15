package com.example.nemus.newspaper2;

import android.content.Context;
import android.graphics.Rect;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.util.Log;

import com.example.nemus.newspaper2.DragandDrop.DragSource;
import com.example.nemus.newspaper2.DragandDrop.DragView;
import com.example.nemus.newspaper2.DragandDrop.DropTarget;

/**
 * Created by nemus on 2016-07-15.
 */
public class DropPager extends ViewPager implements DropTarget{
    public DropPager(Context context) {
        super(context);
    }

    public DropPager(Context context, AttributeSet attr){
        super(context,attr);
    }

    @Override
    public void onDrop(DragSource source, int x, int y, int xOffset, int yOffset, DragView dragView, Object dragInfo) {
        Log.d("drag","drop pager");
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
