package com.example.erox.running;

/**
 * Created by Erox on 20/03/2018.
 */

public class RunningLogs {

    public int timeInSeconds,distanceDone,distanceProposed,avgWalkingTime;

    public int getTimeInSeconds() {
        return timeInSeconds;
    }

    public void setTimeInSeconds(int timeInSeconds) {
        this.timeInSeconds = timeInSeconds;
    }

    public int getDistanceDone() {
        return distanceDone;
    }

    public void setDistanceDone(int distanceDone) {
        this.distanceDone = distanceDone;
    }

    public int getAvgWalkingTime() {
        return avgWalkingTime;
    }

    public void setAvgWalkingTime(int avgWalkingTime) {
        this.avgWalkingTime = avgWalkingTime;
    }

    public int getDistanceProposed() {
        return distanceProposed;
    }

    public void setDistanceProposed(int distanceProposed) {
        this.distanceProposed = distanceProposed;
    }

    @Override
    public String toString() {
        return this.distanceProposed + "KM PROPOSED, " + this.avgWalkingTime + "WALKING TIME, " + this.distanceDone + "KM DONE, " + "IT TOOK YOU:" + (this.timeInSeconds/3600) + "H TO END";
    }
}
