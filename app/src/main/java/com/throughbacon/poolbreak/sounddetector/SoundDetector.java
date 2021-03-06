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

package com.throughbacon.poolbreak.sounddetector;

import com.thoughtbacon.poolbreak.Logger;

import java.util.ArrayList;

import be.tarsos.dsp.AudioEvent;
import be.tarsos.dsp.AudioProcessor;
import be.tarsos.dsp.SilenceDetector;

/**
 * Created by Aaron on 3/15/2015.
 */
public class SoundDetector implements AudioProcessor {

    private final String TAG = SoundDetector.class.getSimpleName();

    private double threshold;
    private SilenceDetector silenceDetector;
    private ArrayList<Long> mNoiseTimes = new ArrayList<Long>();

    private final long REPEAT_THRESHOLD = 90000000;

    public SoundDetector() {
        threshold = -70.0;
        silenceDetector = new SilenceDetector(threshold, false);
    }

    @Override
    public boolean process(AudioEvent audioEvent) {
        handleSound();
        return true;
    }

    private void handleSound() {
        if (silenceDetector.currentSPL() > threshold) {
            if (mNoiseTimes.size() > 0) {

                long currentTime = System.nanoTime();
                long previousTime = mNoiseTimes.get(mNoiseTimes.size() - 1);
                long delta = currentTime - previousTime;

                if (delta > REPEAT_THRESHOLD) {
                    mNoiseTimes.add(currentTime);
                    Logger.WriteLoud(TAG, mNoiseTimes.size() + "");
                }
            }
            else {
                mNoiseTimes.add(System.nanoTime());
                Logger.WriteLoud(TAG, mNoiseTimes.size() + "");
            }
        }
    }

    public SilenceDetector getSilenceDetector() {
        return silenceDetector;
    }

    public ArrayList<Long> getNoises() {
        return mNoiseTimes;
    }

    @Override
    public void processingFinished() { }
}
