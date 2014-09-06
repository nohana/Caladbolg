package com.caladbolg;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ComposeShader;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.Path;
import android.graphics.Path.Direction;
import android.graphics.PorterDuff;
import android.graphics.RadialGradient;
import android.graphics.RectF;
import android.graphics.Shader.TileMode;
import android.graphics.SweepGradient;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

public class ColorPickerView extends View {
    private static final String TAG = ColorPickerView.class.getSimpleName();

    private static final int mParamInnerPadding = 0;
    private static final int mParamOuterPadding = 4;
    private static final int mParamValueSliderWidth = 10; // width of the value slider
    private static final int mParamColorCount = 20;

    private int mInnerPadding;
    private int mOuterPadding;
    private int mValueSliderWidth;

    private int mOuterWheelRadius;
    private int mInnerWheelRadius;
    private int mColorWheelRadius;

    private Paint mColorWheelPaint;
    private RectF mOuterWheelRect;
    private RectF mInnerWheelRect;

    private Paint mColorPointerPaint;
    private Paint mColorPointerOuterPaint;
    private Paint mColorPointerDividerPaint;

    private Path mValuePointerPath;
    private Path mValuePointerOuterPath;
    private Path mValuePointerDividerPath;
    private Paint mValuePointerOuterPaint;
    private Paint mValuePointerDividerPaint;

    private Bitmap mColorWheelBitmap;
    private Paint mValueSliderPaint;
    private Path mValueSliderPath;

    /**
     * Currently selected color
     */
    private float[] mColorHSV = new float[]{0f, 0f, 1f};

    private boolean mIsSlidingValue;
    private float mValueArcStartDegree = 0;
    private double mPreviousTouchDegree;

    private OnChangeColorListener mOnChangeColorListener;

    public ColorPickerView(Context context) {
        super(context);
        init();
    }

    public ColorPickerView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public ColorPickerView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    public void setColor(int initialColor) {
        int rgb = initialColor & 0x00ffffff;
        Color.colorToHSV(rgb, mColorHSV);
        mValueArcStartDegree = (1f - mColorHSV[2]) * 180f;

        invalidate();
    }

    private void init() {
        mColorPointerPaint = new Paint();
        mColorPointerPaint.setAntiAlias(true);
        mColorPointerPaint.setStyle(Style.FILL_AND_STROKE);

        mColorPointerOuterPaint = new Paint();
        mColorPointerOuterPaint.setStyle(Style.STROKE);
        mColorPointerOuterPaint.setStrokeWidth(7f);
        mColorPointerOuterPaint.setColor(Color.WHITE);
        mColorPointerOuterPaint.setAntiAlias(true);

        mColorPointerDividerPaint = new Paint();
        mColorPointerDividerPaint.setStyle(Style.STROKE);
        mColorPointerDividerPaint.setStrokeWidth(2f);
        mColorPointerDividerPaint.setColor(Color.GRAY);
        mColorPointerDividerPaint.setAntiAlias(true);

        mColorWheelPaint = new Paint();
        mColorWheelPaint.setAntiAlias(true);
        mColorWheelPaint.setDither(true);

        mOuterWheelRect = new RectF();
        mInnerWheelRect = new RectF();

        mValueSliderPaint = new Paint();
        mValueSliderPaint.setAntiAlias(true);
        mValueSliderPaint.setDither(true);

        mValueSliderPath = new Path();

        mValuePointerPath = new Path();
        mValuePointerOuterPath = new Path();
        mValuePointerDividerPath = new Path();

        mValuePointerOuterPaint = new Paint();
        mValuePointerOuterPaint.setStyle(Style.FILL_AND_STROKE);
        mValuePointerOuterPaint.setColor(Color.WHITE);
        mValuePointerOuterPaint.setAntiAlias(true);

        mValuePointerDividerPaint = new Paint();
        mValuePointerDividerPaint.setStyle(Style.FILL_AND_STROKE);
        mValuePointerDividerPaint.setColor(Color.GRAY);
        mValuePointerDividerPaint.setAntiAlias(true);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int widthSize = MeasureSpec.getSize(widthMeasureSpec);
        int heightSize = MeasureSpec.getSize(heightMeasureSpec);
        int size = widthSize < heightSize ? widthMeasureSpec : heightMeasureSpec;
        setMeasuredDimension(size, size);
    }

