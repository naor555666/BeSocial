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
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.lifecycle.ViewModelProviders;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import androidx.preference.PreferenceManager;

import com.bumptech.glide.Glide;
import com.example.besocial.R;
import com.example.besocial.data.Event;
import com.example.besocial.data.User;
import com.example.besocial.ui.login.LoginActivity;
import com.example.besocial.ui.mainactivity.mainmenu.LogoutDialog;
import com.example.besocial.utils.ConstantValues;
import com.example.besocial.utils.DateUtils;
import com.example.besocial.utils.GeofenceBroadcastReceiver;
import com.example.besocial.utils.LocationUpdatesService;
import com.example.besocial.utils.MyBroadcastReceiver;
import com.example.besocial.utils.WordsFilter;
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

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;



public class MainActivity extends AppCompatActivity {
    private static final long EXPIRATION_DURATION = 15 * ConstantValues.MINUTE;
    private final String IS_LOCATION_ACTIVATED = "isLocationActivated";
    private static User loggedUser;

    private static boolean isLocationActive = false;
    private static final String TAG = "MainActivity";

    private TextView nav_header_user_email, nav_header_user_full_name;
    private static ImageButton searchButton;
    private AppBarConfiguration mAppBarConfiguration;
    private DrawerLayout drawer;
    private static NavController navController;
    private CircleImageView nav_header_user_profile_picture;
    private BroadcastReceiver myBroadcastReceiver = new MyBroadcastReceiver();
    private static DatabaseReference currentUserDatabaseRef;
    private static FirebaseUser currentUser;
    private static FirebaseAuth fireBaseAuth;
    private SharedPreferences sharedPref;
    private SharedPreferences.Editor editor;
    static UsersViewModel mViewModel;

    private static Toolbar toolbar;
    private GeofencingClient geofencingClient;
    private PendingIntent geofencePendingIntent;

    private int background_location_permission_request_code = 5;
    private String[] locationPermission = {Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_BACKGROUND_LOCATION};
    private static ArrayList<Event> currentOccuringEvents, eventsToRemoveGeofences;
    private ValueEventListener userDetailsListener;
    private ChildEventListener attendingEventsListener;
    private DatabaseReference attendingEventsRef;
    private InputStream inputStream;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d(TAG, "onCreate: ");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //initialize the words to filter hashmap
        inputStream = getResources().openRawResource(R.raw.bad_word_filter);
        WordsFilter.initBadWords(inputStream);

        mViewModel = ViewModelProviders.of(this).get(UsersViewModel.class);

        searchButton = findViewById(R.id.search_button);
        setSearchListener();

        //  get user token (if he is logged in)
        fireBaseAuth = FirebaseAuth.getInstance();
        currentUser = fireBaseAuth.getCurrentUser();

