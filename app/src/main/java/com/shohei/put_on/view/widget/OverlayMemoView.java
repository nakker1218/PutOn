package com.shohei.put_on.view.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.widget.LinearLayout;

import com.shohei.put_on.controller.utils.DebugUtil;

/**
 * Created by nakayamashohei on 15/08/29.
 */
public class OverlayMemoView extends LinearLayout {
    private final static String LOG_TAG = OverlayMemoView.class.getSimpleName();

    private Context mContext;

    private View mTargetView;


    public OverlayMemoView(Context context) {
        this(context, null);
    }

    public OverlayMemoView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public OverlayMemoView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.mContext = context;

    }

    public int getViewHeight(){
        int height = this.getHeight();
        return height;
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        if (getChildCount() > 0) {
            mTargetView = getChildAt(0);
            setTargetView(mTargetView);
        }
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent event) {
        // タッチされたら最初に呼ばれる
        // ここでtrueを返せば親ViewのonTouchEvent
        // ここでfalseを返せば子ViewのonClickやらonLongClickやら
        if (DebugUtil.DEBUG) Log.d(LOG_TAG, "onInterceptTouchEvent/PointerCount: " + event.getAction());
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

    public void setTargetView(View targetView) {
        this.mTargetView = targetView;
    }
}
