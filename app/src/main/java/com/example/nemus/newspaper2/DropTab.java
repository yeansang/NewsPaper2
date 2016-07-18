package com.example.nemus.newspaper2;

import android.animation.Animator;
import android.content.Context;
import android.graphics.Color;
import android.graphics.Rect;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.TabLayout;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.ViewParent;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

import com.example.nemus.newspaper2.DragandDrop.DragSource;
import com.example.nemus.newspaper2.DragandDrop.DragView;
import com.example.nemus.newspaper2.DragandDrop.DropTarget;

/**
 * Created by nemus on 2016-07-15.
 */
public class DropTab extends TabLayout implements DropTarget{
    public DropTab(Context context) {
        super(context);
    }
    public DropTab(Context context, AttributeSet attr){
        super(context,attr);
    }

    @Override
    public void onDrop(DragSource source, int x, int y, int xOffset, int yOffset, final DragView dragView, Object dragInfo) {
        Log.d("drag","droptab");
        Log.d("dragdrop", x+"");

        dragView.setBackgroundColor(Color.WHITE);

        dragView.animate().scaleX(0.4f).scaleY(0.4f).translationX(0).setDuration(1000);
        dragView.animate().setListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animator) {}
            @Override
            public void onAnimationEnd(Animator animator) {
                dragView.remove();
            }
            @Override
            public void onAnimationCancel(Animator animator){}
            @Override
            public void onAnimationRepeat(Animator animator){}
        });
    }

    @Override
    public void onDragEnter(DragSource source, int x, int y, int xOffset, int yOffset, DragView dragView, Object dragInfo) {
        Log.d("drag","tabenter");
        Log.d("drag", x+"");
        this.setBackgroundColor(Color.LTGRAY);
    }

    @Override
    public void onDragOver(DragSource source, int x, int y, int xOffset, int yOffset, DragView dragView, Object dragInfo) {

    }

    @Override
    public void onDragExit(DragSource source, int x, int y, int xOffset, int yOffset, DragView dragView, Object dragInfo) {
        this.setBackgroundColor(0);
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
