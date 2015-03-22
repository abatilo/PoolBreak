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

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;

import com.thoughtbacon.poolbreak.Logger;
import com.thoughtbacon.poolbreak.MainActivity;
import com.thoughtbacon.poolbreak.R;

/**
 * Created by Aaron on 3/21/2015.
 */
public class BreakActivity extends ActionBarActivity {

    private final String TAG = BreakActivity.class.getSimpleName();

    private float mDistance;
    private final float mDefaultDistance = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_break);
        Intent intent = getIntent();
        if (intent != null)
            mDistance = intent.getFloatExtra(MainActivity.DISTANCE_EXTRA, mDefaultDistance);

        Logger.WriteLoud(TAG, mDistance + "");
    }
}
