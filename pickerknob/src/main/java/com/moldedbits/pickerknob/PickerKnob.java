package com.moldedbits.pickerknob;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Build;
import android.support.annotation.NonNull;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;

/**
 * Picker widget shaped like a knob.
 */
public class PickerKnob extends View {

    /** Unit used for the velocity tracker */
    private static final int RADIANS_PER_SECOND = 1;

    /** Minimum height of the view */
    private static final int MIN_HEIGHT_IN_DP = 30;

    /** Minimum width of the view */
    private static final int MIN_WIDTH_IN_DP = 150;

    /** The velocity below which the knob will stop rotating */
    private static final float VELOCITY_THRESHOLD = 0.05f;

    /** The left rotation threshold */
    private static final float MIN_ROTATION = (float) (-1 * Math.PI) / 2;

    /** Distance between dashes (in pixels) */
    private int mDashGap = 20;

    /** View height including dash and text */
    private int mViewHeight;

    /** Height of the bigger dash */
    private int mDashHeight;

    /** Total view width */
    private int mViewWidth;

    /** Radius of the knob */
    private float mRadius;

    /** Used to draw to the canvas */
    private Paint mPaint;

    /** Total number of dashes to draw */
    private int mTotalDashCount;

    /** Current knob rotation */
    private float mRotation ;

    /** Initial velocity when the user flings the knob */
    private float mInitVelocity = .5f;

    /** Knob deceleration */
    private float mDeceleration = 15f;

    /** Track the system time to update knob position */
    private long mCurrentTime;

    /** Minimum value for the knob. This can be set from the XML */
    private int mMinValue = 0;

    /** Maximum value for the knob. This can be set from the XML */
    private int mMaxValue = 10;

    /** Count of smaller dashes between two larger dashes. This can be set from the XML */
    private int mDashCount = 4;

    /** Maximum rotation allowed for the knob. This depends on the max value */
    private float mMaxRotation;

    /** Text size for the values on top */
    private int mTextSize;

    /** Padding between the text and the dashes */
    private int mTextPadding;

    /** Dash color */
    private int mLineColor;

    /** Text color */
    private int mTextColor;

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

    /** Rotation of the point where the touch started */
    private double mTouchStartAngle;

    /** Update listener */
    private OnValueChangeListener mUpdateListener;

    private int mStartValue;

    public interface OnValueChangeListener {
        void onValueUpdated(int newValue);
    }

    /** Physics implementation */
    Runnable mDynamicsRunnable = new Runnable() {
        @Override
        public void run() {
            if(Math.abs(mInitVelocity) < VELOCITY_THRESHOLD) {
                return;
            }
            long newTime = System.nanoTime();
            long deltaNano = (newTime - mCurrentTime);
            double deltaSecs = ((double) deltaNano) / 1000000000;
            mCurrentTime = newTime;
            float finalVelocity;
            if(mInitVelocity > 0) {
                finalVelocity = (float) (mInitVelocity - mDeceleration * deltaSecs);
            } else {
                finalVelocity = (float) (mInitVelocity + mDeceleration * deltaSecs);
            }
            if(mInitVelocity * finalVelocity < 0) {
                return;
            }
            rotate(finalVelocity * deltaSecs);
            PickerKnob.this.postDelayed(mDynamicsRunnable, 1000 / 60);
            mInitVelocity = finalVelocity;
        }
    };

    public PickerKnob(Context context) {
        super(context);
        init(context, null);
    }

