package com.ivan.horniichuk.notebook.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class DateUtils {
    public static Date getCurrentDateWithoutTime()
            throws ParseException {
        SimpleDateFormat formatter = new SimpleDateFormat(
                "dd/MM/yyyy");
        return formatter.parse(formatter.format(new Date()));
    }

    public static boolean verifyToCurrentDate(Date dateToVerify)
    {
        try {
            if(dateToVerify.after(getCurrentDateWithoutTime()))
            {
                return true;
            }
        } catch (ParseException e) {
            e.printStackTrace();
            return false;
        }
        return false;
    }

    public static String toDateString(Date date)
    {
        SimpleDateFormat format= new SimpleDateFormat("MM/dd");
        return format.format(date);
    }

}
