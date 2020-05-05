package com.example.besocial.ui.mainactivity.socialcenter;


import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.example.besocial.utils.ConstantValues;
import com.example.besocial.utils.BitmapUtils;
import com.example.besocial.utils.EventDatePicker;
import com.example.besocial.R;
import com.example.besocial.utils.TimePickerFragment;
import com.example.besocial.data.Event;
import com.example.besocial.data.User;
import com.example.besocial.ui.mainactivity.MainActivity;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.IOException;
import java.util.Calendar;

//import com.example.besocial.utils.EventDatePicker;


/**
 * A simple {@link Fragment} subclass.
 */
public class CreateEventFragment extends Fragment implements View.OnClickListener {
    public final static String EVENT_TIME_ERROR_MESSAGE = "Event cannot be finished before it started,times have changed automatically";
    public static final int PICK_IMAGE = 1;
    private static final String TAG = "CreateEventFragment";
    private ProgressDialog loadingBar;


    private ImageView locationIcon, eventPhoto;
    private Uri pickedImageFromGallery = null;
    private byte[] imageInByte = null;
    private TextView description;
    private TextView eventTitle;
    private Button createEventBtn;
    private static TextView locationName;
    private static LatLng eventLocation;
    private static Spinner categorySpinner;
    private static TextView startTime, endTime, startDate, endDate;
    private TextView helpCategory;
    private static String chosenDate, chosenTime;
    private String saveCurrentDate, saveCurrentTime, eventRandomName, downloadUrl;
    User loggedUser = MainActivity.getLoggedUser();
    private String strEventPhotoUrl = null;
    private String strEventCategory;
    private String strEventTitle;
    private String strStartDate;
    private String strEndDate;
    private String strStartTime;
    private String strEndTime;
    private String strLocationName;
    private String strDescription;

    private FirebaseStorage storage = FirebaseStorage.getInstance();
    private StorageReference imagesReference = storage.getReference();
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
        eventPhoto = view.findViewById(R.id.eventCreate__photo);
        locationIcon = view.findViewById(R.id.create_event_location_ic);
        eventTitle = view.findViewById(R.id.eventCreate_title);
        locationName = view.findViewById(R.id.create_event_location);
        startTime = view.findViewById(R.id.eventCreate_StartTime);
        endTime = view.findViewById(R.id.eventCreate_EndTime);
        startDate = view.findViewById(R.id.eventCreate_StartDate);
        endDate = view.findViewById(R.id.eventCreate_EndDate);
        helpCategory = view.findViewById(R.id.fragment_create_event_category);

        description = view.findViewById(R.id.eventCreate_Description);
        createEventBtn = view.findViewById(R.id.eventCreate_createEventButton);
        loadingBar = new ProgressDialog(getContext());

        initializeStartDate();

