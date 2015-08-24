package com.moldedbits.horizontaldialer;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import com.moldedbits.pickerknob.PickerKnob;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;


public class MainActivity extends AppCompatActivity implements PickerKnob.PositionListener{

    private TextView mPositionTv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        PickerKnob pickerKnob = (PickerKnob)findViewById(R.id.picker_knob);
        mPositionTv = (TextView) findViewById(R.id.tv_position);
        pickerKnob.setPositionListener(this);
    }

    @Override
    public void currentPosition(int position) {
        Log.d("test", "position = " + position);
        mPositionTv.setText(String.valueOf(position));
    }
}
