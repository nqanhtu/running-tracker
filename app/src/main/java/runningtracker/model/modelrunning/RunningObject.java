package runningtracker.Model.modelrunning;

public class RunningObject {
    public int RunningSessionID;
    public int UserID;
    public String StartTimestamp;
    public String FinishTimestamp;
    public double DistanceInKm;
    public int RoadGradient;
    public int RunOnTreadmill;
    public int NetCalorieBurned;
    public int GrossCalorieBurned;
    public int FlagStatus;
    public RunningObject(){}

    public RunningObject(int runningSessionID, int userID, String startTimestamp, String finishTimestamp, double distanceInKm, int roadGradient, int runOnTreadmill, int netCalorieBurned, int grossCalorieBurned, int flagStatus) {
        RunningSessionID = runningSessionID;
        UserID = userID;
        StartTimestamp = startTimestamp;
        FinishTimestamp = finishTimestamp;
        DistanceInKm = distanceInKm;
        RoadGradient = roadGradient;
        RunOnTreadmill = runOnTreadmill;
        NetCalorieBurned = netCalorieBurned;
        GrossCalorieBurned = grossCalorieBurned;
        FlagStatus = flagStatus;
    }

    public int getRunningSessionID() {
        return RunningSessionID;
    }

    public void setRunningSessionID(int runningSessionID) {
        RunningSessionID = runningSessionID;
    }

    public int getUserID() {
        return UserID;
    }

    public void setUserID(int userID) {
        UserID = userID;
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

    public int getRoadGradient() {
        return RoadGradient;
    }

    public void setRoadGradient(int roadGradient) {
        RoadGradient = roadGradient;
    }

    public int getRunOnTreadmill() {
        return RunOnTreadmill;
    }

    public void setRunOnTreadmill(int runOnTreadmill) {
        RunOnTreadmill = runOnTreadmill;
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

    public int getFlagStatus() {
        return FlagStatus;
    }

    public void setFlagStatus(int flagStatus) {
        FlagStatus = flagStatus;
    }
}
