package com.shohei.put_on.view.widget;

import android.content.Context;
import android.support.v4.view.MotionEventCompat;
import android.support.v4.view.ViewCompat;
import android.support.v4.widget.ViewDragHelper;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

import com.shohei.put_on.controller.utils.DebugUtil;

/**
 * Created by nakayamashohei on 15/08/29.
 */
public class SlideLayout extends ViewGroup {
    private final static String LOG_TAG = SlideLayout.class.getSimpleName();

    private ViewDragHelper mDragHelper;
    private View mTargetView;
    private OnCloseListener mCloseListener;

    private float mInitialMotionX;
    private float mInitialMotionY;

    private int mDragRange;
    private int mTop;
    private float mDragOffset;

    public SlideLayout(Context context) {
        this(context, null);
    }

    public SlideLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public SlideLayout(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    private void init() {
        setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
        mDragHelper = ViewDragHelper.create(this, 1.0f, new DragHelperCallback(mTargetView));
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        if (getChildCount() > 0) {
            mTargetView = getChildAt(0);
            setDragTargetView(mTargetView);
        }
    }

    public void setDragTargetView(View targetView) {
        mTargetView = targetView;
        mDragHelper = ViewDragHelper.create(this, 1.0f, new DragHelperCallback(mTargetView));
    }

    public void setOnCloseListener(OnCloseListener closeListener) {
        this.mCloseListener = closeListener;
    }

    boolean smoothSlideTo(float slideOffset) {
        final int topBound = getPaddingTop();
        int y = (int) (topBound + slideOffset * mDragRange);

        if (mDragHelper.smoothSlideViewTo(mTargetView, mTargetView.getLeft(), y)) {
            ViewCompat.postInvalidateOnAnimation(this);
            return true;
        }
        return false;
    }

    @Override
    public void computeScroll() {
        if (mDragHelper.continueSettling(true)) {
            ViewCompat.postInvalidateOnAnimation(this);
        }
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent event) {
        // タッチされたらまずonInterceptTouchEventが呼ばれる
        // ここでtrueを返せば親ViewのonTouchEvent
        // ここでfalseを返せば子ViewのonClickやらonLongClickやら

        if (DebugUtil.DEBUG) Log.d(LOG_TAG, "PointerCount: " + event.getAction());
        if (event.getPointerCount() == 1) {
            return false;
        }

//        mTargetView.setBackgroundColor(Color.BLUE);

        final int action = MotionEventCompat.getActionMasked(event);
        final float x = event.getX();
        final float y = event.getY();
        boolean interceptTap = false;

        switch (action) {
            case MotionEvent.ACTION_DOWN:
            case MotionEvent.ACTION_POINTER_DOWN: {
                if (DebugUtil.DEBUG) Log.d(LOG_TAG, "onInterceptTouchEvent/ACTION_DOWN");
                mInitialMotionX = x;
                mInitialMotionY = y;
                interceptTap = mDragHelper.isViewUnder(mTargetView, (int) x, (int) y);
                break;
            }
            case MotionEvent.ACTION_MOVE: {
                if (DebugUtil.DEBUG) Log.d(LOG_TAG, "onInterceptTouchEvent/ACTION_MOVE");
                final float adx = Math.abs(x - mInitialMotionX);
                final float ady = Math.abs(y - mInitialMotionY);
                final int slop = mDragHelper.getTouchSlop();
                if (DebugUtil.DEBUG) Log.d(LOG_TAG, "adx:" + " ady:" + ady + adx + " slop:" + slop);
                if (ady > slop && adx > ady) {
                    mDragHelper.cancel();
                    return false;
                } else {
                    return true;
                }
            }
            default: {
                mDragHelper.cancel();
            }
        }
        return mDragHelper.shouldInterceptTouchEvent(event) || interceptTap;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        // ここでtrueを返すとイベントはここで終了
        // ここでfalseを返すと子ViewのonClickやらonLongClickやら

        if (DebugUtil.DEBUG) Log.d(LOG_TAG, "onTouchEvent/PointerCount: " + event.getAction());
        if (event.getPointerCount() == 1) {
            return false;
        }

//        mTargetView.setBackgroundColor(Color.RED);

        if (DebugUtil.DEBUG) Log.d(LOG_TAG, "onTouchEvent");
        try {
            mDragHelper.processTouchEvent(event);
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
            return true;
        }

        final int action = event.getAction();
        final float x = event.getX();
        final float y = event.getY();

        boolean isHeaderViewUnder = mDragHelper.isViewUnder(mTargetView, (int) x, (int) y);

        switch (action) {
            case MotionEvent.ACTION_DOWN:
            case MotionEvent.ACTION_POINTER_DOWN: {
                if (DebugUtil.DEBUG) Log.d(LOG_TAG, "onTouchEvent/ACTION_DOWN");
                mInitialMotionX = x;
                mInitialMotionY = y;
                break;
            }
            case MotionEvent.ACTION_MOVE: {
                if (DebugUtil.DEBUG) Log.d(LOG_TAG, "onTouchEvent/ACTION_MOVE");
                final float adx = Math.abs(x - mInitialMotionX);
                final float ady = Math.abs(y - mInitialMotionY);
                final int slop = mDragHelper.getTouchSlop();
                if (ady > slop && adx > ady) {
                    mDragHelper.cancel();
                    return false;
                } else {
                    return true;
                }
            }
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_POINTER_UP: {
                if (DebugUtil.DEBUG) Log.d(LOG_TAG, "onTouchEvent/ACTION_UP");
                final float dx = x - mInitialMotionX;
                final float dy = y - mInitialMotionY;
                final int slop = mDragHelper.getTouchSlop();
                if (DebugUtil.DEBUG) Log.d(LOG_TAG, "mDragOffset: " + mDragOffset);
                if (dx * dx + dy * dy < slop * slop && isHeaderViewUnder) {
                    if (mDragOffset == 0 || (mDragOffset > 0.5f && mDragOffset != 1)) {
                        smoothSlideTo(1f);
                    } else {
                        smoothSlideTo(0f);
                    }
                }
                break;
            }
        }
        return super.onTouchEvent(event) && (isHeaderViewUnder && isViewHit(mTargetView, (int) x, (int) y));
    }

    private boolean isViewHit(View view, int x, int y) {
        int[] viewLocation = new int[2];
        view.getLocationOnScreen(viewLocation);
        int[] parentLocation = new int[2];
        this.getLocationOnScreen(parentLocation);
        int screenX = parentLocation[0] + x;
        int screenY = parentLocation[1] + y;
        return screenX >= viewLocation[0] && screenX < (viewLocation[0] + view.getWidth()) &&
                screenY >= viewLocation[1] && screenY < (viewLocation[1] + view.getHeight());
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        measureChildren(widthMeasureSpec, heightMeasureSpec);

        int maxWidth = MeasureSpec.getSize(widthMeasureSpec);
        int maxHeight = MeasureSpec.getSize(heightMeasureSpec);

        setMeasuredDimension(resolveSizeAndState(maxWidth, widthMeasureSpec, 0),
                resolveSizeAndState(maxHeight, heightMeasureSpec, 0));
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        mDragRange = getHeight() - mTargetView.getHeight();
        if (mTargetView != null) {
            mTargetView.layout(0, mTop, r, mTop + mTargetView.getMeasuredHeight());
        }
    }

    public interface OnCloseListener {
        public void onClosed(int vector);
    }

    private class DragHelperCallback extends ViewDragHelper.Callback {

        public DragHelperCallback(View view) {
            mTargetView = view;
        }

        @Override
        public boolean tryCaptureView(View child, int pointerId) {
            return child == mTargetView;
        }

        @Override
        public void onViewPositionChanged(View changedView, int left, int top, int dx, int dy) {
            if (DebugUtil.DEBUG) Log.d(LOG_TAG, "onViewPositionChanged");
            mTop = top;

            mDragOffset = (float) top / mDragRange;

            mTargetView.setPivotX(mTargetView.getWidth());
            mTargetView.setPivotY(mTargetView.getHeight());

            requestLayout();
        }

        @Override
        public void onViewReleased(View releasedChild, float xvel, float yvel) {
            int top = getPaddingTop();
            if (yvel > 0 || (yvel == 0 && mDragOffset > 0.5f)) {
                top += mDragRange;
            }
            mDragHelper.settleCapturedViewAt(releasedChild.getLeft(), top);
        }

        @Override
        public int getViewVerticalDragRange(View child) {
            return mDragRange;
        }

        @Override
        public int clampViewPositionHorizontal(View child, int left, int dx) {
            if (DebugUtil.DEBUG) Log.d(LOG_TAG, "clampViewPositionHorizontal " + left + "," + dx);

            int leftBound = getPaddingLeft();
            final int rightBound = getWidth() - child.getWidth();
            return Math.min(Math.max(left, leftBound), rightBound);
        }

        @Override
        public int clampViewPositionVertical(View child, int top, int dy) {
            if (DebugUtil.DEBUG) Log.d(LOG_TAG, "clampViewPositionVertical " + top + "," + dy);
            final int topBound = getPaddingTop();
            final int bottomBound = getHeight() - mTargetView.getHeight() - mTargetView.getPaddingBottom();

            final int newTop = Math.min(Math.max(top, topBound), bottomBound);
            return newTop;
        }
    }

}
