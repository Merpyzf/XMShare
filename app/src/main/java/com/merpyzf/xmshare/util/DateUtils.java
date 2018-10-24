package com.merpyzf.xmshare.util;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class DateUtils {
    private DateUtils() {

    }
    public static String getDate(long date) {
        String format = "yyyy-MM-dd";
        Date d = new Date(date);
        SimpleDateFormat dateFormat = new SimpleDateFormat(format);
        String strDate = dateFormat.format(d);
        // 今天
        String nowDay = getDayDate(0);
        // 昨天
        String yesterday = getDayDate(1);
        // 前天
        String beforeYesterday = getDayDate(2);
        if (strDate.equals(nowDay)) {
            strDate = "今天";
        } else if (strDate.equals(yesterday)) {
            strDate = "昨天";
        } else if (strDate.equals(beforeYesterday)) {
            strDate = "前天";
        }
        return strDate;
    }

    public static String getDayDate(int dayBefore) {
        String format = "yyyy-MM-dd";
        SimpleDateFormat dateFormat = new SimpleDateFormat(format);
        Calendar calendar = Calendar.getInstance();
        int day = calendar.get(Calendar.DATE);
        calendar.set(Calendar.DATE, day - dayBefore);
        return dateFormat.format(calendar.getTime());
    }
}
