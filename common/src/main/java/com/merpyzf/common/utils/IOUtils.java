package com.merpyzf.common.utils;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class IOUtils {
    private IOUtils(){

    }

    public static long skipBytesFromStream(InputStream inputStream, long n) {
        long remaining = n;
        // SKIP_BUFFER_SIZE is used to determine the size of skipBuffer
        int SKIP_BUFFER_SIZE = 2048;
        // skipBuffer is initialized in skip(long), if needed.
        byte[] skipBuffer = null;
        int nr = 0;
        if (skipBuffer == null) {
            skipBuffer = new byte[SKIP_BUFFER_SIZE];
        }
        byte[] localSkipBuffer = skipBuffer;
        if (n <= 0) {
            return 0;
        }
        while (remaining > 0) {
            try {
                nr = inputStream.read(localSkipBuffer, 0,
                        (int) Math.min(SKIP_BUFFER_SIZE, remaining));
            } catch (IOException e) {
                e.printStackTrace();
            }
            if (nr < 0) {
                break;
            }
            remaining -= nr;
        }
        return n - remaining;
    }

    /**
     * 输入流转换成字符串
     *
     * @param is 输入流
     * @return String字符串
     */
    public static String stream2Str(InputStream is) {
        StringBuffer sb = new StringBuffer();
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(is));
        String line = "";
        try {
            while ((line = bufferedReader.readLine()) != null) {
                sb.append(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                bufferedReader.close();
                is.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return sb.toString();
    }

    public static void writeStreamToFile(InputStream in, File file) {
        BufferedInputStream bis = null;
        BufferedOutputStream bos = null;
        try {
            bis = new BufferedInputStream(in);
            bos = new BufferedOutputStream(new FileOutputStream(file));
            byte[] buffer = new byte[2048];
            int readLength = -1;
            while ((readLength = bis.read(buffer, 0, buffer.length)) != -1) {
                bos.write(buffer, 0, readLength);
            }

            bos.flush();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            CloseUtils.close(bis, bos);
        }


    }
}