    @SuppressLint("DrawAllocation")
    @Override
    protected void onDraw(Canvas canvas) {

        int centerX = getWidth() / 2;
        int centerY = getHeight() / 2;

        // drawing color wheel

        canvas.drawBitmap(mColorWheelBitmap, centerX - mColorWheelRadius, centerY - mColorWheelRadius, null);


        // drawing color wheel pointer

        float hueAngle = (float) Math.toRadians(mColorHSV[0]);
        float colorPointX = (float) ((-Math.cos(hueAngle) * mColorHSV[1] * mColorWheelRadius) + centerX);
        float colorPointY = (float) ((-Math.sin(hueAngle) * mColorHSV[1] * mColorWheelRadius) + centerY);

        float pointerRadius = 0.12f * mColorWheelRadius;
        mColorPointerPaint.setColor(Color.HSVToColor(mColorHSV));
        canvas.drawCircle(colorPointX, colorPointY, pointerRadius, mColorPointerPaint);
        canvas.drawCircle(colorPointX, colorPointY, pointerRadius + 1f, mColorPointerDividerPaint);
        canvas.drawCircle(colorPointX, colorPointY, pointerRadius + 7f, mColorPointerOuterPaint);

        // drawing value slider

        float[] hsv = new float[]{mColorHSV[0], mColorHSV[1], 1f};
        float sweepAngleStep = 180f / mParamColorCount;

        for(int i = 0; i <= mParamColorCount; i++) {
            hsv[2] = 1f / mParamColorCount * i;
            mValueSliderPaint.setColor(Color.HSVToColor(hsv));
            mValueSliderPaint.setAntiAlias(true);

            mValueSliderPath.reset();
            mValueSliderPath.arcTo(mOuterWheelRect, (float) (mValueArcStartDegree - (i - 0.5) * sweepAngleStep), -sweepAngleStep);
            mValueSliderPath.arcTo(mInnerWheelRect, (float) (-180 + mValueArcStartDegree + (mParamColorCount - i - 0.5) * sweepAngleStep), sweepAngleStep);
            canvas.drawPath(mValueSliderPath, mValueSliderPaint);

            mValueSliderPath.reset();
            mValueSliderPath.arcTo(mOuterWheelRect, (float) (mValueArcStartDegree + (i - 0.5) * sweepAngleStep), sweepAngleStep);
            mValueSliderPath.arcTo(mInnerWheelRect, (float) (180 + mValueArcStartDegree - (mParamColorCount - i - 0.5) * sweepAngleStep), -sweepAngleStep);
            canvas.drawPath(mValueSliderPath, mValueSliderPaint);
        }

        // drawing color value slider selector
        canvas.drawPath(mValuePointerOuterPath, mValuePointerOuterPaint);
        canvas.drawPath(mValuePointerDividerPath, mValuePointerDividerPaint);
        canvas.drawPath(mValuePointerPath, mColorPointerPaint);
    }

    @Override
    protected void onSizeChanged(int width, int height, int oldw, int oldh) {
        int size = Math.min(width, height);
        int centerX = width / 2;
        int centerY = height / 2;

        mInnerPadding = mParamInnerPadding * size / 100;
        mOuterPadding = mParamOuterPadding * size / 100;
        mValueSliderWidth = mParamValueSliderWidth * size / 100;

        mOuterWheelRadius = size / 2 - mOuterPadding;
        mInnerWheelRadius = mOuterWheelRadius - mValueSliderWidth;
        mColorWheelRadius = mInnerWheelRadius - mInnerPadding;

        mOuterWheelRect.set(centerX - mOuterWheelRadius, centerY - mOuterWheelRadius, centerX + mOuterWheelRadius, centerY + mOuterWheelRadius);
        mInnerWheelRect.set(centerX - mInnerWheelRadius, centerY - mInnerWheelRadius, centerX + mInnerWheelRadius, centerY + mInnerWheelRadius);

        mColorWheelBitmap = createmColorWheelBitmap(mColorWheelRadius * 2, mColorWheelRadius * 2);

        float valuePointerHeight = (float) (2 * mOuterWheelRadius * Math.PI / (mParamColorCount * 2));
        float valuePointerOuterWidth = Math.max(5f, valuePointerHeight / 10);
        float valuePointerDividerWidth = Math.max(2f, valuePointerOuterWidth / 10);
        float valuePointerRight = centerX - ((mOuterWheelRadius + mInnerWheelRadius) / 2 + mInnerWheelRadius) / 2;
        float valuePointerLeft = -valuePointerRight;
        float valuePointerTop = centerY - valuePointerHeight / 2;
        float valuePointerBottom = valuePointerTop + valuePointerHeight;

        mValuePointerPath.addRoundRect(new RectF(valuePointerLeft, valuePointerTop,
                valuePointerRight, valuePointerBottom), valuePointerHeight / 2, valuePointerHeight / 2, Direction.CCW);
        mValuePointerOuterPath.addRoundRect(new RectF(valuePointerLeft, valuePointerTop - valuePointerOuterWidth,
                        valuePointerRight + valuePointerOuterWidth, valuePointerBottom + valuePointerOuterWidth),
                valuePointerHeight / 2 + valuePointerOuterWidth, valuePointerHeight / 2 + valuePointerOuterWidth, Direction.CCW);
        mValuePointerDividerPath.addRoundRect(new RectF(valuePointerLeft, valuePointerTop - valuePointerDividerWidth,
                        valuePointerRight + valuePointerDividerWidth, valuePointerBottom + valuePointerDividerWidth),
                valuePointerHeight / 2 + valuePointerDividerWidth, valuePointerHeight / 2 + valuePointerDividerWidth, Direction.CCW
        );
    }

