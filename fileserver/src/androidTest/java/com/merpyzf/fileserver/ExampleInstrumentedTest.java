package com.merpyzf.fileserver;

import android.content.Context;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;

import com.merpyzf.fileserver.util.IOUtils;

import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.IOException;
import java.io.InputStream;

import static org.junit.Assert.assertEquals;

/**
 * Instrumented test, which will execute on an Android device.
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
@RunWith(AndroidJUnit4.class)
public class ExampleInstrumentedTest {
    @Test
    public void useAppContext() {
        // Context of the app under test.
        Context appContext = InstrumentationRegistry.getTargetContext();

        assertEquals("com.merpyzf.fileserver.test", appContext.getPackageName());
    }

    @Test
    public void testReadFileItem() {

        // Context of the app under test.
        Context appContext = InstrumentationRegistry.getTargetContext();
        try {
            InputStream inputStream = appContext.getAssets().open("file_item.template");
            // 单个文件条目模板
            String fileItemTemplate = IOUtils.stream2Str(inputStream);
            String fileItem = fileItemTemplate.replace("{file_name}", "这是一个文件名")
                    .replace("{file_path}", "这是一个文件路径")
                    .replace("{file_size}", "文件大小");
            System.out.println(fileItem);

        } catch (IOException e) {
            e.printStackTrace();
        }

    }

}
