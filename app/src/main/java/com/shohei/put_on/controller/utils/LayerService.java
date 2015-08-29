package com.shohei.put_on.controller.utils;

import android.app.Service;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.os.IBinder;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;

import com.shohei.put_on.R;
import com.shohei.put_on.model.Memo;
import com.shohei.put_on.view.widget.SlideLayout;

/**
 * Created by nakayamashohei on 15/08/29.
 */
public class LayerService extends Service implements SlideLayout.OnCloseListener {
    private final static String LOG_TAG = LayerService.class.getSimpleName();

    private Memo mMemo;
    private ServiceRunningDetector mServiceRunningDetector;
    private SlideLayout mSlideLayout;

    private WindowManager mWindowManager;
    private WindowManager.LayoutParams mLayoutParams;

    private EditText mTagEditText;
    private EditText mTextEditText;
    private Button mSaveButton;
    private Button mCloseButton;

    boolean isValidView = true;

    @Override
    public void onCreate() {
        super.onCreate();
        appearOverlayView();
    }

    @Override
    public void onDestroy() {
        mWindowManager.removeView(mSlideLayout);
    }

    public void appearOverlayView() {

        mWindowManager = (WindowManager) getSystemService(WINDOW_SERVICE);
        mSlideLayout = (SlideLayout) LayoutInflater.from(this).inflate(R.layout.overlay_memo_view, null);
        mSlideLayout.setOnCloseListener(this);

        mMemo = new Memo();
        mServiceRunningDetector = new ServiceRunningDetector(this);

        findViews();

        //Buttonのクリックリスナー
        mSaveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String memo = mTextEditText.getText().toString();
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
        mWindowManager.addView(mSlideLayout, mLayoutParams);

    }

    private void updateFocusParams() {
        if (!isValidView) {
            // フォーカスを取らないようにする
            mLayoutParams.flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
                    | WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS;
        } else {
            // フォーカスを取るようにする
            mLayoutParams.flags = WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL
                    | WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS;
        }
        mWindowManager.updateViewLayout(mSlideLayout, mLayoutParams);
    }

    //関連づけ
    private void findViews() {
        mTextEditText = (EditText) mSlideLayout.findViewById(R.id.memo_EditText_Overlay);
        mTagEditText = (EditText) mSlideLayout.findViewById(R.id.tag_EditText_Overlay);
        mSaveButton = (Button) mSlideLayout.findViewById(R.id.save_Button_Overlay);
        mCloseButton = (Button) mSlideLayout.findViewById(R.id.close_Button_Overlay);
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public void onClosed(int vector) {
        ServiceRunningDetector serviceRunningDetector = new ServiceRunningDetector(this);
        if (serviceRunningDetector.isServiceRunning()) {
            this.stopSelf();
        }
    }

}

