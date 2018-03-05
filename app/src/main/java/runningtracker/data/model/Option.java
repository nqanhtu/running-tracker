package runningtracker.data.model;

/**
 * Created by Anh Tu on 2/12/2018.
 */

public class Option { private String description;
    private int image;

    public Option() {
    }

    public Option(String description, int image) {
        this.description = description;
        this.image = image;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getImage() {
        return image;
    }

    public void setImage(int image) {
        this.image = image;
    }
}
