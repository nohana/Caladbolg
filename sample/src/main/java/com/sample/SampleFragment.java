package com.sample;

import android.graphics.Color;
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

    Caladbolg mCaladbolg;
    LinearLayout mLayout;

    public static SampleFragment newInstance() {
        return new SampleFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getFragmentManager().findFragmentByTag("caladbolg") == null) {
            mCaladbolg = Caladbolg.getInstance(this, Color.BLACK);
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

        return mLayout;
    }

    @Override
    public void onPickColor(int rgb, int alpha) {
        Log.v(SampleFragment.class.getSimpleName(), "RGB:" + rgb + " Alpha:" + alpha);
        mLayout.setBackgroundColor(rgb);
        mLayout.setAlpha((float) alpha);
     }

    @Override
    public void onCancel() {
        Toast.makeText(getActivity(), "cancel", Toast.LENGTH_LONG).show();
    }
}
