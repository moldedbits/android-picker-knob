package com.moldedbits.horizontaldialer;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.TextView;

import com.moldedbits.pickerknob.PickerKnob;


public class MainActivity extends AppCompatActivity implements PickerKnob.OnValueChangeListener {

    private TextView mPositionTv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        PickerKnob pickerKnob = (PickerKnob)findViewById(R.id.picker_knob);
        mPositionTv = (TextView) findViewById(R.id.tv_position);
        pickerKnob.setPositionListener(this);
        pickerKnob.setValue(5);
    }

    @Override
    public void onValueUpdated(int newValue) {
        Log.d("test", "position = " + newValue);
        mPositionTv.setText(String.valueOf(newValue));
    }
}
