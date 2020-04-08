package com.example.besocial;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.besocial.ui.mainactivity.socialcenter.CreateEventFragment;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PointOfInterest;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


public class MapsActivity extends FragmentActivity implements OnMapReadyCallback, GoogleMap.OnMyLocationButtonClickListener,
        GoogleMap.OnMyLocationClickListener, GoogleMap.OnMapClickListener,
        View.OnClickListener, GoogleMap.OnPoiClickListener {

    private static final String TAG = "Map";
    private static GoogleMap mMap;
    private static Marker currentMarker = null;
    private static TextView currentLatLng;
    private static EditText locationName;
    private SearchView searchBar;
    private Button savebtn, cancelbtn;
    private int LOCATION_PERMISSION_REQUEST_CODE = 1234;
    private String[] locationPermission = {Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION};
    private boolean mLocationPermissionGranted;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        savebtn = findViewById(R.id.mapSave);
        savebtn.setEnabled(false);
        cancelbtn = findViewById(R.id.mapCancel);
        currentLatLng = findViewById(R.id.mapCurrentLatLng);
        locationName = findViewById(R.id.mapLocationName);
        searchBar = findViewById(R.id.mapSearchBar);
    }


    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        requestLocationPermission();
        setListeners();
        if (this.mLocationPermissionGranted) {
            mMap.setMyLocationEnabled(true);
            mMap.setOnMyLocationButtonClickListener(this);
            mMap.setOnMyLocationClickListener(this);
        }
    }

    //if not already granted, request for the permission to use location
    private void requestLocationPermission() {
        if (ContextCompat.checkSelfPermission(this.getApplicationContext(), Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            if (mMap != null) {
                mMap.setMyLocationEnabled(true);
                mLocationPermissionGranted = true;
            }
        } else {
            // Permission to access the location is missing. Show rationale and request permission
            ActivityCompat.requestPermissions(this, this.locationPermission,
                    this.LOCATION_PERMISSION_REQUEST_CODE);
        }
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

    public static GoogleMap getmMap() {
        return mMap;
    }

    public static void setmMap(GoogleMap mMap) {
        MapsActivity.mMap = mMap;
    }

    /**
     * user presses on the location button
     *
     * @return
     */
    @Override
    public boolean onMyLocationButtonClick() {
        return false;
    }

    /**
     * user presses on the blue sticker
     *
     * @param location
     */
    @Override
    public void onMyLocationClick(@NonNull Location location) {
        removeMarkersOffMap();
        LatLng currentLocation = new LatLng(location.getLatitude(), location.getLongitude());
        currentMarker = mMap.addMarker(new MarkerOptions().position(currentLocation).title("Event location"));
        currentLatLng.setText("Location: " + String.format("%.5f", currentLocation.latitude) + "," + String.format("%.5f", currentLocation.longitude));
        savebtn.setEnabled(true);
    }

    public void setListeners() {
        mMap.setOnMapClickListener(this);
        mMap.setOnPoiClickListener(this);
        savebtn.setOnClickListener(this);
        cancelbtn.setOnClickListener(this);
        searchBar.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                locateSearch();
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });

    }

    private void locateSearch() {
        String searchString = searchBar.getQuery().toString();
        Geocoder geocoder = new Geocoder(MapsActivity.this);
        List<Address> list = new ArrayList<>();
        try {
            list = geocoder.getFromLocationName(searchString, 1);
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (list.size() > 0) {
            Address address = list.get(0);
            removeMarkersOffMap();
            LatLng latLng = new LatLng(address.getLatitude(), address.getLongitude());
            currentMarker = mMap.addMarker(new MarkerOptions().
                    position(latLng)
                    .title("Event location"));
            currentLatLng.setText("Location: " + String.format("%.5f", address.getLatitude())
                    + "," + String.format("%.5f", address.getLongitude()));
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15f));
            savebtn.setEnabled(true);
        }
    }

    @Override
    public void onMapClick(LatLng latLng) {
        removeMarkersOffMap();
        currentMarker = mMap.addMarker(new MarkerOptions().position(latLng).title("Event location"));
        currentLatLng.setText("Location: " + String.format("%.5f", latLng.latitude)
                + "," + String.format("%.5f", latLng.longitude));
        savebtn.setEnabled(true);
    }

    @Override
    public void onPoiClick(PointOfInterest pointOfInterest) {
        removeMarkersOffMap();
        currentMarker = mMap.addMarker(new MarkerOptions().position(pointOfInterest.latLng).title("Event location"));
        currentLatLng.setText("Location: " + String.format("%.5f", pointOfInterest.latLng.latitude)
                + "," + String.format("%.5f", pointOfInterest.latLng.longitude));
        savebtn.setEnabled(true);
    }

    public void removeMarkersOffMap() {
        mMap.clear();
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.mapSave) {
            if (locationName.getText().toString().isEmpty()) {
                Toast.makeText(this, "Enter a name for the location", Toast.LENGTH_SHORT).show();
            } else {
                LatLng chosenLocation = currentMarker.getPosition();
                CreateEventFragment.setEventLocation(chosenLocation);
                CreateEventFragment.setLocationName(locationName.getText().toString());
                finish();
            }
        } else if (v.getId() == R.id.mapCancel) {
            finish();
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }


}
