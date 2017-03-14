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

package com.thoughtbacon.poolbreak;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.ActionBarActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.throughbacon.poolbreak.sounddetector.SoundDetector;

import java.text.DecimalFormat;
import java.util.ArrayList;

import be.tarsos.dsp.AudioDispatcher;
import be.tarsos.dsp.io.android.AudioDispatcherFactory;

/**
 * Created by Aaron on 3/21/2015.
 */
public class BreakActivity extends ActionBarActivity {

    private final String TAG = BreakActivity.class.getSimpleName();

    private float mDistance = 0;
    private final float mDefaultDistance = 0;

    private boolean isFinished = false;

    private SoundDetector mDetector = new SoundDetector();
    private AudioDispatcher mDispatcher;
    private Thread mDispatcherThread;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_break);
        Intent intent = getIntent();
        if (intent != null)
            mDistance = intent.getFloatExtra(MainActivity.DISTANCE_EXTRA, mDefaultDistance);

        updateListening();

        Button b = (Button) findViewById(R.id.button_finished);
        b.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isFinished = true;
                mDispatcher.stop();

                ArrayList<Long> temp = mDetector.getNoises();
                float timeDelta = (temp.get(1).floatValue() - temp.get(0).floatValue());
                if (timeDelta > 1000) {

                    TextView listening = (TextView) findViewById(R.id.text_status);
                    TextView ellipses = (TextView) findViewById(R.id.text_ellipses);
                    double velocity = calculateVelocity(timeDelta);
                    DecimalFormat df = new DecimalFormat("#.##");
                    listening.setText(df.format(velocity));
                    ellipses.setText(" mph");
                }
            }
        });

        startDispatcher();
    }

    private double calculateVelocity(float inDelta) {
        float milliseconds = inDelta / 1000000;
        float feet = mDistance / 12;
        float miles = feet / 5280;
        float seconds = milliseconds / 1000;
        float minutes = seconds / 60;
        float hours = minutes / 60;
        double velocity = miles / hours;

        Logger.WriteLoud(TAG, "Velocity: " + velocity + " mph");
        return velocity;
    }

    private void startDispatcher() {
        final int sampleRate = AudioUtility.getMaxSampleRate();
        final int bufferSize = AudioUtility.getBufferSize();

        mDispatcher = AudioDispatcherFactory.fromDefaultMicrophone(sampleRate, bufferSize, 0);
        mDispatcher.addAudioProcessor(mDetector.getSilenceDetector());
        mDispatcher.addAudioProcessor(mDetector);

        mDispatcherThread = new Thread(mDispatcher);
        mDispatcherThread.start();
    }

    private void updateListening() {
        final Handler handler = new Handler();
        final TextView ellipses = (TextView) findViewById(R.id.text_ellipses);
        new Thread(new Runnable() {
            @Override
            public void run() {
                while (!isFinished) {
                    for (int i = 0; i <= 3; i++) {
                        try {
                            Thread.sleep(1000);
                        } catch (InterruptedException ex) {

                        }
                        handler.post(new Runnable() {
                            @Override
                            public void run() {
                                if (!isFinished)
                                    ellipses.setText(ellipses.getText() + ".");
                            }
                        });
                    }
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            if (!isFinished)
                                ellipses.setText("");
                        }
                    });
                }
            }
        }).start();
    }
}
