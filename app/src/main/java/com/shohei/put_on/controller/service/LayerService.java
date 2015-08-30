package com.shohei.put_on.controller.service;

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
import com.shohei.put_on.controller.utils.ServiceRunningDetector;
import com.shohei.put_on.model.Memo;
import com.shohei.put_on.view.widget.OverlayMemoView;

/**
 * Created by nakayamashohei on 15/08/29.
 */
public class LayerService extends Service {
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
        mOverlayMemoView = (OverlayMemoView) LayoutInflater.from(this).inflate(R.layout.overlay_memo_view, null);

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

//    @Override
//    public void onClosed(int vector) {
//        ServiceRunningDetector serviceRunningDetector = new ServiceRunningDetector(this);
//        if (serviceRunningDetector.isServiceRunning()) {
//            this.stopSelf();
//        }
//    }

}

