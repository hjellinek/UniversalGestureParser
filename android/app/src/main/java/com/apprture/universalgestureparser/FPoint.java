/*
 *
 * Copyright 2014 by Herb Jellinek.  All rights reserved.
 *
 */
package com.apprture.universalgestureparser;

/**
 * A point with floating-point X and Y coordinates.
 *
 * @author Herb Jellinek
 */
public class FPoint {

    /**
     * The X coordinate.
     */
    private float mX;

    /**
     * The Y coordinate.
     */
    private float mY;

    /**
     * Given <tt>x</tt> and <tt>y</tt>, create a new {@link com.apprture.universalgestureparser.FPoint}.
     * @param x the X coordinate
     * @param y the Y coordinate
     */
    public FPoint(float x, float y) {
        mX = x;
        mY = y;
    }

    /**
     * Return a new {@link com.apprture.universalgestureparser.FPoint} that represents this point
     * (the minuend) minus its argument (the subtrahend).
     * @param pt2 the point to subtract
     * @return the difference
     */
    public FPoint subtract(FPoint pt2) {
        return new FPoint(mX - pt2.mX, mY - pt2.mY);
    }

    /**
     * Return a new {@link com.apprture.universalgestureparser.FPoint} that represents this point
     * plus its argument.
     * @param pt2 the point to add
     * @return the sum
     */
    public FPoint add(FPoint pt2) {
        return new FPoint(mX + pt2.mX, mY + pt2.mY);
    }

    /**
     * Return the X coordinate of the point.
     * @return the X coordinate of the point
     */
    public float getX() {
        return mX;
    }

    /**
     * Return the Y coordinate of the point.
     * @return the Y coordinate of the point
     */
    public float getY() {
        return mY;
    }

    @Override
    public final boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        FPoint fPoint = (FPoint)o;

        if (Float.compare(fPoint.mX, mX) != 0) {
            return false;
        }
        if (Float.compare(fPoint.mY, mY) != 0) {
            return false;
        }

        return true;
    }

    @Override
    public final int hashCode() {
        int result = (mX != +0.0f ? Float.floatToIntBits(mX) : 0);
        result = 31 * result + (mY != +0.0f ? Float.floatToIntBits(mY) : 0);
        return result;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("(");
        sb.append(mX);
        sb.append(", ").append(mY);
        sb.append(')');
        return sb.toString();
    }
}
