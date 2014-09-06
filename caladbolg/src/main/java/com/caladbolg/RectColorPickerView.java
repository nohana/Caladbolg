package com.caladbolg;

import android.content.*;
import android.graphics.*;
import android.graphics.Paint.*;
import android.graphics.PorterDuff.*;
import android.graphics.Shader.*;
import android.util.*;
import android.view.*;

public class RectColorPickerView extends View {
    private static final String TAG = RectColorPickerView.class.getSimpleName();

    private Rect mColorHVParretRect;
    private Rect mColorSSliderRect;

    private Path mColorSSliderPointerPath;

    private Paint mColorHVParretPaint;
    private Paint mColorSSliderPaint;
    private Paint mColorPointerPaint;
    private Paint mColorPointerOuterPaint;
    private Paint mColorSSliderPointerPaint;

    private LinearGradient valueGradient;
    private float[] mColorHSV = new float[] { 0f, 0f, 1f };
    private OnChangeColorListener mListener;

    public RectColorPickerView(Context context) {
        super(context);
        init();
    }

    public RectColorPickerView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public RectColorPickerView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        canvas.drawRect(mColorHVParretRect, mColorHVParretPaint);
        canvas.drawRect(mColorSSliderRect, mColorSSliderPaint);

        float sliderPoinerY = mColorHSV[1] * mColorSSliderRect.height();
        int sliderPointerHeight = mColorSSliderRect.left - mColorHVParretRect.right;
        int sliderPointerWidth = (int) (sliderPointerHeight * 0.7);
        mColorSSliderPointerPath.reset();
        mColorSSliderPointerPath.moveTo(mColorSSliderRect.right, sliderPoinerY);
        mColorSSliderPointerPath.lineTo(mColorSSliderRect.right + sliderPointerWidth, sliderPoinerY + sliderPointerHeight / 2);
        mColorSSliderPointerPath.lineTo(mColorSSliderRect.right + sliderPointerWidth, sliderPoinerY - sliderPointerHeight / 2);
        mColorSSliderPointerPath.lineTo(mColorSSliderRect.right, sliderPoinerY);
        canvas.drawPath(mColorSSliderPointerPath, mColorSSliderPointerPaint);

        int size = Math.min(mColorHVParretRect.width(), mColorHVParretRect.height());
        float pointerRadius = 0.05f * size;
        float pointerOuterRadius = 0.06f * size;
        float colorPointX = mColorHSV[0] / 360f * mColorHVParretRect.width();
        float colorPointY = (1f - mColorHSV[2]) * mColorHVParretRect.height();

        canvas.drawCircle(colorPointX, colorPointY, pointerOuterRadius, mColorPointerOuterPaint);
        canvas.drawCircle(colorPointX, colorPointY, pointerRadius, mColorPointerPaint);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);

        int parretWidth = w / 5 * 4;
        int sliderWidth = (w - parretWidth) / 5 * 3;
        int sliderMargin = (w - parretWidth) / 5;

        mColorHVParretRect.set(0, 0, parretWidth, h);
        mColorSSliderRect.set(parretWidth + sliderMargin, 0, parretWidth + sliderMargin + sliderWidth, h);

        valueGradient = new LinearGradient(0, 0, 0, getHeight(), 0x00000000, 0xFF000000, TileMode.CLAMP);
        setPaintColors();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
            case MotionEvent.ACTION_MOVE:

                float x = event.getX();
                float y = event.getY();

                if (!(0 <= y && y <= getHeight())) return true;

                if (x <= mColorHVParretRect.width()) {
                    mColorHSV[0] = (x / mColorHVParretRect.width()) * 360f;
                    mColorHSV[2] = 1f - y / mColorHVParretRect.height();

                    mListener.onChangeColor(getColor());
                    setPaintColors();
                    invalidate();
                }
                else if (mColorSSliderRect.left <= x && x <= mColorSSliderRect.right) {
                    mColorHSV[1] = y / mColorSSliderRect.height();

                    mListener.onChangeColor(getColor());
                    setPaintColors();
                    invalidate();
                }

                return true;
        }
        return super.onTouchEvent(event);
    }

    public void setColor(int color) {
        Color.colorToHSV(color, mColorHSV);
        if (isActivated()) {
            setPaintColors();
        }
    }

    public int getColor() {
        return Color.HSVToColor(mColorHSV);
    }

    private void setPaintColors() {
        int colorCount = 12;
        int colorStep = 360 / 12;
        int colors[] = new int[colorCount + 1];
        float hsv[] = new float[]{0f, mColorHSV[1], 1f};
        for (int i = 0; i < colors.length; i++) {
            hsv[0] = i * colorStep;
            colors[i] = Color.HSVToColor(hsv);
        }
        colors[colorCount] = colors[0];

        LinearGradient hueGradient = new LinearGradient(0, 0, mColorHVParretRect.width(), 0, colors, null, TileMode.CLAMP);
        ComposeShader composeShader = new ComposeShader(hueGradient, valueGradient, Mode.SRC_OVER);
        mColorHVParretPaint.setShader(composeShader);

        LinearGradient sliderGradient = new LinearGradient(0, 0, 0, getHeight(),
                Color.HSVToColor(new float[]{mColorHSV[0], 0f, mColorHSV[2]}),
                Color.HSVToColor(new float[]{mColorHSV[0], 1f, mColorHSV[2]}), TileMode.CLAMP);
        mColorSSliderPaint.setShader(sliderGradient);

        mColorPointerPaint.setColor(getColor());
    }

    private void init() {
        setLayerType(LAYER_TYPE_SOFTWARE, null);

        mColorHVParretRect = new Rect();
        mColorSSliderRect = new Rect();
        mColorSSliderPointerPath = new Path();

        mColorHVParretPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mColorHVParretPaint.setStyle(Style.FILL_AND_STROKE);

        mColorSSliderPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mColorSSliderPaint.setStyle(Style.FILL_AND_STROKE);

        mColorPointerPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mColorPointerPaint.setStyle(Style.FILL_AND_STROKE);

        mColorPointerOuterPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mColorPointerOuterPaint.setStyle(Style.FILL_AND_STROKE);
        mColorPointerOuterPaint.setColor(Color.WHITE);

        mColorSSliderPointerPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mColorSSliderPointerPaint.setStyle(Style.FILL_AND_STROKE);
        mColorSSliderPointerPaint.setColor(Color.WHITE);
    }

    public void setOnChangeColorListener(OnChangeColorListener listener) {
        this.mListener = listener;
    }
}
