package com.merpyzf.xmshare.common;

import com.merpyzf.xmshare.R;
import com.merpyzf.xmshare.ui.activity.SelectFilesActivity;

import java.util.ArrayList;

/**
 * Created by wangke on 2017/12/23.
 */

public class Const {
    /**
     * sp文件名, 存储用户信息和用户设置的信息
     */
    public static final String SP_USER = "sp_user_info";
    /**
     * 数据库名
     */
    public static final String DB_NAME = "xmshare.db";
    /**
     * 热点模式下用于匹配可连接热点名的前缀规则
     */
    public static final String SSID_PREFIX = "XM";
    /**
     * 传输模式
     */
    public static final String KEY_TRANSFER_MODE = "transfer_mode";
    /**
     * 使用已连接的局域网传输模式
     */
    public static final int TRANSFER_MODE_LAN = -1;
    /**
     * 通过建立热点组件局域网的方式传输模式
     */
    public static final int TRANSFER_MODE_AP = 1;
    public static final String HOME_OBSERVER_NAME = SelectFilesActivity.class.getSimpleName();
    /**
     * 头像信息
     */
    public static ArrayList<Integer> AVATAR_LIST;
    /**
     * 常用的文稿类型
     */
    public static ArrayList<String> FILE_DOCUMENT_TYPES;
    /**
     * 常用的压缩文件类型
     */
    public static ArrayList<String> FILE_COMPACT_TYPES;
    public static final int PING_COUNT = 10;
    public static final int PAGE_MAIN = 0;
    public static final int PAGE_APP = 1;
    public static final int PAGE_IMAGE = 2;
    public static final int PAGE_MUSIC = 3;
    public static final int PAGE_VIDEO = 4;

    public static final String PAGE_MAIN_TITLE = "主页";
    public static final String PAGE_APP_TITLE = "应用";
    public static final String PAGE_IMAGE_TITLE = "图片";
    public static final String PAGE_MUSIC_TITLE = "音乐";
    public static final String PAGE_VIDEO_TITLE = "视频";

    public static final int FAB_STATE_SEND = 0x01;
    public static final int FAB_STATE_CLEAR = 0x02;

    public static final String TAG_FILE_MANAGER = "frg_manager";


    /**
     * 初始化数据
     */
    static {

        AVATAR_LIST = new ArrayList<>();
        AVATAR_LIST.add(R.drawable.ic_avatar_0);
        AVATAR_LIST.add(R.drawable.ic_avatar_1);
        AVATAR_LIST.add(R.drawable.ic_avatar_2);
        AVATAR_LIST.add(R.drawable.ic_avatar_3);
        AVATAR_LIST.add(R.drawable.ic_avatar_4);
        AVATAR_LIST.add(R.drawable.ic_avatar_5);
        AVATAR_LIST.add(R.drawable.ic_avatar_6);
        AVATAR_LIST.add(R.drawable.ic_avatar_7);
        AVATAR_LIST.add(R.drawable.ic_avatar_8);
        AVATAR_LIST.add(R.drawable.ic_avatar_9);
        AVATAR_LIST.add(R.drawable.ic_avatar_10);
        AVATAR_LIST.add(R.drawable.ic_avatar_11);
        AVATAR_LIST.add(R.drawable.ic_avatar_12);
        AVATAR_LIST.add(R.drawable.ic_avatar_13);
        AVATAR_LIST.add(R.drawable.ic_avatar_14);
        AVATAR_LIST.add(R.drawable.ic_avatar_15);
        AVATAR_LIST.add(R.drawable.ic_avatar_16_o);


        FILE_DOCUMENT_TYPES = new ArrayList<>();
        FILE_DOCUMENT_TYPES.add("txt");
        FILE_DOCUMENT_TYPES.add("doc");
        FILE_DOCUMENT_TYPES.add("docx");
        FILE_DOCUMENT_TYPES.add("xls");
        FILE_DOCUMENT_TYPES.add("xlsx");
        FILE_DOCUMENT_TYPES.add("wps");
        FILE_DOCUMENT_TYPES.add("pdf");
        FILE_DOCUMENT_TYPES.add("pdf");
        FILE_DOCUMENT_TYPES.add("mobi");
        FILE_DOCUMENT_TYPES.add("azw");
        FILE_DOCUMENT_TYPES.add("azw3");
        FILE_DOCUMENT_TYPES.add("epub");
        FILE_DOCUMENT_TYPES.add(".key");
        FILE_DOCUMENT_TYPES.add(".psd");
        FILE_DOCUMENT_TYPES.add(".html");
        FILE_DOCUMENT_TYPES.add(".tif");
        FILE_DOCUMENT_TYPES.add(".caj");


        FILE_COMPACT_TYPES = new ArrayList<>();

        FILE_COMPACT_TYPES.add("zip");
        FILE_COMPACT_TYPES.add("rar");
        FILE_COMPACT_TYPES.add("tar");
        FILE_COMPACT_TYPES.add("tgz");
        FILE_COMPACT_TYPES.add("7z");
        FILE_COMPACT_TYPES.add("IMG");
        FILE_COMPACT_TYPES.add("ISO");


    }


}
