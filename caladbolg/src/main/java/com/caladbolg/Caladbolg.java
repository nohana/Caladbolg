package com.caladbolg;

import android.app.Activity;
import android.app.AlertDialog.Builder;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnFocusChangeListener;
import android.view.View.OnKeyListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;
import com.caladbolg.ColorPickerView.OnChangeColorListener;

public class Caladbolg extends DialogFragment implements OnClickListener,
        OnSeekBarChangeListener, TextWatcher, OnChangeColorListener, OnKeyListener, OnFocusChangeListener {
    private static final String KEY_INITIAL_COLOR = "key_initial_color";

    private int mRGB;
    private int mAlpha;

    private SeekBar mAlphaSeekBar;
    private View mColorIndicaterView;
    private TextView mColorCodeParamsText;
    private EditText mColorCodeEdit;
    private ColorPickerView mColorPickerView;

    private ColorPickerCallback mCallback;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        if (activity instanceof ColorPickerCallback) {
            mCallback = (ColorPickerCallback) activity;
        }
        else {
            throw new ClassCastException(activity.toString() + " must implement OnArticleSelectedListener");
        }

    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(KEY_INITIAL_COLOR, toARGB(mRGB, mAlpha));
    }

    public static Caladbolg getInstance(int initialColor) {
        Bundle args = new Bundle();
        args.putInt(KEY_INITIAL_COLOR, initialColor);
        Caladbolg caladbolg = new Caladbolg();
        caladbolg.setArguments(args);
        return caladbolg;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Bundle args = savedInstanceState == null ? getArguments() : savedInstanceState;
        int initialColor = args.getInt(KEY_INITIAL_COLOR);
        mAlpha = Color.alpha(initialColor);
        mRGB = toRGB(initialColor);

        View view = View.inflate(getActivity(), R.layout.dialog_caladbolg, null);
        mColorIndicaterView = view.findViewById(R.id.view_color_indicater);
        mColorCodeEdit = (EditText) view.findViewById(R.id.edit_text_color_code);
        mColorCodeEdit.addTextChangedListener(this);
        mColorCodeEdit.setOnKeyListener(this);
        mColorCodeEdit.setOnFocusChangeListener(this);
        mColorCodeParamsText = (TextView) view.findViewById(R.id.text_color_code_params);
        mColorPickerView = (ColorPickerView) view.findViewById(R.id.view_color_picker);
        mColorPickerView.setOnChangeColor(this);
        mColorPickerView.setColor(initialColor);
        mAlphaSeekBar = (SeekBar) view.findViewById(R.id.seek_bar_alpha);
        mAlphaSeekBar.setOnSeekBarChangeListener(this);

        setColorToIndicaters(mRGB, mAlpha);

        Builder builder = new Builder(getActivity());
        builder.setView(view);
        builder.setPositiveButton(getActivity().getString(R.string.dialog_positive_btn), this);
        builder.setNegativeButton(getActivity().getString(R.string.dialog_negative_btn), this);

        return builder.create();
    }

    @Override
    public void onChangeColor(int rgb) {
        mRGB = rgb;
        setColorToIndicaters(mRGB, mAlpha);
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        mAlpha = progress;
        setColorToIndicaters(mRGB, mAlpha);
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {}
    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {}

    private void setColorToIndicaters(int rgb, int alpha) {
        mColorIndicaterView.setBackgroundColor(toARGB(rgb, alpha));
        mColorCodeEdit.setText(getActivity().getString(R.string.color_fmt, toARGB(rgb, alpha)));
        mColorCodeParamsText.setText(getActivity()
                .getString(R.string.color_param_fmt, Color.red(rgb), Color.green(rgb), Color.blue(rgb), alpha));
        mColorCodeParamsText.requestFocus();
    }

    private void setColorToExceptEditText(int rgb, int alpha) {
        mColorIndicaterView.setBackgroundColor(toARGB(rgb, alpha));
        mColorCodeParamsText.setText(getActivity().getString(R.string.color_param_fmt, Color.red(rgb),
                Color.green(rgb), Color.blue(rgb), alpha));
        mColorPickerView.setColor(rgb);
        mAlphaSeekBar.setProgress(alpha);
    }

    private String toHexColorCode(int rgb, int alpha) {
        return Integer.toHexString(toARGB(rgb, alpha));
    }

    private String toHexColorCode(int argb) {
        return Integer.toHexString(argb);
    }

    private int toRGB(int argb) {
        return argb & 0x00ffffff;
    }

    private int toARGB(String hexColorCode) {
        if (hexColorCode.startsWith("#")) hexColorCode = hexColorCode.substring(1);
        return (int) Long.parseLong(hexColorCode, 16);
    }

    private int toARGB(int rgb, int alpha) {
        return Color.argb(alpha, Color.red(rgb), Color.green(rgb), Color.blue(rgb));
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {}

    @Override
    public void afterTextChanged(Editable s) {
        if (mColorCodeEdit.getText().toString().matches("^#?[\\dabcdf]{8}$")) {
            int argb = toARGB(mColorCodeEdit.getText().toString());
            mRGB = toRGB(argb);
            mAlpha = Color.alpha(argb);

            if (mColorCodeEdit.isFocused()) setColorToExceptEditText(mRGB, mAlpha);
        }
    }

    @Override
    public boolean onKey(View v, int keyCode, KeyEvent event) {
        //EnterKeyが押されたかを判定
        if (event.getAction() == KeyEvent.ACTION_DOWN
                && keyCode == KeyEvent.KEYCODE_ENTER) {

            //ソフトキーボードを閉じる
            InputMethodManager inputMethodManager = (InputMethodManager) getActivity().
                    getSystemService(Context.INPUT_METHOD_SERVICE);
            inputMethodManager.hideSoftInputFromWindow(v.getWindowToken(), 0);

            if (mColorCodeEdit.getText().toString().matches("^#?[\\dabcdf]{8}$")) {
                int argb = toARGB(mColorCodeEdit.getText().toString());
                mRGB = toRGB(argb);
                mAlpha = Color.alpha(argb);
                setColorToExceptEditText(mRGB, mAlpha);
            }
            else {
                mColorCodeEdit.setText((String) mColorCodeEdit.getTag());
            }

            return true;
        }
        return false;
    }

    @Override
    public void onFocusChange(View v, boolean hasFocus) {
        if (v == mColorCodeEdit) {
            mColorCodeEdit.setTag(mColorCodeEdit.getText().toString());
        }
    }

    @Override
    public void onClick(DialogInterface dialog, int which) {
        switch (which) {
            case Dialog.BUTTON_POSITIVE :
                if (mCallback != null) mCallback.onPickColor(mRGB, mAlpha);
                break;
            case Dialog.BUTTON_NEGATIVE :
                if (mCallback != null) mCallback.onCancel();
                break;
        }
    }

    public interface ColorPickerCallback {
        void onPickColor(int rgb, int alpha);
        void onCancel();
    }
}
