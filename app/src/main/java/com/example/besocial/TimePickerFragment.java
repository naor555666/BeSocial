package com.example.besocial;

import android.app.Dialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.fragment.app.DialogFragment;

import com.example.besocial.ui.mainactivity.socialcenter.CreateEventFragment;

import java.util.Calendar;
import java.util.Date;

public class TimePickerFragment extends DialogFragment
        implements TimePickerDialog.OnTimeSetListener {
    private TextView tv, startTime, endTime, startDate, endDate;
    private static Date minTime = Calendar.getInstance().getTime();


    public TimePickerFragment(TextView tv, TextView startTime, TextView endTime,
                              TextView startDate, TextView endDate) {
        this.tv = tv;
        this.startTime = startTime;
        this.endTime = endTime;
        this.startDate = startDate;
        this.endDate = endDate;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Use the current time as the default values for the picker
        final Calendar c = Calendar.getInstance();
        int hour = c.get(Calendar.HOUR_OF_DAY);
        int minute = c.get(Calendar.MINUTE);

        // Create a new instance of TimePickerDialog and return it
        return new TimePickerDialog(getActivity(), this, hour, minute,
                DateFormat.is24HourFormat(getActivity()));
    }

    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
        String strChosenTime = "" + checkDigit(hourOfDay) + ":" + checkDigit(minute);
        String strStartDate, strEndDate;

        strStartDate = startDate.getText().toString();
        strEndDate = endDate.getText().toString();

        if (strStartDate.equals(strEndDate)) {
            if (handleTimingsValidation(startTime, endTime, strChosenTime, tv))
                Toast.makeText(getContext(), CreateEventFragment.EVENT_TIME_ERROR_MESSAGE, Toast.LENGTH_LONG).show();
        }
        tv.setText(strChosenTime);
    }

    public static boolean handleTimingsValidation(TextView startTime, TextView endTime, String strChosenTime, TextView tv) {
        String strStartTime = startTime.getText().toString();
        String strEndTime = endTime.getText().toString();
        boolean isStartTimeBeforeEndTime;
        switch (tv.getId()) {
            case R.id.eventCreate_StartTime:
                isStartTimeBeforeEndTime = checkTimings(strChosenTime, strEndTime);
                break;
            case R.id.eventCreate_EndTime:
                isStartTimeBeforeEndTime = checkTimings(strStartTime, strChosenTime);
                break;
            default:
                isStartTimeBeforeEndTime = checkTimings(strStartTime, strEndTime);
        }

        if (!isStartTimeBeforeEndTime) {
            startTime.setText(strChosenTime);
            endTime.setText(strChosenTime);
            return true;
        }
        return false;
    }

    //format the the digits to fully hh:mm format
    //add '0' digit before if needed
    public String checkDigit(int number) {
        return number <= 9 ? "0" + number : String.valueOf(number);
    }

    //return true if @time comes before @end time, false otherwise
    public static boolean checkTimings(String time, String endtime) {
        if (time.equals("") || endtime.equals(""))
            return true;
        int isStartTimeBeforeEndTime = time.compareTo(endtime);

        if (isStartTimeBeforeEndTime <= 0) {
            return true;
        } else {
            return false;
        }
    }
}
