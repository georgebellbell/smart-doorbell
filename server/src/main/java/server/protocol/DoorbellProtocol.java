package server.protocol;

import database.Data;
import facialrecognition.FaceSimilarity;
import org.springframework.security.crypto.codec.Base64;
import communication.NotificationMessenger;

import java.sql.Blob;
import java.sql.Connection;
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
				Connection conn = dataTable.getConn();
				byte[] Image = Base64.decode(request.getString("data").getBytes());
				Blob blobImage = conn.createBlob();
				blobImage.setBytes(1, Image);
				dataTable.addRecord(new Data(doorbellID, blobImage, "Unknown", LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))));
				NotificationMessenger notificationMessenger = new NotificationMessenger();
				notificationMessenger.setDoorbellGroup(doorbellID);
				notificationMessenger.setMessage("Unrecognised person is at the door", "Open app to find out more!");
				notificationMessenger.sendNotification();
				response.put("response", "fail");
				response.put("message", "Unknown user at the door");
			}
			else {
				Data data = dataTable.getRecord(Integer.parseInt(faceRecognised));
				NotificationMessenger notificationMessenger = new NotificationMessenger();
				notificationMessenger.setDoorbellGroup(doorbellID);
				notificationMessenger.setMessage(data.getPersonName() + " is at the door", "Open app to find out more!");
				notificationMessenger.sendNotification();
				dataTable.updateData(data.getImageID());
				response.put("response", "success");
				response.put("message", data.getPersonName() + " is at the door");
			}
		} catch (Exception e) {
			System.out.println(e);
			e.printStackTrace();
		}
	}

	public void poll() {
		String doorbellID = request.getString("id");
		pollingTable.connect();
		ArrayList<String> polls = pollingTable.getPolls(doorbellID);
		pollingTable.deletePolls(doorbellID);
		pollingTable.disconnect();

		if (polls.size() == 0) {
			response.put("response","fail");
			response.put("message", "none");
		} else {
			response.put("response", "success");
			response.put("message", polls);
		}
	}
}
