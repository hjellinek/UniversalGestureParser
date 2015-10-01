/*
 *
 * Copyright 2014 by Herb Jellinek.  All rights reserved.
 *
 */
package com.apprture.universalgestureparser;

import android.util.Log;

/**
 * A utility class to hold some math utilities.
 *
 * @author Herb Jellinek
 */
public class SomeMath {

    /*
     * Debug tag.
     */
    private static final String TAG = "SomeMath";

    /**
     * You can't instantiate one of these.
     */
    private SomeMath() {
    }

    /**
     * Return the length of a vector (Δx, Δy).
     * @param Δx the X coordinate
     * @param Δy the Y coordinate
     * @return the length (magnitude) of the vector
     */
    public static float length(float Δx, float Δy) {
        return (float)Math.sqrt(Δx * Δx + Δy * Δy);
    }

    /**
     * Return the dot product of two vectors.
     * @param Δx1 the X component of the first vector
     * @param Δy1 the Y component of the first vector
     * @param Δx2 the X component of the second vector
     * @param Δy2 the Y component of the second vector
     * @return the dot product
     */
    public static float dotProduct(float Δx1, float Δy1, float Δx2, float Δy2) {
        return Δx1 * Δx2 + Δy1 * Δy2;
    }

    /**
     * Return the cross product of two vectors.
     * @param Δx1 the X component of the first vector
     * @param Δy1 the Y component of the first vector
     * @param Δx2 the X component of the second vector
     * @param Δy2 the Y component of the second vector
     * @return the cross product
     */
    public static float crossProduct(float Δx1, float Δy1, float Δx2, float Δy2) {
        return Δx1 * Δy2 - Δx2 * Δy1;
    }

    /**
     * Return the angle between two vectors, using a previously-calculated dot product.
     * @param dotProduct the dot product, already calculated
     * @param crossProduct the cross product, already calculated
     * @param Δx1 the X component of the first vector
     * @param Δy1 the Y component of the first vector
     * @param Δx2 the X component of the second vector
     * @param Δy2 the Y component of the second vector
     * @return the angle between the two vectors, in radians
     */
    public static float angleBetween(float dotProduct, float crossProduct, float Δx1, float Δy1, float Δx2, float Δy2) {
        float lengthV1 = length(Δx1, Δy1);
        float lengthV2 = length(Δx2, Δy2);
        float theta = (float)Math.asin(crossProduct / (lengthV1 * lengthV2));
        if (dotProduct < 0) {
            if (crossProduct >= 0) {
                theta = (float)(Math.PI - theta);
            } else {
                theta = (float)(-Math.PI - theta);
            }
        }
        if (Float.isNaN(theta)) {
            Log.e(TAG, "bad"); // can stick a breakpoint here
        }
        return theta;
    }

    /**
     * Convert radians to degrees.
     * @param radians the number of radians
     * @return the radians converted to degrees
     */
    public static float radiansToDegrees(float radians) {
        return (float)((180 * radians) / Math.PI);
    }

}
