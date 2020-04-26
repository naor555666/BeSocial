package com.example.besocial.ui.mainactivity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.os.Build;
import android.os.Bundle;

import com.bumptech.glide.Glide;
import com.example.besocial.GeofenceBroadcastReceiver;
import com.example.besocial.ui.login.RegisterFragment;
import com.example.besocial.ui.mainactivity.mainmenu.LogoutDialog;
import com.example.besocial.utils.LocationUpdatesService;
import com.example.besocial.utils.MyBroadcastReceiver;
import com.example.besocial.utils.MyMusicPlayerForegroundService;
import com.example.besocial.R;
import com.example.besocial.data.Post;
import com.example.besocial.data.User;
import com.example.besocial.ui.login.LoginActivity;

import android.os.PersistableBundle;
import android.provider.Settings;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.example.besocial.utils.ShareLocationForegroundService;
import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofencingClient;
import com.google.android.gms.location.GeofencingEvent;
import com.google.android.gms.location.GeofencingRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.navigation.NavigationView;

import androidx.preference.PreferenceManager;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;


import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;


public class MainActivity extends AppCompatActivity implements TextWatcher {
    public static final String LOCATION_1 = "555";
    private static User loggedUser;

    private static boolean isMusicPlaying = false;
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


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        handleGeofencingEvents();
        geofenceList = new ArrayList<>();
        //
        geofencingClient = LocationServices.getGeofencingClient(this);

        geofenceList.add(new Geofence.Builder()
                // Set the request ID of the geofence. This is a string to identify this
                // geofence.
                .setRequestId(LOCATION_1)

                .setCircularRegion(
                        32.828925,
                        35.076868,
                        30
                )
                .setExpirationDuration(15 * 60 * 1000)
                .setTransitionTypes(Geofence.GEOFENCE_TRANSITION_ENTER |
                        Geofence.GEOFENCE_TRANSITION_EXIT)
                .build());
        Log.d(TAG, "geofence created");
//
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
        MainActivity.isMusicPlaying = sharedPref.getBoolean("isMusicPlaying", false);
        System.out.println("on create: music playing: " + isMusicPlaying);
        AppCompatImageView playButton = findViewById(R.id.play_music);

        if (MyMusicPlayerForegroundService.getInstance() != null) {

            if (MainActivity.isMusicPlaying == false) {
                playButton.setImageResource(R.drawable.ic_play_arrow_green_24dp);
            } else {
                playButton.setImageResource(R.drawable.ic_pause_green_24dp);
            }
        } else {
            isMusicPlaying = false;
            playButton.setImageResource(R.drawable.ic_play_arrow_green_24dp);
        }


        //
        addGeofences();
        //
    }

    private void handleGeofencingEvents() {

    }

    private void addGeofences() {
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
    }


    @Override
    protected void onStart() {
        super.onStart();
        Log.d(TAG, "inside on Start");

        if (MainActivity.currentUser == null) {    // if the user is not logged in
            sendUserToLogin();
        }
        //
        else {
            currentUserDatabaseRef = FirebaseDatabase.getInstance().getReference().child("users").child(currentUser.getUid());
//            currentUserDatabaseRef.addListenerForSingleValueEvent(new ValueEventListener() {
            currentUserDatabaseRef.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    MainActivity.loggedUser = dataSnapshot.getValue(User.class);
                    nav_header_user_email.setText(MainActivity.loggedUser.getUserEmail());
                    nav_header_user_full_name.setText(MainActivity.loggedUser.getUserFirstName() + " " + MainActivity.loggedUser.getUserLastName());
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
/*        if (view.getId() == R.id.play_music) {
            Intent intent = new Intent(this, MyMusicPlayerForegroundService.class);
            if (MainActivity.isIsMusicPlaying()) {
                intent.putExtra("isPlaying", true);
                setIsMusicPlaying(false);
                ((AppCompatImageView) view).setImageResource(R.drawable.ic_play_arrow_green_24dp);
            } else {
                intent.putExtra("isPlaying", false);
                setIsMusicPlaying(true);
                ((AppCompatImageView) view).setImageResource(R.drawable.ic_pause_green_24dp);

            }
            startService(intent);
        } else if (view.getId() == R.id.stop_music) {
            setIsMusicPlaying(false);
            AppCompatImageView playButton = findViewById(R.id.play_music);
            playButton.setImageResource(R.drawable.ic_play_arrow_green_24dp);
            Intent intent = new Intent(this, MyMusicPlayerForegroundService.class);
            stopService(intent);
        }*/
        if (view.getId() == R.id.play_music) {
            Intent intent = new Intent(this, LocationUpdatesService.class);
            if (MainActivity.isIsMusicPlaying()) {
                stopService(intent);
                setIsMusicPlaying(false);
                ((AppCompatImageView) view).setImageResource(R.drawable.ic_play_arrow_green_24dp);
            } else {
                intent.putExtra("isPlaying", false);
                setIsMusicPlaying(true);
                ((AppCompatImageView) view).setImageResource(R.drawable.ic_pause_green_24dp);
                startService(intent);
            }
        } else if (view.getId() == R.id.stop_music) {
            setIsMusicPlaying(false);
            AppCompatImageView playButton = findViewById(R.id.play_music);
            playButton.setImageResource(R.drawable.ic_play_arrow_green_24dp);
            Intent intent = new Intent(this, MyMusicPlayerForegroundService.class);
            stopService(intent);
        }
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState, @NonNull PersistableBundle outPersistentState) {
        super.onSaveInstanceState(outState, outPersistentState);
    }

    @Override
    public void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        Log.d(TAG, "inside on RestoreInstanceState");

    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.d(TAG, "inside on Stop");
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "inside on Destroy");
        unregisterReceiver(myBroadcastReceiver);

        editor.putBoolean("isMusicPlaying", MainActivity.isMusicPlaying);
        editor.commit();
    }


    @Override
    protected void onPause() {
        super.onPause();
        Log.d(TAG, "inside on Pause");
    }

    public static NavController getNavController() {
        return navController;
    }

    public static boolean isIsMusicPlaying() {
        return isMusicPlaying;
    }

    public static void setIsMusicPlaying(boolean isMusicPlaying) {
        MainActivity.isMusicPlaying = isMusicPlaying;
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


    private GeofencingRequest getGeofencingRequest() {
        GeofencingRequest.Builder builder = new GeofencingRequest.Builder();
        builder.setInitialTrigger(GeofencingRequest.INITIAL_TRIGGER_ENTER);
        builder.addGeofences(geofenceList);
        return builder.build();
    }

    private PendingIntent getGeofencePendingIntent() {
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
//                    mLocationPermissionGranted = false;
                    break;
                }
            }
        } else {
            //permission was granted
            addGeofences();
        }
    }
}
