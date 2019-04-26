package com.gauravk.audiovisualizer.visualizer;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;
import android.graphics.Shader;
import android.support.annotation.Nullable;
import android.util.AttributeSet;

import com.gauravk.audiovisualizer.base.BaseVisualizer;

/**
 * @author maple on 2019/4/24 15:17.
 * @version v1.0
 * @see 1040441325@qq.com
 */
public class CircleLineVisualizer extends BaseVisualizer {
    private static final int BAR_MAX_POINTS = 240;
    private static final int BAR_MIN_POINTS = 30;
    private Rect mClipBounds;
    private int mPoints;
    private int mPointRadius;
    private float[] mSrcY;
    private int mRadius;
    private Paint mGPaint;
    private boolean drawLine;

    public CircleLineVisualizer(Context context) {
        super(context);
    }

    public CircleLineVisualizer(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public CircleLineVisualizer(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public boolean isDrawLine() {
        return drawLine;
    }

    /**
     * control the display of drawLine
     *
     * @param drawLine is show drawLine
     */
    public void setDrawLine(boolean drawLine) {
        this.drawLine = drawLine;
    }

    @Override
    protected void init() {
        mPoints = (int) (BAR_MAX_POINTS * mDensity);
        if (mPoints < BAR_MIN_POINTS)
            mPoints = BAR_MIN_POINTS;
        mSrcY = new float[mPoints];
        mClipBounds = new Rect();
        setAnimationSpeed(mAnimSpeed);
        mPaint.setAntiAlias(true);
        mGPaint = new Paint();
        mGPaint.setAntiAlias(true);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        mRadius = Math.min(w, h) / 4;
        mPointRadius = Math.abs((int) (2 * mRadius * Math.sin(Math.PI / mPoints / 3)));
        LinearGradient lg = new LinearGradient(getWidth() / 2 + mRadius, getHeight() / 2, getWidth() / 2 + mRadius + mPointRadius * 5, getHeight() / 2
                , Color.parseColor("#77FF5722"), Color.parseColor("#10FF5722"), Shader.TileMode.CLAMP);
        mGPaint.setShader(lg);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.getClipBounds(mClipBounds);
        updateData();
        // draw circle's points
        for (int i = 0; i < 360; i = i + 360 / mPoints) {
            float cx = (float) (getWidth() / 2 + Math.cos(i * Math.PI / 180) * mRadius);
            float cy = (float) (getHeight() / 2 - Math.sin(i * Math.PI / 180) * mRadius);
            canvas.drawCircle(cx, cy, mPointRadius, mPaint);
        }
        // draw lines
        if (drawLine) drawLines(canvas);
        // draw bar
        for (int i = 0; i < 360; i = i + 360 / mPoints) {
            if (mSrcY[i * mPoints / 360] == 0) continue;
            canvas.save();
            canvas.rotate(-i, getWidth() / 2, getHeight() / 2);
            float cx = (float) (getWidth() / 2 + mRadius);
            float cy = (float) (getHeight() / 2);
            canvas.drawRect(cx, cy - mPointRadius, cx + mSrcY[i * mPoints / 360],
                    cy + mPointRadius, mPaint);
            canvas.drawCircle(cx + mSrcY[i * mPoints / 360], cy, mPointRadius, mPaint);
            canvas.restore();
        }
    }

    /**
     * Draw a translucent ray
     *
     * @param canvas target canvas
     */
    private void drawLines(Canvas canvas) {
        int lineLen = 14 * mPointRadius;//default len,
        for (int i = 0; i < 360; i = i + 360 / mPoints) {
            canvas.save();
            canvas.rotate(-i, getWidth() / 2, getHeight() / 2);
            float cx = (float) (getWidth() / 2 + mRadius) + mSrcY[i * mPoints / 360];
            float cy = (float) (getHeight() / 2);
            Path path = new Path();
            path.moveTo(cx, cy + mPointRadius);
            path.lineTo(cx, cy - mPointRadius);
            path.lineTo(cx + lineLen, cy);
            canvas.drawPath(path, mGPaint);
            canvas.restore();
        }
    }

    private void updateData() {
        if (isVisualizationEnabled && mRawAudioBytes != null) {
            if (mRawAudioBytes.length == 0) return;
            for (int i = 0; i < mSrcY.length; i++) {
                int x = (int) Math.ceil((i + 1) * (mRawAudioBytes.length / mPoints));
                int t = 0;
                if (x < 1024) {
                    t = ((byte) (Math.abs(mRawAudioBytes[x]) + 128)) * mRadius / 128;
                }
                mSrcY[i] = -t;
            }
        }
    }
}
