package runningtracker.data.model.running;

public class LocationTObject {

    private double latitudeValue;
    private double longitudeValue;
    private String timeUpdate;

    public LocationTObject() {
    }

    public LocationTObject(double latitudeValue, double longitudeValue, String timeUpdate) {
        this.latitudeValue = latitudeValue;
        this.longitudeValue = longitudeValue;
        this.timeUpdate = timeUpdate;
    }

    public double getLatitudeValue() {
        return latitudeValue;
    }

    public void setLatitudeValue(double latitudeValue) {
        this.latitudeValue = latitudeValue;
    }

    public double getLongitudeValue() {
        return longitudeValue;
    }

    public void setLongitudeValue(double longitudeValue) {
        this.longitudeValue = longitudeValue;
    }

    public String getTimeUpdate() {
        return timeUpdate;
    }

    public void setTimeUpdate(String timeUpdate) {
        this.timeUpdate = timeUpdate;
    }
}
