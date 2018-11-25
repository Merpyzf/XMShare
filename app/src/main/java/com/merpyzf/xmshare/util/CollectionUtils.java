package com.merpyzf.xmshare.util;

import com.merpyzf.transfermanager.entity.BaseFileInfo;

import java.util.Collections;
import java.util.List;

public class CollectionUtils {
    private CollectionUtils(){

    }

    public static void shortingByFirstCase(List<BaseFileInfo> fileLists) {
        Collections.sort(fileLists, (o1, o2) -> {
            Integer integer1 = (int) o1.getFirstLetter();
            Integer integer2 = (int) o2.getFirstLetter();
            return integer1.compareTo(integer2);
        });
    }
}
