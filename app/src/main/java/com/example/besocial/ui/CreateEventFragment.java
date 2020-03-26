package com.example.besocial.ui;


import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.Image;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.besocial.MainActivity;
import com.example.besocial.MapsActivity;
import com.example.besocial.R;
import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;


/**
 * A simple {@link Fragment} subclass.
 */
public class CreateEventFragment extends Fragment implements View.OnClickListener {
    private ImageView locationIcon;
    private static TextView locationName;
    private static LatLng eventLocation;
    private FirebaseStorage storage = FirebaseStorage.getInstance();

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
        StorageReference storageRef = storage.getReference();

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
    }

    public static void setEventLocation(LatLng eventLocation) {
        eventLocation = eventLocation;
    }

    public static void setLocationName(String locationName) {
        CreateEventFragment.locationName.setText(locationName);
    }
}
