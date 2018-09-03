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
import android.support.annotation.Nullable;
import android.util.AttributeSet;

import com.gauravk.audiovisualizer.base.BaseVisualizer;

/**
 * Custom view to create blast visualizer
 * <p>
 * Created by gk
 */

public class BlastVisualizer extends BaseVisualizer {

    private static final int BLAST_MAX_POINTS = 1000;
    private static final int BLAST_MIN_POINTS = 3;

    private Path mSpikePath;
    private int mRadius;
    private int nPoints;

    public BlastVisualizer(Context context) {
        super(context);
    }

    public BlastVisualizer(Context context,
                           @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public BlastVisualizer(Context context,
                           @Nullable AttributeSet attrs,
                           int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void init() {
        mRadius = -1;
        nPoints = (int) (BLAST_MAX_POINTS * mDensity);
        if (nPoints < BLAST_MIN_POINTS)
            nPoints = BLAST_MIN_POINTS;

        mSpikePath = new Path();
    }

    @Override
    protected void onDraw(Canvas canvas) {

        //first time initialization
        if (mRadius == -1) {
            mRadius = getHeight() < getWidth() ? getHeight() : getWidth();
            mRadius = (int) (mRadius * 0.65 / 2);
        }

        //create the path and draw
        if (isVisualizationEnabled && mRawAudioBytes != null) {

            if (mRawAudioBytes.length == 0) {
                return;
            }

            mSpikePath.rewind();

            double angle = 0;
            for (int i = 0; i < nPoints; i++, angle += (360.0f / nPoints)) {
                int x = (int) Math.ceil(i * (mRawAudioBytes.length / nPoints));
                int t = 0;
                if (x < 1024)
                    t = ((byte) (-Math.abs(mRawAudioBytes[x]) + 128)) * (canvas.getHeight() / 4) / 128;

                float posX = (float) (getWidth() / 2
                        + (mRadius + t)
                        * Math.cos(Math.toRadians(angle)));

                float posY = (float) (getHeight() / 2
                        + (mRadius + t)
                        * Math.sin(Math.toRadians(angle)));

                if (i == 0)
                    mSpikePath.moveTo(posX, posY);
                else
                    mSpikePath.lineTo(posX, posY);

            }
            mSpikePath.close();

            canvas.drawPath(mSpikePath, mPaint);

        }

        super.onDraw(canvas);
    }
}