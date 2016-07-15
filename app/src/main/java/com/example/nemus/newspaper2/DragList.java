package com.example.nemus.newspaper2;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ListView;

import com.example.nemus.newspaper2.DragandDrop.DragController;
import com.example.nemus.newspaper2.DragandDrop.DragSource;

/**
 * Created by nemus on 2016-07-15.
 */
public class DragList extends ListView implements DragSource {

    public DragList(Context context) {
        super(context);
    }

    public DragList(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public DragList(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public DragList(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    @Override
    public boolean allowDrag() {
        return true;
    }

    @Override
    public void setDragController(DragController dragger) {

    }

    @Override
    public void onDropCompleted(View target, boolean success) {

    }
}
