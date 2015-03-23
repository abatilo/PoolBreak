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

import android.app.Activity;
import android.os.Handler;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.thoughtbacon.poolbreak.R;

import java.util.ArrayList;

/**
 * Created by Aaron on 3/22/2015.
 */
public class DecibelThread implements Runnable {

    private static final String TAG = PitchTask.class.getSimpleName();

    private final Activity mActivity;
    private final Handler mHandler;
    private final LineChart mLineChart;

    private final ArrayList<String> mXAxisLabels = new ArrayList<String>();
    private final ArrayList<Entry> mSetValues = new ArrayList<Entry>();
    private LineDataSet dataSet = new LineDataSet(mSetValues, "Decibels");
    private LineData chartData = new LineData(mXAxisLabels, dataSet);

    public DecibelThread(Activity inActivity, Handler inHandler) {
        mActivity = inActivity;
        mHandler = inHandler;
        mLineChart = (LineChart) mActivity.findViewById(R.id.chart_decibel);
        setupCharts();
    }

    private void setupCharts() {
        chartData.setDrawValues(false);
        dataSet.setDrawCircles(false);
        mLineChart.getXAxis().setEnabled(false);
        mLineChart.getAxisLeft().setEnabled(false);
        mLineChart.getAxisRight().setEnabled(false);
        mLineChart.setDrawGridBackground(false);
        mLineChart.setDescription("");

        mLineChart.setData(chartData);

        mLineChart.getLegend().setEnabled(false);
        dataSet.setDrawFilled(true);
    }

    @Override
    public void run() {
        int i = 0;
        while (i++ < 10) {
            try {
                Thread.sleep(1000);
            }
            catch (InterruptedException ex) {

            }
            mXAxisLabels.add(i + "");
            mSetValues.add(new Entry(i % 2, i));
            dataSet = new LineDataSet(mSetValues, "Decibels");
            chartData = new LineData(mXAxisLabels, dataSet);
            mLineChart.setData(chartData);
            dataSet.setDrawValues(false);
            dataSet.setDrawCircles(false);
            dataSet.setDrawFilled(true);
            mLineChart.invalidate();
        }
    }
}
