package com.shohei.put_on.controller.service;

import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.PixelFormat;
import android.graphics.Point;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.support.v4.content.ContextCompat;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.AnimationSet;
import android.view.animation.ScaleAnimation;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.Toast;

import com.shohei.put_on.R;
import com.shohei.put_on.controller.utils.Logger;
import com.shohei.put_on.controller.utils.ServiceRunningDetector;
import com.shohei.put_on.model.Memo;
import com.shohei.put_on.view.adapter.MemoAdapter;
import com.shohei.put_on.view.widget.OverlayMemoView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by nakayamashohei on 15/08/29.
 */
public class LayerService extends Service implements View.OnTouchListener {
    private final static String LOG_TAG = LayerService.class.getSimpleName();

    public final static int NOTIFICATION_ID = 001;

    private Memo mMemo;
    private OverlayMemoView mOverlayMemoView;
    private ServiceRunningDetector mServiceRunningDetector;

    private WindowManager mWindowManager;
    private WindowManager.LayoutParams mLayoutParams;

    private FrameLayout mMemoFrameLayout;
    private AutoCompleteTextView mTagEditText;
    private EditText mMemoEditText;
    private View mSaveButton;
    private View mFab;

    private int mPositionX;
    private int mPositionY;
    private int mDisplayHeight;

    private boolean mIsOpen = true;
    private boolean mIsClicked = true;

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
        mOverlayMemoView.setOnTouchListener(this);

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
        mWindowManager.addView(mOverlayMemoView, mLayoutParams);

        setAutoComplete();

    }

    public void saveOverlay(View v) {
        final String memo = mMemoEditText.getText().toString();
        final String tag = mTagEditText.getText().toString();

        mMemo.saveMemo(memo, tag);
        if (!memo.isEmpty()) {
            mSaveButton.startAnimation(buttonAnimation(getResources().getDimension(R.dimen.fab_size_small)));
            Toast.makeText(this, R.string.text_save_toast, Toast.LENGTH_SHORT).show();
            stopSelf();
            setNotification();
        }
    }

    public void closeOverlay(View v) {
        Logger.d(LOG_TAG, "Close");

        if (mServiceRunningDetector.isServiceRunning()) {
            stopSelf();
            setNotification();
        }
    }

    public void minimizeOverlay(View v) {
        Logger.d(LOG_TAG, "Minimize");

        mIsOpen = false;
        mMemoFrameLayout.setVisibility(View.GONE);
        mFab.setVisibility(View.VISIBLE);

        updateLayoutParams(
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
                mOverlayMemoView
        );
    }

    private Point getDisplaySize() {
        Display display = mWindowManager.getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        return size;
    }

    private AnimationSet buttonAnimation(final float size) {
        AnimationSet buttonAnim = new AnimationSet(true);
        ScaleAnimation startAnim = new ScaleAnimation(1.0f, 0.9f, 1.0f, 0.9f, size / 2, size / 2);
        startAnim.setDuration(500);
        buttonAnim.addAnimation(startAnim);
        ScaleAnimation imageEndAnim = new ScaleAnimation(0.9f, 1.1f, 0.9f, 1.1f, size / 2, size / 2);
        imageEndAnim.setDuration(300);
        buttonAnim.addAnimation(imageEndAnim);
        return buttonAnim;
    }

    private void setNotification() {
        float[] hsv = new float[3];
        Color.colorToHSV(ContextCompat.getColor(this, R.color.primary), hsv);

        Intent intent = new Intent(this, LayerService.class);
        PendingIntent contentIntent = PendingIntent.getService(this, 0, intent, 0);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(getApplicationContext())
                .setSmallIcon(R.mipmap.ic_notification)
                .setContentTitle(getResources().getText(R.string.app_name))
                .setContentText(getResources().getText(R.string.text_content_notification))
                .setColor(Color.HSVToColor(hsv))
                .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
                .setPriority(NotificationCompat.PRIORITY_LOW)
                .setCategory(NotificationCompat.CATEGORY_RECOMMENDATION)
                .setContentIntent(contentIntent)
                .setAutoCancel(true);

        NotificationManagerCompat manager = NotificationManagerCompat.from(getApplicationContext());
        manager.notify(NOTIFICATION_ID, builder.build());
    }

    // Layoutのパラメータの設定
    private void updateLayoutParams(int widthParam, int flagParam, View view) {
        mLayoutParams.width = widthParam;
        mLayoutParams.height = WindowManager.LayoutParams.WRAP_CONTENT;
        mLayoutParams.flags = flagParam;
        mWindowManager.updateViewLayout(view, mLayoutParams);
    }

    private void setAutoComplete(){
        List<String> list =new ArrayList<String>();
        list.add("android");
        list.add("apple");
        String[] stringArray = list.toArray(new String[list.size()]);
//        List<Memo> tagList = MemoAdapter.searchTag();
//        String[] tags = tagList.toArray(new String[tagList.size()]);
        ArrayAdapter adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, stringArray);
        mTagEditText.setAdapter(adapter);
    }

    private void findViews() {
        mMemoFrameLayout = (FrameLayout) mOverlayMemoView.findViewById(R.id.memoCreate_FrameLayout_Overlay);
        mMemoEditText = (EditText) mOverlayMemoView.findViewById(R.id.memo_EditText_Overlay);
//        mTagEditText = (AutoCompleteTextView) mOverlayMemoView.findViewById(R.id.tag_EditText_Overlay);
        mTagEditText = (AutoCompleteTextView) mOverlayMemoView.findViewById(R.id.tag_EditText_Overlay);
        mSaveButton = mOverlayMemoView.findViewById(R.id.save_FAB_Overlay);
        mFab = mOverlayMemoView.findViewById(R.id.fab_Overlay);
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public boolean onTouch(View view, MotionEvent event) {
        float mInitialTouchX;
        float mInitialTouchY;
        // Viewを動かす
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN: {
                mInitialTouchX = event.getRawX();
                mInitialTouchY = event.getRawY();
                mPositionX = (int) mInitialTouchX;
                mPositionY = (int) mInitialTouchY;

                if (!mIsOpen) mIsClicked = true;
                break;
            }
            case MotionEvent.ACTION_MOVE: {
                if (mIsOpen) {
                    final int y = mDisplayHeight - (int) event.getRawY() - (mOverlayMemoView.getHeight() / 2);
                    mLayoutParams.y = y;
                } else {
                    final int x = mPositionX - (int) event.getRawX();
                    final int y = mPositionY - (int) event.getRawY();
                    mLayoutParams.x -= x;
                    mLayoutParams.y += y;
                    mPositionX = (int) event.getRawX();
                    mPositionY = (int) event.getRawY();
                    mIsClicked = false;
                }
                Logger.d(LOG_TAG, "X:" + mLayoutParams.x + " Y:" + mLayoutParams.y);
                mWindowManager.updateViewLayout(view, mLayoutParams);
                break;
            }
            case MotionEvent.ACTION_UP: {
                if (!mIsOpen && mIsClicked) {
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