    public PickerKnob(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public PickerKnob(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    public void setPositionListener(OnValueChangeListener listener) {
        mUpdateListener = listener;
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public PickerKnob(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(context, attrs);
    }

    private void init(Context context, AttributeSet attrs) {
        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPaint.setStyle(Paint.Style.FILL);
        mPaint.setColor(Color.BLUE);
        mCurrentTime = System.nanoTime();

        if(attrs != null) {
            int[] attrsArray = new int[]{
                    android.R.attr.color,
            };
            TypedArray a = context.getTheme().obtainStyledAttributes(attrs, attrsArray, 0, 0);
            mLineColor = a.getColor(0, Color.GREEN);
            mPaint.setColor(mLineColor);
            a.recycle();

            a = context.getTheme().obtainStyledAttributes(attrs, R.styleable.PickerKnob, 0, 0);
            mMinValue = a.getInt(R.styleable.PickerKnob_picker_min_value, mMinValue);
            mMaxValue = a.getInt(R.styleable.PickerKnob_picker_max_value, mMaxValue);
            mTextSize = a.getDimensionPixelSize(R.styleable.PickerKnob_picker_text_size, 12);
            mTextPadding = a.getDimensionPixelSize(R.styleable.PickerKnob_picker_text_padding, 10);
            mDashGap = a.getDimensionPixelSize(R.styleable.PickerKnob_picker_dash_gap, 20);
            mTextColor = a.getColor(R.styleable.PickerKnob_picker_text_color, Color.BLACK);
            mDashCount = a.getInteger(R.styleable.PickerKnob_picker_dash_count, mDashCount);
            mDeceleration = a.getFloat(R.styleable.PickerKnob_picker_friction, mDeceleration);
            mStartValue = a.getInt(R.styleable.PickerKnob_picker_start_value,
                    (mMinValue + mMaxValue) / 2);
            a.recycle();
        }
        mPaint.setTextSize(mTextSize);

        mViewHeight = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, MIN_HEIGHT_IN_DP,
                context.getResources().getDisplayMetrics()) + mTextSize;

        mViewWidth = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, MIN_WIDTH_IN_DP,
                context.getResources().getDisplayMetrics());
    }

    public void setValue(int value) {
        if(value <= mMaxValue && value >= mMinValue) {
            mStartValue = value;
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int widthSize = MeasureSpec.getSize(widthMeasureSpec);
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        int heightSize = MeasureSpec.getSize(heightMeasureSpec);

        int width, height;

        //Measure Width
        if (widthMode == MeasureSpec.EXACTLY) {
            //Must be this size
            width = widthSize;
        } else if (widthMode == MeasureSpec.AT_MOST) {
            //Can't be bigger than...
            width = Math.min(mViewWidth, widthSize);
        } else {
            //Be whatever you want
            width = mViewWidth;
        }

        //Measure Height
        if (heightMode == MeasureSpec.EXACTLY) {
            //Must be this size
            height = heightSize;
        } else if (heightMode == MeasureSpec.AT_MOST) {
            //Can't be bigger than...
            height = Math.min(mViewHeight, heightSize);
        } else {
            //Be whatever you want
            height = mViewHeight;
        }

        setMeasuredDimension(width, height);

        updateCount();
    }

    private void updateCount() {
        mViewHeight = getMeasuredHeight();
        mDashHeight = mViewHeight - mTextSize - mTextPadding;

        mRadius = getMeasuredWidth()/ 2;

        mTotalDashCount = (mMaxValue - mMinValue);
        int visibleDashCount = (int) Math.ceil(Math.PI * mRadius / mDashGap);
        mMaxRotation = (float) ((mTotalDashCount * Math.PI / visibleDashCount) - Math.PI / 2);

        mRotation = (float) ((mDashGap * (mStartValue - mMinValue) / mRadius) - Math.PI / 2);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        int startPosition = (int)Math.ceil((mRadius * mRotation)/ mDashGap);
        startPosition = Math.max(0, startPosition);
        float oldX = -1;
        while(true) {
            float theta = (startPosition * mDashGap)/mRadius;
            if(startPosition > mTotalDashCount) {
                break;
            }
            theta = theta - mRotation;
            float x = (float) (mRadius * (1 - Math.cos(theta)));
            if(x < oldX) {
                break;
            }
            oldX = x;

            if(startPosition % (mDashCount + 1) == 0) {
                String text = String.valueOf(getValueAtPosition(startPosition));
                float textWidth = mPaint.measureText(text);
                mPaint.setColor(mTextColor);
                canvas.drawText(text, x - textWidth / 2, mTextSize, mPaint);
            }
            mPaint.setColor(mLineColor);
            canvas.drawLine(x, ((startPosition % (mDashCount + 1) == 0) ? 0 : mDashHeight / 2) + mTextSize + mTextPadding, x, mViewHeight, mPaint);
            startPosition++;
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
                return false;
        }
    }


    public boolean processTouch(final MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_MOVE:
                if (mTouchState == TOUCH_STATE_CLICK) {
                    startScrollIfNeeded(event);
                }
                if (mTouchState == TOUCH_STATE_SCROLL) {
                    mVelocityTracker.addMovement(event);
                    rotateOnTouch((int) event.getX());
                }
                break;

            case MotionEvent.ACTION_UP:
                float velocity = 0;
                if (mTouchState == TOUCH_STATE_SCROLL) {
                    mVelocityTracker.addMovement(event);
                    mVelocityTracker.computeCurrentVelocity(RADIANS_PER_SECOND);
                    velocity = -1 * mVelocityTracker.getXVelocity();
                }
                endTouch(velocity);
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
        mCurrentTime = System.nanoTime();
        mInitVelocity = velocity;
        post(mDynamicsRunnable);

        // reset touch state
        mTouchState = TOUCH_STATE_RESTING;
    }

    private void rotateOnTouch(int finalX) {
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
        rotate(delta);
    }

    private void rotate(double deltaTheta) {
        mRotation = (float) (mRotation + deltaTheta);
        mRotation = Math.max(mRotation, MIN_ROTATION);
        mRotation = Math.min(mRotation, mMaxRotation);
        invalidate();

        if (mUpdateListener != null) {
            int position = (int) Math.ceil(mRadius * (mRotation + Math.PI / 2) / mDashGap);
            mUpdateListener.onValueUpdated(getValueAtPosition(position));
        }
    }

    private int getValueAtPosition(int position) {
        return mMinValue + position;
    }
}
