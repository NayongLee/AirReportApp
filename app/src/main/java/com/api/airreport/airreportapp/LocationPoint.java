package com.api.airreport.airreportapp;

/**
 * Created by 이나영 on 2017-11-06.
 */

public class LocationPoint {

    public double x;
    public double y;
    public double z;

    public LocationPoint() {
        super();
    }

    public LocationPoint(double x, double y) {
        super();
        this.x = x;
        this.y = y;
    }

    public LocationPoint(double x, double y, double z) {
        super();
        this.x = x;
        this.y = y;
        this.z = z;
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
