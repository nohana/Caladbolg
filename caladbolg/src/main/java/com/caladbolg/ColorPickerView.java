package com.caladbolg;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ComposeShader;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PorterDuff;
import android.graphics.RadialGradient;
import android.graphics.RectF;
import android.graphics.Shader.TileMode;
import android.graphics.SweepGradient;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

public class ColorPickerView extends View {
    private Paint colorWheelPaint;
    private RectF outerWheelRect;
    private RectF innerWheelRect;

    private int paramInnerPadding = 0;
    private int paramOuterPadding = 2;
    private int paramValueSliderWidth = 5; // width of the value slider
    private int paramColorCount = 20;

    private int innerPadding;
    private int outerPadding;
    private int valueSliderWidth;
    private int outerWheelRadius;
    private int innerWheelRadius;
    private int colorWheelRadius;
    private Bitmap colorWheelBitmap;
    private Paint valueSliderPaint;
    private Path valueSliderPath;


    /** Currently selected color */
    private float[] colorHSV = new float[] { 0f, 0f, 1f };

    private boolean isSlidingValue;
    private float valueArcStartDegree = 0;
    private double previousTouchDegree;

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

    private void init() {
        colorWheelPaint = new Paint();
        colorWheelPaint.setAntiAlias(true);
        colorWheelPaint.setDither(true);

        outerWheelRect = new RectF();
        innerWheelRect = new RectF();

        valueSliderPaint = new Paint();
        valueSliderPaint.setAntiAlias(true);
        valueSliderPaint.setDither(true);

        valueSliderPath = new Path();
    }


    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int widthSize = MeasureSpec.getSize(widthMeasureSpec);
        int heightSize = MeasureSpec.getSize(heightMeasureSpec);
        int size = Math.min(widthSize, heightSize);
        setMeasuredDimension(size, size);
    }

    @SuppressLint("DrawAllocation")
    @Override
    protected void onDraw(Canvas canvas) {

        int centerX = getWidth() / 2;
        int centerY = getHeight() / 2;

        // drawing color wheel

        canvas.drawBitmap(colorWheelBitmap, centerX - colorWheelRadius, centerY - colorWheelRadius, null);

        // drawing value slider

        float[] hsv = new float[] { colorHSV[0], colorHSV[1], 1f };
        float sweepAngleStep = 180f / paramColorCount;

        for(int i = 0; i < paramColorCount; i++) {
            hsv[2] = 1f / (paramColorCount-1) * i;
            valueSliderPaint.setColor(Color.HSVToColor(hsv));
            valueSliderPaint.setAntiAlias(true);

            valueSliderPath.reset();
            valueSliderPath.arcTo(outerWheelRect, valueArcStartDegree - i * sweepAngleStep, -sweepAngleStep);
            valueSliderPath.arcTo(innerWheelRect, -180 + valueArcStartDegree + (paramColorCount - i - 1) * sweepAngleStep, sweepAngleStep);
            canvas.drawPath(valueSliderPath, valueSliderPaint);

            valueSliderPath.reset();
            valueSliderPath.arcTo(outerWheelRect, valueArcStartDegree + i * sweepAngleStep, sweepAngleStep);
            valueSliderPath.arcTo(innerWheelRect, 180 + valueArcStartDegree - (paramColorCount - i - 1) * sweepAngleStep, -sweepAngleStep);
            canvas.drawPath(valueSliderPath, valueSliderPaint);
        }
    }

    @Override
    protected void onSizeChanged(int width, int height, int oldw, int oldh) {

        int centerX = width / 2;
        int centerY = height / 2;

        innerPadding = paramInnerPadding * width / 100;
        outerPadding = paramOuterPadding * width / 100;
        valueSliderWidth = paramValueSliderWidth * width / 100;

        outerWheelRadius = width / 2 - outerPadding;
        innerWheelRadius = outerWheelRadius - valueSliderWidth;
        colorWheelRadius = innerWheelRadius - innerPadding;

        outerWheelRect.set(centerX - outerWheelRadius, centerY - outerWheelRadius, centerX + outerWheelRadius, centerY + outerWheelRadius);
        innerWheelRect.set(centerX - innerWheelRadius, centerY - innerWheelRadius, centerX + innerWheelRadius, centerY + innerWheelRadius);

        colorWheelBitmap = createColorWheelBitmap(colorWheelRadius * 2, colorWheelRadius * 2);
    }

    private Bitmap createColorWheelBitmap(int width, int height) {

        Bitmap bitmap = Bitmap.createBitmap(width, height, Config.ARGB_8888);

        int colorCount = 12;
        int colorAngleStep = 360 / 12;
        int colors[] = new int[colorCount + 1];
        float hsv[] = new float[] { 0f, 1f, 1f };
        for (int i = 0; i < colors.length; i++) {
            hsv[0] = (i * colorAngleStep + 180) % 360;
            colors[i] = Color.HSVToColor(hsv);
        }
        colors[colorCount] = colors[0];

        SweepGradient sweepGradient = new SweepGradient(width / 2, height / 2, colors, null);
        RadialGradient radialGradient = new RadialGradient(width / 2, height / 2, colorWheelRadius, 0xFFFFFFFF, 0x00FFFFFF, TileMode.CLAMP);
        ComposeShader composeShader = new ComposeShader(sweepGradient, radialGradient, PorterDuff.Mode.SRC_OVER);

        colorWheelPaint.setShader(composeShader);

        Canvas canvas = new Canvas(bitmap);
        canvas.drawCircle(width / 2, height / 2, colorWheelRadius, colorWheelPaint);

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


                if (isSlidingValue || (d <= outerWheelRadius && d >= innerWheelRadius)) {
                    double degree = Math.toDegrees(Math.atan2(dy, dx));

                    if (isSlidingValue) {
                        float degreeDiff = (float) (degree - previousTouchDegree);
                        valueArcStartDegree = (valueArcStartDegree + degreeDiff) % 360;
                        if (valueArcStartDegree > 180) valueArcStartDegree = valueArcStartDegree - 360;
                        if (valueArcStartDegree < -180) valueArcStartDegree = valueArcStartDegree + 360;
                    }

                    colorHSV[2] = 1f - Math.abs(valueArcStartDegree / 180);

                    previousTouchDegree = degree;
                    isSlidingValue = true;

                    invalidate();
                }
                else if (d <= colorWheelRadius) {
                    colorHSV[0] = (float) (Math.toDegrees(Math.atan2(dy, dx)) + 180f);
                    colorHSV[1] = Math.max(0f, Math.min(1f, (float) (d / colorWheelRadius)));

                    invalidate();
                }

                return true;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                isSlidingValue = false;
        }
        return super.onTouchEvent(event);
    }
}
