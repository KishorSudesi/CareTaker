package com.hdfc.views;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.MotionEvent;

/**
 * Created by balamurugan@adstringo.in on 02-01-2016.
 */
public class CustomViewPager extends ViewPager {

    private static boolean _enabled;

    public CustomViewPager(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public static void setPagingEnabled(boolean enabled) {
        _enabled = enabled;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (_enabled) {
            return super.onTouchEvent(event);
        }

        return false;
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent event) {
        if (_enabled) {
            return super.onInterceptTouchEvent(event);
        }

        return false;
    }
}
