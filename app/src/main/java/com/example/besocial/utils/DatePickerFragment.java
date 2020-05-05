package com.example.besocial.utils;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.graphics.Paint;
import android.icu.util.LocaleData;
import android.icu.util.TimeZone;
import android.os.Bundle;
import android.util.Log;
import android.widget.DatePicker;

import androidx.fragment.app.DialogFragment;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;

public class DatePickerFragment extends DialogFragment
        implements DatePickerDialog.OnDateSetListener {

    private static final String TAG = "DatePickerFragment";
    protected String strChosenDate;

    public DatePickerFragment() {
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Use the current date as the default date in the picker
        final Calendar c = Calendar.getInstance();
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH);
        int day = c.get(Calendar.DAY_OF_MONTH);


        // Create a new instance of DatePickerDialog and return it
        DatePickerDialog dpd = new DatePickerDialog(getActivity(), this, year, month, day);
        return dpd;
    }

    public void onDateSet(DatePicker view, int year, int month, int day) {
        //original 2 lines
        DateFormat df = new SimpleDateFormat("dd/MM/yyyy");
        strChosenDate = "" + day + "/" + (month + 1) + "/" + year;

        //parse to format like : April 20, 2020
/*
        DateFormat df = new SimpleDateFormat("dd/MM/yyyy",Locale.US);
        strChosenDate ="" + day + "/" + (month + 1) + "/" + year;
        try {
            Log.d(TAG, "Chosen date: " + DateFormat.getDateInstance(1,Locale.US).format(df.parse(strChosenDate)));
            strChosenDate = DateFormat.getDateInstance(1,Locale.US).format(df.parse(strChosenDate));
        } catch (ParseException e) {
            e.printStackTrace();
        }
*/
    }
}