/*
 *
 * Copyright 2014 by Herb Jellinek.  All rights reserved.
 *
 */
package com.apprture.universalgestureparser;

import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

/**
 * The gesture classes we're interested in are:
 * <ul>
 * <li>SWIPE - in any of the four cardinal directions
 * <ul>
 * <li>UP</li>
 * <li>DOWN</li>
 * <li>LEFT</li>
 * <li>RIGHT</li>
 * </ul>
 * </li>
 * <li>FLICK - in any of the four cardinal directions
 * <ul>
 * <li>UP</li>
 * <li>DOWN</li>
 * <li>LEFT</li>
 * <li>RIGHT</li>
 * </ul>
 * </li>
 * <li>SPIRAL (also called ROTATE)
 * <ul>
 * <li>clockwise ("right")</li>
 * <li>counterclockwise ("left")</li>
 * </ul>
 * </li>
 * <li>RUB OUT</li>
 * <li>CLICK</li>
 * <li>DOUBLE-CLICK</li>
 * </ul>
 *
 * @author Herb Jellinek
 */
public class GestureParser implements View.OnTouchListener, View.OnClickListener {

    /*
     * Debug tag.
     */
    private static final String TAG = "GestureParser";

    private static final int ENOUGH_REVERSES_TO_BE_RUB_OUT = 6;

    private static final float FLICK_THRESHOLD = 3.0f;

    private static final float CIRCLE_RADIANS = (float)(2 * Math.PI);

    private static final float MOVE_THRESHOLD = 5.0f;

    private static final int FEW_ENOUGH_POINTS_TO_BE_A_CLICK = 3;

    private static final float PORTION_OF_A_CIRCLE_TO_BE_CLOSED = .75f;

    private GestureListener mGestureListener;

    private List<FPoint> mGesturePoints;

    private long mGestureStartTime;

    private float mDownX;

    private float mDownY;

    private float mLastMoveX;

    private float mLastMoveY;

    private int mDotProductReverses = 0;

    private float mSumOfAngles = 0;

    private boolean mDirectionClockwise;

    private int mNumVectors;

    /**
     * Create a new {@link com.apprture.universalgestureparser.GestureParser} that calls the given
     * {@link GestureListener}.
     * @param gestureListener the listener that will receive points and the parsed gesture
     */
    public GestureParser(GestureListener gestureListener) {
        mGestureListener = gestureListener;
        mGesturePoints = null;
    }

    /**
     * Call this to make this parser listen to the events it requires from the given {@link View}.
     * @param view the {@link View} to listen to
     */
    public void listenToView(View view) {
        view.setOnTouchListener(this);
        view.setOnClickListener(this);
    }

    /**
     * Handle a touch event.
     * @param v the {@link View} where it occurred
     * @param event the event
     * @return true if we handled the event, false otherwise
     */
    @Override
    public boolean onTouch(View v, MotionEvent event) {
        final int action = event.getAction();
        switch (action) {
            case MotionEvent.ACTION_DOWN: {
                mGesturePoints = new LinkedList<FPoint>();
                mDownX = mLastMoveX = event.getX();
                mDownY = mLastMoveY = event.getY();
                mGestureStartTime = event.getDownTime();
                break;
            }
            case MotionEvent.ACTION_MOVE: {
                if (mGesturePoints != null) {
                    int points = event.getHistorySize();
                    float lastX = mLastMoveX;
                    float lastY = mLastMoveY;
                    for (int i = 0; i < points; i++) {
                        float eventX = event.getHistoricalX(i);
                        float eventY = event.getHistoricalY(i);
                        if (SomeMath.length(eventX - lastX, eventY - lastY) > MOVE_THRESHOLD) {
                            final FPoint p = new FPoint(eventX, eventY);
                            mGesturePoints.add(p);
                            lastX = eventX;
                            lastY = eventY;
                        }
                    }
                    float eventX = event.getX();
                    float eventY = event.getY();
                    if (SomeMath.length(eventX - lastX, eventY - lastY) > MOVE_THRESHOLD) {
                        final FPoint p = new FPoint(eventX, eventY);
                        mGesturePoints.add(p);
                        lastX = eventX;
                        lastY = eventY;
                    }
                    mLastMoveX = lastX;
                    mLastMoveY = lastY;
                    mGestureListener.points(mGesturePoints);
                }
                break;
            }
            case MotionEvent.ACTION_UP: {
                if (mGesturePoints != null) {
                    // classify the points in mGesturePoints, emit token
                    GestureType type = classify(event.getEventTime());
                    if (!mGesturePoints.isEmpty()) {
                        mGestureListener.gesture(type, mDownX, mDownY, event.getX(), event
                                .getY(), mGesturePoints);
                    }
                }
                float x = event.getX();
                float y = event.getY();
                break;
            }
            default: {
                Log.i(TAG, "onTouch " + actionToString(action));
                break;
            }
        }

        return false;
    }

