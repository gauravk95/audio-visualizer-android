package com.gauravk.audiovisualizer.visualizer;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Point;
import android.graphics.RectF;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;

import com.gauravk.audiovisualizer.base.BaseVisualizer;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * @author maple on 2019/4/25 10:17.
 * @version v1.0
 * @see 1040441325@qq.com
 */
public class HiFiVisualizer extends BaseVisualizer {
    private static final int BAR_MAX_POINTS = 240;
    private static final int BAR_MIN_POINTS = 30;
    private int mRadius;
    private float perRadius = .65f;
    private int mPoints;
    private int[] mHeights;
    private Path mPath;
    private Path mPath1;
    private int mPointRadius = 50;

    public HiFiVisualizer(Context context) {
        super(context);
    }

    public HiFiVisualizer(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public HiFiVisualizer(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void init() {
        mRadius = -1;

        mPath = new Path();
        mPath1 = new Path();
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setAntiAlias(true);
        mPaint.setStrokeWidth(1.0f);
        mPoints = (int) (BAR_MAX_POINTS * mDensity);
        if (mPoints < BAR_MIN_POINTS)
            mPoints = BAR_MIN_POINTS;
        mHeights = new int[mPoints];
    }

    private int mBer;

    @Override
    protected void onDraw(Canvas canvas) {
        if (mRadius == -1) {
            mRadius = (int) (Math.min(getWidth(), getHeight()) / 2 * perRadius);
            mBer = (int) (mRadius / Math.cos(Math.PI / mPoints));
        }
        //update();
        updateHeights();
        mPath.reset();
        mPath1.reset();
        float cxL = (float) (getWidth() / 2 + Math.cos((360 - 360 / mPoints) * Math.PI / 180) * (mRadius + mHeights[mPoints - 1]));
        float cyL = (float) (getHeight() / 2 - Math.sin((360 - 360 / mPoints) * Math.PI / 180) * (mRadius + mHeights[mPoints - 1]));
        mPath.moveTo(cxL, cyL);
        float cxL1 = (float) (getWidth() / 2 + Math.cos((360 - 360 / mPoints) * Math.PI / 180) * (mRadius - mHeights[mPoints - 1]));
        float cyL1 = (float) (getHeight() / 2 - Math.sin((360 - 360 / mPoints) * Math.PI / 180) * (mRadius - mHeights[mPoints - 1]));
        mPath1.moveTo(cxL1, cyL1);
        for (int i = 0; i < 360; i = i + 360 / mPoints) {
            float cx = (float) (getWidth() / 2 + Math.cos(i * Math.PI / 180) * (mRadius + mHeights[i * mPoints / 360]));
            float cy = (float) (getHeight() / 2 - Math.sin(i * Math.PI / 180) * (mRadius + mHeights[i * mPoints / 360]));
            float bx = (float) (getWidth() / 2 + Math.cos((i - (180 / mPoints)) * Math.PI / 180) * (mBer + mHeights[i * mPoints / 360]));
            float by = (float) (getHeight() / 2 - Math.sin((i - (180 / mPoints)) * Math.PI / 180) * (mBer + mHeights[i * mPoints / 360]));
            int last = i==0?mPoints - 1:i * mPoints / 360-1;
            float ax = (float) (getWidth() / 2 + Math.cos((i - (180 / mPoints)) * Math.PI / 180) * (mBer + mHeights[last] ));
            float ay = (float) (getHeight() / 2 - Math.sin((i - (180 / mPoints)) * Math.PI / 180) * (mBer + mHeights[last]));
            mPath.cubicTo(ax,ay,bx, by, cx, cy);
            // 反向
            float cx1 = (float) (getWidth() / 2 + Math.cos(i * Math.PI / 180) * (mRadius - mHeights[i * mPoints / 360]));
            float cy1 = (float) (getHeight() / 2 - Math.sin(i * Math.PI / 180) * (mRadius - mHeights[i * mPoints / 360]));
            float bx1 = (float) (getWidth() / 2 + Math.cos((i - (180 / mPoints)) * Math.PI / 180) * (mBer - mHeights[i * mPoints / 360]));
            float by1 = (float) (getHeight() / 2 - Math.sin((i - (180 / mPoints)) * Math.PI / 180) * (mBer - mHeights[i * mPoints / 360]));
            float ax1 = (float) (getWidth() / 2 + Math.cos((i - (180 / mPoints)) * Math.PI / 180) * (mBer - mHeights[last] ));
            float ay1 = (float) (getHeight() / 2 - Math.sin((i - (180 / mPoints)) * Math.PI / 180) * (mBer - mHeights[last]));
            mPath1.cubicTo(ax1,ay1,bx1, by1, cx1, cy1);
            canvas.drawLine(cx,cy,cx1,cy1,mPaint);
        }

        canvas.drawPath(mPath, mPaint);
        canvas.drawPath(mPath1, mPaint);
    }
    private ArrayList<P> data = new ArrayList<>();
    static class P{
        int index;//角度
        int max;
        int cur;
        boolean reduce;

        public P(int max, int index) {
            this.max = max;
            this.index = index;

        }

        boolean update() {
            if (!reduce) cur+=15;
            else cur-=15;
            if (cur >= max) reduce = true;
            if (cur <= 0) return true;
            return false;
        }
    }
    public void addRandom() {
        P p = new P((int) (Math.random() * 3 * mPointRadius), (int) (Math.random() * mPoints));
        Log.i("TAG", "addRandom: " + p.index + "--- " + p.max);
        data.add(p);
    }
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                //addRandom();
                addRandom();
                break;
        }
        return true;
    }

    private void updateHeights() {
        Arrays.fill(mHeights, 0);
        for (int i = data.size() - 1; i >= 0; i--) {
            P p = data.get(i);
            if (p.update()) data.remove(i);
            int h = p.cur;
            mHeights[p.index] = h;
            int range = 1;
            h -= mPointRadius;
            while (h > 0) {
                mHeights[getRight(p.index, range)] = Math.max(h, mHeights[getRight(p.index, range)]);
                mHeights[getLeft(p.index, range)] = Math.max(h, mHeights[getLeft(p.index, range)]);

                range++;
                h -= mPointRadius * range;
            }

        }
    }

    private int getRight(int index, int range) {
        return (index + range) % mPoints;
    }

    private int getLeft(int index, int range) {
        return index - range < 0 ? index - range + mPoints : index - range;
    }
    private void update() {
        if (isVisualizationEnabled && mRawAudioBytes != null) {
            if (mRawAudioBytes.length == 0) return;
            for (int i = 0; i < mHeights.length; i++) {
                int x = (int) Math.ceil((i + 1) * (mRawAudioBytes.length / mPoints));
                int t = 0;
                if (x < 1024)
                    t =
                            ((byte) (Math.abs(mRawAudioBytes[x]) + 128)) * mRadius / 128;
                mHeights[i] = -t;
            }
        }
    }
}
