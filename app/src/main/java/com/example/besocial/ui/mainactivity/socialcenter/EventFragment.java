package com.example.besocial.ui.mainactivity.socialcenter;


import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Looper;
import android.provider.Settings;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;

import com.bumptech.glide.Glide;
import com.example.besocial.ConstantValues;
import com.example.besocial.LatLng;
import com.example.besocial.R;
import com.example.besocial.data.Event;
import com.example.besocial.data.LiteUserDetails;
import com.example.besocial.databinding.FragmentEventBinding;
import com.example.besocial.ui.mainactivity.MainActivity;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.GeofencingClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;


/**
 * A simple {@link Fragment} subclass.
 */
public class EventFragment extends Fragment {
    private static final String TAG = "EventFragment";
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 666;
    private FragmentEventBinding binding;
    private Event chosenEvent;
    private SocialCenterViewModel socialCenterViewModel;
    private boolean isUserAttending = false;
    private boolean isCheckInPossible = false;

    private String[] locationPermission = {Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION};
    private boolean mLocationPermissionGranted;

    FusedLocationProviderClient mFusedLocationClient;
    private GeofencingClient geofencingClient;

    public EventFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentEventBinding.inflate(inflater, container, false);
        View view = binding.getRoot();
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        geofencingClient = LocationServices.getGeofencingClient(getActivity());
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(getActivity());
    }

    @Override
    public void onStart() {
        super.onStart();
        setEventDetails();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        socialCenterViewModel = ViewModelProviders.of(getActivity()).get(SocialCenterViewModel.class);
        chosenEvent = socialCenterViewModel.getEvent().getValue();
        setListeners();
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
        databaseReference.child(ConstantValues.USERS_ATTENDING_TO_EVENTS)
                .child(MainActivity.getLoggedUser().getUserId())
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        String path = "/" + chosenEvent.getEventId();
                        if (dataSnapshot.hasChild(path)) {
                            isUserAttending = true;
                            Log.d(TAG, "user attending");
                        }
                        setAttendingButton();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                    }
                });
    }

    private void setAttendingButton() {
        //if user is not the host
        if (!chosenEvent.getEventCreatorUid().equals(MainActivity.getLoggedUser().getUserId())) {
            Log.d(TAG, "user is not the host");
            //check if user is attending
            if (isUserAttending) {
                Log.d(TAG, "set to check in");
                binding.fragmentEventAttendBtn.setText("Check in");
            }
            Log.d(TAG, "set to visible");
            binding.fragmentEventAttendBtn.setVisibility(View.VISIBLE);
        }
    }
/*
    private void handleLocationPermission() {
        requestLocationPermission();
    }*/

    @SuppressLint("MissingPermission")
    private void getLastLocation() {
        if (checkLocationPermission()) {
            if (isLocationEnabled()) {
                mFusedLocationClient.getLastLocation().addOnCompleteListener(
                        new OnCompleteListener<Location>() {
                            @Override
                            public void onComplete(@NonNull Task<Location> task) {
                                Location location = task.getResult();
                                if (location == null) {
                                    requestNewLocationData();
                                } else {
                                    Log.d(TAG, "latitude: " + location.getLatitude() + " longitude :" + location.getLongitude());

                                }
                            }
                        }
                );
            } else {
                Toast.makeText(getActivity(), "Turn on location", Toast.LENGTH_LONG).show();
                Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                startActivity(intent);
            }
        } else {
            requestPermissions();
        }
    }

    //if not already granted, request for the permission to use location
    private boolean checkLocationPermission() {
        if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            return true;
        }
        return false;
    }
    private void requestPermissions() {
        // Permission to access the location is missing. Show rationale and request permission
        ActivityCompat.requestPermissions(getActivity(), this.locationPermission,
                this.LOCATION_PERMISSION_REQUEST_CODE);
    }

    //handle the user result for the permission request
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode != LOCATION_PERMISSION_REQUEST_CODE) {
            return;
        }

        if (grantResults.length > 0) {
            for (int i = 0; i < grantResults.length; i++) {
                if (grantResults[i] != PackageManager.PERMISSION_GRANTED) {
                    mLocationPermissionGranted = false;
                    break;
                }
            }
        } else {
            //permission was granted
            mLocationPermissionGranted = true;
        }
    }


    @SuppressLint("MissingPermission")
    private void requestNewLocationData() {

        LocationRequest mLocationRequest = new LocationRequest();
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        mLocationRequest.setInterval(0);
        mLocationRequest.setFastestInterval(0);
        mLocationRequest.setNumUpdates(1);

        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(getActivity());
        mFusedLocationClient.requestLocationUpdates(
                mLocationRequest, mLocationCallback,
                Looper.myLooper()
        );
    }

    private LocationCallback mLocationCallback = new LocationCallback() {
        @Override
        public void onLocationResult(LocationResult locationResult) {
            Location mLastLocation = locationResult.getLastLocation();
            Log.d(TAG, "latitude: " + mLastLocation.getLatitude() + " longitude :" + mLastLocation.getLongitude());

        }
    };

    private boolean isLocationEnabled() {
        LocationManager locationManager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) || locationManager.isProviderEnabled(
                LocationManager.NETWORK_PROVIDER
        );
    }

    private void setListeners() {
        binding.fragmentEventAttendBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                attendToEvent();
            }
        });
        binding.fragmentEventLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Uri locationURL= Uri.parse(new StringBuilder().append("https://www.google.com/maps/search/?api=1&query=")
                        .append(chosenEvent.getLocation().getLatitude()).append(",")
                        .append(chosenEvent.getLocation().getLongitude()).toString());
                Intent mapsIntent = new Intent(Intent.ACTION_VIEW, locationURL);

                startActivity(mapsIntent);
            }
        });
    }

    private void attendToEvent() {
        binding.fragmentEventAttendBtn.setEnabled(false);
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
        LiteUserDetails user = new LiteUserDetails(MainActivity.getLoggedUser().getProfileImage()
                , MainActivity.getLoggedUser().getUserId()
                , MainActivity.getLoggedUser().getUserFirstName()
                , MainActivity.getLoggedUser().getUserLastName());

        Map<String, Object> childUpdates = new HashMap<>();

        childUpdates.put("/" + ConstantValues.USERS_ATTENDING_TO_EVENTS
                + "/" + MainActivity.getLoggedUser().getUserId() + "/" + chosenEvent.getEventId(), chosenEvent);

        childUpdates.put("/" + ConstantValues.EVENTS_WITH_ATTENDINGS
                + "/" + chosenEvent.getEventId() + "/" + MainActivity.getLoggedUser().getUserId(), user);

        databaseReference.updateChildren(childUpdates);
        Toast.makeText(getContext(), "Attending to this event!", Toast.LENGTH_LONG).show();
    }


    private void setEventDetails() {
        Glide.with(getContext()).load(chosenEvent.getStrEventPhotoUrl()).placeholder(R.drawable.social_event0).centerCrop().into(binding.fragmentEventPhoto);
        binding.fragmentEventTitle.setText(chosenEvent.getTitle());
        binding.fragmentEventHostFullName.setText("Hosted by: " + chosenEvent.getEventCreatorUserName());
        binding.fragmentEventDateTime.setText(chosenEvent.getBeginDate() + "," + chosenEvent.getBeginTime()
                + "-" + chosenEvent.getFinishDate() + "," + chosenEvent.getFinishTime());
        binding.fragmentEventLocation.setText("Location: " + chosenEvent.getLocationTitle());
        binding.fragmentEventDescription.setText(chosenEvent.getDescription());
    }
}
