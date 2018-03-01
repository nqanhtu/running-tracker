package runningtracker.data.model.suggest_place;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.NotNull;
import org.greenrobot.greendao.annotation.Generated;


@Entity
public class SuggestLocation {
    @Id(autoincrement = true)
    private Long id;

    @NotNull
    private double latitudeValue;

    @NotNull
    private double longitudeValue;

    @NotNull
    private int typePlace;

    @Generated(hash = 1098021798)
    public SuggestLocation(Long id, double latitudeValue, double longitudeValue,
            int typePlace) {
        this.id = id;
        this.latitudeValue = latitudeValue;
        this.longitudeValue = longitudeValue;
        this.typePlace = typePlace;
    }

    @Generated(hash = 798487447)
    public SuggestLocation() {
    }

    public Long getId() {
        return this.id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public double getLatitudeValue() {
        return this.latitudeValue;
    }

    public void setLatitudeValue(double latitudeValue) {
        this.latitudeValue = latitudeValue;
    }

    public double getLongitudeValue() {
        return this.longitudeValue;
    }

    public void setLongitudeValue(double longitudeValue) {
        this.longitudeValue = longitudeValue;
    }

    public int getTypePlace() {
        return this.typePlace;
    }

    public void setTypePlace(int typePlace) {
        this.typePlace = typePlace;
    }
    
}
