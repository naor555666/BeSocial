package com.example.besocial;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.example.besocial.ui.mainactivity.MainActivity;
import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofenceStatusCodes;
import com.google.android.gms.location.GeofencingClient;
import com.google.android.gms.location.GeofencingEvent;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.List;
import java.util.concurrent.Executor;


public class GeofenceBroadcastReceiver extends BroadcastReceiver {
    private String TAG = "GeofenceBroadcastReceiver";
    private MainActivity main = null;

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d(TAG, "onReceive: user id "+intent.getStringExtra("uid"));
        PendingIntent geofencePendingIntent = PendingIntent.getBroadcast(context, 0, intent, PendingIntent.
                FLAG_UPDATE_CURRENT);
        GeofencingEvent geofencingEvent = GeofencingEvent.fromIntent(intent);
        if (geofencingEvent.hasError()) {
            String errorMessage = GeofenceStatusCodes.getStatusCodeString(geofencingEvent.getErrorCode());
            Log.e(TAG, errorMessage);
            return;
        }

        // Get the transition type.
        int geofenceTransition = geofencingEvent.getGeofenceTransition();

        // Test that the reported transition was of interest.
        if (geofenceTransition == Geofence.GEOFENCE_TRANSITION_ENTER ||
                geofenceTransition == Geofence.GEOFENCE_TRANSITION_EXIT) {

            // Get the geofences that were triggered. A single event can trigger
            // multiple geofences.
            List<Geofence> triggeringGeofences = geofencingEvent.getTriggeringGeofences();

            // Get the transition details as a String.
            handleGeofenceTransitionDetails(context,
                    geofenceTransition,
                    triggeringGeofences,geofencePendingIntent,intent
            );

/*            // Send notification and log the transition details.
            Toast.makeText(context, geofenceTransitionDetails, Toast.LENGTH_LONG).show();
            Log.i(TAG, geofenceTransitionDetails);*/
        }/* else {
            // Log the error.
            Log.e(TAG, "error receiving geofence");
        }*/
    }

    private void handleGeofenceTransitionDetails(final Context context, int geofenceTransition, List<Geofence> triggeringGeofences,
                                                 final PendingIntent geofencePendingIntent, final Intent intent) {

        final String requestId, transition;
        transition = geofenceTransition == Geofence.GEOFENCE_TRANSITION_ENTER ? "Entered" : "Exited";
        DatabaseReference usersAttendingEventsRef;
        //requestId = triggeringGeofences.get(0).getRequestId().equals(MainActivity.LOCATION_1) ? "Home" : "";
        for (Geofence geo : triggeringGeofences) {
            usersAttendingEventsRef = FirebaseDatabase.getInstance().getReference()
                    .child(ConstantValues.USERS_ATTENDING_TO_EVENTS)
                    .child(FirebaseAuth.getInstance().getUid()).child(geo.getRequestId());

            usersAttendingEventsRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    alertWithGeofenceDetails(context, dataSnapshot.child("title").getValue().toString(), transition);

                    removeGeofence(context,intent,geofencePendingIntent);
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }
    }

    private void removeGeofence(Context context, Intent intent, PendingIntent geofencePendingIntent) {
        GeofencingClient geofencingClient = LocationServices.getGeofencingClient(context);
        geofencingClient.removeGeofences(geofencePendingIntent).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Log.d(TAG, "onSuccess: geofence removed!");
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.d(TAG, "onFailure: failed to remove geofence");
            }
        });

    }

    private void alertWithGeofenceDetails(Context context, String title, String transition) {
        // Send notification and log the transition details.
        Toast.makeText(context, String.valueOf(new StringBuilder().append(title).append(" ").append(transition)), Toast.LENGTH_LONG).show();
        Log.i(TAG, String.valueOf(new StringBuilder().append(title).append(" ").append(transition)));
    }


    void setMainActivityHandler(MainActivity main) {
        this.main = main;
    }
}
