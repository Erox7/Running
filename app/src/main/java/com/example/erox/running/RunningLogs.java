package com.example.erox.running;

import java.io.Serializable;

/**
 * Created by Erox on 20/03/2018.
 */

public class RunningLogs implements Serializable{


    private float timeInSeconds;
    private String distanceProposed, avgWalkingtime,distanceDone;

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
        return this.distanceProposed + " PROPOSED, " + this.distanceDone + " DONE, " + "\n"  + this.avgWalkingtime + " AVERAGE WALKING TIME, " + "IT TOOK YOU:" + (this.timeInSeconds/60) + "H TO END";
    }
}
