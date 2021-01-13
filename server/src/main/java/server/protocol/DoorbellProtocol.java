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
		try {
			byte[] image = Base64.decode(request.getString("data").getBytes());
			String faceRecognised = faceSimilarity.compareFaces(image, request.getString("id"));
			if (faceRecognised == null) {
				dataTable.connect();
				Connection conn = dataTable.getConn();
				byte[] Image = Base64.decode(request.getString("data").getBytes());
				Blob blobImage = conn.createBlob();
				blobImage.setBytes(1, Image);
				dataTable.addRecord(new Data(request.getString("id"), blobImage, "Unknown"));
				dataTable.disconnect();
				NotificationMessenger.sendNotification("dIOSu3QMSIOy8_G3ZAiPN3:APA91bF1HmL1wx29nruL2xheo9KNGZnjuQPv88RguGNxl5enwrAWtYIBYfdxKbeTxzzg49WCmx0ZFn-Ja9sD8XiqPv2xwBUOhINSjhzz2pssF2c7kKm9-nnfU1hqFMr7r7XX77W7eH5_",
						"Someone is at your door", "Open app to find out more");
				System.out.println("Unrecognised face");
				response.put("response", "fail");
				response.put("message", "Unknown user at the door");
			}
			else {
				NotificationMessenger.sendNotification("dIOSu3QMSIOy8_G3ZAiPN3:APA91bF1HmL1wx29nruL2xheo9KNGZnjuQPv88RguGNxl5enwrAWtYIBYfdxKbeTxzzg49WCmx0ZFn-Ja9sD8XiqPv2xwBUOhINSjhzz2pssF2c7kKm9-nnfU1hqFMr7r7XX77W7eH5_",
						faceRecognised + " is at the door", "Open app to let them in");

				System.out.println("Recognised face");
				response.put("response", "success");
				response.put("message", faceRecognised + " is at the door");
			}
		} catch (Exception e) {
			System.out.println(e);
		}
	}
}
