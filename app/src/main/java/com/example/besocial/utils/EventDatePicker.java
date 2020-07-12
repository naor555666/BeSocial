package com.example.besocial.utils;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.widget.DatePicker;
import android.widget.TextView;
import android.widget.Toast;

import com.example.besocial.R;
import com.example.besocial.ui.mainactivity.socialcenter.CreateEventFragment;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class EventDatePicker extends DatePickerFragment implements DatePickerDialog.OnDateSetListener {
    private TextView tv, startDate, endDate;
    private static Date minDate = Calendar.getInstance().getTime();

    public EventDatePicker(TextView tv, TextView startDate, TextView endDate) {
        this.tv = tv;
        this.startDate = startDate;
        this.endDate = endDate;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        DatePickerDialog dpd;
        dpd = (DatePickerDialog) super.onCreateDialog(savedInstanceState);
        // Create a new instance of DatePickerDialog and return it
        if (tv.getId() == R.id.eventCreate_EndDate) {
            dpd.getDatePicker().setMinDate(minDate.getTime());
        } else
            dpd.getDatePicker().setMinDate(System.currentTimeMillis() - 1000);
        return dpd;
    }


    @Override
    public void onDateSet(DatePicker view, int year, int month, int day) {
        DateFormat df = new SimpleDateFormat("dd/MM/yyyy");
        super.onDateSet(view,year,month,day);
        String strEndDate = endDate.getText().toString();
        String strStartDate = startDate.getText().toString();

        //take care of dates input and correctness for event choosing dates
        if (tv.getId() == R.id.eventCreate_StartDate) {
            try {
                minDate = df.parse(strChosenDate);
                Date realEndDate = df.parse(strEndDate);
                if (minDate.after(realEndDate))
                    this.endDate.setText(strChosenDate);
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
        tv.setText(strChosenDate);
        strEndDate = endDate.getText().toString();
        strStartDate = startDate.getText().toString();
        if (strStartDate.equals(strEndDate))
            if (TimePickerFragment.handleTimingsValidation(CreateEventFragment.getStartTime(),
                    CreateEventFragment.getEndTime(), "", tv))
                Toast.makeText(getContext(), CreateEventFragment.EVENT_TIME_ERROR_MESSAGE,
                        Toast.LENGTH_LONG).show();
    }
}
