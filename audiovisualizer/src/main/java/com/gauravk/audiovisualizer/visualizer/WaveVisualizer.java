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
import android.graphics.Path;
import android.graphics.PointF;
import android.graphics.Rect;
import android.support.annotation.Nullable;
import android.util.AttributeSet;

import com.gauravk.audiovisualizer.base.BaseVisualizer;
import com.gauravk.audiovisualizer.model.AnimSpeed;
import com.gauravk.audiovisualizer.model.PaintStyle;
import com.gauravk.audiovisualizer.model.PositionGravity;
import com.gauravk.audiovisualizer.utils.AVConstants;

import java.util.Random;

/**
 * Custom view to create wave visualizer
 * <p>
 * Created by gk
 */

public class WaveVisualizer extends BaseVisualizer {

    private static final int WAVE_MAX_POINTS = 54;
    private static final int WAVE_MIN_POINTS = 3;

    private int mMaxBatchCount;

    private Path mWavePath;

    private int nPoints;

    private PointF[] mBezierPoints, mBezierControlPoints1, mBezierControlPoints2;

    private float[] mSrcY, mDestY;

    private float mWidthOffset;
    private Rect mClipBounds;

    private int nBatchCount;

    private Random mRandom;

    public WaveVisualizer(Context context) {
        super(context);
    }

    public WaveVisualizer(Context context,
                          @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public WaveVisualizer(Context context,
                          @Nullable AttributeSet attrs,
                          int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void init() {
        nPoints = (int) (WAVE_MAX_POINTS * mDensity);
        if (nPoints < WAVE_MIN_POINTS)
            nPoints = WAVE_MIN_POINTS;

        mWidthOffset = -1;
        nBatchCount = 0;

        setAnimationSpeed(mAnimSpeed);

        mRandom = new Random();

        mClipBounds = new Rect();

        mWavePath = new Path();

        mSrcY = new float[nPoints + 1];
        mDestY = new float[nPoints + 1];

        //initialize mBezierPoints
        mBezierPoints = new PointF[nPoints + 1];
        mBezierControlPoints1 = new PointF[nPoints + 1];
        mBezierControlPoints2 = new PointF[nPoints + 1];
        for (int i = 0; i < mBezierPoints.length; i++) {
            mBezierPoints[i] = new PointF();
            mBezierControlPoints1[i] = new PointF();
            mBezierControlPoints2[i] = new PointF();
        }

    }

    @Override
    public void setAnimationSpeed(AnimSpeed animSpeed) {
        super.setAnimationSpeed(animSpeed);
        this.mMaxBatchCount = AVConstants.MAX_ANIM_BATCH_COUNT - mAnimSpeed.ordinal();
    }

    @Override
    protected void onDraw(Canvas canvas) {

        if (mWidthOffset == -1) {

            canvas.getClipBounds(mClipBounds);

            mWidthOffset = canvas.getWidth() / nPoints;

            //initialize bezier points
            for (int i = 0; i < mBezierPoints.length; i++) {
                float posX = mClipBounds.left + (i * mWidthOffset);

                float posY;
                if (mPositionGravity == PositionGravity.TOP)
                    posY = mClipBounds.top;
                else
                    posY = mClipBounds.bottom;

                mSrcY[i] = posY;
                mDestY[i] = posY;
                mBezierPoints[i].set(posX, posY);
            }
        }

        //create the path and draw
        if (isVisualizationEnabled && mRawAudioBytes != null) {

            if (mRawAudioBytes.length == 0) {
                return;
            }

            mWavePath.rewind();

            //find the destination bezier point for a batch
            if (nBatchCount == 0) {

                float randPosY = mDestY[mRandom.nextInt(nPoints)];
                for (int i = 0; i < mBezierPoints.length; i++) {

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

                mDestY[mBezierPoints.length - 1] = randPosY;
            }

            //increment batch count
            nBatchCount++;

            //for smoothing animation
            for (int i = 0; i < mBezierPoints.length; i++) {
                mBezierPoints[i].y = mSrcY[i] + (((float) (nBatchCount) / mMaxBatchCount) * (mDestY[i] - mSrcY[i]));
            }

            //reset the batch count
            if (nBatchCount == mMaxBatchCount)
                nBatchCount = 0;

            //calculate the bezier curve control points
            for (int i = 1; i < mBezierPoints.length; i++) {
                mBezierControlPoints1[i].set((mBezierPoints[i].x + mBezierPoints[i - 1].x) / 2, mBezierPoints[i - 1].y);
                mBezierControlPoints2[i].set((mBezierPoints[i].x + mBezierPoints[i - 1].x) / 2, mBezierPoints[i].y);
            }

            //create the path
            mWavePath.moveTo(mBezierPoints[0].x, mBezierPoints[0].y);
            for (int i = 1; i < mBezierPoints.length; i++) {
                mWavePath.cubicTo(mBezierControlPoints1[i].x, mBezierControlPoints1[i].y,
                        mBezierControlPoints2[i].x, mBezierControlPoints2[i].y,
                        mBezierPoints[i].x, mBezierPoints[i].y);
            }

            //add last 3 line to close the view
            //mWavePath.lineTo(mClipBounds.right, mBezierPoints[0].y);
            if (mPaintStyle == PaintStyle.FILL) {
                mWavePath.lineTo(mClipBounds.right, mClipBounds.bottom);
                mWavePath.lineTo(mClipBounds.left, mClipBounds.bottom);
                mWavePath.close();
            }

            canvas.drawPath(mWavePath, mPaint);
        }

        super.onDraw(canvas);
    }
}