package com.sample;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.Toast;
import com.caladbolg.Caladbolg;
import com.caladbolg.Caladbolg.ColorPickerCallback;


public class SampleActivity extends ActionBarActivity implements ColorPickerCallback{
    private static final String TAG = SampleActivity.class.getSimpleName();
    private static final String SAVED_STATE_BACKGROUND_COLOR = "com.sample.SAVED_STATE_BACKGROUND_COLOR";

    Caladbolg mCaladbolg;
    RelativeLayout mLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mLayout = (RelativeLayout) getLayoutInflater().inflate(R.layout.activity_sample, null);
        mLayout.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                mCaladbolg.show(getSupportFragmentManager(), "caladbolg");
                return false;
            }
        });
        if (savedInstanceState != null) {
            mLayout.setBackgroundColor(savedInstanceState.getInt(SAVED_STATE_BACKGROUND_COLOR));
        }
        setContentView(mLayout);

        mCaladbolg = (Caladbolg) getSupportFragmentManager().findFragmentByTag("caladbolg");
        if (mCaladbolg == null) {
            int color = Color.BLACK;
            if (mLayout.getBackground() instanceof ColorDrawable) {
                color = ((ColorDrawable) mLayout.getBackground()).getColor();
            }
            mCaladbolg = Caladbolg.getInstance(color);
            mCaladbolg.show(getSupportFragmentManager(), "caladbolg");
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if (mLayout.getBackground() instanceof ColorDrawable) {
            int color = ((ColorDrawable) mLayout.getBackground()).getColor();
            outState.putInt(SAVED_STATE_BACKGROUND_COLOR, color);
        }
    }

    @Override
    public void onPickColor(int rgb, int alpha) {
        Log.v(TAG, "RGB:" + rgb + " Alpha:" + alpha);
        int argb = Color.argb(alpha, Color.red(rgb), Color.green(rgb), Color.blue(rgb));
        mLayout.setBackgroundColor(argb);
    }

    @Override
    public void onCancel() {
        Toast.makeText(this, "cancel", Toast.LENGTH_LONG).show();
    }
}
