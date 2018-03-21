package com.example.erox.running;

/**
 * Created by Erox on 20/03/2018.
 */

public class RunningLogs {


    public float timeInSeconds;
    String distanceProposed, avgWalkingtime,distanceDone;
    public float getTimeInSeconds() { return timeInSeconds; }

    public void setTimeInSeconds(float timeInSeconds) {
        this.timeInSeconds = timeInSeconds;
    }

    public String getDistanceDone() {
        return distanceDone;
    }

    public void setDistanceDone(String distanceDone) {
        this.distanceDone = distanceDone;
    }

    public String getAvgWalkingTime() {
        return avgWalkingtime;
    }

    public void setAvgWalkingTime(String avgWalkingTime) {
        this.avgWalkingtime = avgWalkingTime;
    }

    public String getDistanceProposed() {
        return distanceProposed;
    }

    public void setDistanceProposed(String distanceProposed) { this.distanceProposed = distanceProposed;  }

    @Override
    public String toString() {
        return this.distanceProposed + "KM PROPOSED, " + this.avgWalkingtime + "WALKING TIME, " + this.distanceDone + "KM DONE, " + "IT TOOK YOU:" + (this.timeInSeconds/3600) + "H TO END";
    }
}