        //initializing the spinner list of categories
        categorySpinner = view.findViewById(R.id.eventCreate_categorySpinner);
        ArrayAdapter<CharSequence> arrayAdapter = ArrayAdapter.createFromResource(getContext(), R.array.list_of_categories, R.layout.support_simple_spinner_dropdown_item);
        arrayAdapter.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item);
        categorySpinner.setAdapter(arrayAdapter);
        StorageReference storageRef = storage.getReference();

        //set the spinner to 'Help Me' category if the user chose get help option in socioal center
        if (!(getArguments().isEmpty())) {
            isHelpEvent = getArguments().getBoolean(ConstantValues.IS_HELP_EVENT);
/*            categorySpinner.setSelection(categorySpinner.getAdapter().getCount() - 1);
            categorySpinner.setEnabled(false);*/
            categorySpinner.setVisibility(View.GONE);
            helpCategory.setVisibility(view.VISIBLE);

        }
        setListeners();
    }

    private void initializeStartDate() {
        // Use the current date as the default date in the picker
        final Calendar c = Calendar.getInstance();
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH);
        int day = c.get(Calendar.DAY_OF_MONTH);

        String strDefaultDate = "" + day + "/" + (month + 1) + "/" + year;
        startDate.setText(strDefaultDate);
    }


    @Override
    public void onClick(View v) {
        //start the map
        if (v.getId() == R.id.create_event_location || v.getId() == R.id.mapViewLocationIcon) {
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
        createEventBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                validateEventDetails();
            }
        });
        eventPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openGallery();
            }
        });
    }

    private void validateEventDetails() {
        strEventCategory = isHelpEvent ? ConstantValues.HELP_ME : (String) categorySpinner.getSelectedItem();
        strEventTitle = eventTitle.getText().toString();
        strStartDate = startDate.getText().toString();
        strEndDate = endDate.getText().toString();
        strStartTime = startTime.getText().toString();
        strEndTime = endTime.getText().toString();
        strLocationName = locationName.getText().toString();
        strDescription = description.getText().toString();

        if (strEventCategory.isEmpty()) {
            Toast.makeText(getContext(), "Please choose a category for the event", Toast.LENGTH_LONG).show();
        } else if (strEventTitle.isEmpty()) {
            Toast.makeText(getContext(), "Please choose a title for the event", Toast.LENGTH_LONG).show();
        } else if (strStartDate.isEmpty()) {
            Toast.makeText(getContext(), "Please choose the date for the event", Toast.LENGTH_LONG).show();
        } else if (strEndDate.isEmpty()) {
            Toast.makeText(getContext(), "Please choose the date for the event", Toast.LENGTH_LONG).show();
        } else if (strStartTime.isEmpty()) {
            Toast.makeText(getContext(), "Please choose the time for the event", Toast.LENGTH_LONG).show();
        } else if (strEndTime.isEmpty()) {
            Toast.makeText(getContext(), "Please choose the time for the event", Toast.LENGTH_LONG).show();
        } else if (strLocationName.isEmpty()) {
            Toast.makeText(getContext(), "Please choose the location for the event", Toast.LENGTH_LONG).show();
        } else if (strDescription.isEmpty()) {
            Toast.makeText(getContext(), "Please choose a short description for the event", Toast.LENGTH_LONG).show();
        } else {
            showLoadingBar();
            if (pickedImageFromGallery == null) {
                saveEventInformationToDatabase();
            } else storeImageToFirebaseStorage();


        }


    }

    private void showLoadingBar() {
        loadingBar.setTitle("Add New Event");
        loadingBar.setMessage("Please wait, while we are updating your new event...");
        loadingBar.show();
        loadingBar.setCanceledOnTouchOutside(true);
    }

    private void storeImageToFirebaseStorage() {
        Long currentTime = System.currentTimeMillis();
        eventRandomName = pickedImageFromGallery == null ?
                currentTime.toString() : currentTime.toString() + pickedImageFromGallery.getLastPathSegment();

        Log.d(TAG, "random name + path is: " + eventRandomName);

        final StorageReference filePath = imagesReference.child("Events images/" + eventRandomName);
        filePath.putBytes(imageInByte).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                if (task.isSuccessful()) {
                    //strEventPhotoUrl = task.getResult().getUploadSessionUri().toString();
                    filePath.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            strEventPhotoUrl = uri.toString();
                            Toast.makeText(getContext(), "image uploaded successfully to Storage...", Toast.LENGTH_SHORT).show();
                            saveEventInformationToDatabase();
                        }
                    });

                } else {
                    String message = task.getException().getMessage();
                    Toast.makeText(getContext(), "Error occured: " + message, Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void saveEventInformationToDatabase() {

        DatabaseReference eventsRef = FirebaseDatabase.getInstance().getReference();
        if (!isHelpEvent) {
            eventsRef=eventsRef.child(ConstantValues.EVENTS).push();
        }else {
            eventsRef=eventsRef.child(ConstantValues.HELP_ME).push();
        }

//        eventsRef.child("Events").child(loggedUser.getUserId() + eventRandomName)
        eventRandomName = eventsRef.getKey();
        Event newEvent = new Event(eventRandomName, strEventPhotoUrl, strEventCategory, strEventTitle, strStartDate, strEndDate, strStartTime
                , strEndTime, new com.example.besocial.data.LatLng(eventLocation.latitude, eventLocation.longitude), strLocationName, strDescription, loggedUser.getUserId()
                , loggedUser.getUserFirstName() + " " + loggedUser.getUserLastName()
                , loggedUser.isManager());

        Log.d(TAG, "new event is:\n" + newEvent.toString());
        eventsRef.setValue(newEvent).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    Toast.makeText(getContext(), "New event is updated successfully.", Toast.LENGTH_SHORT).show();
                    loadingBar.dismiss();
                    getFragmentManager().popBackStack();
                } else {
                    Toast.makeText(getContext(), "Error Occured while updating your event.", Toast.LENGTH_SHORT).show();
                    loadingBar.dismiss();
                }
            }
        });
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
            EventDatePicker newFragment = new EventDatePicker(tv, startDate, endDate);
            newFragment.show(getFragmentManager(), null);
        }
    }

    public void openGallery() {
        Intent galleryIntent = new Intent();
        galleryIntent.setType("image/*");
        galleryIntent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(galleryIntent, "Select Picture"), PICK_IMAGE);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == PICK_IMAGE && resultCode == Activity.RESULT_OK && data != null) {
            pickedImageFromGallery = data.getData();
            try {
                BitmapUtils rotateBitmap = new BitmapUtils();
                imageInByte = rotateBitmap.compressAndRotateBitmap(getActivity(), pickedImageFromGallery);
                Glide.with(getContext()).load(imageInByte).centerCrop().into(eventPhoto);

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    //getters&setters
    public static void setEventLocation(LatLng chosenEventLocation) {
        CreateEventFragment.eventLocation = chosenEventLocation;
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
