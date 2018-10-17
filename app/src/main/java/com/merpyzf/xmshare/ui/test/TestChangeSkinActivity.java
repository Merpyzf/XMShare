package com.merpyzf.xmshare.ui.test;

import android.content.Intent;
import android.content.res.AssetManager;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import com.merpyzf.xmshare.R;
import com.merpyzf.xmshare.ui.test.skin.ResourcesManager;
import com.merpyzf.xmshare.ui.activity.SelectFilesActivity;

import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class TestChangeSkinActivity extends AppCompatActivity {

    private Button btnPlugin;
    private ImageView ivLoadRes;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test_change_skin);

        btnPlugin = findViewById(R.id.btn_plugin);
        ivLoadRes = findViewById(R.id.iv_load_res);

        String skinPluginPath = Environment.getExternalStorageDirectory() + File.separator + "Skinpl";
        String pkgName = "com.merpyzf.skinplugin";


        btnPlugin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {



                startActivity(new Intent(getApplicationContext(), SelectFilesActivity.class));


            }
        });


    }

    private void loadPlugin(String skinPluginPath, String pkgName) {

        try {
            AssetManager assetManager = AssetManager.class.newInstance();
            Method addAssetPathMethod = assetManager.getClass().getMethod("addAssetPath", String.class);
            addAssetPathMethod.invoke(assetManager, skinPluginPath);

            Resources superResources = getResources();
            Resources resources = new Resources(assetManager, superResources.getDisplayMetrics(), superResources.getConfiguration());

            ResourcesManager resourcesManager = new ResourcesManager(resources, pkgName);

            Drawable skinBgDrawable = resourcesManager.getDrawableByResName("skin_bg");

            if (skinBgDrawable != null) {

                ivLoadRes.setImageDrawable(skinBgDrawable);


            }


        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }

    }
}
