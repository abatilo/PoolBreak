/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2015 Aaron
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package com.thoughtbacon.poolbreak.BreakRecorder;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.View;
import android.widget.Button;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.thoughtbacon.poolbreak.MainActivity;
import com.thoughtbacon.poolbreak.R;

import java.util.ArrayList;

/**
 * Created by Aaron on 3/21/2015.
 */
public class BreakActivity extends ActionBarActivity {

    private final String TAG = BreakActivity.class.getSimpleName();

    private float mDistance = 0;
    private final float mDefaultDistance = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_break);
        Intent intent = getIntent();
        if (intent != null)
            mDistance = intent.getFloatExtra(MainActivity.DISTANCE_EXTRA, mDefaultDistance);

        PitchTask pitchTask = new PitchTask(this);
        pitchTask.execute();

        new Thread(new Runnable() {
            @Override
            public void run() {
                while (true) {
                    stuff();
                    try {
                        Thread.sleep(200);
                    }
                    catch (InterruptedException ex) {

                    }
                }
            }
        }).start();

        Button finishedButton = (Button) findViewById(R.id.button_finished);
        finishedButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

//                Handler handler = new Handler();
//                DecibelThread decibelRunnable = new DecibelThread(BreakActivity.this, handler);
//                BreakActivity.this.runOnUiThread(decibelRunnable);
            }
        });
    }

    void stuff() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mChart = (LineChart) findViewById(R.id.chart_decibel);

                xVals.add((j) + "");
                vals1.add(new Entry((float) (Math.random() * j) + 20, j++));

                // create a dataset and give it a type
                LineDataSet set1 = new LineDataSet(vals1, "DataSet 1");
                set1.setDrawCubic(true);
                set1.setCubicIntensity(0.2f);
                //set1.setDrawFilled(true);
                set1.setDrawCircles(false);
                set1.setLineWidth(2f);
                set1.setCircleSize(5f);
                set1.setHighLightColor(Color.rgb(244, 117, 117));
                set1.setColor(Color.rgb(104, 241, 175));
                set1.setFillColor(ColorTemplate.getHoloBlue());

                // create a data object with the datasets
                LineData data = new LineData(xVals, set1);
                data.setValueTextSize(9f);
                data.setDrawValues(false);

                // set data
                mChart.setData(data);

                //mChart.animateY(1);

                mChart.invalidate();
            }
        });
    }

    private LineChart mChart;
    ArrayList<String> xVals = new ArrayList<String>();
    ArrayList<Entry> vals1 = new ArrayList<Entry>();
    int j = 0;

    private void setData(int count, float range) {
        mChart = (LineChart) findViewById(R.id.chart_decibel);

        for (int i = 0; i < count; i++) {
            xVals.add((1990 +i) + "");
        }


        for (int i = 0; i < count; i++) {
            float mult = (range + 1);
            float val = (float) (Math.random() * mult) + 20;// + (float)
            // ((mult *
            // 0.1) / 10);
            vals1.add(new Entry(val, i));
        }

        // create a dataset and give it a type
        LineDataSet set1 = new LineDataSet(vals1, "DataSet 1");
        set1.setDrawCubic(true);
        set1.setCubicIntensity(0.2f);
        //set1.setDrawFilled(true);
        set1.setDrawCircles(false);
        set1.setLineWidth(2f);
        set1.setCircleSize(5f);
        set1.setHighLightColor(Color.rgb(244, 117, 117));
        set1.setColor(Color.rgb(104, 241, 175));
        set1.setFillColor(ColorTemplate.getHoloBlue());

        // create a data object with the datasets
        LineData data = new LineData(xVals, set1);
        data.setValueTextSize(9f);
        data.setDrawValues(false);

        // set data
        mChart.setData(data);
    }
}
