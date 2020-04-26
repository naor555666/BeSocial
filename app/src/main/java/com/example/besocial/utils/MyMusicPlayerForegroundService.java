package com.example.besocial.utils;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;
import android.media.MediaPlayer;


import androidx.annotation.Nullable;

import com.example.besocial.R;
import com.example.besocial.ui.mainactivity.MainActivity;

public class MyMusicPlayerForegroundService extends Service {
    MediaPlayer mediaPlayer;
    public static final String CHANNEL_ID = "ForegroundServiceChannel";
    public static final int NOTIFICATION_ID1 = 1;
    private static MyMusicPlayerForegroundService instance;
    private NotificationManager mNotiMgr;
    private Notification.Builder mNotifyBuilder;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d("foreground service", "on create");
        instance=this;

        mediaPlayer = MediaPlayer.create(this, R.raw.heroes);
        mediaPlayer.setLooping(true); // Set looping
        mediaPlayer.setVolume(100, 100);
        initForeground();
    }

    private void initForeground() {
        Log.d("foreground service", "on initForeground");

        if (mNotiMgr == null)
            mNotiMgr = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        NotificationChannel channel = new NotificationChannel(CHANNEL_ID,
                "My foreground channel",
                NotificationManager.IMPORTANCE_DEFAULT);

        ((NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE))
                .createNotificationChannel(channel);
        // Create an explicit intent for an Activity in your app
        Intent intent = new Intent(this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, 0);
        mNotifyBuilder = new Notification.Builder(this, CHANNEL_ID)
                .setContentTitle("Music player")
                .setSmallIcon(R.drawable.ic_besocial_notification)
                .setContentIntent(pendingIntent);
    }

    @Override
    public void onDestroy() {
        mediaPlayer.stop();
        mediaPlayer.release();
    }

    @Override
    public int onStartCommand(final Intent intent, int flags, int startId) {
        final boolean isPlaying = intent.getBooleanExtra("isPlaying", false);
        // on/off music and show suitable notification
        if (!isPlaying) {
            mediaPlayer.start();
            startForeground(NOTIFICATION_ID1, updateNotification("Playing HOMM3 in the Background"));
        } else {
            mediaPlayer.pause();
            startForeground(NOTIFICATION_ID1, updateNotification("HOMM3 is paused..."));
        }
        return super.onStartCommand(intent, flags, startId);
    }

    public Notification updateNotification(String details) {
        mNotifyBuilder.setContentText(details).setOnlyAlertOnce(false);
        Notification noti = mNotifyBuilder.build();
        noti.flags = Notification.FLAG_ONLY_ALERT_ONCE;
        mNotiMgr.notify(NOTIFICATION_ID1, noti);
        return noti;
    }

    @Override
    public boolean stopService(Intent name) {
        mediaPlayer.stop();
        mediaPlayer.reset();
        mediaPlayer.release();
        mediaPlayer=null;
        return super.stopService(name);
    }

    public static MyMusicPlayerForegroundService getInstance() {
        return instance;
    }

    @Override
    public void onTaskRemoved(Intent rootIntent) {
        super.onTaskRemoved(rootIntent);
        mediaPlayer.stop();
        mediaPlayer.reset();
        mediaPlayer.release();
        mediaPlayer=null;
    }
}
