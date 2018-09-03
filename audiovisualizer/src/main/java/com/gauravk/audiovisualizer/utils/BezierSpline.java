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
package com.gauravk.audiovisualizer.utils;

import android.graphics.PointF;

public class BezierSpline {

    private final int nSize;
    private final PointF[] firstControlPoints, secondControlPoints;

    public BezierSpline(int size) {
        this.nSize = size - 1;
        firstControlPoints = new PointF[nSize];
        secondControlPoints = new PointF[nSize];
        for (int i = 0; i < nSize; i++) {
            firstControlPoints[i] = new PointF();
            secondControlPoints[i] = new PointF();
        }
    }

    /**
     * Get open-ended bezier spline control points.
     *
     * @param knots bezier spline points
     * @throws IllegalArgumentException if less than two knots are passed.
     */
    public void updateCurveControlPoints(PointF[] knots) {
        if (knots == null || knots.length < 2) {
            throw new IllegalArgumentException("At least two knot points are required");
        }

        final int n = knots.length - 1;

        // Special case: bezier curve should be a straight line
        if (n == 1) {
            // 3P1 = 2P0 + P3
            float x = (2 * knots[0].x + knots[1].x) / 3;
            float y = (2 * knots[0].y + knots[1].y) / 3;

            firstControlPoints[0].x = x;
            firstControlPoints[0].y = y;

            // P2 = 2P1 - P0
            x = 2 * firstControlPoints[0].x - knots[0].x;
            y = 2 * firstControlPoints[0].y - knots[0].y;

            secondControlPoints[0].x = x;
            secondControlPoints[0].y = y;

        } else {

            // Calculate first bezier control points
            // Right hand side vector
            float[] rhs = new float[n];

            // Set right hand side X values
            for (int i = 1; i < n - 1; i++) {
                rhs[i] = 4 * knots[i].x + 2 * knots[i + 1].x;
            }
            rhs[0] = knots[0].x + 2 * knots[1].x;
            rhs[n - 1] = (8 * knots[n - 1].x + knots[n].x) / 2f;

            // Get first control points X-values
            float[] x = getFirstControlPoints(rhs);

            // Set right hand side Y values
            for (int i = 1; i < n - 1; i++) {
                rhs[i] = 4 * knots[i].y + 2 * knots[i + 1].y;
            }
            rhs[0] = knots[0].y + 2 * knots[1].y;
            rhs[n - 1] = (8 * knots[n - 1].y + knots[n].y) / 2f;

            // Get first control points Y-values
            float[] y = getFirstControlPoints(rhs);

            for (int i = 0; i < n; i++) {
                // First control point
                firstControlPoints[i].x = x[i];
                firstControlPoints[i].y = y[i];

                // Second control point
                if (i < n - 1) {
                    float xx = 2 * knots[i + 1].x - x[i + 1];
                    float yy = 2 * knots[i + 1].y - y[i + 1];
                    secondControlPoints[i].x = xx;
                    secondControlPoints[i].y = yy;
                } else {
                    float xx = (knots[n].x + x[n - 1]) / 2;
                    float yy = (knots[n].y + y[n - 1]) / 2;
                    secondControlPoints[i].x = xx;
                    secondControlPoints[i].y = yy;
                }
            }
        }
    }

    /**
     * Solves a tridiagonal system for one of coordinates (x or y) of first
     * bezier control points.
     *
     * @param rhs right hand side vector.
     * @return Solution vector.
     */
    private float[] getFirstControlPoints(float[] rhs) {
        int n = rhs.length;
        float[] x = new float[n]; // Solution vector
        float[] tmp = new float[n]; // Temp workspace

        float b = 2.0f;
        x[0] = rhs[0] / b;

        // Decomposition and forward substitution
        for (int i = 1; i < n; i++) {
            tmp[i] = 1 / b;
            b = (i < n - 1 ? 4.0f : 3.5f) - tmp[i];
            x[i] = (rhs[i] - x[i - 1]) / b;
        }

        // Backsubstitution
        for (int i = 1; i < n; i++) {
            x[n - i - 1] -= tmp[n - i] * x[n - i];
        }

        return x;
    }

    public PointF[] getFirstControlPoints() {
        return firstControlPoints;
    }

    public PointF[] getSecondControlPoints() {
        return secondControlPoints;
    }
}