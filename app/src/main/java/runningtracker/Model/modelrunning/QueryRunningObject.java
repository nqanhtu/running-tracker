package runningtracker.Model.modelrunning;


public class QueryRunningObject {
    private int id;
    private double latitudeValue;
    private double longitudeValue;

    public QueryRunningObject() {
    }

    public QueryRunningObject(int id, double latitudeValue, double longitudeValue) {
        this.id = id;
        this.latitudeValue = latitudeValue;
        this.longitudeValue = longitudeValue;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
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

}
