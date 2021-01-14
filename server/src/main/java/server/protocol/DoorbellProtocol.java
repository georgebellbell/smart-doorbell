package server.protocol;

import database.Data;
import facialrecognition.FaceSimilarity;
import org.springframework.security.crypto.codec.Base64;
import server.NotificationMessenger;
import server.ResponseHandler;

import java.sql.Blob;
import java.sql.Connection;

public class DoorbellProtocol extends Protocol{
	FaceSimilarity faceSimilarity = new FaceSimilarity();
	public DoorbellProtocol() {
		requestResponse.put("image", new ResponseHandler(this::image, "id", "data"));
	}

	public void image() {
		String doorbellID = request.getString("id");
		try {
			byte[] image = Base64.decode(request.getString("data").getBytes());
			String faceRecognised = faceSimilarity.compareFaces(image,doorbellID);
			if (faceRecognised == null) {
				dataTable.connect();
				Connection conn = dataTable.getConn();
				byte[] Image = Base64.decode(request.getString("data").getBytes());
				Blob blobImage = conn.createBlob();
				blobImage.setBytes(1, Image);
				dataTable.addRecord(new Data(doorbellID, blobImage, "Unknown"));
				dataTable.disconnect();
				NotificationMessenger notificationMessenger = new NotificationMessenger();
				notificationMessenger.setDoorbellGroup(doorbellID);
				notificationMessenger.setMessage("Unrecognised person is at the door", "Open app to find out more!");
				notificationMessenger.sendNotification();
				response.put("response", "fail");
				response.put("message", "Unknown user at the door");
			}
			else {
				NotificationMessenger notificationMessenger = new NotificationMessenger();
				notificationMessenger.setDoorbellGroup(doorbellID);
				notificationMessenger.setMessage(faceRecognised + " is at the door", "Open app to find out more!");
				notificationMessenger.sendNotification();
				response.put("response", "success");
				response.put("message", faceRecognised + " is at the door");
			}
		} catch (Exception e) {
			System.out.println(e);
		}
	}
}
