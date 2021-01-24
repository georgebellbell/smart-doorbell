/*
 * @author Jack Reed and George Bell
 * @version 1.0
 * @since 24/01/2021
 */

package com.example.doorbellandroidapp;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

/**
 * Sets up notification messages from firebase server to be displayed in defined format
 */
public class NotificationService extends FirebaseMessagingService {

    /**
     * Outputs the token associated with device being ran
     * @param token identifier for phone
     */
    @Override
    public void onNewToken(String token) {
        System.out.println(token);
    }

    /**
     * Upon receiving notifications from firebase server, app will create notification
     * @param remoteMessage
     */
    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        if (remoteMessage.getNotification() != null) {
            CreateNotification createNotification = new CreateNotification();
            createNotification.createNotification(this, remoteMessage.getNotification().getTitle(),
                    remoteMessage.getNotification().getBody());
        }
    }
}
