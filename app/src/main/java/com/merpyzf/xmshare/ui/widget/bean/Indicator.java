package com.merpyzf.xmshare.ui.widget.bean;

import java.util.Objects;

/**
 * @author wangke
 */
public class Indicator {
    private String name;
    private String value;
    private Object tag;

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

    public Object getTag() {
        return tag;
    }

    public void setTag(Object tag) {
        this.tag = tag;
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
        if (tag != null ? !tag.equals(indicator.tag) : indicator.tag != null) {
            return false;
        }
        return value != null ? value.equals(indicator.value) : indicator.value == null;
    }


    @Override
    public int hashCode() {
        return Objects.hash(name, value, tag);
    }
}