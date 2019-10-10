package com.runningtracker.model.modelrunning;

public class RunningObject {
    public int RunningSessionID;
    public String StartTimestamp;
    public String FinishTimestamp;
    public double DistanceInKm;
    public double maxPace;
    public double avgPace;
    public double duration;
    public int NetCalorieBurned;
    public int GrossCalorieBurned;
    public RunningObject(){}

    public RunningObject(int runningSessionID, String startTimestamp, String finishTimestamp, double distanceInKm, double maxPace,
                         double avgPace, double duration, int netCalorieBurned, int grossCalorieBurned) {
        RunningSessionID = runningSessionID;
        StartTimestamp = startTimestamp;
        FinishTimestamp = finishTimestamp;
        DistanceInKm = distanceInKm;
        this.maxPace = maxPace;
        this.avgPace = avgPace;
        this.duration = duration;
        NetCalorieBurned = netCalorieBurned;
        GrossCalorieBurned = grossCalorieBurned;
    }

    public int getRunningSessionID() {
        return RunningSessionID;
    }

    public void setRunningSessionID(int runningSessionID) {
        RunningSessionID = runningSessionID;
    }

    public String getStartTimestamp() {
        return StartTimestamp;
    }

    public void setStartTimestamp(String startTimestamp) {
        StartTimestamp = startTimestamp;
    }

    public String getFinishTimestamp() {
        return FinishTimestamp;
    }

    public void setFinishTimestamp(String finishTimestamp) {
        FinishTimestamp = finishTimestamp;
    }

    public double getDistanceInKm() {
        return DistanceInKm;
    }

    public void setDistanceInKm(double distanceInKm) {
        DistanceInKm = distanceInKm;
    }

    public double getMaxPace() {
        return maxPace;
    }

    public void setMaxPace(double maxPace) {
        this.maxPace = maxPace;
    }

    public double getAvgPace() {
        return avgPace;
    }

    public void setAvgPace(double avgPace) {
        this.avgPace = avgPace;
    }

    public double getDuration() {
        return duration;
    }

    public void setDuration(double duration) {
        this.duration = duration;
    }

    public int getNetCalorieBurned() {
        return NetCalorieBurned;
    }

    public void setNetCalorieBurned(int netCalorieBurned) {
        NetCalorieBurned = netCalorieBurned;
    }

    public int getGrossCalorieBurned() {
        return GrossCalorieBurned;
    }

    public void setGrossCalorieBurned(int grossCalorieBurned) {
        GrossCalorieBurned = grossCalorieBurned;
    }
}
