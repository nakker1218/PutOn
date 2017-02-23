package me.nakker.put_on.view.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.widget.FrameLayout;

import me.nakker.put_on.controller.utils.Logger;


/**
 * Created by nakayamashohei on 15/08/29.
 */
public class OverlayMemoView extends FrameLayout {

    public OverlayMemoView(Context context) {
        super(context);
    }

    public OverlayMemoView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public OverlayMemoView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent event) {
        // タッチされたら最初に呼ばれる
        // ここでtrueを返せば親ViewのonTouchEvent
        // ここでfalseを返せば子ViewのonClickやらonLongClickやら
        Logger.d(this.getClass(), "onInterceptTouchEvent/PointerCount: " + event.getAction());
        if (event.getPointerCount() == 1) {
            return false;
        }
        return true;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        // ここでtrueを返すとイベントはここで終了
        // ここでfalseを返すと子ViewのonClickやらonLongClickやら
        Logger.d(this.getClass(), "onTouchEvent/PointerCount: " + event.getAction());
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