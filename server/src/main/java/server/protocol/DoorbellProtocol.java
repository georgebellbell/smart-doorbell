package server.protocol;

import database.Data;
import facialrecognition.FaceSimilarity;
import org.springframework.security.crypto.codec.Base64;
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
				System.out.println("Unrecognised face");
				response.put("response", "success");
				response.put("message", "Unknown user at the door");
			}
			else {
				System.out.println("Recognised face");
				response.put("response", "success");
				response.put("message", faceRecognised + " is at the door");
			}
		} catch (Exception e) {
			System.out.println(e);
		}
	}
}
