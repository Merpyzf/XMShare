package com.merpyzf.xmshare.ui.widget.bean;

/**
 * @author wangke
 */
public class Indicator {
    private String name;
    private String value;

    public Indicator(String name, String value) {
        this.name = name;
        this.value = value;
    }

    public String getName() {
        return name;
    }

    public void setName(String key) {
        this.name = key;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Indicator indicator = (Indicator) o;
        if (name != null ? !name.equals(indicator.name) : indicator.name != null) {
            return false;
        }
        return value != null ? value.equals(indicator.value) : indicator.value == null;
    }

    @Override
    public int hashCode() {
        int result = name != null ? name.hashCode() : 0;
        result = 31 * result + (value != null ? value.hashCode() : 0);
        return result;
    }
}