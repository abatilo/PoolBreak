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

import android.media.AudioFormat;
import android.media.AudioRecord;

/**
 * Created by Aaron on 3/15/2015.
 */
public class AudioUtility {

    public static int getMaxSampleRate() {
        int maxSampleRate = -1;
        int[] possibleSampleRates =
                {
                        48000, 44100, 22050, 16000, 11025, 8000
                };

        for (int i = 0; i < possibleSampleRates.length; i++) {
            int bufferSize = AudioRecord.getMinBufferSize(possibleSampleRates[i], AudioFormat.CHANNEL_IN_MONO, AudioFormat.ENCODING_PCM_16BIT);
            if (bufferSize > 0) {
                maxSampleRate = possibleSampleRates[i];
                return maxSampleRate;
            }
        }
        return maxSampleRate;
    }

    public static int getBufferSize() {
        return AudioRecord.getMinBufferSize(getMaxSampleRate(),
                android.media.AudioFormat.CHANNEL_IN_MONO,
                android.media.AudioFormat.ENCODING_PCM_16BIT);
    }
}
