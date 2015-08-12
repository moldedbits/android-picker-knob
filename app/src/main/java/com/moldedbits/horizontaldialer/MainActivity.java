package com.moldedbits.horizontaldialer;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;

import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;


public class MainActivity extends AppCompatActivity {

    @InjectView(R.id.recyclerView)
    RecyclerView mRecyclerView;

    LineWithNumberAdapter mAdapter;

    List<Line> mList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.inject(this);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        mRecyclerView.setLayoutManager(linearLayoutManager);
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());
        mList = new ArrayList<>();
        enterData();
        mAdapter = new LineWithNumberAdapter(this, mList);
        mRecyclerView.setAdapter(mAdapter);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void enterData() {
        Line line1 = new  Line(0);
        Line line2 = new  Line(1);
        Line line3 = new  Line(2);
        Line line4 = new  Line(3);
        Line line5 = new  Line(4);
        line5.setType(Line.LARGE_LINE);
        Line line6 = new  Line(5);
        Line line7 = new  Line(6);
        line7.setType(Line.LARGE_LINE);
        Line line8 = new  Line(7);
        Line line9 = new  Line(8);
        mList.add(line1);
        mList.add(line2);
        mList.add(line3);
        mList.add(line4);
        mList.add(line5);
        mList.add(line6);
        mList.add(line7);
        mList.add(line8);
        mList.add(line9);
    }
}
