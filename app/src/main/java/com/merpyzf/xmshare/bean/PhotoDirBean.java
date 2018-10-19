package com.merpyzf.xmshare.bean;

import android.util.Log;

import com.merpyzf.transfermanager.entity.FileInfo;
import com.merpyzf.transfermanager.entity.PicFile;
import com.merpyzf.transfermanager.util.FileUtils;
import com.merpyzf.xmshare.App;

import java.io.File;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by merpyzf on 2018/4/2.
 */

public class PhotoDirBean implements Serializable {

    // 相册文件夹的封面图
    private String coverImg;
    // 相册名
    private String name;
    // 是否选中
    private boolean isChecked = false;

    private List<FileInfo> imageList;


    public PhotoDirBean(String coverImg, String name, boolean isChecked, List<FileInfo> imageList) {
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
        for (FileInfo fileInfo : imageList) {
            if (!App.getSendFileList().contains(fileInfo)) {
                isChecked = false;
                return isChecked;
            }
            Log.i("WW2K", "执行了");
        }
        isChecked = true;
        return isChecked;
    }

    public void setChecked(boolean checked) {
        isChecked = checked;
    }

    public List<FileInfo> getImageList() {
        return imageList;
    }

    public void setImageList(List<FileInfo> imageList) {
        this.imageList = imageList;
    }


    public void setImageList(File[] images) {

        List<FileInfo> imageList = new ArrayList<>();
        for (File image : images) {

            PicFile picFile = new PicFile();
            picFile.setPath(image.getPath());
            picFile.setName(image.getName());
            picFile.setLength((int) image.length());
            picFile.setSuffix(FileUtils.getFileSuffix(image));
            picFile.setType(FileInfo.FILE_TYPE_IMAGE);
            Log.i("WK", "图片: " + picFile.getName() + " 后缀: " + picFile.getSuffix());
            imageList.add(picFile);

        }

        Collections.sort(imageList, (o1, o2) -> {

            File file1 = new File(o1.getPath());
            File file2 = new File(o2.getPath());

            if (file1.lastModified() > file2.lastModified()) {

                return -1;
            } else {
                return 1;
            }
        });


        this.imageList = imageList;


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
