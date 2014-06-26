package com.nashglover.myapplication.app;

/**
 * Created by rglover3 on 6/26/2014.
 */
public class Coordinate {
    public double x, y, z;
    public long timestamp;

    Coordinate(long _timestamp, double _x, double _y, double _z) {
        timestamp = _timestamp;
        x = _x;
        y = _y;
        z = _z;
    }

    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }

    public double getZ() {
        return z;
    }
}