    /**
     * Return the velocity of movement averaged across the entire gesture, in points per millisecond.
     *
     * @param upTime the time of the "up" event that ended the gesture
     * @return the velocity in points per millisecond
     */
    private float velocity(long upTime) {
        float distanceTraveledX = 0;
        float distanceTraveledY = 0;
        float lastX = mDownX;
        float lastY = mDownY;
        for (FPoint pt : mGesturePoints) {
            distanceTraveledX += Math.abs(pt.getX() - lastX);
            distanceTraveledY += Math.abs(pt.getY() - lastY);
            lastX = pt.getX();
            lastY = pt.getY();
        }

        float distance = (float)Math.sqrt((distanceTraveledX * distanceTraveledX) +
                                          (distanceTraveledY * distanceTraveledY));
        return distance / (upTime - mGestureStartTime);
    }

    /**
     * Classify the gesture, if possible
     * @param upTime the time at which the touch ended
     */
    private GestureType classify(long upTime) {
        if (mGesturePoints.size() < FEW_ENOUGH_POINTS_TO_BE_A_CLICK) {
            return GestureType.CLICK;
        }
        GestureBoundingBox box = new GestureBoundingBox(mGesturePoints);

        FPoint lastPoint = analyzePoints();

        if (mDotProductReverses >= ENOUGH_REVERSES_TO_BE_RUB_OUT) {
            return GestureType.RUB_OUT;
        }

        if (Math.abs(mSumOfAngles) >= CIRCLE_RADIANS * PORTION_OF_A_CIRCLE_TO_BE_CLOSED) {
            if (mDirectionClockwise) {
                return GestureType.SPIRAL_CLOCKWISE;
            } else {
                return GestureType.SPIRAL_COUNTERCLOCKWISE;
            }
        }

        boolean narrow = box.isNarrow();
        if (narrow) {
            float v = velocity(upTime);
            FPoint firstPoint = mGesturePoints.get(0);
            if (v <= FLICK_THRESHOLD) {
                if (box.isShort()) {
                    if (firstPoint.getX() < lastPoint.getX()) {
                        return GestureType.SWIPE_RIGHT;
                    } else {
                        return GestureType.SWIPE_LEFT;
                    }
                } else {
                    if (firstPoint.getY() < lastPoint.getY()) {
                        return GestureType.SWIPE_DOWN;
                    } else {
                        return GestureType.SWIPE_UP;
                    }
                }
            } else {
                if (box.isShort()) {
                    if (firstPoint.getX() < lastPoint.getX()) {
                        return GestureType.FLICK_RIGHT;
                    } else {
                        return GestureType.FLICK_LEFT;
                    }
                } else {
                    if (firstPoint.getY() < lastPoint.getY()) {
                        return GestureType.FLICK_DOWN;
                    } else {
                        return GestureType.FLICK_UP;
                    }
                }
            }
        }

        return GestureType.UNKNOWN;
    }