    private Bitmap createmColorWheelBitmap(int width, int height) {

        Bitmap bitmap = Bitmap.createBitmap(width, height, Config.ARGB_8888);

        int colorCount = 12;
        int colorAngleStep = 360 / 12;
        int colors[] = new int[colorCount + 1];
        float hsv[] = new float[]{0f, 1f, 1f};
        for (int i = 0; i < colors.length; i++) {
            hsv[0] = (i * colorAngleStep + 180) % 360;
            colors[i] = Color.HSVToColor(hsv);
        }
        colors[colorCount] = colors[0];

        SweepGradient sweepGradient = new SweepGradient(width / 2, height / 2, colors, null);
        RadialGradient radialGradient = new RadialGradient(width / 2, height / 2, mColorWheelRadius, 0xFFFFFFFF, 0x00FFFFFF, TileMode.CLAMP);
        ComposeShader composeShader = new ComposeShader(sweepGradient, radialGradient, PorterDuff.Mode.SRC_OVER);

        mColorWheelPaint.setShader(composeShader);

        Canvas canvas = new Canvas(bitmap);
        canvas.drawCircle(width / 2, height / 2, mColorWheelRadius, mColorWheelPaint);

        return bitmap;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        int action = event.getAction();
        switch (action) {
            case MotionEvent.ACTION_DOWN:
            case MotionEvent.ACTION_MOVE:

                int x = (int) event.getX();
                int y = (int) event.getY();
                int centerX = getWidth() / 2;
                int centerY = getHeight() / 2;
                int dx = x - centerX;
                int dy = y - centerY;
                double d = Math.sqrt(dx * dx + dy * dy);


                if (mIsSlidingValue || (d <= mOuterWheelRadius && d >= mInnerWheelRadius)) {
                    double degree = Math.toDegrees(Math.atan2(dy, dx));

                    if (mIsSlidingValue) {
                        float degreeDiff = (float) (degree - mPreviousTouchDegree);
                        mValueArcStartDegree = (mValueArcStartDegree + degreeDiff) % 360;
                        if (mValueArcStartDegree > 180) mValueArcStartDegree = mValueArcStartDegree - 360;
                        if (mValueArcStartDegree < -180) mValueArcStartDegree = mValueArcStartDegree + 360;
                    }

                    mColorHSV[2] = 1f - Math.abs(mValueArcStartDegree / 180);

                    int rgb = Color.HSVToColor(mColorHSV);
                    if (mOnChangeColorListener != null)
                        mOnChangeColorListener.onChangeColor(rgb);

                    mPreviousTouchDegree = degree;
                    mIsSlidingValue = true;

                    invalidate();
                } else if (d <= mColorWheelRadius) {
                    mColorHSV[0] = (float) (Math.toDegrees(Math.atan2(dy, dx)) + 180f);
                    mColorHSV[1] = Math.max(0f, Math.min(1f, (float) (d / mColorWheelRadius)));

                    int rgb = Color.HSVToColor(mColorHSV);
                    if (mOnChangeColorListener != null)
                        mOnChangeColorListener.onChangeColor(rgb);

                    invalidate();
                }

                return true;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                mIsSlidingValue = false;
        }
        return super.onTouchEvent(event);
    }

    public void setOnChangeColor(OnChangeColorListener listener) {
        this.mOnChangeColorListener = listener;
    }
}
