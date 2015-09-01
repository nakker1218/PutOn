package com.shohei.put_on.controller.service;

import android.app.Service;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.graphics.Point;
import android.os.IBinder;
import android.util.Log;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.FrameLayout;

import com.shohei.put_on.R;
import com.shohei.put_on.controller.utils.DebugUtil;
import com.shohei.put_on.controller.utils.ServiceRunningDetector;
import com.shohei.put_on.model.Memo;
import com.shohei.put_on.view.widget.OverlayMemoCreateView;

/**
 * Created by nakayamashohei on 15/08/29.
 */
public class LayerService extends Service implements View.OnTouchListener {
    private final static String LOG_TAG = LayerService.class.getSimpleName();

    private Memo mMemo;
    private OverlayMemoCreateView mOverlayMemoCreateView;
    private ServiceRunningDetector mServiceRunningDetector;

    private WindowManager mWindowManager;
    private WindowManager.LayoutParams mLayoutParams;

    private FrameLayout mMemoFrameLayout;
    private EditText mTagEditText;
    private EditText mMemoEditText;
    private View mFab;

    private float mInitialTouchX;
    private float mInitialTouchY;
    private int mPositionX;
    private int mPositionY;
    private int mDisplayHeight;

    private boolean mIsOpen = true;
    private boolean mIsClickable = true;

    @Override
    public void onCreate() {
        super.onCreate();
        appearOverlayView();
    }

    @Override
    public void onDestroy() {
        mWindowManager.removeView(mOverlayMemoCreateView);
    }

    public void appearOverlayView() {

        mWindowManager = (WindowManager) getSystemService(WINDOW_SERVICE);

        mOverlayMemoCreateView = (OverlayMemoCreateView) LayoutInflater.from(this).inflate(R.layout.overlay_memo_view, null);
        mOverlayMemoCreateView.setOnTouchListener(this);

        mMemo = new Memo();
        mServiceRunningDetector = new ServiceRunningDetector(this);

        findViews();

        mDisplayHeight = getDisplaySize().y;

        //Layoutを設定
        mLayoutParams = new WindowManager.LayoutParams(
                WindowManager.LayoutParams.MATCH_PARENT,
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.TYPE_PHONE,
                WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL,
                PixelFormat.TRANSLUCENT);

        mLayoutParams.gravity = Gravity.LEFT | Gravity.BOTTOM;
        mWindowManager.addView(mOverlayMemoCreateView, mLayoutParams);
    }

    private void findViews() {
        mMemoFrameLayout = (FrameLayout) mOverlayMemoCreateView.findViewById(R.id.memoCreate_FrameLayout_Overlay);
        mMemoEditText = (EditText) mOverlayMemoCreateView.findViewById(R.id.memo_EditText_Overlay);
        mTagEditText = (EditText) mOverlayMemoCreateView.findViewById(R.id.tag_EditText_Overlay);
        mFab = mOverlayMemoCreateView.findViewById(R.id.overlay_FAB);
    }

    public void saveOverlay(View v) {
        final String memo = mMemoEditText.getText().toString();
        final String tag = mTagEditText.getText().toString();

        mMemo.saveMemo(memo, tag);
    }

    public void closeOverlay(View v) {
        if (DebugUtil.DEBUG) Log.d(LOG_TAG, "Close");
        if (mServiceRunningDetector.isServiceRunning()) {
            stopSelf();
        }
    }

    public void minimizeOverlay(View v) {
        if (DebugUtil.DEBUG) Log.d(LOG_TAG, "Minimize");
        mIsOpen = false;
        mMemoFrameLayout.setVisibility(View.GONE);
        mFab.setVisibility(View.VISIBLE);

        updateLayoutParams(
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
                mOverlayMemoCreateView
        );
    }

    private Point getDisplaySize() {
        Display display = mWindowManager.getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        return size;
    }

    // Layoutのパラメータの設定
    private void updateLayoutParams(int widthParam, int flagParam, View view) {
        mLayoutParams.width = widthParam;
        mLayoutParams.height = WindowManager.LayoutParams.WRAP_CONTENT;
        mLayoutParams.flags = flagParam;
        mWindowManager.updateViewLayout(view, mLayoutParams);
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public boolean onTouch(View view, MotionEvent event) {
        // Viewを動かす
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN: {
                mInitialTouchX = event.getRawX();
                mInitialTouchY = event.getRawY();
                mPositionX = (int) mInitialTouchX;
                mPositionY = (int) mInitialTouchY;

                if (!mIsOpen) mIsClickable = true;
                break;
            }
            case MotionEvent.ACTION_MOVE: {
                if (mIsOpen) {
                    final int y = mDisplayHeight - (int) event.getRawY() - (mOverlayMemoCreateView.getHeight() / 2);
                    mLayoutParams.y = y;
                } else {
                    final int x = mPositionX - (int) event.getRawX();
                    final int y = mPositionY - (int) event.getRawY();
                    mLayoutParams.x -= x;
                    mLayoutParams.y += y;
                    mPositionX = (int) event.getRawX();
                    mPositionY = (int) event.getRawY();
                    mIsClickable = false;
                }
                if (DebugUtil.DEBUG)
                    Log.d(LOG_TAG, "X:" + mLayoutParams.x + " Y:" + mLayoutParams.y);
                mWindowManager.updateViewLayout(view, mLayoutParams);
                break;
            }
            case MotionEvent.ACTION_UP: {
                if (!mIsOpen && mIsClickable) {
                    mIsOpen = true;
                    mMemoFrameLayout.setVisibility(View.VISIBLE);
                    mFab.setVisibility(View.GONE);

                    updateLayoutParams(
                            WindowManager.LayoutParams.MATCH_PARENT,
                            WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL,
                            view
                    );
                }
                break;
            }
        }
        return false;
    }
}