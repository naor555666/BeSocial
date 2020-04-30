package com.example.besocial.ui.mainactivity;

import android.Manifest;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import androidx.preference.PreferenceManager;

import com.bumptech.glide.Glide;
import com.example.besocial.ConstantValues;
import com.example.besocial.GeofenceBroadcastReceiver;
import com.example.besocial.R;
import com.example.besocial.data.Event;
import com.example.besocial.data.Post;
import com.example.besocial.data.User;
import com.example.besocial.ui.login.LoginActivity;
import com.example.besocial.ui.mainactivity.mainmenu.LogoutDialog;
import com.example.besocial.utils.DateUtils;
import com.example.besocial.utils.LocationUpdatesService;
import com.example.besocial.utils.MyBroadcastReceiver;
import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofencingClient;
import com.google.android.gms.location.GeofencingRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;


public class MainActivity extends AppCompatActivity implements TextWatcher {
    private static final long EXPIRATION_DURATION = 15*60*1000 ;
    private final String IS_LOCATION_ACTIVATED = "isLocationActivated";
    public static final String LOCATION_1 = "555";
    private static User loggedUser;

    private static boolean isLocationActive = false;
    private static final String TAG = "MainActivity";

    private TextView nav_header_user_email, nav_header_user_full_name;

    private EditText search;
    private AppBarConfiguration mAppBarConfiguration;
    private DrawerLayout drawer;
    private static NavController navController;
    private CircleImageView nav_header_user_profile_picture;
    private BroadcastReceiver myBroadcastReceiver = new MyBroadcastReceiver();
    private static DatabaseReference currentUserDatabaseRef;
    private static FirebaseUser currentUser;
    private FirebaseAuth fireBaseAuth;
    private SharedPreferences sharedPref;
    private SharedPreferences.Editor editor;
    private List<String> usersList;

    private GeofencingClient geofencingClient;
    private ArrayList<Geofence> geofenceList;
    private PendingIntent geofencePendingIntent;
    private int background_location_permission_request_code = 5;
    private String[] locationPermission = {Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_BACKGROUND_LOCATION};
    private boolean mLocationPermissionGranted;
    private static ArrayList<Event> currentOccuringEvents,eventsToRemoveGeofences;;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        geofenceList=new ArrayList<>();

        fireBaseAuth = FirebaseAuth.getInstance();
        currentUser = fireBaseAuth.getCurrentUser();
        //  get user token (if he is logged in)
        loggedUser = new User();

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        //
        HomeFragment.getPosts().add(new Post(getResources().getDrawable(R.drawable.naor_profile_picture),
                "Naor Ohana", "12.1.20", "Hi everyone !", getResources().getDrawable(R.drawable.naor_profile_picture)));
        HomeFragment.getPosts().add(new Post(getResources().getDrawable(R.drawable.besociallogo),
                "BeSocial", "12.1.20", "Hello !", null));
        HomeFragment.getPosts().add(new Post(getResources().getDrawable(R.drawable.or_profile),
                "Or Magogi", "12.1.20", "I am here too", null));


        //

        search = findViewById(R.id.app_bar_search);
        drawer = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        mAppBarConfiguration = new AppBarConfiguration.Builder(
                R.id.nav_home, R.id.nav_my_profile, R.id.nav_notifications, R.id.nav_chat, R.id.nav_social_center, R.id.nav_bonus_area, R.id.nav_settings)
                .setDrawerLayout(drawer)
                .build();
        navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
        NavigationUI.setupWithNavController(navigationView, navController);

        //get reference to the header of the navigation view and its components
        View header = navigationView.getHeaderView(0);
        nav_header_user_email = (TextView) header.findViewById(R.id.nav_header_user_email);
        nav_header_user_full_name = (TextView) header.findViewById(R.id.nav_header_user_full_name);
        nav_header_user_profile_picture = (CircleImageView) header.findViewById(R.id.nav_header_user_profile_picture);

        IntentFilter intentFilter = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
        registerReceiver(myBroadcastReceiver, intentFilter);
        search.addTextChangedListener(new RegisterTextWatcher(search.getId()));

        sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
        editor = sharedPref.edit();
        isLocationActive = sharedPref.getBoolean(IS_LOCATION_ACTIVATED, false);
        System.out.println("on create: music playing: " + isLocationActive);
        ImageButton activateLocation = findViewById(R.id.app_bar_activate_location);

