package com.crooks;

/**
 * Created by johncrooks on 7/21/16.
 */
public class SwellPeriod {
    int minHeight;
    int maxHeight;
    long unixTime;
    String windDirection;
    int windSpeed;


    public SwellPeriod(int minHeight, int maxHeight, long unixTime, String windDirection, int windSpeed) {
        this.minHeight = minHeight;
        this.maxHeight = maxHeight;
        this.unixTime = unixTime;
        this.windDirection = windDirection;
        this.windSpeed = windSpeed;
    }

    public SwellPeriod() {
    }

    public int getMinHeight() {
        return minHeight;
    }

    public void setMinHeight(int minHeight) {
        this.minHeight = minHeight;
    }

    public int getMaxHeight() {
        return maxHeight;
    }

    public void setMaxHeight(int maxHeight) {
        this.maxHeight = maxHeight;
    }

    public long getUnixTime() {
        return unixTime;
    }

    public void setUnixTime(long unixTime) {
        this.unixTime = unixTime;
    }

    public String getWindDirection() {
        return windDirection;
    }

    public void setWindDirection(String windDirection) {
        this.windDirection = windDirection;
    }

    public int getWindSpeed() {
        return windSpeed;
    }

    public void setWindSpeed(int windSpeed) {
        this.windSpeed = windSpeed;
    }
}
