package com.caladbolg;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
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

public class Caladbolg extends DialogFragment implements
        OnSeekBarChangeListener, TextWatcher, OnChangeColorListener, OnKeyListener, OnFocusChangeListener {
    static final String KEY_INITIAL_COLOR = "key_initial_color";

    int rgb;
    int alpha;

    private SeekBar alphaSeekBar;
    private View colorIndicaterView;
    private TextView colorCodeParamsText;
    private EditText colorCodeEdit;
    private ColorPickerView colorPickerView;

    public static Caladbolg getInstance(int initialColor) {
        Bundle args = new Bundle();
        args.putInt(KEY_INITIAL_COLOR, initialColor);
        Caladbolg caladbolg = new Caladbolg();
        caladbolg.setArguments(args);
        return caladbolg;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        int initialColor = getArguments().getInt(KEY_INITIAL_COLOR);
        alpha = Color.alpha(initialColor);
        rgb = toRGB(initialColor);

        View view = View.inflate(getActivity(), R.layout.dialog_calabolg, null);
        colorIndicaterView = view.findViewById(R.id.view_color_indicater);
        colorCodeEdit = (EditText) view.findViewById(R.id.edit_text_color_code);
        colorCodeEdit.addTextChangedListener(this);
        colorCodeEdit.setOnKeyListener(this);
        colorCodeEdit.setOnFocusChangeListener(this);
        colorCodeParamsText = (TextView) view.findViewById(R.id.text_color_code_params);
        colorPickerView = (ColorPickerView) view.findViewById(R.id.view_color_picker);
        colorPickerView.setOnChangeColor(this);
        colorPickerView.setColor(initialColor);
        alphaSeekBar = (SeekBar) view.findViewById(R.id.seek_bar_alpha);
        alphaSeekBar.setOnSeekBarChangeListener(this);

        setColorToIndicaters(rgb, alpha);

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setView(view);

        return builder.create();
    }

    @Override
    public void onChangeColor(int rgb) {
        this.rgb = rgb;
        setColorToIndicaters(rgb, alpha);
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        this.alpha = progress;
        setColorToIndicaters(rgb, alpha);
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {}
    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {}

    private void setColorToIndicaters(int rgb, int alpha) {
        colorIndicaterView.setBackgroundColor(toARGB(rgb, alpha));
        colorCodeEdit.setText(getActivity().getString(R.string.color_fmt, toARGB(rgb, alpha)));
        colorCodeParamsText.setText(getActivity()
                .getString(R.string.color_param_fmt, Color.red(rgb), Color.green(rgb), Color.blue(rgb), alpha));
        colorCodeParamsText.requestFocus();
    }

    private void setColorToExceptEditText(int rgb, int alpha) {
        colorIndicaterView.setBackgroundColor(toARGB(rgb, alpha));
        colorCodeParamsText.setText(getActivity()
                .getString(R.string.color_param_fmt, Color.red(rgb), Color.green(rgb), Color.blue(rgb), alpha));
        colorPickerView.setColor(rgb);
        alphaSeekBar.setProgress(alpha);
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
        if (colorCodeEdit.getText().toString().matches("^#?[\\dabcdf]{8}$")) {
            int argb = toARGB(colorCodeEdit.getText().toString());
            rgb = toRGB(argb);
            alpha = Color.alpha(argb);

            if (colorCodeEdit.isFocused()) setColorToExceptEditText(rgb, alpha);
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

            if (colorCodeEdit.getText().toString().matches("^#?[\\dabcdf]{8}$")) {
                int argb = toARGB(colorCodeEdit.getText().toString());
                rgb = toRGB(argb);
                alpha = Color.alpha(argb);
                setColorToExceptEditText(rgb, alpha);
            }
            else {
                colorCodeEdit.setText((String) colorCodeEdit.getTag());
            }

            return true;
        }
        return false;
    }

    @Override
    public void onFocusChange(View v, boolean hasFocus) {
        if (v == colorCodeEdit) {
            colorCodeEdit.setTag(colorCodeEdit.getText().toString());
        }
    }
}
