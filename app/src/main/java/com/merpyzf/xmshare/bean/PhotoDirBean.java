package com.merpyzf.xmshare.bean;

import com.merpyzf.transfermanager.entity.BaseFileInfo;
import com.merpyzf.transfermanager.entity.PicFile;
import com.merpyzf.transfermanager.utils.FileUtils;
import com.merpyzf.xmshare.App;

import java.io.File;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Created by merpyzf on 2018/4/2.
 */

public class PhotoDirBean implements Serializable {

    /**
     * 相册文件夹的封面图
     */
    private String coverImg;

    /**
     * 相册文件夹的封面图
     */
    private String name;

    /**
     * 相册文件夹的封面图
     */
    private boolean isChecked = false;

    private CopyOnWriteArrayList<BaseFileInfo> imageList;


    public PhotoDirBean(String coverImg, String name, boolean isChecked, CopyOnWriteArrayList<BaseFileInfo> imageList) {
        this.coverImg = coverImg;
        this.name = name;
        this.isChecked = isChecked;
        this.imageList = imageList;
    }

    public PhotoDirBean() {
    }

    public String getCoverImg() {
        return coverImg;
    }

    public void setCoverImg(String coverImg) {
        this.coverImg = coverImg;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isChecked() {
        for (BaseFileInfo fileInfo : imageList) {
            if (!App.getTransferFileList().contains(fileInfo)) {
                isChecked = false;
                return isChecked;
            }
        }
        isChecked = true;
        return isChecked;
    }

    public void setChecked(boolean checked) {
        isChecked = checked;
    }

    public List<BaseFileInfo> getImageList() {
        return imageList;
    }

    public void setImageList(CopyOnWriteArrayList<BaseFileInfo> imageList) {
        this.imageList = imageList;
    }


    public void setImageList(File[] images) {

        List<BaseFileInfo> sortImageList = new ArrayList<>();
        for (File image : images) {
            PicFile picFile = new PicFile();
            picFile.setPath(image.getPath());
            picFile.setName(image.getName());
            picFile.setLength((int) image.length());
            picFile.setSuffix(FileUtils.getFileSuffix(image));
            picFile.setType(BaseFileInfo.FILE_TYPE_IMAGE);
            sortImageList.add(picFile);
        }

        Collections.sort(sortImageList, (o1, o2) -> {
            File file1 = new File(o1.getPath());
            File file2 = new File(o2.getPath());
            if (file1.lastModified() > file2.lastModified()) {
                return -1;
            } else if (file1.lastModified() < file2.lastModified()) {
                return 1;
            } else {
                return 0;
            }
        });


        this.imageList = new CopyOnWriteArrayList<>();
        this.imageList.addAll(sortImageList);
    }


    /**
     * 返回一个相册照片的数量
     *
     * @return
     */
    public int getImageNumber() {

        if (imageList == null) {
            return 0;
        }
        return imageList.size();

    }
}
