const MOVE_THRESHOLD = 5.0;
const CROSS_OUT_REVERSES = 6;
const FLICK_THRESHOLD = 0.5;
const CIRCLE_RADIANS = 2 * Math.PI;

/**
 * Construct a new vector at the specified x and y.
 * @param x the vector's x coordinate.
 * @param y the vector's y coordinate.
 * @constructor
 */
function Vec2(x, y) {
    this.x = x;
    this.y = y;

    /**
     * Add this vector to another vector and return the result.
     * @param that the other vector.
     * @returns {Vec2} the sum of this vector and the other vector.
     */
    this.add = function (that) {
        return new Vec2(this.x + that.x, this.y + that.y);
    };

    /**
     * Subtract another vector from this vector and return the result.
     * @param that the other vector.
     * @returns {Vec2} the difference between this vector and the other vector.
     */
    this.sub = function (that) {
        return new Vec2(this.x - that.x, this.y - that.y);
    };

    /**
     * Compute the length of this vector.
     * @returns {number} the vector's length.
     */
    this.len = function () {
        return Math.sqrt(x * x + y * y);
    };

    /**
     * Compute the dot product of this vector and another vector.
     * @param that the other vector.
     * @returns {number} the dot product.
     */
    this.dot = function (that) {
        return this.x * that.x + this.y * that.y;
    };

    /**
     * Compute the cross product of this vector and another vector.
     * @param that the other vector.
     * @returns {number} the cross product.
     */
    this.crs = function (that) {
        return this.x * that.y - that.x * this.y;
    };

    /**
     * Compute the angle between this vector and another vector.
     * @param dot a precomputed dot product.
     * @param crs a precomputed cross product.
     * @param that the other vector.
     * @returns {number} the angle between this vector and the other vector.
     */
    this.angle = function (dot, crs, that) {
        var lengthV1 = this.len();
        var lengthV2 = that.len();
        var theta = Math.asin(crs / (lengthV1 * lengthV2));
        if (dot < 0) {
            if (crs >= 0) {
                theta = Math.PI - theta;
            } else {
                theta = -Math.PI - theta;
            }
        }
        return theta;
    };
}

/**
 * Construct a new box containing the specified points.
 * @param points the points.
 * @constructor
 */
function Box(points) {
    var minX = Number.MAX_VALUE;
    var minY = Number.MAX_VALUE;
    var maxX = Number.MIN_VALUE;
    var maxY = Number.MIN_VALUE;

    points.forEach(function (point) {
        console.log(point.x);

        minX = Math.min(minX, point.x);
        minY = Math.min(minY, point.y);
        maxX = Math.max(maxX, point.x);
        maxY = Math.max(maxY, point.y);
    });

    this.upperLeft = new Vec2(minX, minY);
    this.lowerRight = new Vec2(maxX, maxY);
    this.width = this.lowerRight.x - this.upperLeft.x;
    this.height = this.lowerRight.y - this.upperLeft.y;
    this.isNarrow =
        this.width <= this.height * 0.2 ||
        this.height <= this.width * 0.2;
    this.isShort = this.height <= this.width * 0.5;
}

/**
 * The "enum" of gesture types. Immutable.
 */
const GestureType = Object.freeze({
    SWIPE_UP: "Swipe Up",
    SWIPE_DOWN: "Swipe Down",
    SWIPE_LEFT: "Swipe Left",
    SWIPE_RIGHT: "Swipe Right",

    FLICK_UP: "Flick Up",
    FLICK_DOWN: "Flick Down",
    FLICK_LEFT: "Flick Left",
    FLICK_RIGHT: "Flick Right",

    SPIRAL_CLOCKWISE: "Spiral Clockwise",
    SPIRAL_COUNTERCLOCKWISE: "Spiral Counterclockwise",

    RUB_OUT: "Rub Out",

    CLICK: "Click",
    DOUBLE_CLICK: "Double-Click",

    UNKNOWN: "Unknown"
});

