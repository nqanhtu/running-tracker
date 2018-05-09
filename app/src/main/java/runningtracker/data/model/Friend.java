package runningtracker.data.model;

public class Friend {
    private String displayName;
    private String email;
    private String uid;
    private boolean notify;
    private boolean blockNotify;

    public Friend(){}

    public Friend(String displayName, String email, String uid, boolean notify, boolean blockNotify) {
        this.displayName = displayName;
        this.email = email;
        this.uid = uid;
        this.notify = notify;
        this.blockNotify = blockNotify;
    }


    public Friend(String displayName, String email, String uid) {
        this.displayName = displayName;
        this.email = email;
        this.uid = uid;
        this.notify = true;
        this.blockNotify = false;
    }

    public String getDisplayName() {
        return displayName;
    }

    public String getEmail() {
        return email;
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
