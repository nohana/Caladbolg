package com.sample;

import android.graphics.*;
import android.graphics.drawable.*;
import android.os.*;
import android.support.v7.app.*;
import android.widget.*;
import com.caladbolg.*;

public class Caladbolg2SampleActivity extends ActionBarActivity implements OnChangeColorListener {
    private static final String TAG = SampleActivity.class.getSimpleName();
    private static final String SAVED_STATE_BACKGROUND_COLOR = "com.sample.SAVED_STATE_BACKGROUND_COLOR";

    Caladbolg2 mCaladbolg;
    LinearLayout mLayout;

    @SuppressWarnings("ConstantConditions")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getActionBar().setTitle(Caladbolg2SampleActivity.class.getSimpleName());
        mLayout = (LinearLayout) getLayoutInflater().inflate(R.layout.activity_caladbolg2_sample, null);
        setContentView(mLayout);

        if (savedInstanceState != null) {
            mLayout.setBackgroundColor(savedInstanceState.getInt(SAVED_STATE_BACKGROUND_COLOR));
        }
        setContentView(mLayout);

        if (mCaladbolg == null) {
            int color = Color.BLACK;
            if (mLayout.getBackground() instanceof ColorDrawable) {
                color = ((ColorDrawable) mLayout.getBackground()).getColor();
            }
            mCaladbolg = (Caladbolg2) getFragmentManager().findFragmentById(R.id.fragment_caladbolg2);
            mCaladbolg.setColor(color);
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
    public void onChangeColor(int rgb) {
        mLayout.setBackgroundColor(rgb);
    }
}
