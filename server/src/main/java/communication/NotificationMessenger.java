package communication;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingException;
import com.google.firebase.messaging.Message;
import com.google.firebase.messaging.Notification;
import database.UserTokenTable;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;

public class NotificationMessenger {
	private String title;
	private String body;
	private ArrayList<String> tokens = new ArrayList<>();
	UserTokenTable userTokenTable = new UserTokenTable();

	public void sendNotification() {
		for (String token : tokens) {
			Message message = Message.builder()
					.setNotification(com.google.firebase.messaging.Notification.builder()
							.setTitle(title)
							.setBody(body)
							.build())
					.setToken(token)
					.build();
			try {
				FirebaseMessaging.getInstance().send(message);
			} catch (FirebaseMessagingException ignored) {
			}
		}
	}

	public void setDoorbellGroup(String doorbellID) {
		userTokenTable.connect();
		tokens = userTokenTable.getTokensByDoorbell(doorbellID);
		userTokenTable.disconnect();
	}

	public void setMessage(String title, String body) {
		this.title = title;
		this.body = body;
	}
}
