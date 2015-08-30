package com.shohei.put_on.view.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.widget.LinearLayout;

/**
 * Created by nakayamashohei on 15/08/29.
 */
public class OverlayMemoView extends LinearLayout {
    private final static String LOG_TAG = OverlayMemoView.class.getSimpleName();

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
    public boolean dispatchKeyEvent(KeyEvent event) {
        if (event.getKeyCode() == KeyEvent.KEYCODE_BACK) {
            return false;
        }
        return super.dispatchKeyEvent(event);
    }

}
