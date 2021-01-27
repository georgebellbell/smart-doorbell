/**
 * @author Dominykas Makarovas, Jack Reed
 * @version 1.0
 * @since 25/01/2021
 */

package server.protocol;

import database.ImageData;
import facialrecognition.FaceSimilarity;
import communication.NotificationMessenger;
import org.springframework.security.crypto.codec.Base64;

import java.util.ArrayList;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class DoorbellProtocol extends Protocol{
	private FaceSimilarity faceSimilarity = new FaceSimilarity();

	@Override
	public void init() {
		requestHashMap.put("image", new RequestHandler(this::image, "id", "data"));
		requestHashMap.put("poll", new RequestHandler(this::poll, "id"));
	}

	public void image() {
		String doorbellID = request.getString("id");
		try {
			byte[] image = Base64.decode(request.getString("data").getBytes());
			String faceRecognised = faceSimilarity.compareFaces(image,doorbellID);
			if (faceRecognised == null) {
				dataTable.addRecord(new ImageData(doorbellID, image, "Unknown", LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))));
				NotificationMessenger notificationMessenger = new NotificationMessenger();
				notificationMessenger.setDoorbellGroup(doorbellID);
				notificationMessenger.setMessage("Unrecognised person is at the door", "Open app to find out more!");
				notificationMessenger.sendNotification();
				response.put("response", "fail");
				response.put("message", "Unknown user at the door");
			}
			else {
				ImageData imageData = dataTable.getRecord(Integer.parseInt(faceRecognised));
				NotificationMessenger notificationMessenger = new NotificationMessenger();
				notificationMessenger.setDoorbellGroup(doorbellID);
				notificationMessenger.setMessage(imageData.getPersonName() + " is at the door", "Open app to find out more!");
				notificationMessenger.sendNotification();
				dataTable.updateData(imageData.getImageID());
				response.put("response", "success");
				response.put("message", imageData.getPersonName() + " is at the door");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void poll() {
		String doorbellID = request.getString("id");
		ArrayList<String> polls = pollingTable.getPolls(doorbellID);
		pollingTable.deletePolls(doorbellID);

		if (polls.size() == 0) {
			response.put("response","fail");
			response.put("message", "none");
		} else {
			response.put("response", "success");
			response.put("message", polls);
		}
	}
}
