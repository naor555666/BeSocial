package com.example.besocial;

import android.app.ActionBar;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.widget.DatePicker;
import android.widget.TextView;

import androidx.fragment.app.DialogFragment;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class DatePickerFragment extends DialogFragment
        implements DatePickerDialog.OnDateSetListener {

    private TextView tv, endDate;
    private static Date minDate = Calendar.getInstance().getTime();

    public DatePickerFragment(TextView tv, TextView endDate) {
        this.tv = tv;
        this.endDate = endDate;
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
        if (tv.getId() == R.id.eventCreate_EndDate) {
            dpd.getDatePicker().setMinDate(minDate.getTime());
        } else
            dpd.getDatePicker().setMinDate(System.currentTimeMillis() - 1000);
        return dpd;
    }

    public void onDateSet(DatePicker view, int year, int month, int day) {
        DateFormat df = new SimpleDateFormat("dd/MM/yyyy");
        String strChosenDate = "" + day + "/" + (month + 1) + "/" + year;

        //take care of dates input
        if (tv.getId() == R.id.eventCreate_StartDate) {
            try {
                minDate = df.parse(strChosenDate);
                String strEndDate = endDate.getText().toString();
                Date realEndDate = df.parse(strEndDate);
                if (minDate.after(realEndDate))
                    this.endDate.setText(strChosenDate);
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
        tv.setText(strChosenDate);
    }
}