        if (LocationUpdatesService.getInstance() != null) {

            if (!isLocationActive) {
                Glide.with(this).load(R.drawable.ic_my_location_blue_24dp).into((ImageButton) activateLocation);
            }
        } else {
            isLocationActive = false;
            Glide.with(this).load(R.drawable.ic_my_location_black_24dp).into((ImageButton) activateLocation);

        }

    }


    @Override
    protected void onStart() {
        super.onStart();
        Log.d(TAG, "inside on Start");

        if (currentUser == null) {    // if the user is not logged in
            sendUserToLogin();
        }
        //
        else {
            currentUserDatabaseRef = FirebaseDatabase.getInstance().getReference().child("users").child(currentUser.getUid());
//            currentUserDatabaseRef.addListenerForSingleValueEvent(new ValueEventListener() {
            currentUserDatabaseRef.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    loggedUser = dataSnapshot.getValue(User.class);
                    nav_header_user_email.setText(loggedUser.getUserEmail());
                    nav_header_user_full_name.setText(new StringBuilder().append(loggedUser.getUserFirstName())
                            .append(" ").append(loggedUser.getUserLastName()).toString());
                    String myProfileImage = loggedUser.getProfileImage();
                    Glide.with(MainActivity.this).load(myProfileImage).placeholder(R.drawable.empty_profile_image).into(nav_header_user_profile_picture);
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    // Failed to read value
                    Log.d(TAG, "Failed to read value.", databaseError.toException());
                }
            });
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d(TAG, "inside on Resume");

    }


    public void sendUserToLogin() {
        Intent intent = new Intent(MainActivity.this, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        return NavigationUI.navigateUp(navController, mAppBarConfiguration)
                || super.onSupportNavigateUp();
    }

    /**
     * if drawer is opened and the 'back' button was pressed-close the drawer
     * else, close the app if needed
     */
    @Override
    public void onBackPressed() {
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else
            super.onBackPressed();
    }


    /**
     * handles app bar menu items selection
     *
     * @param item -user's selection
     * @return boolean
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

//     handles logout
        switch (item.getItemId()) {
            case R.id.logout:
                new LogoutDialog("Logging out").show(getSupportFragmentManager(), null);
                return true;

//if about was pressed while the current page is not about- navigate to about page, else do nothing
            case R.id.about:
                Context context;
                try {
                    if (!navController.getCurrentDestination().getLabel().equals("AboutFragment"))
                        navController.navigate(R.id.nav_about);
                } catch (NullPointerException e) {
                    Log.d("NullPointerException", "label was not found");
                }

                return true;

            default:
                return super.onContextItemSelected(item);
        }
    }

    public void clickHandler(View view) {
/*        if (view.getId() == R.id.send) {
            String input = title.getText().toString();
            Toast.makeText(getApplicationContext(), filterText(input, ""), Toast.LENGTH_LONG).show();
        }*/

        if (view.getId() == R.id.app_bar_activate_location) {
            if (currentOccuringEvents == null) {
                currentOccuringEvents = new ArrayList<>();
            }


            if (checkLocationPermission()) {
                Log.d(TAG, "permission was granted");
                Intent intent = new Intent(this, LocationUpdatesService.class);
                if (isLocationActive) {
                    isLocationActive = false;
                    stopService(intent);
                    Glide.with(this).load(R.drawable.ic_my_location_black_24dp).into((ImageButton) view);
                } else {
                    getAttendingEventsList();
                    isLocationActive = true;
                    startService(intent);
                    Glide.with(this).load(R.drawable.ic_my_location_blue_24dp).into((ImageButton) view);
                }
                editor.putBoolean(IS_LOCATION_ACTIVATED, isLocationActive);
                editor.commit();
            }else{
                requestPermissions();
            }
        }
    }

    private void getAttendingEventsList() {
        DatabaseReference attendingEventsRef = FirebaseDatabase.getInstance().getReference()
                .child(ConstantValues.USERS_ATTENDING_TO_EVENTS)
                .child(loggedUser.getUserId());
        attendingEventsRef.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                Log.d(TAG, "children count on dataSnapshot: "+ dataSnapshot.getChildrenCount());
                saveRelevantEvents(dataSnapshot);
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {
                    currentOccuringEvents.remove(dataSnapshot.getValue(Event.class));
                   if(eventsToRemoveGeofences==null)
                       eventsToRemoveGeofences=new ArrayList<>();
                   eventsToRemoveGeofences.add(dataSnapshot.getValue(Event.class));
            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void saveRelevantEvents(DataSnapshot ds) {
        Log.d(TAG, "ds.eventID is: "+ (String) ds.child(ConstantValues.EVENT_ID).getValue());
            if (DateUtils.isEventCurrentlyOccurring(ds.child(ConstantValues.BEGIN_DATE).getValue().toString()
                    , ds.child(ConstantValues.FINISH_DATE).getValue().toString()
                    , ds.child(ConstantValues.BEGIN_TIME).getValue().toString()
                    , ds.child(ConstantValues.FINISH_TIME).getValue().toString())) {
                Log.d(TAG, "this event is suitable for geofencing: "+ (String) ds.child(ConstantValues.EVENT_TITLE).getValue());
                currentOccuringEvents.add(ds.getValue(Event.class));
                handleGeofencingEvents(ds.getValue(Event.class));
//                addGeofences(geofence);
            }
            else{
                Log.d(TAG, "this event is not suitable for geofencing: "+ (String) ds.child(ConstantValues.EVENT_TITLE).getValue());
            }

    }

/*    private void handleGeofencingEvents(Event event) {
        //geofenceList = new ArrayList<>();
        //
        geofencingClient = LocationServices.getGeofencingClient(this);

        geofenceList.add(new Geofence.Builder()
                // Set the request ID of the geofence. This is a string to identify this
                // geofence.
                .setRequestId(LOCATION_1)

                .setCircularRegion(
                        event.getLocation().getLatitude().doubleValue(),
                        event.getLocation().getLongitude().doubleValue(),
                        30
                )
                .setExpirationDuration(Geofence.NEVER_EXPIRE)
                .setTransitionTypes(Geofence.GEOFENCE_TRANSITION_ENTER |
                        Geofence.GEOFENCE_TRANSITION_EXIT)
                .build());
        Log.d(TAG, "geofence created");
//
    }*/
private void handleGeofencingEvents(Event event) {
    //geofenceList = new ArrayList<>();
    //
    geofencingClient = LocationServices.getGeofencingClient(this);
    Log.d(TAG, "creating geofence with id: "+ event.getEventId());
    Geofence geofence=new Geofence.Builder()
            // Set the request ID of the geofence. This is a string to identify this
            // geofence.

            .setRequestId(event.getEventId())

            .setCircularRegion(
                    event.getLocation().getLatitude().doubleValue(),
                    event.getLocation().getLongitude().doubleValue(),
                    30
            )
            .setExpirationDuration(EXPIRATION_DURATION)
            .setTransitionTypes(Geofence.GEOFENCE_TRANSITION_ENTER |
                    Geofence.GEOFENCE_TRANSITION_EXIT)
            .build();
    Log.d(TAG, "geofence created");
addGeofences(geofence);
}

/*    private void addGeofences() {
        Log.d(TAG, "checking permission before adding geofence");
        if (checkLocationPermission()) {
            Log.d(TAG, "permission was granted");
            // Background location runtime permission already granted.
            // You can now call geofencingClient.addGeofences().
            geofencingClient.addGeofences(getGeofencingRequest(), getGeofencePendingIntent())
                    .addOnSuccessListener(this, new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            // Geofences added
                            Log.d(TAG, "geofence added");
                            // ...
                        }
                    })
                    .addOnFailureListener(this, new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Log.d(TAG, "Failed to add geofences");
                            // ...
                        }
                    });
        } else {
            requestPermissions();
        }
    }*/
private void addGeofences(Geofence geofence) {
    Log.d(TAG, "checking permission before adding geofence");
    if (checkLocationPermission()) {
        Log.d(TAG, "permission was granted");
        // Background location runtime permission already granted.
        // You can now call geofencingClient.addGeofences().
        geofencingClient.addGeofences(getGeofencingRequest(geofence), getGeofencePendingIntent())
                .addOnSuccessListener(this, new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        // Geofences added
                        Log.d(TAG, "geofence added");
                        // ...
                    }
                })
                .addOnFailureListener(this, new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d(TAG, "Failed to add geofences");
                        // ...
                    }
                });
    } else {
        requestPermissions();
    }
}

