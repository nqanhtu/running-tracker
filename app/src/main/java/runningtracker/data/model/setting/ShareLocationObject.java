package runningtracker.data.model.setting;


public class ShareLocationObject {
    public Boolean isShareLocation;

    public ShareLocationObject() {
    }

    public ShareLocationObject(Boolean isShareLocation) {
        this.isShareLocation = isShareLocation;
    }

    public Boolean getShareLocation() {
        return isShareLocation;
    }

    public void setShareLocation(Boolean shareLocation) {
        isShareLocation = shareLocation;
    }
}
