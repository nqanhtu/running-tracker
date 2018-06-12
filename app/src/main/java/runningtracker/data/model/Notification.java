package runningtracker.data.model;

public class Notification {
    private String from;
    private String fromName;
    private String message;
    private double latitudeValue;
    private double longitudeValue;

    public Notification() {
    }

    public Notification(String from, String fromName, String message, double latitudeValue, double longitudeValue) {
        this.from = from;
        this.fromName = fromName;
        this.message = message;
        this.latitudeValue = latitudeValue;
        this.longitudeValue = longitudeValue;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    public void setFromName(String fromName) {
        this.fromName = fromName;
    }

    public void setMessage(String message) {
        this.message = message;
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

    public String getFrom() {
        return from;
    }

    public String getFromName() {
        return fromName;
    }

    public String getMessage() {
        return message;
    }
}
