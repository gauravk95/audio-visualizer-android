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
import android.support.annotation.Nullable;
import android.util.AttributeSet;

import com.gauravk.audiovisualizer.base.BaseVisualizer;
import com.gauravk.audiovisualizer.model.AnimSpeed;
import com.gauravk.audiovisualizer.model.PaintStyle;
import com.gauravk.audiovisualizer.utils.BezierSpline;

/**
 * Custom view to create blob visualizer
 * <p>
 * Created by gk
 */

public class BlobVisualizer extends BaseVisualizer {

    private static final int BLOB_MAX_POINTS = 60;
    private static final int BLOB_MIN_POINTS = 3;

    private Path mBlobPath;
    private int mRadius;

    private int nPoints;

    private PointF[] mBezierPoints;
    private BezierSpline mBezierSpline;

    private float mAngleOffset;
    private float mChangeFactor;

    public BlobVisualizer(Context context) {
        super(context);
    }

    public BlobVisualizer(Context context,
                          @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public BlobVisualizer(Context context,
                          @Nullable AttributeSet attrs,
                          int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void init() {
        mRadius = -1;
        nPoints = (int) (mDensity * BLOB_MAX_POINTS);
        if (nPoints < BLOB_MIN_POINTS)
            nPoints = BLOB_MIN_POINTS;

        mAngleOffset = (360.0f / nPoints);

        updateChangeFactor(mAnimSpeed, false);

        mBlobPath = new Path();

        //initialize mBezierPoints, 2 extra for the smoothing first and last point
        mBezierPoints = new PointF[nPoints + 2];
        for (int i = 0; i < mBezierPoints.length; i++) {
            mBezierPoints[i] = new PointF();
        }

        mBezierSpline = new BezierSpline(mBezierPoints.length);
    }

    @Override
    public void setAnimationSpeed(AnimSpeed animSpeed) {
        super.setAnimationSpeed(animSpeed);
        updateChangeFactor(animSpeed, true);
    }

    private void updateChangeFactor(AnimSpeed animSpeed, boolean useHeight) {
        int height = 1;
        if (useHeight)
            height = getHeight() > 0 ? getHeight() : 1000;

        if (animSpeed == AnimSpeed.SLOW)
            mChangeFactor = height * 0.003f;
        else if (animSpeed == AnimSpeed.MEDIUM)
            mChangeFactor = height * 0.006f;
        else
            mChangeFactor = height * 0.01f;
    }

    @Override
    protected void onDraw(Canvas canvas) {

        double angle = 0;
        //first time initialization
        if (mRadius == -1) {
            mRadius = getHeight() < getWidth() ? getHeight() : getWidth();
            mRadius = (int) (mRadius * 0.65 / 2);

            mChangeFactor = getHeight() * mChangeFactor;

            //initialize bezier points
            for (int i = 0; i < nPoints; i++, angle += mAngleOffset) {
                float posX = (float) (getWidth() / 2
                        + (mRadius)
                        * Math.cos(Math.toRadians(angle)));

                float posY = (float) (getHeight() / 2
                        + (mRadius)
                        * Math.sin(Math.toRadians(angle)));

                mBezierPoints[i].set(posX, posY);
            }
        }

        //create the path and draw
        if (isVisualizationEnabled && mRawAudioBytes != null) {

            if (mRawAudioBytes.length == 0) {
                return;
            }

            mBlobPath.rewind();

            //find the destination bezier point for a batch
            for (int i = 0; i < nPoints; i++, angle += mAngleOffset) {

                int x = (int) Math.ceil((i + 1) * (mRawAudioBytes.length / nPoints));
                int t = 0;
                if (x < 1024)
                    t = ((byte) (-Math.abs(mRawAudioBytes[x]) + 128)) * (canvas.getHeight() / 4) / 128;

                float posX = (float) (getWidth() / 2
                        + (mRadius + t)
                        * Math.cos(Math.toRadians(angle)));

                float posY = (float) (getHeight() / 2
                        + (mRadius + t)
                        * Math.sin(Math.toRadians(angle)));

                //calculate the new x based on change
                if (posX - mBezierPoints[i].x > 0) {
                    mBezierPoints[i].x += mChangeFactor;
                } else {
                    mBezierPoints[i].x -= mChangeFactor;
                }

                //calculate the new y based on change
                if (posY - mBezierPoints[i].y > 0) {
                    mBezierPoints[i].y += mChangeFactor;
                } else {
                    mBezierPoints[i].y -= mChangeFactor;
                }
            }
            //set the first and last point as first
            mBezierPoints[nPoints].set(mBezierPoints[0].x, mBezierPoints[0].y);
            mBezierPoints[nPoints + 1].set(mBezierPoints[0].x, mBezierPoints[0].y);

            //update the control points
            mBezierSpline.updateCurveControlPoints(mBezierPoints);
            PointF[] firstCP = mBezierSpline.getFirstControlPoints();
            PointF[] secondCP = mBezierSpline.getSecondControlPoints();

            //create the path
            mBlobPath.moveTo(mBezierPoints[0].x, mBezierPoints[0].y);
            for (int i = 0; i < firstCP.length; i++) {
                mBlobPath.cubicTo(firstCP[i].x, firstCP[i].y,
                        secondCP[i].x, secondCP[i].y,
                        mBezierPoints[i + 1].x, mBezierPoints[i + 1].y);
            }
            //add an extra line to center cover the gap generated by last cubicTo
            if (mPaintStyle == PaintStyle.FILL)
                mBlobPath.lineTo(getWidth() / 2, getHeight() / 2);

            canvas.drawPath(mBlobPath, mPaint);

        }

        super.onDraw(canvas);
    }
}