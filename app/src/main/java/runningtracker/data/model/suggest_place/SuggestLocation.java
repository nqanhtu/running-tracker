package runningtracker.data.model.suggest_place;





public class SuggestLocation {

    private double latitudeValue;


    private double longitudeValue;


    private int typePlace;

    public SuggestLocation() {
    }

    public SuggestLocation(double latitudeValue, double longitudeValue, int typePlace) {
        this.latitudeValue = latitudeValue;
        this.longitudeValue = longitudeValue;
        this.typePlace = typePlace;
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

    public int getTypePlace() {
        return typePlace;
    }

    public void setTypePlace(int typePlace) {
        this.typePlace = typePlace;
    }
}
