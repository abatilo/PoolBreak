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
import android.os.AsyncTask;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.thoughtbacon.poolbreak.R;

import java.util.ArrayList;

/**
 * Created by Aaron on 3/22/2015.
 */
public class PitchTask extends AsyncTask<Void, Void, Void> {

    private static final String TAG = PitchTask.class.getSimpleName();

    private final Activity mActivity;
    private final LineChart mLineChart;

    public PitchTask(Activity inActivity) {
        mActivity = inActivity;
        mLineChart = (LineChart) mActivity.findViewById(R.id.chart_pitch);
    }

    @Override
    protected Void doInBackground(Void... params) {
        ArrayList<String> data = new ArrayList<String>();
        ArrayList<Entry> entries = new ArrayList<Entry>();
        for (int i = 0; i < 10; i++) {
            data.add(i + "");
            entries.add(new Entry(i, i));
        }
        LineDataSet set1 = new LineDataSet(entries, "DataSet 1");
        LineData lineData = new LineData(data, set1);

        mLineChart.setData(lineData);
        return null;
    }
}
