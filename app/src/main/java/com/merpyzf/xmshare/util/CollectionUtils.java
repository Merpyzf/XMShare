package com.merpyzf.xmshare.util;

import com.merpyzf.transfermanager.entity.FileInfo;

import java.util.Collections;
import java.util.List;

public class CollectionUtils {
    private CollectionUtils(){

    }

    public static void shortingByFirstCase(List<FileInfo> fileLists) {
        Collections.sort(fileLists, (o1, o2) -> {
            Integer integer1 = (int) o1.getFirstCase();
            Integer integer2 = (int) o2.getFirstCase();
            return integer1.compareTo(integer2);
        });
    }
}
