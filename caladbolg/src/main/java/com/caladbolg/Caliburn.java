package com.caladbolg;

import android.app.*;
import android.graphics.*;
import android.os.*;
import android.view.*;

public class Caliburn extends Fragment implements OnChangeColorListener {
    private static final int REQUEST_CODE = 100;
    private static final String STATE_COLOR = "com.caladbolg.Caliburn.STATE_COLOR";

    private int mColor = Color.WHITE;
    private OnChangeColorListener mListener;

    public static Caliburn newInstance() {
        return new Caliburn();
    }

    public static Caliburn newInstance(Fragment fragment) {
        Caliburn caliburn = new Caliburn();
        caliburn.setTargetFragment(fragment, REQUEST_CODE);
        return caliburn;
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
            throw new ClassCastException(getActivity().getString(R.string.callback_implement_msg_caliburn));
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
        return inflater.inflate(R.layout.fragment_caliburn, null);
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