        //define the tool bar
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //define the application's main drawer menu
        drawer = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);

        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        mAppBarConfiguration = new AppBarConfiguration.Builder(
                R.id.nav_home, R.id.nav_my_profile, R.id.nav_notifications, R.id.chatActivity, R.id.nav_social_center, R.id.nav_bonus_area)
                .setDrawerLayout(drawer)
                .build();
        //define the navigation controller of this activity
        navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
        NavigationUI.setupWithNavController(navigationView, navController);

        //get reference to the header of the navigation view and its components
        View header = navigationView.getHeaderView(0);
        nav_header_user_email = header.findViewById(R.id.nav_header_user_email);
        nav_header_user_full_name = header.findViewById(R.id.nav_header_user_full_name);
        nav_header_user_profile_picture = header.findViewById(R.id.nav_header_user_profile_picture);

        //register connectivity broadcast receiver to this activity
        IntentFilter intentFilter = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
        registerReceiver(myBroadcastReceiver, intentFilter);

        //retrieve essential parameters from the shared preference XML file
        sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
        editor = sharedPref.edit();
        isLocationActive = sharedPref.getBoolean(IS_LOCATION_ACTIVATED, false);
        ImageButton activateLocation = findViewById(R.id.app_bar_activate_location);

        //set the location icon
        if (LocationUpdatesService.getInstance() != null) {
            if (isLocationActive) {
                Glide.with(this).load(R.drawable.ic_my_location_blue_24dp).into(activateLocation);
            } else {
                Glide.with(this).load(R.drawable.ic_my_location_black_24dp).into(activateLocation);
            }
        }

    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.d(TAG, "inside on Start");
        final ImageButton activateLocation = findViewById(R.id.app_bar_activate_location);
        activateLocation.setEnabled(false);
        if (currentUser == null) {    // if the user is not logged in
            sendUserToLogin();
        }
        //the user is logged in, the firebase token exists
        else {
            currentUserDatabaseRef = FirebaseDatabase.getInstance().getReference().child("users").child(currentUser.getUid());

            userDetailsListener = currentUserDatabaseRef.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    MainActivity.loggedUser = dataSnapshot.getValue(User.class);
                    mViewModel.setUser(loggedUser);
                    String myProfileImage = loggedUser.getProfileImage();
                    loggedUser = dataSnapshot.getValue(User.class);
                    activateLocation.setEnabled(true);
                    nav_header_user_email.setText(loggedUser.getUserEmail());
                    nav_header_user_full_name.setText(new StringBuilder().append(loggedUser.getUserFirstName())
                            .append(" ").append(loggedUser.getUserLastName()).toString());
                    Glide.with(MainActivity.this).load(myProfileImage).placeholder(R.drawable.empty_profile_image).into(nav_header_user_profile_picture);
                    if (loggedUser != null) {
                        if (loggedUser.getAccountStatus().equals("Blocked")) {
                            sendUserToLogin();
                            Toast.makeText(MainActivity.this, "Your account has been BLOCKED. Contact us for account retrieval", Toast.LENGTH_LONG).show();
                        }
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    // Failed to read value
                    //Toast.makeText(MainActivity.this, "sorry, something wrong happened...", Toast.LENGTH_SHORT).show();
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
                    if (!navController.getCurrentDestination().getLabel().equals("AppUseFragment"))
                        navController.navigate(R.id.appUseFragment);
                } catch (NullPointerException e) {
                    Log.d("NullPointerException", "label was not found");
                }

                return true;

            default:
                return super.onContextItemSelected(item);
        }
    }

    public void clickHandler(View view) {
        if (view.getId() == R.id.app_bar_activate_location) {
            if (currentOccuringEvents == null) {
                currentOccuringEvents = new ArrayList<>();
            }

            if (checkLocationPermission()) {
                Log.d(TAG, "permission was granted");
                Intent intent = new Intent(this, LocationUpdatesService.class);
                if (isLocationActive) {
                    if (attendingEventsRef != null) {
                        attendingEventsRef.removeEventListener(attendingEventsListener);
                    }
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
            } else {
                requestPermissions();
            }
        }
    }

    private void getAttendingEventsList() {
        attendingEventsRef = FirebaseDatabase.getInstance().getReference()
                .child(ConstantValues.USERS_ATTENDING_TO_EVENTS)
                .child(fireBaseAuth.getUid());
        attendingEventsListener = attendingEventsRef.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                Log.d(TAG, "children count on dataSnapshot: " + dataSnapshot.getChildrenCount());
                saveRelevantEvents(dataSnapshot);
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {
                currentOccuringEvents.remove(dataSnapshot.getValue(Event.class));
                if (eventsToRemoveGeofences == null)
                    eventsToRemoveGeofences = new ArrayList<>();
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
        Boolean isCurrentUserCheckedIn = false;
        if (ds.hasChild(ConstantValues.IS_CHECKED_IN)) isCurrentUserCheckedIn = true;

        boolean isEventOccuring = DateUtils.isEventCurrentlyOccurring(ds.child(ConstantValues.BEGIN_DATE).getValue().toString()
                , ds.child(ConstantValues.FINISH_DATE).getValue().toString()
                , ds.child(ConstantValues.BEGIN_TIME).getValue().toString()
                , ds.child(ConstantValues.FINISH_TIME).getValue().toString());

        boolean isEventFinished = ((Boolean) ds.child(ConstantValues.IS_FINISHED).getValue());

        boolean isNonManagementNormalEvent = ((Boolean) ds.child(ConstantValues.IS_COMPANY_EVENT).getValue()
                || ((String) ds.child(ConstantValues.CATEGORY).getValue()).equals(ConstantValues.HELP_ME));

        if (isEventOccuring && !isEventFinished && !isCurrentUserCheckedIn && isNonManagementNormalEvent) {
            Log.d(TAG, "this event is suitable for geofencing: " + ds.child(ConstantValues.EVENT_TITLE).getValue());
            currentOccuringEvents.add(ds.getValue(Event.class));
            handleGeofencingEvents(ds.getValue(Event.class));
        } else {
            Log.d(TAG, "this event is not suitable for geofencing: " + ds.child(ConstantValues.EVENT_TITLE).getValue());
        }
    }


    private void handleGeofencingEvents(Event event) {
        //
        geofencingClient = LocationServices.getGeofencingClient(this);
        Log.d(TAG, "creating geofence with id: " + event.getEventId());
        Geofence geofence = new Geofence.Builder()
                // Set the request ID of the geofence. This is a string to identify this
                // geofence.

                .setRequestId(event.getEventId())
                .setCircularRegion(
                        event.getLocation().getLatitude().doubleValue(),
                        event.getLocation().getLongitude().doubleValue(),
                        ConstantValues.GEOFENCE_RADIUS
                )
                .setExpirationDuration(EXPIRATION_DURATION)
                .setTransitionTypes(Geofence.GEOFENCE_TRANSITION_ENTER)
                .build();
        Log.d(TAG, "geofence created");
        addGeofences(geofence);
    }

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
        intent.putExtra("uid", fireBaseAuth.getUid());
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
        return ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED;
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

                    break;
                }
            }
        } else {
            //permission was granted


        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.d(TAG, "inside on Stop");
        if (currentUserDatabaseRef != null && userDetailsListener != null) {
            currentUserDatabaseRef.removeEventListener(userDetailsListener);
        }
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
        if (inputStream != null) {
            try {
                inputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
//        stopService(notificationsServiceIntent);
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


    public static ImageButton getSearch() {
        return searchButton;
    }

    void setSearchListener() {
        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                navController.navigate(R.id.searchUsersFragment);
            }
        });
    }

    public static UsersViewModel getmViewModel() {
        return mViewModel;
    }

    public static void setmViewModel(UsersViewModel mViewModel) {
        MainActivity.mViewModel = mViewModel;
    }

    public static FirebaseAuth getFireBaseAuth() {
        return fireBaseAuth;
    }
}
