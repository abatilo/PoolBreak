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

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.thoughtbacon.poolbreak.R;

/**
 * Created by Aaron on 3/22/2015.
 */
public class DecibelThread implements Runnable {

    private static final String TAG = PitchTask.class.getSimpleName();

    private final Activity mActivity;
    private final LineChart mChart;
    private final LineData chartData = new LineData();
    private boolean isEnabled = true;

    private final String DATA_NAME = "Decibel Data";
    private final int VISIBLE_ENTRIES = 200;

    private static int temp = 0;

    public DecibelThread(Activity inActivity ) {
        mActivity = inActivity;

        mChart = (LineChart) mActivity.findViewById(R.id.chart_decibel);

        mChart.setTouchEnabled(false);
        mChart.setDragEnabled(false);
        mChart.setScaleEnabled(true);
        mChart.setDrawGridBackground(false);
        mChart.setPinchZoom(false);

        mChart.setBackgroundColor(0);

        LineData data = new LineData();

        // add empty data
        mChart.setData(data);

        mChart.getLegend().setEnabled(false);
        mChart.getXAxis().setEnabled(false);
        mChart.getAxisLeft().setEnabled(false);
        mChart.getAxisRight().setEnabled(false);
    }

    private void addEntry() {
        LineData data = mChart.getData();
        if (data != null) {
            LineDataSet set = data.getDataSetByIndex(0);
            if (set == null) {
                set = createSet();
                data.addDataSet(set);
            }
            data.addXValue(data.getXValCount() + "");
            data.addEntry(new Entry((float) (Math.random() * 40), set.getEntryCount()), 0);

            mChart.setData(data);
            mChart.notifyDataSetChanged();
            mChart.setVisibleXRange(VISIBLE_ENTRIES);
            mChart.moveViewToX(data.getXValCount() - VISIBLE_ENTRIES);

            set.setDrawFilled(true);
            mChart.invalidate();
        }
    }

    private LineDataSet createSet() {

        LineDataSet set = new LineDataSet(null, "Dynamic Data");
        set.setColor(mActivity.getResources().getColor(R.color.material_green_600));
        set.setDrawCircles(false);
        set.setFillColor(mActivity.getResources().getColor(R.color.material_green_500));
        set.setDrawValues(false);
        set.setDrawCubic(true);
        return set;
    }

    @Override
    public void run() {
        addEntry();
    }

//    private void setupCharts() {
//        //Disable user interaction
//        mLineChart.setTouchEnabled(true);
//        mLineChart.setDragEnabled(true);
//        //mLineChart.setPinchZoom(false);
//
//        //Set appearance
//        mLineChart.setScaleEnabled(true);
//        //mLineChart.getXAxis().setEnabled(false);
//        //mLineChart.getAxisLeft().setEnabled(false);
//        //mLineChart.getAxisRight().setEnabled(false);
//
//        mLineChart.setData(chartData);
//    }
//
//    private void addEntry2() {
//        LineData currentChartData = mLineChart.getData();
//
//        if (currentChartData != null) {
//            LineDataSet currentSet = currentChartData.getDataSetByIndex(0);
//            if (currentSet == null) {
//                currentSet = createInitialSet();
//                currentChartData.addDataSet(currentSet);
//            }
//
//            currentChartData.addXValue(temp + "");
//            currentChartData.addEntry(new Entry((float)temp, temp++), 0);
//            //mLineChart.setVisibleXRange(VISIBLE_ENTRIES);
//            //mLineChart.moveViewToX(currentChartData.getXValCount() - VISIBLE_ENTRIES);
//            Logger.WriteLoud(TAG, currentSet.getEntryCount() + "");
//            mLineChart.animateY(100);
//            mLineChart.invalidate();
//        }
//    }
//
//    private LineDataSet createInitialSet() {
//        LineDataSet newSet = new LineDataSet(null, DATA_NAME);
//        //Set appearance
//        newSet.setDrawCircles(false);
//        newSet.setDrawCubic(true);
//        newSet.setDrawFilled(true);
//        return newSet;
//    }
}
