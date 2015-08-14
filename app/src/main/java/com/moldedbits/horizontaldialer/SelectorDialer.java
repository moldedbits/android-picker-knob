package com.moldedbits.horizontaldialer;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Build;
import android.support.annotation.NonNull;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;

/**
 * Created by shubham on 13/08/15.
 */
public class SelectorDialer extends View {

    /** Unit used for the velocity tracker */
    private static final int PIXELS_PER_SECOND = 1;

    private int mDistance = 20;

    private int mHeight = 30;

    private float mRadius;

    private Paint mPaint;

    private int mCount;

    private float mRotation = (float) (Math.PI / 4);

    private float mInitVelocity = .5f;

    private static final float DECELARATION = 0.5f;

    private long mCurrentTime;

    /** User is not touching the list */
    private static final int TOUCH_STATE_RESTING = 0;

    /** User is touching the list and right now it's still a "click" */
    private static final int TOUCH_STATE_CLICK = 1;

    /** User is scrolling the list */
    private static final int TOUCH_STATE_SCROLL = 2;

    /** Distance to drag before we intercept touch events */
    private static final int TOUCH_SCROLL_THRESHOLD = 10;

    /** Velocity tracker used to get fling velocities */
    private VelocityTracker mVelocityTracker;

    /** X-coordinate of the down event */
    private int mTouchStartX;

    /** Y-coordinate of the down event */
    private int mTouchStartY;

    /** Current touch state */
    private int mTouchState = TOUCH_STATE_RESTING;

    private double mTouchStartAngle;

    Runnable mDynamicsRunnable = new Runnable() {
        @Override
        public void run() {
            long mNewTime = System.nanoTime();
            long nanoTime = (mNewTime - mCurrentTime);
            double time = ((double) nanoTime) / 1000000000;
            mCurrentTime = mNewTime;
            if(mInitVelocity > 0) {
                float finalVelocity = (float) (mInitVelocity - DECELARATION * time);
                mRotation = (float) ( mRotation + finalVelocity * time );
                invalidate();
                SelectorDialer.this.postDelayed(mDynamicsRunnable, 1000 / 60);
                mInitVelocity = finalVelocity;
            } else {
                float finalVelocity = (float) (mInitVelocity + DECELARATION * time);
                mRotation = (float) (mRotation + finalVelocity * time);
                invalidate();
                SelectorDialer.this.postDelayed(mDynamicsRunnable, 1000 / 60);
                mInitVelocity = finalVelocity;
            }
        }
    };

    public SelectorDialer(Context context) {
        super(context);
        init();
    }

    public SelectorDialer(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public SelectorDialer(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public SelectorDialer(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init();
    }

    private void init() {
        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPaint.setStyle(Paint.Style.FILL);
        mPaint.setColor(Color.BLUE);
        mCurrentTime = System.nanoTime();
//        postDelayed(mDynamicsRunnable, 1000 / 60);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        setMeasuredDimension(800, 100);
        mRadius = getMeasuredWidth()/ 2;
        mCount = (int)(Math.floor(2 * Math.PI * mRadius)/ mDistance)/2;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        int startPosition = (int)Math.ceil((mRadius * mRotation)/ mDistance);
        for(int i = startPosition; i < mCount + startPosition; ++i) {
            float theta = (i * mDistance )/mRadius;
            theta = theta - mRotation;
            float x = (float) (mRadius * (1 - Math.cos(theta)));
            canvas.drawLine(x, mHeight *(i % 2 + 1), x, 0, mPaint);
        }
    }

    @Override
    public boolean onTouchEvent(@NonNull MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                startTouch(event);
                return true;

            case MotionEvent.ACTION_MOVE:
                if(startScrollIfNeeded(event)){
                    processTouch(event);
                    return true;
                } else {
                    return false;
                }

            case MotionEvent.ACTION_UP:
                processTouch(event);
                return true;

            default:
                endTouch(0);
                return false;
        }
    }


    public boolean processTouch(final MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                startTouch(event);
                break;

            case MotionEvent.ACTION_MOVE:
                if (mTouchState == TOUCH_STATE_CLICK) {
                    startScrollIfNeeded(event);
                }
                if (mTouchState == TOUCH_STATE_SCROLL) {
                    mVelocityTracker.addMovement(event);
                    rotate((int)event.getX());
                }
                break;

            case MotionEvent.ACTION_UP:
                float velocity = 0;
                if (mTouchState == TOUCH_STATE_SCROLL) {
                    mVelocityTracker.addMovement(event);
                    mVelocityTracker.computeCurrentVelocity(PIXELS_PER_SECOND);
                    velocity = -1 * mVelocityTracker.getXVelocity() / 2;
                }
                endTouch(velocity);
                break;

            default:
                endTouch(0);
                break;
        }
        return true;
    }

    /**
     * Sets and initializes all things that need to when we start a touch
     * gesture.
     *
     * @param event The down event
     */
    private void startTouch(final MotionEvent event) {
        // user is touching the list -> no more fling
        removeCallbacks(mDynamicsRunnable);

        // save the start place
        mTouchStartX = (int)event.getX();
        mTouchStartY = (int)event.getY();
        mTouchStartAngle = Math.acos((mRadius - mTouchStartX)/ mRadius);
        Log.d("test", "start rotation = " + mTouchStartAngle);

        // obtain a velocity tracker and feed it its first event
        mVelocityTracker = VelocityTracker.obtain();
        mVelocityTracker.addMovement(event);

        // we don't know if it's a click or a scroll yet, but until we know
        // assume it's a click
        mTouchState = TOUCH_STATE_CLICK;
    }

    /**
     * Checks if the user has moved far enough for this to be a scroll and if
     * so, sets the list in scroll mode
     *
     * @param event The (move) event
     * @return true if scroll was started, false otherwise
     */
    private boolean startScrollIfNeeded(final MotionEvent event) {
        final int xPos = (int)event.getX();
        final int yPos = (int)event.getY();
        if (xPos < mTouchStartX - TOUCH_SCROLL_THRESHOLD
                || xPos > mTouchStartX + TOUCH_SCROLL_THRESHOLD
                || yPos < mTouchStartY - TOUCH_SCROLL_THRESHOLD
                || yPos > mTouchStartY + TOUCH_SCROLL_THRESHOLD) {
            // we've moved far enough for this to be a scroll
            mTouchState = TOUCH_STATE_SCROLL;
            return true;
        }
        return false;
    }

    /**
     * Resets and recycles all things that need to when we end a touch gesture
     *
     * @param velocity The velocity of the gesture
     */
    private void endTouch(final float velocity) {
        // recycle the velocity tracker
        mVelocityTracker.recycle();
        mVelocityTracker = null;

        mInitVelocity = velocity;
        post(mDynamicsRunnable);

        // reset touch state
        mTouchState = TOUCH_STATE_RESTING;
    }

    private void rotate(int finalX) {
        float deltaX = mRadius - finalX;
        if(deltaX > mRadius) {
            deltaX = mRadius;
        }
        if(deltaX < -1 * mRadius) {
            deltaX = -1 * mRadius;
        }
        double currentTouchAngle = Math.acos((deltaX)/ mRadius);
        double delta = mTouchStartAngle - currentTouchAngle ;
        mTouchStartAngle = currentTouchAngle;
        mRotation = (float) (mRotation + delta);
        if(mRotation < 0) {
            mRotation = 0;
        }
        if(mRotation > Math.PI) {
            mRotation = (float) (Math.PI);
        }
        Log.d("rotation", "rotation is " + mRotation);
        invalidate();
    }
}
