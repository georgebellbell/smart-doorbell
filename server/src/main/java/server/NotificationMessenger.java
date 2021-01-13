package server;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingException;
import com.google.firebase.messaging.Message;
import com.google.firebase.messaging.Notification;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

public class NotificationMessenger {
	public static void sendNotification(String token, String title, String text) throws IOException {


		// This registration token comes from the client FCM SDKs.
		String registrationToken = "dIOSu3QMSIOy8_G3ZAiPN3:APA91bF1HmL1wx29nruL2xheo9KNGZnjuQPv88RguGNxl5enwrAWtYIBYfdxKbeTxzzg49WCmx0ZFn-Ja9sD8XiqPv2xwBUOhINSjhzz2pssF2c7kKm9-nnfU1hqFMr7r7XX77W7eH5_";

// See documentation on defining a message payload.
		Message message = Message.builder()
				.setNotification(com.google.firebase.messaging.Notification.builder()
						.setTitle(title)
						.setBody(text)
						.build())
				.setToken(token)
				.build();

// Send a message to the device corresponding to the provided
// registration token.
		String response = null;
		try {
			response = FirebaseMessaging.getInstance().send(message);
		} catch (FirebaseMessagingException e) {
			e.printStackTrace();
		}
// Response is a message ID string.
		System.out.println("Successfully sent message: " + response);

	}
}
