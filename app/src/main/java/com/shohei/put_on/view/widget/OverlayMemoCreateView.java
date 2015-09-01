package com.shohei.put_on.view.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

import com.shohei.put_on.controller.utils.DebugUtil;

/**
 * Created by nakayamashohei on 15/08/29.
 */
public class OverlayMemoCreateView extends FrameLayout {
    private final static String LOG_TAG = OverlayMemoCreateView.class.getSimpleName();

    public OverlayMemoCreateView(Context context) {
        super(context);
    }

    public OverlayMemoCreateView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public OverlayMemoCreateView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent event) {
        // タッチされたら最初に呼ばれる
        // ここでtrueを返せば親ViewのonTouchEvent
        // ここでfalseを返せば子ViewのonClickやらonLongClickやら
        if (DebugUtil.DEBUG)
            Log.d(LOG_TAG, "onInterceptTouchEvent/PointerCount: " + event.getAction());
        if (event.getPointerCount() == 1) {
            return false;
        }
        return true;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        // ここでtrueを返すとイベントはここで終了
        // ここでfalseを返すと子ViewのonClickやらonLongClickやら
        if (DebugUtil.DEBUG) Log.d(LOG_TAG, "onTouchEvent/PointerCount: " + event.getAction());
        if (event.getPointerCount() == 1) {
            return false;
        }
        return true;
    }

    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        if (event.getKeyCode() == KeyEvent.KEYCODE_BACK) {
            return false;
        }
        return super.dispatchKeyEvent(event);
    }
}