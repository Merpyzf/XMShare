package com.merpyzf.xmshare.util;

import com.merpyzf.transfermanager.entity.BaseFileInfo;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * @author wangke
 */
public class CollectionUtils {
    private CollectionUtils(){

    }

    public static void shortingByFirstCase(CopyOnWriteArrayList<BaseFileInfo> fileLists) {
        List<BaseFileInfo> sortList = new ArrayList(fileLists);
        Collections.sort(sortList, (o1, o2) -> {
            Integer integer1 = (int) o1.getFirstLetter();
            Integer integer2 = (int) o2.getFirstLetter();
            return integer1.compareTo(integer2);
        });

        fileLists.clear();
        fileLists.addAll(sortList);
    }
}
