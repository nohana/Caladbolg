package com.sample;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.Toast;
import com.caladbolg.Caladbolg;
import com.caladbolg.Caladbolg.ColorPickerCallback;

public class SampleFragment extends Fragment implements ColorPickerCallback {
    private static final String TAG = SampleFragment.class.getSimpleName();
    private static final String SAVED_STATE_BACKGROUND_COLOR = "com.sample.SAVED_STATE_BACKGROUND_COLOR";

    Caladbolg mCaladbolg;
    LinearLayout mLayout;

    public static SampleFragment newInstance() {
        return new SampleFragment();
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        mCaladbolg = (Caladbolg) getFragmentManager().findFragmentByTag("caladbolg");
        if (mCaladbolg == null) {
            int color = Color.BLACK;
            if (mLayout.getBackground() instanceof ColorDrawable) {
                color = ((ColorDrawable) mLayout.getBackground()).getColor();
            }
            mCaladbolg = Caladbolg.newInstance(this, color);
            mCaladbolg.show(getFragmentManager(), "caladbolg");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mLayout = (LinearLayout) inflater.inflate(R.layout.fragment_sample, null);
        mLayout.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                mCaladbolg.show(getFragmentManager(), "caladbolg");
                return false;
            }
        });
        if (savedInstanceState != null) {
            mLayout.setBackgroundColor(savedInstanceState.getInt(SAVED_STATE_BACKGROUND_COLOR));
        }

        return mLayout;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
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
        Toast.makeText(getActivity(), "cancel", Toast.LENGTH_LONG).show();
    }
}