function GestureParser(listener) {
    this.onTouchDown = function (event) {
        this.gesturePoints = [];
        this.history = [];
        this.downX = this.lastMoveX = event.x;
        this.downY = this.lastMoveY = event.y;
        this.gestureStartTime = new Date().getTime();
    };

    this.onTouchMoved = function (event) {
        if (this.hasOwnProperty("gesturePoints")) {
            var lastX = this.lastMoveX;
            var lastY = this.lastMoveY;
            var self = this;
            this.history.forEach(function (point) {
                if (history.hasOwnProperty(point) && point.sub(new Vec2(lastX, lastY)).len() > MOVE_THRESHOLD) {
                    self.gesturePoints.push(point);
                    lastX = point.x;
                    lastY = point.y;
                }
            });

            var eventPosition = new Vec2(event.x, event.y);
            if (eventPosition.len() > MOVE_THRESHOLD) {
                this.gesturePoints.push(eventPosition);
                lastX = event.x;
                lastY = event.y;
            }
            this.lastMoveX = lastX;
            this.lastMoveY = lastY;

            this.history.push(eventPosition);
        }
    };

    this.onTouchUp = function (ignored) {
        if (this.hasOwnProperty("gesturePoints")) {
            var type = this.classify(new Date().getTime());
            listener.onGesture(type, this.gesturePoints);
        }
    };

    /**
     * Calculate this gesture's velocity.
     * @param upTime the mouse-up time in milliseconds.
     * @returns {number} this gesture's velocity.
     */
    this.velocity = function (upTime) {
        var distanceX = 0;
        var distanceY = 0;
        var lastX = this.downX;
        var lastY = this.downY;
        this.gesturePoints.forEach(function (point) {
            distanceX += Math.abs(point.x - lastX);
            distanceY += Math.abs(point.y - lastY);
            lastX = point.x;
            lastY = point.y;
        });

        var distance = Math.sqrt((distanceX * distanceX) + (distanceY * distanceY));
        return distance / (upTime - this.gestureStartTime);
    };

    /**
     * Analyze the gesture's points. Must be called before {@link classify}.
     * @returns {Vec2} the gesture's last point.
     */
    this.analyzePoints = function () {
        var i = this.gesturePoints.concat();
        var firstPoint = i.shift();
        var secondPoint = i.shift();
        var vector0 = secondPoint.sub(firstPoint);
        var lastPoint = i.shift();
        var vector1 = lastPoint.sub(secondPoint);

        var dotProduct = vector0.dot(vector1);
        var crsProduct = vector0.crs(vector1);
        var angle = vector0.angle(dotProduct, crsProduct, vector1);

        this.dotProductReverses = 0;
        this.sumOfAngles = angle;
        this.numVectors = 2;

        var dotWasPositive = dotProduct > 0;

        while (i.length > 0) {
            var vector = i.shift();

            vector0 = vector1;
            vector1 = vector.sub(lastPoint);
            lastPoint = vector;

            this.numVectors++;
            dotProduct = vector0.dot(vector1);
            crsProduct = vector0.crs(vector1);
            angle = vector0.angle(dotProduct, crsProduct, vector1);

            if (!isNaN(angle)) {
                this.sumOfAngles += angle;
            }

            if (dotProduct > 0) {
                if (!dotWasPositive) {
                    this.dotProductReverses++;
                    dotWasPositive = true;
                }
            } else if (dotProduct < 0) {
                if (dotWasPositive) {
                    this.dotProductReverses++;
                    dotWasPositive = false;
                }
            }
        }

        this.directionClockwise = this.sumOfAngles > 0;
        return lastPoint;
    };

    /**
     * Classify the gesture's type.
     * @param upTime the mouse-up time in milliseconds.
     * @returns {*} the gesture's type.
     */
    this.classify = function (upTime) {
        if (this.gesturePoints.length < 3) {
            return GestureType.CLICK;
        }

        var box = new Box(this.gesturePoints);
        var lastPoint = this.analyzePoints();

        if (this.dotProductReverses >= CROSS_OUT_REVERSES) {
            return GestureType.RUB_OUT;
        } else if (Math.abs(this.sumOfAngles) >= CIRCLE_RADIANS * 0.75) {
            if (this.directionClockwise) {
                return GestureType.SPIRAL_CLOCKWISE;
            } else {
                return GestureType.SPIRAL_COUNTERCLOCKWISE;
            }
        }

        var narrow = box.isNarrow;
        if (narrow) {
            var v = this.velocity(upTime);
            var firstPoint = this.gesturePoints[0];
            if (v <= FLICK_THRESHOLD) {
                if (box.isShort) {
                    if (firstPoint.x < lastPoint.x) {
                        return GestureType.SWIPE_RIGHT;
                    } else {
                        return GestureType.SWIPE_LEFT;
                    }
                } else {
                    if (firstPoint.y < lastPoint.y) {
                        return GestureType.SWIPE_DOWN;
                    } else {
                        return GestureType.SWIPE_UP;
                    }
                }
            } else {
                if (box.isShort) {
                    if (firstPoint.x < lastPoint.x) {
                        return GestureType.FLICK_RIGHT;
                    } else {
                        return GestureType.FLICK_LEFT;
                    }
                } else {
                    if (firstPoint.y < lastPoint.y) {
                        return GestureType.FLICK_DOWN;
                    } else {
                        return GestureType.FLICK_UP;
                    }
                }
            }
        }

        return GestureType.UNKNOWN;
    }
}
