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

import android.app.Activity;
import android.graphics.Point;
import android.widget.ImageView;

/**
 * Created by Aaron on 3/21/2015.
 */
public class DistanceCalculator {

    /**
     * Log tag to represent this class making log writes
     */
    public static final String TAG = DistanceCalculator.class.getSimpleName();

    //region Member variables
    /**
     * Stores information on origin to rack distance for various table sizes
     */
    public static class TableType {
        /**
         * Represents a 7ft pool table.
         */
        public static final int SEVEN = 0;

        /**
         * Represents an 8ft pool table.
         */
        public static final int EIGHT = 1;

        /**
         * Represents a 9ft pool table.
         */
        public static final int NINE = 2;

        /**
         * The actual distance in inches for a 7ft table.
         */
        private static final int DISTANCE_SEVEN = 39;

        /**
         * The actual distance in inches for an 8ft table.
         */
        private static final int DISTANCE_EIGHT = 44;

        /**
         * The actual distance in inches for a 9ft table.
         */
        private static final int DISTANCE_NINE = 50;

        /**
         * The distances of all the table types by index matching their static definitions.
         */
        private static final int[] Distance = { DISTANCE_SEVEN, DISTANCE_EIGHT, DISTANCE_NINE };
    };

    /**
     * The activity upon which DistanceCalculator is being called from.
     */
    private Activity mActivity = null;

    /**
     * The image for the cue ball image.
     */
    private ImageView ballImage = null;

    /**
     * The image for the pool table image.
     */
    private ImageView tableImage = null;

    /**
     * The x coordinate of the ball on the table
     */
    private int mBallX = -1;

    /**
     * The y coordinate of the ball on the table
     */
    private int mBallY = -1;

    /**
     * The point variable that represents the cue ball
     */
    private Point mBallPoint = null;

    /**
     * The x coordinate of the origin point on the table
     */
    private int mOriginX = -1;

    /**
     * The y coordinate of the origin point on the table
     */
    private int mOriginY = -1;

    /**
     * The point variable that represents the origin
     */
    private Point mOriginPoint = null;

    /**
     * The x coordinate of the head ball in the rack on the table
     */
    private int mRackX = -1;

    /**
     * The y coordinate of the head ball in the rack on the table
     */
    private int mRackY = -1;

    /**
     * The point variable that represents the head ball of the rack on the table
     */
    private Point mRackPoint = null;

    /**
     * The distance from the origin point to the head ball in the rack
     */
    private int mDistanceOriginToRack = -1;

    /**
     * Represents the number of pixels that count as one inch on our current table setting
     */
    private int mPixelsPerInch = -1;

    private float mDistanceFromCueToRack  = -1.0f;
    //endregion

    public DistanceCalculator(final Activity inActivity, final int inTableType) {
        mActivity = inActivity;
        ballImage = (ImageView) mActivity.findViewById(R.id.image_cue_ball);
        tableImage = (ImageView) mActivity.findViewById(R.id.image_table);
        mDistanceOriginToRack = TableType.Distance[inTableType];
        setCoordinateMembers();
    }

    //region Member variable initialization methods

    /**
     * Sets the ball, origin, and rack Points
     */
    private void setCoordinateMembers() {
        Logger.Write(TAG, "Setting coordinate members");
        setBallMember();
        setOriginMember();
        setRackMember();
    }

    /**
     * Sets the ball member variable Point
     */
    private void setBallMember() {
        Logger.Write(TAG, "Setting ball point");
        mBallX = (int) ballImage.getX();
        Logger.Write(TAG, "mBallX: " + mBallX);
        mBallY = (int) ballImage.getY();
        Logger.Write(TAG, "mBallY: " + mBallY);
        mBallPoint = new Point(mBallX, mBallY);
    }

    /**
     * Sets the origin member variable Point
     */
    private void setOriginMember() {
        Logger.Write(TAG, "Setting origin point");
        mOriginX = mBallX;
        mOriginY = mBallY;
        mOriginPoint = new Point(mOriginX, mOriginY);
    }

    /**
     * Sets the rack member variable Point
     */
    private void setRackMember() {
        Logger.Write(TAG, "Setting rack point");
        mRackX = mOriginX;
        mRackY = mOriginY - mDistanceOriginToRack * getPixelsPerInch();
        mRackPoint = new Point(mRackX, mRackY);
    }
    //endregion

    //region Private utility methods
    /**
     * Gets the number of pixels that represent an inch on our current table type
     * @return Returns an integer that represents roughly how many pixels represent an inch
     */
    private int calculatePixelsPerInch() {
        //Find the left edge of the image, then the rail is about the same width as the ball
        final float playAreaLeftBound = tableImage.getLeft() + ballImage.getWidth();
        Logger.Write(TAG, "playAreaLeftBound: " + playAreaLeftBound);

        //Find the right edge of the table, then move the ball left to move it onto the rail
        //The second getWidth is to move it on to the table
        //Remember that the coordinates are measured from the top left of the image
        final float playAreaRightBound = tableImage.getRight() - ballImage.getWidth() * 2;
        Logger.Write(TAG, "playAreaRightBound: " + playAreaRightBound);

        //Get the distance from the left edge of teh play area to the right edge
        float playWidth = playAreaLeftBound + playAreaRightBound + ballImage.getWidth();
        Logger.Write(TAG, "playWidth: " + playWidth);

        //The distance to the rack from the origin is half the length, which is exactly the width
        //We know that there are playWidth pixels, and those pixels represent the distance
        //Divide to get the pixels per inch
        int pixelsPerInch = (int)playWidth / mDistanceOriginToRack;
        Logger.Write(TAG, "pixelsPerInch: " + pixelsPerInch);

        //Truncate the decimal points. We lose precision but the images need to be re-done
        //to be more precise anyways.
        //TODO Get better images created then increase calculation precision
        return pixelsPerInch;
    }

    /**
     * Calculates the distance from the cue ball to the rack
     * @return Returns a floating point with the distance in inches
     */
    private float calculateDistanceFromCueToRack() {
        //The actual distance to return
        float distanceFromCueToRack = -1;

        //The difference between the cue ball X coordinate and the rack X coordinate
        float deltaX = mBallPoint.x - mRackPoint.x;
        //Square this difference as part of the distance formula
        deltaX *= deltaX;

        //The difference between the cue ball Y coordinate and the rack Y coordinate
        float deltaY = mBallPoint.y - mRackPoint.y;
        //Square this difference as part of the distance formula
        deltaY *= deltaY;

        //Take the square root of the sum of these two squared differences. This is your distance
        distanceFromCueToRack = (float)Math.sqrt(deltaX + deltaY);

        //Convert from pixels to inches
        distanceFromCueToRack /= mPixelsPerInch;

        return distanceFromCueToRack;
    }
    //endregion

    //region Accessors

    /**
     * The number of pixels that represent one inch
     * @return Integer with the rough pixels per inch
     */
    private int getPixelsPerInch() {
        if (mPixelsPerInch < 0) {
            mPixelsPerInch = calculatePixelsPerInch();
        }
        Logger.Write(TAG, "pixelsPerInch: " + mPixelsPerInch);
        return mPixelsPerInch;
    }

    /**
     * The distance in inches from the cue ball to the head ball in the rack
     * @return Returns a float representing distance in inches
     */
    public float getDistanceFromCueToRack() {
        if (mDistanceFromCueToRack < 0) {
            mDistanceFromCueToRack = calculateDistanceFromCueToRack();
        }
        Logger.Write(TAG, "distanceFromCueToRack: " + mDistanceFromCueToRack);
        return mDistanceFromCueToRack;
    }
    //endregion
}
