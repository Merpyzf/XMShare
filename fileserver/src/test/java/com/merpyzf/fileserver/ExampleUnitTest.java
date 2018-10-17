package com.merpyzf.fileserver;

import com.merpyzf.fileserver.util.FileUtils;

import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class ExampleUnitTest {
    @Test
    public void addition_isCorrect() {
        assertEquals(4, 2 + 2);
    }

    @Test
    public void getFileSuffix() {
        String path = "./data/app/comjiongjiandriodcard-2/baseapk";
        int beginIndex = path.lastIndexOf(".")+1;
        System.out.println(beginIndex);
        String suffix = path.substring(beginIndex);
        System.out.println(suffix);

    }
    @Test
    public void testSplit(){
        String fileStr = "20ec83e260730a4ef53175c604089114_b.jpg";
        String[] split = fileStr.split("\\.");
        System.out.println(split[0]);


    }


}