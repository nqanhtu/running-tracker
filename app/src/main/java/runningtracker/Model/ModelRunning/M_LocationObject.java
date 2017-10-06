package runningtracker.Model.ModelRunning;


public class M_LocationObject {
    public double latitudeValue;
    public double longitudeValue;

    public M_LocationObject(){

    }
    public M_LocationObject(double latitudeValue, double longitudeValue) {
        this.latitudeValue = latitudeValue;
        this.longitudeValue = longitudeValue;
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
