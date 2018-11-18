package com.merpyzf.xmshare.bean;

public class Volume {
    private String path;
    private boolean removable;
    private String state;

    public Volume(String path, boolean removable, String state) {
        this.path = path;
        this.removable = removable;
        this.state = state;
    }

    public Volume() {
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public boolean isRemovable() {
        return removable;
    }

    public void setRemovable(boolean removable) {
        this.removable = removable;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }
}
