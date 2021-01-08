package server.protocol;

import database.Data;
import org.springframework.security.crypto.codec.Base64;
import server.ResponseHandler;

import java.sql.Blob;
import java.sql.Connection;

public class DoorbellProtocol extends Protocol{
	public DoorbellProtocol() {
		requestResponse.put("image", new ResponseHandler(this::image, "id", "data"));
	}

	public void image() {
		try {
			dataTable.connect();
			Connection conn = dataTable.getConn();
			byte[] Image = Base64.decode(request.getString("data").getBytes());
			Blob blobImage = conn.createBlob();
			blobImage.setBytes(1, Image);
			dataTable.addRecord(new Data(request.getString("id"), blobImage, "Jeff"));
			dataTable.disconnect();
		} catch (Exception e){
			System.out.println("image " + e);
		}
	}
}
