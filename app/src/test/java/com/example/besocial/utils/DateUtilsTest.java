package com.example.besocial.utils;

import org.junit.Test;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import static org.junit.Assert.*;

public class DateUtilsTest {
    String strBeginDate;
    String strFinishDate;
    String strBeginTime;
    String strFinishTime;
    DateFormat df = new SimpleDateFormat("dd/MM/yyyy");
    String strCurrentDate = df.format(Calendar.getInstance().getTime());
    final Calendar c = Calendar.getInstance();
    int hour = c.get(Calendar.HOUR_OF_DAY);
    int minute = c.get(Calendar.MINUTE);
    String strCurrentTime = new StringBuilder().append("").append(DateUtils.checkDigit(hour)).append(":").append(DateUtils.checkDigit(minute)).toString();



    @Test
    public void isEventCurrentlyOccuring_true() {
        strBeginDate = strCurrentDate;
        strFinishDate = strCurrentDate;
        strBeginTime = "00:00";
        strFinishTime = "23:59";
        boolean result;

        result = DateUtils.isEventCurrentlyOccurring(strBeginDate, strFinishDate, strBeginTime, strFinishTime);
        assertTrue(result);
    }

    @Test
    public void isEventCurrentlyOccuring_beginDateFalse() {
        strBeginDate = "27/04/2050";
        strFinishDate = "27/04/2050";
        strBeginTime = strCurrentTime;
        strFinishTime = "23:59";
        boolean result;

        result = DateUtils.isEventCurrentlyOccurring(strBeginDate, strFinishDate, strBeginTime, strFinishTime);
        assertFalse(result);
    }

    @Test
    public void isEventCurrentlyOccuring_endDateFalse() {
        strBeginDate = "25/04/2020";
        strFinishDate = "25/04/2020";
        strBeginTime = strCurrentTime;
        strFinishTime = "23:59";
        boolean result;

        result = DateUtils.isEventCurrentlyOccurring(strBeginDate, strFinishDate, strBeginTime, strFinishTime);
        assertFalse(result);
    }
    @Test
    public void isEventCurrentlyOccuring_beginTimeFalse() {
        strBeginDate = strCurrentDate;
        strFinishDate = strCurrentDate;
        strBeginTime = "23:59";
        strFinishTime = "23:59";
        boolean result;

        result = DateUtils.isEventCurrentlyOccurring(strBeginDate, strFinishDate, strBeginTime, strFinishTime);
        assertFalse(result);
    }
    @Test
    public void isEventCurrentlyOccuring_endTimeFalse() {
        strBeginDate = "25/04/2020";
        strFinishDate = "25/04/2020";
        strBeginTime = "00:00";
        strFinishTime = "00:00";
        boolean result;

        result = DateUtils.isEventCurrentlyOccurring(strBeginDate, strFinishDate, strBeginTime, strFinishTime);
        assertFalse(result);
    }

/*    @Test
    public void isDateBetweenTwoGivenDate() {
    }

    @Test
    public void isTimeBetweenTwoGivenTimes() {
    }

    @Test
    public void isTimeBeforeGivenTime() {
    }

    @Test
    public void checkDigit() {
    }*/
}