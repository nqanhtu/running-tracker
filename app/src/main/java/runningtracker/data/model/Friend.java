package runningtracker.data.model;

public class Friend {
    private String displayName;
    private String username;
    private String uid;
    private boolean notify;
    private boolean blockNotify;

    public Friend(){}

    public Friend(String displayName, String username, String uid, boolean notify, boolean blockNotify) {
        this.displayName = displayName;
        this.username = username;
        this.uid = uid;
        this.notify = notify;
        this.blockNotify = blockNotify;
    }


    public Friend(String displayName, String username, String uid) {
        this.displayName = displayName;
        this.username = username;
        this.uid = uid;
        this.notify = true;
        this.blockNotify = false;
    }

    public String getDisplayName() {
        return displayName;
    }

    public String getUsername() {
        return username;
    }

    public String getUid() {
        return uid;
    }

    public boolean isNotify() {
        return notify;
    }

    public boolean isBlockNotify() {
        return blockNotify;
    }

    public void setNotify(boolean notify) {
        this.notify = notify;
    }

    public void setBlockNotify(boolean blockNotify) {
        this.blockNotify = blockNotify;
    }
}
