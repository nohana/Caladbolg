package com.caladbolg;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.View;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;
import com.caladbolg.ColorPickerView.OnChangeColorListener;

public class Caladbolg extends DialogFragment implements OnChangeColorListener, OnSeekBarChangeListener {
    static final String KEY_INITIAL_COLOR = "key_initial_color";

    int rgb;
    int alpha;

    private SeekBar alphaSeekBar;
    private View colorIndicaterView;
    private TextView colorCodeParamsText;
    private EditText colorCodeEdit;
    private ColorPickerView colorPickerView;

    @SuppressLint("ValidFragment")
    private Caladbolg(){}

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
        rgb = initialColor & 0x00ffffff;

        View view = View.inflate(getActivity(), R.layout.dialog_calabolg, null);
        colorIndicaterView = view.findViewById(R.id.view_color_indicater);
        colorCodeEdit = (EditText) view.findViewById(R.id.edit_text_color_code);
        colorCodeParamsText = (TextView) view.findViewById(R.id.text_color_code_params);
        colorPickerView = (ColorPickerView) view.findViewById(R.id.view_color_picker);
        colorPickerView.setOnChangeColor(this);
        colorPickerView.setColor(initialColor);
        alphaSeekBar = (SeekBar) view.findViewById(R.id.seek_bar_alpha);
        alphaSeekBar.setOnSeekBarChangeListener(this);

        setColorToView(rgb, alpha);

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setView(view);

        return builder.create();
    }

    @Override
    public void onChangeColor(int rgb) {
        this.rgb = rgb;
        setColorToView(rgb, alpha);
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        this.alpha = progress;
        setColorToView(rgb, alpha);
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {}
    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {}

    private void setColorToView(int rgb, int alpha) {
        colorIndicaterView.setBackgroundColor(toARGB(rgb, alpha));
        colorCodeEdit.setText(getActivity().getString(R.string.color_fmt, toARGB(rgb, alpha)));
        colorCodeParamsText.setText(getActivity()
                .getString(R.string.color_param_fmt, Color.red(rgb), Color.green(rgb), Color.blue(rgb), alpha));
    }

    private String toHexColorCode(int rgb, int alpha) {
        return Integer.toHexString(toARGB(rgb, alpha));
    }

    private String toHexColorCode(int argb) {
        return Integer.toHexString(argb);
    }

    private int toARGB(int rgb, int alpha) {
        return Color.argb(alpha, Color.red(rgb), Color.green(rgb), Color.blue(rgb));
    }
}
