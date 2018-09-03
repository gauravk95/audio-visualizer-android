/*
        Copyright 2018 Gaurav Kumar

        Licensed under the Apache License, Version 2.0 (the "License");
        you may not use this file except in compliance with the License.
        You may obtain a copy of the License at

        http://www.apache.org/licenses/LICENSE-2.0

        Unless required by applicable law or agreed to in writing, software
        distributed under the License is distributed on an "AS IS" BASIS,
        WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
        See the License for the specific language governing permissions and
        limitations under the License.
*/
package com.gauravk.audiovisualizer.visualizer;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.support.annotation.Nullable;
import android.util.AttributeSet;

import com.gauravk.audiovisualizer.base.BaseVisualizer;
import com.gauravk.audiovisualizer.model.AnimSpeed;
import com.gauravk.audiovisualizer.model.PositionGravity;
import com.gauravk.audiovisualizer.utils.AVConstants;

import java.util.Random;

/**
 * Custom view to create bar visualizer
 * <p>
 * Created by gk
 */

public class BarVisualizer extends BaseVisualizer {

    private static final int BAR_MAX_POINTS = 120;
    private static final int BAR_MIN_POINTS = 3;

    private int mMaxBatchCount;

    private int nPoints;

    private float[] mSrcY, mDestY;

    private float mBarWidth;
    private Rect mClipBounds;

    private int nBatchCount;

    private Random mRandom;

    public BarVisualizer(Context context) {
        super(context);
    }

    public BarVisualizer(Context context,
                         @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public BarVisualizer(Context context,
                         @Nullable AttributeSet attrs,
                         int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void init() {
        nPoints = (int) (BAR_MAX_POINTS * mDensity);
        if (nPoints < BAR_MIN_POINTS)
            nPoints = BAR_MIN_POINTS;

        mBarWidth = -1;
        nBatchCount = 0;

        setAnimationSpeed(mAnimSpeed);

        mRandom = new Random();

        mClipBounds = new Rect();

        mSrcY = new float[nPoints];
        mDestY = new float[nPoints];

    }

    @Override
    public void setAnimationSpeed(AnimSpeed animSpeed) {
        super.setAnimationSpeed(animSpeed);
        mMaxBatchCount = AVConstants.MAX_ANIM_BATCH_COUNT - mAnimSpeed.ordinal();
    }

    @Override
    protected void onDraw(Canvas canvas) {

        if (mBarWidth == -1) {

            canvas.getClipBounds(mClipBounds);

            mBarWidth = canvas.getWidth() / nPoints;

            //initialize points
            for (int i = 0; i < mSrcY.length; i++) {
                float posY;
                if (mPositionGravity == PositionGravity.TOP)
                    posY = mClipBounds.top;
                else
                    posY = mClipBounds.bottom;

                mSrcY[i] = posY;
                mDestY[i] = posY;
            }
        }

        //create the path and draw
        if (isVisualizationEnabled && mRawAudioBytes != null) {

            if (mRawAudioBytes.length == 0) {
                return;
            }

            //find the destination bezier point for a batch
            if (nBatchCount == 0) {
                float randPosY = mDestY[mRandom.nextInt(nPoints)];
                for (int i = 0; i < mSrcY.length; i++) {

                    int x = (int) Math.ceil((i + 1) * (mRawAudioBytes.length / nPoints));
                    int t = 0;
                    if (x < 1024)
                        t = canvas.getHeight() +
                                ((byte) (Math.abs(mRawAudioBytes[x]) + 128)) * canvas.getHeight() / 128;

                    float posY;
                    if (mPositionGravity == PositionGravity.TOP)
                        posY = mClipBounds.bottom - t;
                    else
                        posY = mClipBounds.top + t;

                    //change the source and destination y
                    mSrcY[i] = mDestY[i];
                    mDestY[i] = posY;
                }

                mDestY[mSrcY.length - 1] = randPosY;
            }

            //increment batch count
            nBatchCount++;

            //calculate bar position and draw
            for (int i = 0; i < mSrcY.length; i++) {
                float barY = mSrcY[i] + (((float) (nBatchCount) / mMaxBatchCount) * (mDestY[i] - mSrcY[i]));
                float barX = (i * mBarWidth) + (mBarWidth / 2);
                canvas.drawLine(barX, canvas.getHeight(), barX, barY, mPaint);
            }

            //reset the batch count
            if (nBatchCount == mMaxBatchCount)
                nBatchCount = 0;

        }

        super.onDraw(canvas);
    }
}