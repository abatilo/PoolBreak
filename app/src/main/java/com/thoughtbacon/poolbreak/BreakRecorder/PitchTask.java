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
import org.eazegraph.lib.models.ValueLinePoint;
import org.eazegraph.lib.models.ValueLineSeries;

import be.tarsos.dsp.AudioDispatcher;
import be.tarsos.dsp.AudioEvent;
import be.tarsos.dsp.io.android.AudioDispatcherFactory;
import be.tarsos.dsp.pitch.PitchDetectionHandler;
import be.tarsos.dsp.pitch.PitchDetectionResult;
import be.tarsos.dsp.pitch.PitchProcessor;

/**
 * Created by Aaron on 3/22/2015.
 */
public class PitchTask extends AsyncTask<Void, Float, Void> {

    private static String TAG = PitchTask.class.getSimpleName();

    private Activity mActivity = null;
    private SoundDetector mSoundDetector;
    private AudioDispatcher mDispatcher;
    private Thread mDispatcherThread;
    private ValueLineChart mPitchChart;
    private ValueLineSeries mPitchSeries;

    public PitchTask(Activity inActivity) {
        mActivity = inActivity;
        setupCharts();
        setupAudioProcessors();
    }

    private void setupCharts() {
        mPitchChart = (ValueLineChart) mActivity.findViewById(R.id.chart_pitch);

        mPitchSeries = new ValueLineSeries();
        mPitchSeries.setColor(0xFF4CAF50);

        mPitchChart.addSeries(mPitchSeries);
    }

    private void setupAudioProcessors() {

        final int sampleRate = AudioUtility.getMaxSampleRate();
        final int bufferSize = AudioUtility.getBufferSize();

        mSoundDetector = new SoundDetector();
        mDispatcher = AudioDispatcherFactory.fromDefaultMicrophone(sampleRate, bufferSize, 0);

        PitchDetectionHandler pdh = new PitchDetectionHandler() {
            @Override
            public void handlePitch(PitchDetectionResult pitchDetectionResult, AudioEvent audioEvent) {
                publishProgress(pitchDetectionResult.getPitch());
            }
        };

        mDispatcher.addAudioProcessor(new PitchProcessor(PitchProcessor.PitchEstimationAlgorithm.FFT_YIN, sampleRate, bufferSize, pdh));
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

    @Override
    protected void onProgressUpdate(Float... progress) {
        if (mPitchSeries.getSeries().size() > 100) {
            mPitchSeries.getSeries().remove(0);
        }

        mPitchSeries.addPoint(new ValueLinePoint(Math.abs(progress[0])));
        mPitchChart.getDataSeries().clear();
        mPitchChart.addSeries(mPitchSeries);
    }
}
