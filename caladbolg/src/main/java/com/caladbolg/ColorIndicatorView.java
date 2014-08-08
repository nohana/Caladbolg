package com.caladbolg;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.Path;
import android.graphics.Path.Direction;
import android.graphics.Rect;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

public class ColorIndicatorView extends View {
    private static final int TRANSPARENT = 0x00000000;
    private static final String TAG = ColorIndicatorView.class.getSimpleName();

    private Paint mBackgroundPaint;
    private Path mBackgroundPath;
    private RectF mBackgroundRectF;
    private Paint mFrontPaint;
    private Rect mFrontRect;
    private int mInnerMargin;

    public ColorIndicatorView(Context context) {
        super(context);
        init(null);
    }

    public ColorIndicatorView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(attrs);
    }

    public ColorIndicatorView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(attrs);
    }

    private void init(AttributeSet attrs) {
        int backgroundColor = TRANSPARENT;
        int frontColor = TRANSPARENT;
        int innerMargin = 0;

        if (attrs != null) {
            TypedArray ta = getContext().obtainStyledAttributes(attrs, R.styleable.ColorIndicatorView);
            backgroundColor = ta.getColor(0, Color.YELLOW);
            frontColor = ta.getColor(1, TRANSPARENT);
            innerMargin = ta.getDimensionPixelSize(2, 0);
        }

        mBackgroundPaint = new Paint();
        mBackgroundPaint.setColor(Color.YELLOW);
        mBackgroundPaint.setStyle(Style.FILL_AND_STROKE);

        mBackgroundPath = new Path();
        mBackgroundRectF = new RectF();

        mFrontPaint = new Paint();
        mFrontPaint.setColor(Color.BLACK);
        mFrontRect = new Rect();

        mInnerMargin = innerMargin;

        setBackgroundColor(TRANSPARENT);
    }

    public void setFrontColor(int color) {
        mFrontPaint.setColor(color);
        invalidate();
    }

    @Override
    public void setBackgroundColor(int color) {
        mBackgroundPaint.setColor(color);
        invalidate();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int widthSize = MeasureSpec.getSize(widthMeasureSpec);
        int heightSize = MeasureSpec.getSize(heightMeasureSpec);
        int size = Math.min(widthSize, heightSize);
        super.onMeasure(size, size);
        setMeasuredDimension(size, size);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        int size = Math.min(w, h);

        Log.d(TAG, String.valueOf(w));
        Log.d(TAG, String.valueOf(h));
        mBackgroundRectF.set(0, 0, size, size);
        mBackgroundPath.addRoundRect(mBackgroundRectF, size / 10, size / 10, Direction.CCW);

        mFrontRect.set(mInnerMargin, mInnerMargin, size - mInnerMargin, size - mInnerMargin);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        canvas.drawPath(mBackgroundPath, mBackgroundPaint);
//        canvas.drawRect(mFrontRect, mFrontPaint);
    }
}
