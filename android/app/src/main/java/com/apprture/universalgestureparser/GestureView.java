/*
 *
 * Copyright 2014 by Herb Jellinek.  All rights reserved.
 *
 */
package com.apprture.universalgestureparser;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.view.View;

import java.util.List;

/**
 * A View type that's suitable for demonstrating gesture parsing.  It provides support for
 * drawing the path traced by the current gesture.
 *
 * @author Herb Jellinek
 */
public class GestureView extends View {

    /*
     * Debug tag.
     */
    private static final String TAG = "GestureView";

    private static final Paint GESTURE_PAINT = new Paint(Paint.ANTI_ALIAS_FLAG);

    static {
        GESTURE_PAINT.setColor(Color.BLUE);
        GESTURE_PAINT.setStyle(Paint.Style.STROKE);
    }

    private static final Paint TEXT_PAINT = new Paint(Paint.ANTI_ALIAS_FLAG);

    static {
        TEXT_PAINT.setTypeface(Typeface.SANS_SERIF);
    }

    private Path mPath = new Path();

    /**
     * Mandatory constructor.  This is invoked from the layout file.
     * @param context the current context
     */
    public GestureView(Context context) {
        super(context);
    }

    /**
     * Mandatory constructor.  This is invoked from the layout file.
     * @param context the current context
     * @param attrs the AttributeSet
     */
    public GestureView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    /**
     * Mandatory constructor.  This is invoked from the layout file.
     * @param context the current context
     * @param attrs the AttributeSet
     * @param defStyleAttr An attribute in the current theme that contains a reference to a style
     *                     resource to apply to this view. If 0, no default style will be applied.
     */
    public GestureView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    /**
     * Remove the current set of points.
     */
    public void clearPath() {
        mPath = new Path();
    }

    /**
     * Add some points to the current set.
     * @param points the points to add
     */
    public void addPoints(List<FPoint> points) {
        if (points.isEmpty()) {
            return;
        }
        FPoint firstPoint = points.get(0);
        mPath.moveTo(firstPoint.getX(), firstPoint.getY());
        for (FPoint p : points) {
            mPath.lineTo(p.getX(), p.getY());
        }
    }

    /**
     * Draw the current points on the provided canvas.
     * @param canvas the {@link android.graphics.Canvas} to draw on
     */
    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.drawPath(mPath, GESTURE_PAINT);
    }

}
