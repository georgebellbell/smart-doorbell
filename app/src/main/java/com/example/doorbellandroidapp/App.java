package com.example.doorbellandroidapp;

import android.app.Application;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.view.View;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

public class App extends Application {
	public static final String CHANNEL_1_ID = "channel1";


	private NotificationManagerCompat notificationManager;

	@Override
	public void onCreate() {
		super.onCreate();
		createNotificationChannels();
	}

	/**
	 * creates notification channel for notifications to be sent on
	 */
	private void createNotificationChannels(){
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
			NotificationChannel channel1 = new NotificationChannel(
					CHANNEL_1_ID,
					"Channel 1",
					NotificationManager.IMPORTANCE_HIGH
			);
			channel1.setDescription("Channel for doorbell");
			NotificationManager manager = getSystemService(NotificationManager.class);
			manager.createNotificationChannel(channel1);
		}
	}

	/**
	 * creates a notification for app on created channel
	 * @param context current location of app
	 * @param title title of notification
	 * @param text message of notification
	 */
	public void createNotification(Context context, String title, String text) {
		Intent resultIntent = new Intent(context,MainActivity.class);
		PendingIntent resultPendingIntent = PendingIntent.getActivity(context,1,resultIntent,PendingIntent.FLAG_UPDATE_CURRENT);
		notificationManager = NotificationManagerCompat.from(context);
		Notification notification = new NotificationCompat.Builder(context,CHANNEL_1_ID)
				.setContentIntent(resultPendingIntent)
				.setSmallIcon(R.drawable.ic_notif_logo)
				.setContentTitle(title)
				.setContentText(text)
				.setAutoCancel(true)
				.setPriority(NotificationCompat.PRIORITY_HIGH)
				.setCategory(NotificationCompat.CATEGORY_MESSAGE)
				.build();
		notificationManager.notify(1, notification);
	}
}