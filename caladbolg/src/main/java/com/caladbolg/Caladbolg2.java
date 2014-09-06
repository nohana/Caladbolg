package com.caladbolg;

import android.app.*;
import android.graphics.*;
import android.os.*;
import android.view.*;

public class Caladbolg2 extends Fragment implements OnChangeColorListener {
    private static final int REQUEST_CODE = 100;
    private static final String STATE_COLOR = "com.caladbolg.Caladbolg2.STATE_COLOR";

    private int mColor = Color.WHITE;
    private OnChangeColorListener mListener;

    public static Caladbolg2 newInstance() {
        return new Caladbolg2();
    }

    public static Caladbolg2 newInstance(Fragment fragment) {
        Caladbolg2 caladbolg2 = new Caladbolg2();
        caladbolg2.setTargetFragment(fragment, REQUEST_CODE);
        return caladbolg2;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        Fragment target = getTargetFragment();
        if (activity instanceof OnChangeColorListener) {
            mListener = (OnChangeColorListener) activity;
        }
        else if (target != null && target instanceof OnChangeColorListener) {
            mListener = (OnChangeColorListener) target;
        }
        else {
            throw new ClassCastException(getActivity().getString(R.string.callback_implement_msg_caladbolg2));
        }
    }

    public void setColor(int color) {
        mColor = color;
        if (null != getView()) {
            RectColorPickerView colorPicker = (RectColorPickerView) getView().findViewById(R.id.view_rect_color_picker);
            colorPicker.setColor(mColor);
        }
    }

    public int getColor() {
        return mColor;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_caladbolg2, null);
    }

    @SuppressWarnings("ConstantConditions")
    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        if (savedInstanceState != null) {
            mColor = savedInstanceState.getInt(STATE_COLOR);
        }

        RectColorPickerView colorPicker = (RectColorPickerView) getView().findViewById(R.id.view_rect_color_picker);
        colorPicker.setColor(mColor);
        colorPicker.setOnChangeColorListener(this);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(STATE_COLOR, mColor);
    }

    @Override
    public void onChangeColor(int rgb) {
        mColor = rgb;
        mListener.onChangeColor(rgb);
    }
}
