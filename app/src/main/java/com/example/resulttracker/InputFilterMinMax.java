package com.example.resulttracker;

import android.content.Context;
import android.text.InputFilter;
import android.text.Spanned;
import android.widget.Toast;

public class InputFilterMinMax implements InputFilter {

    private int min, max;
    private Context mContext;

    public InputFilterMinMax(int min, int max, Context mContext) {
        this.min = min;
        this.max = max;
        this.mContext=mContext;
    }

    public InputFilterMinMax(String min, String max,Context mContext) {
        this.min = Integer.parseInt(min);
        this.max = Integer.parseInt(max);
        this.mContext=mContext;
    }

    @Override
    public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {
        try {
            // Remove the string out of destination that is to be replaced
            String newVal = dest.toString().substring(0, dstart) + dest.toString().substring(dend, dest.toString().length());
            // Add the new string in
            newVal = newVal.substring(0, dstart) + source.toString() + newVal.substring(dstart, newVal.length());
            int input = Integer.parseInt(newVal);
            if (isInRange(min, max, input))
                return null;
        } catch (NumberFormatException nfe) { }
        return "";
    }
    private boolean isInRange(int a, int b, int c) {
        boolean value=b > a ? c >= a && c <= b : c >= b && c <= a;
        if(!value){
            Toast.makeText(mContext, "Value should be less than full marks", Toast.LENGTH_SHORT).show();
        }
        return value;
    }
}