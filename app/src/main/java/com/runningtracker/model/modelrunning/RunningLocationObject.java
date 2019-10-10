package com.runningtracker.model.modelrunning;

public class RunningLocationObject {
    private String name;
    private int type;

    public RunningLocationObject() {
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public RunningLocationObject(String name, int type) {
        this.name = name;
        this.type = type;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }
}
