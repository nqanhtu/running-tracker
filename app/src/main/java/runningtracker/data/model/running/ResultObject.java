package runningtracker.data.model.running;

/**
 * Created by Tri on 4/14/2018.
 */

public class ResultObject {

    private String duration;
    private float distance;
    private float pace;
    private float maxPace;
    private float netCalorie;
    private float grossCalorie;

    public ResultObject() {
    }

    public ResultObject(String duration, float distance, float pace, float maxPace, float netCalorie, float grossCalorie) {
        this.duration = duration;
        this.distance = distance;
        this.pace = pace;
        this.maxPace = maxPace;
        this.netCalorie = netCalorie;
        this.grossCalorie = grossCalorie;
    }

    public String getDuration() {
        return duration;
    }

    public void setDuration(String duration) {
        this.duration = duration;
    }

    public float getDistance() {
        return distance;
    }

    public void setDistance(float distance) {
        this.distance = distance;
    }

    public float getPace() {
        return pace;
    }

    public void setPace(float pace) {
        this.pace = pace;
    }

    public float getMaxPace() {
        return maxPace;
    }

    public void setMaxPace(float maxPace) {
        this.maxPace = maxPace;
    }

    public float getNetCalorie() {
        return netCalorie;
    }

    public void setNetCalorie(float netCalorie) {
        this.netCalorie = netCalorie;
    }

    public float getGrossCalorie() {
        return grossCalorie;
    }

    public void setGrossCalorie(float grossCalorie) {
        this.grossCalorie = grossCalorie;
    }
}
