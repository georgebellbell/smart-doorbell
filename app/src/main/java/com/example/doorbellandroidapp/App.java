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

	private void createNotificationChannels(){
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
			NotificationChannel channel1 = new NotificationChannel(
					CHANNEL_1_ID,
					"Channel 1",
					NotificationManager.IMPORTANCE_HIGH
			);
			channel1.setDescription("This is channel 1");

			NotificationManager manager = getSystemService(NotificationManager.class);
			manager.createNotificationChannel(channel1);

		}
	}

	public void createNotification(Context context, String title, String text) {
		Intent resultIntent = new Intent(context,MainActivity.class);
		PendingIntent resultPendingIntent = PendingIntent.getActivity(context,1,resultIntent,PendingIntent.FLAG_UPDATE_CURRENT);
		notificationManager = NotificationManagerCompat.from(context);
		Notification notification = new NotificationCompat.Builder(context,CHANNEL_1_ID)
				.setContentIntent(resultPendingIntent)
				.setSmallIcon(R.drawable.ic_one)
				.setContentTitle(title)
				.setContentText(text)
				.setAutoCancel(true)
				.setPriority(NotificationCompat.PRIORITY_HIGH)
				.setCategory(NotificationCompat.CATEGORY_MESSAGE)
				.build();


		notificationManager.notify(1, notification);

	}
}