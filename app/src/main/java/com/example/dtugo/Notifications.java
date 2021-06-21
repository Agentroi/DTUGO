package com.example.dtugo;

import android.app.Application;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.os.Build;

public class Notifications extends Application {
    public static final String CHANNEL_CHALLENGE_ID = "nChannel";

    @Override
    public void onCreate() {
        super.onCreate();

        createNotificationChannels();
    }

    private void createNotificationChannels() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel1 = new NotificationChannel(CHANNEL_CHALLENGE_ID, "Challenge", NotificationManager.IMPORTANCE_DEFAULT);
            channel1.setDescription("Notification channel for Challenges");

            NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            assert manager != null;
            manager.createNotificationChannel(channel1);
        }
    }
}
