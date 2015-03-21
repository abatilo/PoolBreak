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

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Spinner;

import com.throughbacon.poolbreak.sounddetector.SoundDetector;

import be.tarsos.dsp.AudioDispatcher;
import be.tarsos.dsp.AudioEvent;
import be.tarsos.dsp.io.android.AudioDispatcherFactory;
import be.tarsos.dsp.pitch.PitchDetectionHandler;
import be.tarsos.dsp.pitch.PitchDetectionResult;

public class MainActivity extends ActionBarActivity {
    final String TAG = MainActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        SoundDetector soundDetector = new SoundDetector();

        final int sampleRate = AudioUtility.getMaxSampleRate();
        final int bufferSize = AudioUtility.getBufferSize();

        AudioDispatcher dispatcher = AudioDispatcherFactory.fromDefaultMicrophone(sampleRate, bufferSize, 0);

        PitchDetectionHandler pdh = new PitchDetectionHandler() {
            @Override
            public void handlePitch(PitchDetectionResult pitchDetectionResult, AudioEvent audioEvent) {
                Log.d(TAG, "" + pitchDetectionResult.getPitch());
            }
        };

        //dispatcher.addAudioProcessor(new PitchProcessor(PitchProcessor.PitchEstimationAlgorithm.FFT_YIN, sampleRate, bufferSize, pdh));

        dispatcher.addAudioProcessor(soundDetector.getSilenceDetector());
        dispatcher.addAudioProcessor(soundDetector);

        //new Thread(dispatcher,"Sound Detector").start();

        RelativeLayout setupLayout = (RelativeLayout) findViewById(R.id.setup_layout);

        setupLayout.setOnTouchListener(new View.OnTouchListener() {
            ImageView ball = (ImageView) findViewById(R.id.cue_ball);
            ImageView setupTable = (ImageView) findViewById(R.id.image_table);

            float originX = ball.getX();
            float originY = ball.getY();

            float touchStartX = 0;
            float touchStartY = 0;
            float ballStartX, ballStartY;
            float deltaX, deltaY;
            float ballNewX, ballNewY;


            @Override
            public boolean onTouch(View v, MotionEvent event) {
                final float lowerXBound = setupTable.getLeft() + ball.getWidth();
                final float upperXBound = setupTable.getRight() - ball.getWidth() * 2;

                final float upperYBound = (setupTable.getBottom() - ball.getHeight()) / 2;
                final float lowerYBound = setupTable.getHeight() - ball.getHeight() - ball.getWidth();

                final float table_width = 38F;

                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    Log.d(TAG, (upperXBound - lowerXBound) / 4 + "");
                    touchStartX = event.getX();
                    touchStartY = event.getY();
                }
                else if (event.getAction() == MotionEvent.ACTION_MOVE) {
                    ballStartX = ball.getX();
                    ballStartY = ball.getY();

                    deltaX = event.getX() - touchStartX;
                    deltaY = event.getY() - touchStartY;

                    ballNewX = ballStartX + deltaX;
                    ballNewY = ballStartY + deltaY;

                    if (ballNewX < lowerXBound) {
                        ballNewX = lowerXBound;
                    }
                    if (ballNewX > upperXBound) {
                        ballNewX = upperXBound;
                    }
                    if (ballNewY < upperYBound) {
                        ballNewY = upperYBound;
                    }
                    if (ballNewY > lowerYBound) {
                        ballNewY = lowerYBound;
                    }
                    ball.setX(ballNewX);
                    ball.setY(ballNewY);
                    touchStartX = event.getX();
                    touchStartY = event.getY();

                    float distance = (float)Math.sqrt(Math.pow(ballNewX - originX, 2) + Math.pow(ballNewY - originY, 2));
                    float distanceToRack = (float)Math.sqrt(table_width * table_width + distance * distance);
                    float pixelPerInch = ((upperXBound - lowerXBound) / 4) / (table_width / 4);
                    Log.d(TAG, distanceToRack / pixelPerInch + "");
                }
                return true;
            }
        });

        Spinner sizeSpinner = (Spinner) findViewById(R.id.size_spinner);
        ArrayAdapter<CharSequence> spinnerAdapter =
                ArrayAdapter.createFromResource(this,
                                                R.array.size_values,
                                                android.R.layout.simple_spinner_dropdown_item);
        sizeSpinner.setAdapter(spinnerAdapter);
    }
}
