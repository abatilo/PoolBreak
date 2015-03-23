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

import com.thoughtbacon.poolbreak.AudioUtility;
import com.thoughtbacon.poolbreak.R;
import com.throughbacon.poolbreak.sounddetector.SoundDetector;

import org.eazegraph.lib.charts.ValueLineChart;
import org.eazegraph.lib.models.ValueLineSeries;

import be.tarsos.dsp.AudioDispatcher;
import be.tarsos.dsp.io.android.AudioDispatcherFactory;

/**
 * Created by Aaron on 3/22/2015.
 */
public class DecibelTask extends AsyncTask<Void, Float, Void> {

    private Activity mActivity;
    private SoundDetector mSoundDetector;
    private AudioDispatcher mDispatcher;
    private Thread mDispatcherThread;
    private ValueLineChart mDecibelChart;
    private ValueLineSeries mDecibelSeries;

    public DecibelTask(Activity inActivity) {
        mActivity = inActivity;
        setupCharts();
        setupAudioProcessors();
    }

    private void setupCharts() {
        mDecibelChart = (ValueLineChart) mActivity.findViewById(R.id.chart_decibel);

        mDecibelSeries = new ValueLineSeries();
        mDecibelSeries.setColor(0xFF4CAF50);

        mDecibelChart.addSeries(mDecibelSeries);
    }

    private void setupAudioProcessors() {
        final int sampleRate = AudioUtility.getMaxSampleRate();
        final int bufferSize = AudioUtility.getBufferSize();

        mSoundDetector = new SoundDetector();
        mDispatcher = AudioDispatcherFactory.fromDefaultMicrophone(sampleRate, bufferSize, 0);

        mDispatcher.addAudioProcessor(mSoundDetector.getSilenceDetector());
        mDispatcher.addAudioProcessor(mSoundDetector);
    }

    private void startDispatcher() {
        mDispatcherThread = new Thread(mDispatcher, "Sound Detector");
        mDispatcherThread.start();
    }

    public void stopDispatcher() {
        mDispatcher.stop();
        mDispatcherThread.interrupt();
    }

    @Override
    protected Void doInBackground(Void... params) {
        startDispatcher();
        return null;
    }
}
