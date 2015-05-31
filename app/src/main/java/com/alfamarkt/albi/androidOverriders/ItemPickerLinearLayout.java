package com.alfamarkt.albi.androidOverriders;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.LinearLayout;

import com.alfamarkt.albi.GameItemPicker;
import com.alfamarkt.albi.R;

/**
 * Created by Ansem on 31-5-2015.
 */
public class ItemPickerLinearLayout extends LinearLayout{

    public ItemPickerLinearLayout(Context context) {
        super(context);
    }

    public ItemPickerLinearLayout(Context context, AttributeSet attrs) {
        super(context,attrs);
    }

    public ItemPickerLinearLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context,attrs, defStyleAttr);
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        int x = Math.round(ev.getX());
        int y = Math.round(ev.getY());
        View child = getChildAt(1);
        if(x > child.getLeft() && x < child.getRight() && y > child.getTop() && y < child.getBottom()){
            child.setBackgroundResource(R.drawable.checkmarkgreen);
            if(ev.getAction() == MotionEvent.ACTION_UP){
                ((GameItemPicker)getContext()).inStock();
                child.setBackgroundResource(R.drawable.checkmarkgrey);
            }
        } else {
            child.setBackgroundResource(R.drawable.checkmarkgrey);
        }
        View child2 = getChildAt(3);
        if(x > child2.getLeft() && x < child2.getRight() && y > child2.getTop() && y < child2.getBottom()){
            child2.setBackgroundResource(R.drawable.xred);
            if(ev.getAction() == MotionEvent.ACTION_UP){
                ((GameItemPicker)getContext()).notInStock();
                child2.setBackgroundResource(R.drawable.xgrey);
            }
        } else {
            child2.setBackgroundResource(R.drawable.xgrey);
        }
        return true;
    }
}
