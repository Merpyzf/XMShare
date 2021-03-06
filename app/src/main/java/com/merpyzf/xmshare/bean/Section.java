package com.merpyzf.xmshare.bean;

import com.chad.library.adapter.base.entity.SectionEntity;
import com.merpyzf.transfermanager.entity.BaseFileInfo;

public class Section extends SectionEntity<BaseFileInfo> {
    private boolean isCheckedAllChild = false;
    private int childNum;
    public Section(boolean isHeader, String header) {
        super(isHeader, header);
    }

    public Section(boolean isHeader, String header, int childNum) {
        super(isHeader, header);
        this.childNum = childNum;
    }

    public Section(BaseFileInfo fileInfo) {
        super(fileInfo);
    }

    public boolean isCheckedAllChild() {
        return isCheckedAllChild;
    }

    public void setCheckedAllChild(boolean checkedAllChild) {
        isCheckedAllChild = checkedAllChild;
    }

    public void setChildNum(int childNum) {
        this.childNum = childNum;
    }

    public int getChildNum() {
        return childNum;
    }
}

