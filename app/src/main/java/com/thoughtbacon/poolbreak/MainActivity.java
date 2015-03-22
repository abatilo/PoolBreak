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
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Spinner;

import com.getbase.floatingactionbutton.FloatingActionButton;
import com.thoughtbacon.poolbreak.BreakRecorder.BreakActivity;
import com.throughbacon.poolbreak.sounddetector.SoundDetector;

import be.tarsos.dsp.AudioDispatcher;
import be.tarsos.dsp.AudioEvent;
import be.tarsos.dsp.io.android.AudioDispatcherFactory;
import be.tarsos.dsp.pitch.PitchDetectionHandler;
import be.tarsos.dsp.pitch.PitchDetectionResult;

public class MainActivity extends ActionBarActivity {

    private final String TAG = MainActivity.class.getSimpleName();
    public static final String DISTANCE_EXTRA = "distance_extra";
    private DistanceCalculator mDistanceCalculator;
    private int mSelectedSpinnerItemIndex = 0;

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

        populateSpinner();
        setupListeners();
    }

    //region Prepare UI elements

    /**
     * Populates the spinner with values from an array in strings.xml
     */
    private void populateSpinner() {
        Spinner sizeSpinner = (Spinner) findViewById(R.id.size_spinner);
        ArrayAdapter<CharSequence> spinnerAdapter =
                ArrayAdapter.createFromResource(this,
                        R.array.size_values,
                        android.R.layout.simple_spinner_dropdown_item);
        sizeSpinner.setAdapter(spinnerAdapter);
    }
    //endregion

    //region Set UI listeners

    /**
     * Sets all of the listeners
     */
    private void setupListeners() {
        setupSpinnerListener();
        setupDragListener();
        setupButtonListener();
    }

    /**
     * Sets the spinner item selected listener
     */
    private void setupSpinnerListener() {
        Spinner sizeSpinner = (Spinner) findViewById(R.id.size_spinner);
        sizeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                mSelectedSpinnerItemIndex = position;
                if (mDistanceCalculator != null) {
                    mDistanceCalculator.setTableType(mSelectedSpinnerItemIndex);
                }
                else if (mDistanceCalculator == null) {
                    mDistanceCalculator = new DistanceCalculator(MainActivity.this, mSelectedSpinnerItemIndex);
                    mDistanceCalculator.setTableType(mSelectedSpinnerItemIndex);
                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    /**
     * Sets the dragging for cue ball placement
     */
    private void setupDragListener() {
        RelativeLayout setupLayout = (RelativeLayout) findViewById(R.id.setup_layout);
        setupLayout.setOnTouchListener(new View.OnTouchListener() {
            final ImageView ballImage = (ImageView) findViewById(R.id.image_cue_ball);
            ImageView tableImage = (ImageView) findViewById(R.id.image_table);

            int touchStartX, touchStartY;
            int ballStartX, ballStartY;
            int deltaX, deltaY;
            int ballNewX, ballNewY;

            @Override
            public boolean onTouch(View v, MotionEvent event) {

                final int leftBound = tableImage.getLeft() + ballImage.getWidth();
                final int rightBound = tableImage.getRight() - ballImage.getWidth() * 2;
                final int topBound = (tableImage.getBottom() - ballImage.getHeight()) / 2;
                final int bottomBound = tableImage.getHeight() - ballImage.getHeight() - ballImage.getWidth();

                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    if (mDistanceCalculator == null)
                        mDistanceCalculator = new DistanceCalculator(MainActivity.this, mSelectedSpinnerItemIndex);

                    touchStartX = (int) event.getX();
                    touchStartY = (int) event.getY();
                }
                else if (event.getAction() == MotionEvent.ACTION_MOVE) {

                    ballStartX = (int) ballImage.getX();
                    ballStartY = (int) ballImage.getY();
                    deltaX = (int) event.getX() - touchStartX;
                    deltaY = (int) event.getY() - touchStartY;
                    ballNewX = ballStartX + deltaX;
                    ballNewY = ballStartY + deltaY;

                    if (ballNewX < leftBound) {
                        ballNewX = leftBound;
                    }
                    if (ballNewX > rightBound) {
                        ballNewX = rightBound;
                    }
                    if (ballNewY < topBound) {
                        ballNewY = topBound;
                    }
                    if (ballNewY > bottomBound) {
                        ballNewY = bottomBound;
                    }
                    ballImage.setX(ballNewX);
                    ballImage.setY(ballNewY);
                    touchStartX = (int) event.getX();
                    touchStartY = (int) event.getY();
                    mDistanceCalculator.setCueBallLocation(ballNewX, ballNewY);
                }
                return true;
            }
        });
    }

    /**
     * Sets the FAB for starting a new break
     */
    private void setupButtonListener() {
        FloatingActionButton breakButton = (FloatingActionButton) findViewById(R.id.button_break);
        breakButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mDistanceCalculator == null)
                    mDistanceCalculator = new DistanceCalculator(MainActivity.this, DistanceCalculator.TableType.SEVEN);

                float distance = mDistanceCalculator.getDistanceFromCueToRack();

                Intent breakIntent = new Intent(MainActivity.this, BreakActivity.class);
                breakIntent.putExtra(DISTANCE_EXTRA, distance);
                startActivity(breakIntent);
            }
        });
    }
    //endregion
}
