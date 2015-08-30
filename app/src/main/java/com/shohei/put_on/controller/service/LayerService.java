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
import android.widget.Button;
import android.widget.EditText;

import com.shohei.put_on.R;
import com.shohei.put_on.controller.utils.DebugUtil;
import com.shohei.put_on.controller.utils.ServiceRunningDetector;
import com.shohei.put_on.model.Memo;
import com.shohei.put_on.view.widget.OverlayMemoView;

/**
 * Created by nakayamashohei on 15/08/29.
 */
public class LayerService extends Service implements View.OnTouchListener {
    private final static String LOG_TAG = LayerService.class.getSimpleName();

    private Memo mMemo;
    private OverlayMemoView mOverlayMemoView;
    private ServiceRunningDetector mServiceRunningDetector;

    private WindowManager mWindowManager;
    private WindowManager.LayoutParams mLayoutParams;

    private EditText mTagEditText;
    private EditText mMemoEditText;
    private Button mSaveButton;
    private Button mCloseButton;

    private int initialX;
    private int initialY;
    private float initialTouchX;
    private float initialTouchY;
    private int mDisplayHeight;

    @Override
    public void onCreate() {
        super.onCreate();
        appearOverlayView();
    }

    @Override
    public void onDestroy() {
        mWindowManager.removeView(mOverlayMemoView);
    }

    public void appearOverlayView() {

        mWindowManager = (WindowManager) getSystemService(WINDOW_SERVICE);

        Display display = mWindowManager.getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        mDisplayHeight = size.y;

        mOverlayMemoView = (OverlayMemoView) LayoutInflater.from(this).inflate(R.layout.overlay_memo_view, null);
        mOverlayMemoView.setOnTouchListener(this);

        mMemo = new Memo();
        mServiceRunningDetector = new ServiceRunningDetector(this);

        findViews();

        //Buttonのクリックリスナー
        mSaveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String memo = mMemoEditText.getText().toString();
                final String tag = mTagEditText.getText().toString();

                mMemo.saveMemo(memo, tag);
            }
        });

        mCloseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mServiceRunningDetector.isServiceRunning()) {
                    stopSelf();
                }
            }
        });

        //Layoutを設定
        mLayoutParams = new WindowManager.LayoutParams(
                WindowManager.LayoutParams.MATCH_PARENT,
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.TYPE_PHONE,
                WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL,
                PixelFormat.TRANSLUCENT);

        mLayoutParams.gravity = Gravity.LEFT | Gravity.BOTTOM;
        mWindowManager.addView(mOverlayMemoView, mLayoutParams);

    }

    //関連づけ
    private void findViews() {
        mMemoEditText = (EditText) mOverlayMemoView.findViewById(R.id.memo_EditText_Overlay);
        mTagEditText = (EditText) mOverlayMemoView.findViewById(R.id.tag_EditText_Overlay);
        mSaveButton = (Button) mOverlayMemoView.findViewById(R.id.save_Button_Overlay);
        mCloseButton = (Button) mOverlayMemoView.findViewById(R.id.close_Button_Overlay);
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN: {
                initialX = mLayoutParams.x;
                initialY = mLayoutParams.y;
                initialTouchX = event.getRawX();
                initialTouchY = event.getRawY();
                break;
            }
            case MotionEvent.ACTION_MOVE: {
                final int x = initialX + (int) (event.getRawX() - initialTouchX);
                final int y = mDisplayHeight - (initialY + (int) (event.getRawY() - initialTouchY) + (mOverlayMemoView.getHeight() / 2));
                mLayoutParams.x = x;
                mLayoutParams.y = y;
                if (DebugUtil.DEBUG)
                    Log.d(LOG_TAG, "X:" + mLayoutParams.x + " Y:" + mLayoutParams.y);
                mWindowManager.updateViewLayout(v, mLayoutParams);
                break;
            }
            case MotionEvent.ACTION_UP: {
                break;
            }
        }
        return false;
    }
}

