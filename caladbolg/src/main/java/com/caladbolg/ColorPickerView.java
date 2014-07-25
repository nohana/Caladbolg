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
    private Paint colorWheelPaint;
    private RectF outerWheelRect;
    private RectF innerWheelRect;

    private int paramInnerPadding = 0;
    private int paramOuterPadding = 4;
    private int paramValueSliderWidth = 10; // width of the value slider
    private int paramColorCount = 20;

    private Paint colorPointerPaint;
    private Paint colorPointerOuterPaint;
    private Paint colorPointerDividerPaint;

    private Path valuePointerPath;
    private Path valuePointerOuterPath;
    private Path valuePointerDividerPath;
    private Paint valuePointerOuterPaint;
    private Paint valuePointerDividerPaint;

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
    private OnChangeColorListener onChangeColorListener;

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
        Color.colorToHSV(rgb, colorHSV);
        valueArcStartDegree = colorHSV[2] * 180;

        invalidate();
    }

    private void init() {
        colorPointerPaint = new Paint();
        colorPointerPaint.setAntiAlias(true);
        colorPointerPaint.setStyle(Style.FILL_AND_STROKE);

        colorPointerOuterPaint = new Paint();
        colorPointerOuterPaint.setStyle(Style.STROKE);
        colorPointerOuterPaint.setStrokeWidth(7f);
        colorPointerOuterPaint.setColor(Color.WHITE);
        colorPointerOuterPaint.setAntiAlias(true);

        colorPointerDividerPaint = new Paint();
        colorPointerDividerPaint.setStyle(Style.STROKE);
        colorPointerDividerPaint.setStrokeWidth(2f);
        colorPointerDividerPaint.setColor(Color.GRAY);
        colorPointerDividerPaint.setAntiAlias(true);

        colorWheelPaint = new Paint();
        colorWheelPaint.setAntiAlias(true);
        colorWheelPaint.setDither(true);

        outerWheelRect = new RectF();
        innerWheelRect = new RectF();

        valueSliderPaint = new Paint();
        valueSliderPaint.setAntiAlias(true);
        valueSliderPaint.setDither(true);

        valueSliderPath = new Path();

        valuePointerPath = new Path();
        valuePointerOuterPath = new Path();
        valuePointerDividerPath = new Path();

        valuePointerOuterPaint = new Paint();
        valuePointerOuterPaint.setStyle(Style.FILL_AND_STROKE);
        valuePointerOuterPaint.setColor(Color.WHITE);
        valuePointerOuterPaint.setAntiAlias(true);

        valuePointerDividerPaint = new Paint();
        valuePointerDividerPaint.setStyle(Style.FILL_AND_STROKE);
        valuePointerDividerPaint.setColor(Color.GRAY);
        valuePointerDividerPaint.setAntiAlias(true);
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


        // drawing color wheel pointer

        float hueAngle = (float) Math.toRadians(colorHSV[0]);
        float colorPointX = (float) ((-Math.cos(hueAngle) * colorHSV[1] * colorWheelRadius) + centerX);
        float colorPointY = (float) ((-Math.sin(hueAngle) * colorHSV[1] * colorWheelRadius) + centerY);

        float pointerRadius = 0.12f * colorWheelRadius;
        colorPointerPaint.setColor(Color.HSVToColor(colorHSV));
        canvas.drawCircle(colorPointX, colorPointY, pointerRadius, colorPointerPaint);
        canvas.drawCircle(colorPointX, colorPointY, pointerRadius + 1f, colorPointerDividerPaint);
        canvas.drawCircle(colorPointX, colorPointY, pointerRadius+7f, colorPointerOuterPaint);

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

        // drawing color value slider selector
        canvas.drawPath(valuePointerOuterPath, valuePointerOuterPaint);
        canvas.drawPath(valuePointerDividerPath, valuePointerDividerPaint);
        canvas.drawPath(valuePointerPath, colorPointerPaint);
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

        float valuePointerHeight = (float) (2 * outerWheelRadius * Math.PI / (paramColorCount * 2));
        float valuePointerOuterWidth = Math.max(5f, valuePointerHeight / 10);
        float valuePointerDividerWidth = Math.max(2f, valuePointerOuterWidth / 10);
        float valuePointerRight = centerX - ((outerWheelRadius + innerWheelRadius) / 2 + innerWheelRadius) / 2;
        float valuePointerLeft = -valuePointerRight;
        float valuePointerTop = centerY - valuePointerHeight / 2;
        float valuePointerBottom = valuePointerTop + valuePointerHeight;

        valuePointerPath.addRoundRect(new RectF(valuePointerLeft, valuePointerTop,
                valuePointerRight, valuePointerBottom), valuePointerHeight / 2, valuePointerHeight / 2 , Direction.CCW);
        valuePointerOuterPath.addRoundRect(new RectF(valuePointerLeft, valuePointerTop - valuePointerOuterWidth,
                valuePointerRight + valuePointerOuterWidth, valuePointerBottom + valuePointerOuterWidth),
                valuePointerHeight / 2 + valuePointerOuterWidth, valuePointerHeight / 2 + valuePointerOuterWidth, Direction.CCW);
        valuePointerDividerPath.addRoundRect(new RectF(valuePointerLeft, valuePointerTop - valuePointerDividerWidth,
                        valuePointerRight + valuePointerDividerWidth, valuePointerBottom + valuePointerDividerWidth),
                valuePointerHeight / 2 + valuePointerDividerWidth, valuePointerHeight / 2 + valuePointerDividerWidth, Direction.CCW
        );
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

                    int rgb = Color.HSVToColor(colorHSV);
                    if (onChangeColorListener != null)
                        onChangeColorListener.onChangeColor(rgb);

                    previousTouchDegree = degree;
                    isSlidingValue = true;

                    invalidate();
                }
                else if (d <= colorWheelRadius) {
                    colorHSV[0] = (float) (Math.toDegrees(Math.atan2(dy, dx)) + 180f);
                    colorHSV[1] = Math.max(0f, Math.min(1f, (float) (d / colorWheelRadius)));

                    int rgb = Color.HSVToColor(colorHSV);
                    if (onChangeColorListener != null)
                        onChangeColorListener.onChangeColor(rgb);

                    invalidate();
                }

                return true;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                isSlidingValue = false;
        }
        return super.onTouchEvent(event);
    }

    public void setOnChangeColor(OnChangeColorListener listener) {
        this.onChangeColorListener = listener;
    }

    public interface OnChangeColorListener {
        void onChangeColor(int rgb);
    }
}
