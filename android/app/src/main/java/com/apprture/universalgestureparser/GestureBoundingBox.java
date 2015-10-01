/*
 *
 * Copyright 2014 by Herb Jellinek.  All rights reserved.
 *
 */
package com.apprture.universalgestureparser;

import java.util.Collection;

/**
 * A bounding box, expressed as an upper left point and a lower right point.
 *
 * @author Herb Jellinek
 */
public class GestureBoundingBox {

    /**
     * Less than or equal to this aspect ratio (height:width or width:height) means the box is
     * "narrow."
     */
    private static final float NARROWNESS_THRESHOLD = 0.2f;

    /**
     * If the height of the box is less than or equal to this proportion of the width, we call the
     * box "short."
     */
    private static final float SHORTNESS_THRESHOLD = 0.5f;

    /**
     * If the width of the box is less than or equal to this proportion of the height, we call the
     * box "wide."
     */
    private static final float WIDENESS_THRESHOLD = 0.5f;

    /**
     * The upper left point.
     */
    private FPoint mUpperLeft;

    /**
     * The lower right point.
     */
    private FPoint mLowerRight;

    /**
     * Create a bounding box based on the points.
     * @param points a {@link java.util.Collection} of points
     */
    public GestureBoundingBox(Collection<FPoint> points) {
        float minX = Float.MAX_VALUE;
        float maxX = Float.MIN_VALUE;
        float minY = Float.MAX_VALUE;
        float maxY = Float.MIN_VALUE;

        for (FPoint pt : points) {
            float x = pt.getX();
            float y = pt.getY();

            if (x < minX) {
                minX = x;
            }
            if (x > maxX) {
                maxX = x;
            }

            if (y < minY) {
                minY = y;
            }
            if (y > maxY) {
                maxY = y;
            }
        }

        mUpperLeft = new FPoint(minX, minY);
        mLowerRight = new FPoint(maxX, maxY);
    }

    /**
     * Return the height of the box.
     * @return the height of the box
     */
    public float getHeight() {
        return mLowerRight.getY() - mUpperLeft.getY();
    }

    /**
     * Return the width of the box.
     * @return the width of the box
     */
    public float getWidth() {
        return mLowerRight.getX() - mUpperLeft.getX();
    }

    /**
     * Is the box very narrow (in either axis)?
     * @return true if it's narrow
     */
    public boolean isNarrow() {
        return getWidth() <= getHeight() * NARROWNESS_THRESHOLD ||
               getHeight() <= getWidth() * NARROWNESS_THRESHOLD;
    }

    /**
     * Is this box short?
     * @return true if it's short
     */
    public boolean isShort() {
        return getHeight() <= getWidth() * SHORTNESS_THRESHOLD;
    }

    /**
     * Is this box wide?
     * @return true if it's wide
     */
    public boolean isWide() {
        return getWidth() <= getHeight() * WIDENESS_THRESHOLD;
    }



}
