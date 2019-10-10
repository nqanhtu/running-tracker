package com.runningtracker.data.model;

import com.google.firebase.firestore.DocumentReference;

public class Friend {
    private String displayName;
    private String username;
    private String uid;
    private boolean notify;
    private boolean blockNotify;
    private DocumentReference friend;

    public Friend(){}

    public Friend(String displayName, String username, String uid, boolean notify, boolean blockNotify, DocumentReference friend) {
        this.displayName = displayName;
        this.username = username;
        this.uid = uid;
        this.notify = notify;
        this.blockNotify = blockNotify;
        this.friend = friend;
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

    public DocumentReference getFriend() {
        return friend;
    }

    public void setFriend(DocumentReference friend) {
        this.friend = friend;
    }
}
