package com.example.besocial;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.example.besocial.data.Event;
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

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executor;


public class GeofenceBroadcastReceiver extends BroadcastReceiver {
    private String TAG = "GeofenceBroadcastReceiver";
    private MainActivity main = null;

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d(TAG, "onReceive: user id " + intent.getStringExtra("uid"));
        String uid = intent.getStringExtra("uid");
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
                    triggeringGeofences, geofencePendingIntent, uid
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
                                                 final PendingIntent geofencePendingIntent, final String uid) {

        final String transition;
        transition = geofenceTransition == Geofence.GEOFENCE_TRANSITION_ENTER ? "Entered" : "Exited";
        DatabaseReference usersAttendingEventsRef;
        for (Geofence geo : triggeringGeofences) {
            usersAttendingEventsRef = FirebaseDatabase.getInstance().getReference()
                    .child(ConstantValues.USERS_ATTENDING_TO_EVENTS)
                    .child(FirebaseAuth.getInstance().getUid()).child(geo.getRequestId());

            usersAttendingEventsRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    Event chosenEvent = dataSnapshot.getValue(Event.class);
                    checkInUserToEvent(context, chosenEvent, uid);
                    alertWithGeofenceDetails(context, dataSnapshot.child("title").getValue().toString(), transition);

                    removeGeofence(context, geofencePendingIntent);
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }
    }

    private void checkInUserToEvent(final Context context, final Event chosenEvent, String uid) {
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
        Map<String, Object> childUpdates = new HashMap<>();

        childUpdates.put("/" + ConstantValues.USERS_ATTENDING_TO_EVENTS
                + "/" + uid + "/" + chosenEvent.getEventId() + "/" + ConstantValues.IS_CHECKED_IN, true);

        childUpdates.put("/" + ConstantValues.EVENTS_WITH_ATTENDINGS
                + "/" + chosenEvent.getEventId() + "/" + uid +"/"+ ConstantValues.IS_CHECKED_IN, true);

        databaseReference.updateChildren(childUpdates).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Toast.makeText(context, "Checked in successful to the event: " + chosenEvent.getTitle(), Toast.LENGTH_LONG).show();

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.d(TAG, "onFailure: problem on checking user to event");
            }
        });
    }

    private void removeGeofence(Context context, PendingIntent geofencePendingIntent) {
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
        Log.d(TAG, String.valueOf(new StringBuilder().append(title).append(" ").append(transition)));
    }


    void setMainActivityHandler(MainActivity main) {
        this.main = main;
    }
}
