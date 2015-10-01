/*
 *
 * Copyright 2014 by Herb Jellinek.  All rights reserved.
 *
 */
package com.apprture.universalgestureparser;

/**
 * The gesture classes we're interested in are:
 * <ul>
 *    <li>SWIPE - in any of the four cardinal directions
 *        <ul>
 *            <li>UP</li>
 *            <li>DOWN</li>
 *            <li>LEFT</li>
 *            <li>RIGHT</li>
 *        </ul>
 *    </li>
 *    <li>FLICK - in any of the four cardinal directions
 *        <ul>
 *            <li>UP</li>
 *            <li>DOWN</li>
 *            <li>LEFT</li>
 *            <li>RIGHT</li>
 *        </ul>
 *    </li>
 *    <li>SPIRAL (also called ROTATE)
 *        <ul>
 *            <li>clockwise ("right")</li>
 *            <li>counterclockwise ("left")</li>
 *        </ul>
 *    </li>
 *    <li>RUB OUT</li>
 *    <li>CLICK</li>
 *    <li>DOUBLE-CLICK</li>
 * </ul>
 *
 * We've added DOUBLE_CLICK to this set, though it's not used yet.
 *
 * @author Herb Jellinek
 */
public enum GestureType {
    SWIPE_UP,
    SWIPE_DOWN,
    SWIPE_LEFT,
    SWIPE_RIGHT,

    FLICK_UP,
    FLICK_DOWN,
    FLICK_LEFT,
    FLICK_RIGHT,

    SPIRAL_CLOCKWISE,
    SPIRAL_COUNTERCLOCKWISE,

    RUB_OUT,

    CLICK,
    DOUBLE_CLICK, // experimental

    UNKNOWN

}
