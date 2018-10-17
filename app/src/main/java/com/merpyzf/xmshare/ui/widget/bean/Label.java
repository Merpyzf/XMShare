package com.merpyzf.xmshare.ui.widget.bean;

public class Label {

    public Label(String name, String value) {
        this.name = name;
        this.path = value;
    }

    private String name;
    private String path;

    public String getName() {
        return name;
    }

    public void setName(String key) {
        this.name = key;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        Label label = (Label) o;

        if (name != null ? !name.equals(label.name) : label.name != null) {
            return false;
        }
        return path != null ? path.equals(label.path) : label.path == null;
    }

    @Override
    public int hashCode() {
        int result = name != null ? name.hashCode() : 0;
        result = 31 * result + (path != null ? path.hashCode() : 0);
        return result;
    }
}