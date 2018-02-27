package runningtracker.model.modelrunning;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.NotNull;
import org.greenrobot.greendao.annotation.Generated;

/**
 * Created by Anh Tu on 1/30/2018.
 */
@Entity
public class SuggestLocation {
    @Id(autoincrement = true)
    private Long id;

    @NotNull
    private double latitudeValue;

    @NotNull
    private double longitudeValue;

    @Generated(hash = 1368365809)
    public SuggestLocation(Long id, double latitudeValue, double longitudeValue) {
        this.id = id;
        this.latitudeValue = latitudeValue;
        this.longitudeValue = longitudeValue;
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
}
