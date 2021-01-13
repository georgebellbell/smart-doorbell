package com.example.doorbellandroidapp;

import android.content.Intent;

import androidx.annotation.NonNull;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

public class NotificationService extends FirebaseMessagingService {

    @Override
    public void onNewToken(String token) {
        System.out.println(token);
    }

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        if (remoteMessage.getNotification() != null) {
            App app = new App();
            app.createNotification(this, remoteMessage.getNotification().getTitle(),
                    remoteMessage.getNotification().getBody());
        }
    }
}
