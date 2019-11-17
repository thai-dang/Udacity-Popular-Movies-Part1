package com.example.android.udacity_popular_movies_part1;

import android.content.Context;

import java.util.Date;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;

class DateTimeUtils {

    // Returns a Date object
    private static Date getFormattedDate(String date, String format) throws ParseException {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(format);

        return simpleDateFormat.parse(date);
    }


    // Returns date string in short form based on the device settings.
    public static String getLocalizedDate(Context context, String date, String format) throws ParseException {
        DateFormat dateFormat = android.text.format.DateFormat.getDateFormat(context);

        return dateFormat.format(getFormattedDate(date, format));
    }
}
