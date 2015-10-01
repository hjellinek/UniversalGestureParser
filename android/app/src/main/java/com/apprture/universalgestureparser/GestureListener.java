/*
 *
 * Copyright 2014 by Herb Jellinek.  All rights reserved.
 *
 */
package com.apprture.universalgestureparser;

import java.util.List;

/**
 * Interface between the gesture parser and the consumer of the parsed gestures.
 *
 * @author Herb Jellinek
 */
public interface GestureListener {

    /**
     * The parser has finished its work and has determined what type of gesture the user made.
     * @param type the {@link com.apprture.universalgestureparser.GestureType} recognized
     * @param startX the gesture's starting X coordinate
     * @param startY the gesture's starting Y coordinate
     * @param endX the gesture's ending X coordinate
     * @param endY the gesture's ending Y coordinate
     * @param points all of the points comprising the gesture
     */
    public void gesture(GestureType type, float startX, float startY, float endX, float endY,
                        List<FPoint> points);

    /**
     * The parser has received some points and is passing them along, perhaps to display them.  The parser
     * has not tried to recognize the gesture yet.
     * @param points points along the path the user has traced
     */
    public void points(List<FPoint> points);

}
