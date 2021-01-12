package server.protocol;

import authentication.TwoFactorAuthentication;
import database.Data;
import database.User;
import org.json.JSONObject;
import org.springframework.security.crypto.codec.Base64;
import server.ResponseHandler;

import java.sql.Blob;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;

public class UserProtocol extends Protocol {
	public UserProtocol() {
		requestResponse.put("login", new ResponseHandler(this::login, "username", "password"));
		requestResponse.put("signup", new ResponseHandler(this::signUp, "username", "email", "password"));
		requestResponse.put("twofactor", new ResponseHandler(this::twoFactor, "username", "code"));
		requestResponse.put("resendtwofactor", new ResponseHandler(this::resendTwoFactor, "username"));
		requestResponse.put("faces", new ResponseHandler(this::faces, "username"));
		requestResponse.put("deleteface", new ResponseHandler(this::deleteFace, "id"));
		requestResponse.put("renameface", new ResponseHandler(this::renameFace, "id", "name"));
	}

	public void renameFace() {
		dataTable.connect();
		int id = request.getInt("id");
		String name = request.getString("name");
		if (dataTable.changeName(id, name))
			response.put("response", "sucess");
		dataTable.disconnect();
	}
	public void deleteFace() {
		dataTable.connect();
		int id = request.getInt("id");
		if (dataTable.deleteRecordById(id))
			response.put("response", "success");
		dataTable.disconnect();
	}

	public void faces() {
		String username = request.getString("username");
		accountTable.connect();
		ArrayList<String> doorbells = accountTable.getDeviceID(username);
		accountTable.disconnect();
		dataTable.connect();
		ArrayList<Data> allImages = new ArrayList<>();
		for (String doorbell : doorbells) {
			allImages.addAll(dataTable.getAllImages(doorbell));
		}
		dataTable.disconnect();
		ArrayList<JSONObject> jsonImages = new ArrayList<>();
		if (allImages.size() != 0) {
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

	public void login() {
		String username = request.getString("username");
		String password = request.getString("password");

		// Connect to database
		accountTable.connect();
		boolean validLogin = accountTable.getLogin(username, password, "user");
		User currentUser = accountTable.getRecord(username);
		accountTable.disconnect();

		if (validLogin) {
			// Successful login response
			response.put("response", "success");
			response.put("message", "Successfully logged in!");

			// Create and send 2FA code
			Thread emailThread = new Thread(() -> {
				TwoFactorAuthentication twoFactorAuthentication = new TwoFactorAuthentication(currentUser);
				twoFactorAuthentication.generateCode();
				twoFactorAuthentication.sendEmail();
			});
			emailThread.start();

		}
		else {
			// Failed login response
			response.put("response", "fail");
			response.put("message", "invalid login");
		}

	}

	public void signUp() {
		String username = request.getString("username");
		String email = request.getString("email");
		String password = request.getString("password");
		User user = new User(username, email, password, "user");
		accountTable.connect();
		if (accountTable.addRecord(user)) {
			response.put("response", "success");
			response.put("message", "Account created");
		}
		else {
			response.put("response", "fail");
			response.put("message", "Failed to create account");
		}
		accountTable.disconnect();
	}

	public void twoFactor() {
		String username = request.getString("username");
		String code = request.getString("code");

		// Get user trying to enter 2FA code
		accountTable.connect();
		User user = accountTable.getRecord(username);
		accountTable.disconnect();

		if (user != null) {
			TwoFactorAuthentication twoFactorAuthentication = new TwoFactorAuthentication(user);

			// Check code has not expired
			boolean validCode = twoFactorAuthentication.hasValidCode();
			if (!validCode) {
				response.put("response", "fail");
				response.put("message", "2FA code has expired, request a new one");
				return;
			}

			// Check code matches
			boolean codeMatched = twoFactorAuthentication.checkGeneratedCode(code);
			if (codeMatched) {
				// Correct 2FA code entered
				response.put("response", "success");
				response.put("message", "2FA code is correct");
			} else {
				// Incorrect 2FA code entered
				response.put("response", "fail");
				response.put("message", "2FA code is incorrect");
			}
		}
	}

	public void resendTwoFactor() {
		String username = request.getString("username");

		// Get user requesting 2FA email
		accountTable.connect();
		User user = accountTable.getRecord(username);
		accountTable.disconnect();

		if (user != null) {
			// Generate code and send email
			TwoFactorAuthentication twoFactorAuthentication = new TwoFactorAuthentication(user);
			twoFactorAuthentication.generateCode();
			twoFactorAuthentication.sendEmail();
			response.put("response", "success");
		}

	}
}
