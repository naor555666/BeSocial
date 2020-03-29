package com.example.besocial.ui;


import android.Manifest;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.Image;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;

import com.example.besocial.DatePickerFragment;
import com.example.besocial.MainActivity;
import com.example.besocial.MapsActivity;
import com.example.besocial.R;
import com.example.besocial.TimePickerFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.Calendar;


/**
 * A simple {@link Fragment} subclass.
 */
public class CreateEventFragment extends Fragment implements View.OnClickListener {
    public final static String EVENT_TIME_ERROR_MESSAGE = "Event cannot be finished before it started,times have changed automatically";
    private ImageView locationIcon;
    private static TextView locationName;
    private static LatLng eventLocation;
    private static Spinner categorySpinner;
    private static TextView startTime, endTime, startDate, endDate;
    private static String chosenDate, chosenTime;
    private FirebaseStorage storage = FirebaseStorage.getInstance();
    private Boolean isHelpEvent = false;

    public CreateEventFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_create_event, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        locationIcon = view.findViewById(R.id.create_event_location_ic);
        locationName = view.findViewById(R.id.create_event_location);
        startTime = view.findViewById(R.id.eventCreate_StartTime);
        endTime = view.findViewById(R.id.eventCreate_EndTime);
        startDate = view.findViewById(R.id.eventCreate_StartDate);
        endDate = view.findViewById(R.id.eventCreate_EndDate);

        // Use the current date as the default date in the picker
        final Calendar c = Calendar.getInstance();
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH);
        int day = c.get(Calendar.DAY_OF_MONTH);

        String strDefaultDate = "" + day + "/" + (month + 1) + "/" + year;
        startDate.setText(strDefaultDate);

        //initializing the spinner list of categories
        categorySpinner = view.findViewById(R.id.eventCreate_categorySpinner);
        ArrayAdapter<CharSequence> arrayAdapter = ArrayAdapter.createFromResource(getContext(), R.array.list_of_categories, R.layout.support_simple_spinner_dropdown_item);
        arrayAdapter.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item);
        categorySpinner.setAdapter(arrayAdapter);
        StorageReference storageRef = storage.getReference();

        //set the spinner to 'Help Me' category if the user chose get help option in socioal center
        if (!(getArguments().isEmpty())) {
            isHelpEvent = getArguments().getBoolean(SocialCenterFragment.IS_Help_EVENT);
            categorySpinner.setSelection(categorySpinner.getAdapter().getCount() - 1);
            categorySpinner.setEnabled(false);
        }
        setListeners();
    }


    @Override
    public void onClick(View v) {
        //start the map
        if (v.getId() == R.id.create_event_location) {
            Intent mapIntent = new Intent(getActivity(), MapsActivity.class);
            startActivity(mapIntent);
        }

    }


    public void setListeners() {
        locationName.setOnClickListener(this);

        DateHandler dp = new DateHandler();
        startDate.setOnClickListener(dp);
        endDate.setOnClickListener(dp);

        TimeHandler th = new TimeHandler();
        startTime.setOnClickListener(th);
        endTime.setOnClickListener(th);
    }

    private class TimeHandler implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            showTimePickerDialog((TextView) v);
        }

        private void showTimePickerDialog(TextView tv) {
            TimePickerFragment newFragment = new TimePickerFragment(tv, startTime, endTime, startDate, endDate);
            newFragment.show(getFragmentManager(), null);
        }
    }

    private class DateHandler implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            showDatePickerDialog((TextView) v);
        }

        private void showDatePickerDialog(TextView tv) {
            DatePickerFragment newFragment = new DatePickerFragment(tv, startDate, endDate);
            newFragment.show(getFragmentManager(), null);
        }
    }


    public static void setEventLocation(LatLng eventLocation) {
        eventLocation = eventLocation;
    }

    public static void setLocationName(String locationName) {
        CreateEventFragment.locationName.setText(locationName);
    }

    public static void setChosenTime(String chosenTime) {
        CreateEventFragment.chosenTime = chosenTime;
    }

    public static TextView getStartTime() {
        return startTime;
    }

    public static TextView getEndTime() {
        return endTime;
    }

    public static void setStartTime(TextView startTime) {
        CreateEventFragment.startTime = startTime;
    }

    public static void setEndTime(TextView endTime) {
        CreateEventFragment.endTime = endTime;
    }
}
