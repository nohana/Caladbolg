package com.caladbolg;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;

public class Caladbolg extends DialogFragment {
    static final String KEY_INITIAL_COLOR = "key_initial_color";

    int rgb;
    int alpha;

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
        rgb = initialColor - alpha;




        return super.onCreateDialog(savedInstanceState);
    }
}