/*    private GeofencingRequest getGeofencingRequest() {
        GeofencingRequest.Builder builder = new GeofencingRequest.Builder();
        builder.setInitialTrigger(GeofencingRequest.INITIAL_TRIGGER_ENTER);
        builder.addGeofences(geofenceList);
        return builder.build();
    }*/
private GeofencingRequest getGeofencingRequest(Geofence geofence) {
    GeofencingRequest.Builder builder = new GeofencingRequest.Builder();
    builder.setInitialTrigger(GeofencingRequest.INITIAL_TRIGGER_ENTER);
    builder.addGeofence(geofence);
    return builder.build();
}

    public PendingIntent getGeofencePendingIntent() {
        // Reuse the PendingIntent if we already have it.
        if (geofencePendingIntent != null) {
            return geofencePendingIntent;
        }
        Intent intent = new Intent(this, GeofenceBroadcastReceiver.class);
        // We use FLAG_UPDATE_CURRENT so that we get the same pending intent back when
        // calling addGeofences() and removeGeofences().
        geofencePendingIntent = PendingIntent.getBroadcast(this, 0, intent, PendingIntent.
                FLAG_UPDATE_CURRENT);
        return geofencePendingIntent;
    }

    //if not already granted, request for the permission to use location
    private boolean checkLocationPermission() {
        if (Build.VERSION.SDK_INT >= 29) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_BACKGROUND_LOCATION)
                    != PackageManager.PERMISSION_GRANTED) {
                return false;
            }
        }
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            return false;
        }
        return true;
    }

    public void requestPermissions() {
        // Permission to access the location is missing. Show rationale and request permission
        ActivityCompat.requestPermissions(this, this.locationPermission,
                background_location_permission_request_code);
    }

    //handle the user result for the permission request
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode != background_location_permission_request_code) {
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
//            addGeofences();
        }
    }


    @Override
    protected void onStop() {
        super.onStop();
        Log.d(TAG, "inside on Stop");
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.d(TAG, "inside on Pause");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "inside on Destroy");
        unregisterReceiver(myBroadcastReceiver);
        editor.putBoolean(IS_LOCATION_ACTIVATED, isLocationActive);
        editor.commit();
    }

    public static NavController getNavController() {
        return navController;
    }

    public static User getLoggedUser() {
        return loggedUser;
    }

    public static FirebaseUser getCurrentUser() {
        return currentUser;
    }

    public static DatabaseReference getCurrentUserDatabaseRef() {
        return currentUserDatabaseRef;
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {

    }

    @Override
    public void afterTextChanged(Editable s) {

    }

    public class RegisterTextWatcher implements android.text.TextWatcher {
        private int chosenEditText;

        public RegisterTextWatcher(int chosenEditText) {
            super();
            this.chosenEditText = chosenEditText;
        }

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            if (chosenEditText == search.getId()) {
                if (!search.getText().toString().trim().equals("")) {

                }
            }
        }

        @Override
        public void afterTextChanged(Editable s) {

        }
    }

    /*    public class LocationHandler {
            int PERMISSION_ID = 44;
            private boolean checkPermissions(Activity activity) {
                if (ActivityCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED &&
                        ActivityCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                    return true;
                }
                return false;
            }

            private void requestPermissions(Activity activity) {
                ActivityCompat.requestPermissions(activity, new String[]{Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION},
                        PERMISSION_ID
                );
            }

            private boolean isLocationEnabled(Activity activity) {
                LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
                return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) || locationManager.isProviderEnabled(
                        LocationManager.NETWORK_PROVIDER
                );
            }

            @Override
            public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
                if (requestCode == PERMISSION_ID) {
                    if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                        // Granted. Start getting the location information
                    }
                }
            }

            @SuppressLint("MissingPermission")
            private void getLastLocation() {
                if (checkPermissions()) {
                    if (isLocationEnabled()) {
                        mFusedLocationClient.getLastLocation().addOnCompleteListener(
                                new OnCompleteListener<Location>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Location> task) {
                                        Location location = task.getResult();
                                        if (location == null) {
                                            requestNewLocationData();
                                        } else {
                                            latTextView.setText(location.getLatitude() + "");
                                            lonTextView.setText(location.getLongitude() + "");
                                        }
                                    }
                                }
                        );
                    } else {
                        Toast.makeText(getApplicationContext(), "Turn on location", Toast.LENGTH_LONG).show();
                        Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                        startActivity(intent);
                    }
                } else {
                    requestPermissions();
                }
            }
        }*/


}
