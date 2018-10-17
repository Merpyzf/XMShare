package com.merpyzf.xmshare.ui.test.skin;

import android.content.res.Resources;
import android.graphics.drawable.Drawable;

/**
 * Created by merpyzf on 2018/3/28.
 */

public class ResourcesManager {

    private Resources mResources;
    private String mSkinPluginPkg;

    public ResourcesManager(Resources resources, String skinPluginPkg) {
        this.mResources = resources;
        this.mSkinPluginPkg = skinPluginPkg;
    }


    public Drawable getDrawableByResName(String name) {

        try {

            return mResources.getDrawable(mResources.getIdentifier(name, "drawable", mSkinPluginPkg));
        } catch (Exception e) {
            e.printStackTrace();
            return null;

        }


    }


}
