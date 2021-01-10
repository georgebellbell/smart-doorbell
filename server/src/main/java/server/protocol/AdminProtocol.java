package server.protocol;

import authentication.TwoFactorAuthentication;
import database.Data;
import database.User;
import org.json.JSONObject;
import server.ResponseHandler;

import java.sql.Blob;
import java.sql.SQLException;
import java.util.ArrayList;

public class AdminProtocol extends Protocol {
	private User user;

	public AdminProtocol() {
		requestResponse.put("login", new ResponseHandler(this::login, "username", "password"));
		requestResponse.put("user", new ResponseHandler(this::userInfo, "username"));
		requestResponse.put("deleteuser", new ResponseHandler(this::deleteUser, "username"));
		requestResponse.put("faces", new ResponseHandler(this::faces, "username"));
		requestResponse.put("update", new ResponseHandler(this::update, "username"));
	}

	public void update() {

	}

	public void faces() {
		dataTable.connect();
		ArrayList<Data> allImages = dataTable.getAllImages(request.getString("username"));
		dataTable.disconnect();
		ArrayList<JSONObject> jsonImages = new ArrayList<>();
		if (allImages != null) {
			for (Data data: allImages) {
				JSONObject jsonData = new JSONObject();
				Blob blob = data.getImage();
				byte[] image = null;
				String encodedImage = null;
				try {
					image = blob.getBytes(1, (int) blob.length());
					encodedImage = java.util.Base64.getEncoder().encodeToString(image);
				} catch (SQLException e) {
					e.printStackTrace();
				}
				jsonData.put("id", data.getImageID());
				jsonData.put("image", encodedImage);
				jsonData.put("person", data.getPersonName());
				jsonData.put("created", data.getCreatedAt());
				jsonImages.add(jsonData);
			}
			response.put("response", "success");
			response.put("images", jsonImages);
		}
		else {
			response.put("response", "fail");
			response.put("message", "failure to retrieve all images");
		}
	}

	public void deleteUser() {
		accountTable.connect();
		accountTable.deleteRecord(request.getString("usnername"));
		accountTable.disconnect();
		response.put("response", "success");
	}

	public void userInfo() {
		String username = request.getString("username");
		accountTable.connect();
		accountTable.getRecord(username);
		accountTable.getDeviceID(username);
		accountTable.disconnect();
		response.put("response", "success");
	}

	public void setUser(User user) {
		this.user = user;
	}

	public User getUser() {
		return user;
	}

	public void login() {
		String username = request.getString("username");
		String password = request.getString("password");

		// Check login details for account which must be an admin
		accountTable.connect();
		boolean validLogin = accountTable.getLogin(username, password, "admin");
		User currentUser = accountTable.getRecord(username);
		accountTable.disconnect();

		if (validLogin) {
			// Successful login response
			response.put("response", "success");

			// Set current user
			setUser(currentUser);

			// Create and send 2FA code
			/*TwoFactorAuthentication twoFactorAuthentication = new TwoFactorAuthentication(currentUser);
			twoFactorAuthentication.generateCode();
			twoFactorAuthentication.sendEmail();*/

		}
		else {
			// Failed login response
			response.put("response", "fail");
			response.put("message", "Incorrect username/password for admin account");
		}

	}
}