    /**
     * Analyze the points so we have the data necessary for classification.  Sets various
     * instance variables with the data we need.
     * @return the last point in the set
     */
    private FPoint analyzePoints() {
        Iterator<FPoint> i = mGesturePoints.iterator();
        FPoint firstPoint = i.next();
        FPoint secondPoint = i.next();
        FPoint vector0 = secondPoint.subtract(firstPoint);
        FPoint lastPoint = i.next();
        FPoint vector1 = lastPoint.subtract(secondPoint);

        float dotProduct =
                SomeMath.dotProduct(vector0.getX(), vector0.getY(), vector1.getX(), vector1.getY());
        float crossProduct =
                SomeMath.crossProduct(vector0.getX(), vector0.getY(), vector1.getX(), vector1.getY());
        float angle =
                SomeMath.angleBetween(dotProduct, crossProduct, vector0.getX(), vector0.getY(), vector1.getX(), vector1.getY());
        // if the sign of the dot product reverses a lot, it's a cross-out
        mDotProductReverses = 0;

        // sum of the angles
        mSumOfAngles = angle;

        mNumVectors = 2;

        boolean dotProductWasPositive = dotProduct > 0;

        while (i.hasNext()) {
            final FPoint p = i.next();

            vector0 = vector1;
            vector1 = p.subtract(lastPoint);
            lastPoint = p;

            mNumVectors++;
            dotProduct =
                    SomeMath.dotProduct(vector0.getX(), vector0.getY(), vector1.getX(), vector1.getY());
            crossProduct =
                    SomeMath.crossProduct(vector0.getX(), vector0.getY(), vector1.getX(), vector1.getY());
            angle =
                    SomeMath.angleBetween(dotProduct, crossProduct, vector0.getX(), vector0.getY(), vector1.getX(), vector1.getY());
            if (!Float.isNaN(angle)) {
                mSumOfAngles += angle;
            }

            if (dotProduct > 0) {
                if (!dotProductWasPositive) {
                    mDotProductReverses++;
                    dotProductWasPositive = true;
                }
            } else if (dotProduct < 0) {
                if (dotProductWasPositive) {
                    mDotProductReverses++;
                    dotProductWasPositive = false;
                }
            }
        }

        mDirectionClockwise = mSumOfAngles > 0;

        return lastPoint;
    }

    /**
     * (Copied from Android API 19 MotionEvent.actionToString source code.)
     * For debugging only.
     * Returns a string that represents the symbolic name of the specified unmasked action
     * such as "ACTION_DOWN", "ACTION_POINTER_DOWN(3)" or an equivalent numeric constant
     * such as "35" if unknown.
     *
     * @param action The unmasked action.
     * @return The symbolic name of the specified action.
     */
    private static String actionToString(int action) {
        switch (action) {
            case MotionEvent.ACTION_DOWN:
                return "ACTION_DOWN";
            case MotionEvent.ACTION_UP:
                return "ACTION_UP";
            case MotionEvent.ACTION_CANCEL:
                return "ACTION_CANCEL";
            case MotionEvent.ACTION_OUTSIDE:
                return "ACTION_OUTSIDE";
            case MotionEvent.ACTION_MOVE:
                return "ACTION_MOVE";
            case MotionEvent.ACTION_HOVER_MOVE:
                return "ACTION_HOVER_MOVE";
            case MotionEvent.ACTION_SCROLL:
                return "ACTION_SCROLL";
            case MotionEvent.ACTION_HOVER_ENTER:
                return "ACTION_HOVER_ENTER";
            case MotionEvent.ACTION_HOVER_EXIT:
                return "ACTION_HOVER_EXIT";
        }
        int index = (action & MotionEvent.ACTION_POINTER_INDEX_MASK) >> MotionEvent.ACTION_POINTER_INDEX_SHIFT;
        switch (action & MotionEvent.ACTION_MASK) {
            case MotionEvent.ACTION_POINTER_DOWN:
                return "ACTION_POINTER_DOWN(" + index + ")";
            case MotionEvent.ACTION_POINTER_UP:
                return "ACTION_POINTER_UP(" + index + ")";
            default:
                return Integer.toString(action);
        }
    }

    /**
     * Satisfy the {@link android.view.View.OnClickListener} contract, enabling us to receive touch points.
     * We do nothing here.
     * @param v the {@link View}
     */
    @Override
    public void onClick(View v) {

    }
}