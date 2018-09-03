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
package com.gauravk.audiovisualizer.base;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Paint;
import android.media.audiofx.Visualizer;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;

import com.gauravk.audiovisualizer.R;
import com.gauravk.audiovisualizer.utils.AVConstants;
import com.gauravk.audiovisualizer.model.AnimSpeed;
import com.gauravk.audiovisualizer.model.PaintStyle;
import com.gauravk.audiovisualizer.model.PositionGravity;

/**
 * Base class for the visualizers
 * <p>
 * Created by gk
 */

abstract public class BaseVisualizer extends View {

    protected byte[] mRawAudioBytes;
    protected Paint mPaint;
    protected Visualizer mVisualizer;
    protected int mColor = AVConstants.DEFAULT_COLOR;

    protected PaintStyle mPaintStyle = PaintStyle.FILL;
    protected PositionGravity mPositionGravity = PositionGravity.BOTTOM;

    protected float mStrokeWidth = AVConstants.DEFAULT_STROKE_WIDTH;
    protected float mDensity = AVConstants.DEFAULT_DENSITY;

    protected AnimSpeed mAnimSpeed = AnimSpeed.MEDIUM;
    protected boolean isVisualizationEnabled = true;

    public BaseVisualizer(Context context) {
        super(context);
        init(context, null);
        init();
    }

    public BaseVisualizer(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
        init();
    }

    public BaseVisualizer(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
        init();
    }

    private void init(Context context, AttributeSet attrs) {

        //get the attributes specified in attrs.xml using the name we included
        TypedArray typedArray = context.getTheme().obtainStyledAttributes(attrs,
                R.styleable.BaseVisualizer, 0, 0);
        if (typedArray != null && typedArray.length() > 0) {
            try {
                //get the text and colors specified using the names in attrs.xml
                this.mDensity = typedArray.getFloat(R.styleable.BaseVisualizer_avDensity, AVConstants.DEFAULT_DENSITY);
                this.mColor = typedArray.getColor(R.styleable.BaseVisualizer_avColor, AVConstants.DEFAULT_COLOR);
                this.mStrokeWidth = typedArray.getDimension(R.styleable.BaseVisualizer_avWidth, AVConstants.DEFAULT_STROKE_WIDTH);

                String paintType = typedArray.getString(R.styleable.BaseVisualizer_avType);
                if (paintType != null && !paintType.equals(""))
                    this.mPaintStyle = paintType.toLowerCase().equals("outline") ? PaintStyle.OUTLINE : PaintStyle.FILL;

                String gravityType = typedArray.getString(R.styleable.BaseVisualizer_avGravity);
                if (gravityType != null && !gravityType.equals(""))
                    this.mPositionGravity = gravityType.toLowerCase().equals("top") ? PositionGravity.TOP : PositionGravity.BOTTOM;

                String speedType = typedArray.getString(R.styleable.BaseVisualizer_avSpeed);
                if (speedType != null && !speedType.equals("")) {
                    this.mAnimSpeed = AnimSpeed.MEDIUM;
                    if (speedType.toLowerCase().equals("slow"))
                        this.mAnimSpeed = AnimSpeed.SLOW;
                    else if (speedType.toLowerCase().equals("fast"))
                        this.mAnimSpeed = AnimSpeed.FAST;
                }

            } finally {
                typedArray.recycle();
            }
        }

        mPaint = new Paint();
        mPaint.setColor(mColor);
        mPaint.setStrokeWidth(mStrokeWidth);
        if (mPaintStyle == PaintStyle.FILL)
            mPaint.setStyle(Paint.Style.FILL);
        else {
            mPaint.setStyle(Paint.Style.STROKE);
        }
    }

    /**
     * Set color to visualizer with color resource id.
     *
     * @param color color resource id.
     */
    public void setColor(int color) {
        this.mColor = color;
        this.mPaint.setColor(this.mColor);
    }

    /**
     * Set the density of the visualizer
     *
     * @param density density for visualization
     */
    public void setDensity(float density) {
        //TODO: Check dynamic density change, may cause crash
        synchronized (this) {
            this.mDensity = density;
            init();
        }
    }

    /**
     * Sets the paint style of the visualizer
     *
     * @param paintStyle style of the visualizer.
     */
    public void setPaintStyle(PaintStyle paintStyle) {
        this.mPaintStyle = paintStyle;
        this.mPaint.setStyle(paintStyle == PaintStyle.FILL ? Paint.Style.FILL : Paint.Style.STROKE);
    }

    /**
     * Sets the position of the Visualization{@link PositionGravity}
     *
     * @param positionGravity position of the Visualization
     */
    public void setPositionGravity(PositionGravity positionGravity) {
        this.mPositionGravity = positionGravity;
    }

    /**
     * Sets the Animation speed of the visualization{@link AnimSpeed}
     *
     * @param animSpeed speed of the animation
     */
    public void setAnimationSpeed(AnimSpeed animSpeed) {
        this.mAnimSpeed = animSpeed;
    }

    /**
     * Sets the width of the outline {@link PaintStyle}
     *
     * @param width style of the visualizer.
     */
    public void setStrokeWidth(float width) {
        this.mStrokeWidth = width;
        this.mPaint.setStrokeWidth(width);
    }

    /**
     * Sets the audio bytes to be visualized form {@link Visualizer} or other sources
     *
     * @param bytes of the raw bytes of music
     */
    public void setRawAudioBytes(byte[] bytes) {
        this.mRawAudioBytes = bytes;
        this.invalidate();
    }

    /**
     * Sets the audio session id for the currently playing audio
     *
     * @param audioSessionId of the media to be visualised
     */
    public void setAudioSessionId(int audioSessionId) {
        if (mVisualizer != null)
            release();

        mVisualizer = new Visualizer(audioSessionId);
        mVisualizer.setCaptureSize(Visualizer.getCaptureSizeRange()[1]);

        mVisualizer.setDataCaptureListener(new Visualizer.OnDataCaptureListener() {
            @Override
            public void onWaveFormDataCapture(Visualizer visualizer, byte[] bytes,
                                              int samplingRate) {
                BaseVisualizer.this.mRawAudioBytes = bytes;
                invalidate();
            }

            @Override
            public void onFftDataCapture(Visualizer visualizer, byte[] bytes,
                                         int samplingRate) {
            }
        }, Visualizer.getMaxCaptureRate() / 2, true, false);

        mVisualizer.setEnabled(true);
    }

    /**
     * Releases the visualizer
     */
    public void release() {
        if (mVisualizer != null)
            mVisualizer.release();
    }

    /**
     * Enable Visualization
     */
    public void show() {
        this.isVisualizationEnabled = true;
    }

    /**
     * Disable Visualization
     */
    public void hide() {
        this.isVisualizationEnabled = false;
    }

    protected abstract void init();

}