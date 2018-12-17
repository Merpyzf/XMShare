package com.merpyzf.common.utils;

import java.io.Closeable;
import java.io.IOException;

/**
 * @author wangke
 */
public class CloseUtils {
    /**
     * Don't let anyone instantiate this class.
     */
    private CloseUtils() {
        throw new Error("Do not need instantiate!");
    }

    public static void close(Closeable... closeables) {
        if (closeables == null) {
            return;
        }
        for (Closeable closeable : closeables) {
            try {
                if (closeable != null) {
                    closeable.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
