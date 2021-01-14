package server.protocol;

import authentication.TwoFactorAuthentication;
import database.Data;
import database.User;
import database.UserTokenTable;
import org.json.JSONObject;
import org.springframework.security.crypto.codec.Base64;
import server.ResponseHandler;

import java.sql.Blob;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;

public class UserProtocol extends Protocol {
	private User user;

	ArrayList<String> noValidTokenRequests;

	public UserProtocol() {
		requestResponse.put("login", new ResponseHandler(this::login, "username", "password"));
		requestResponse.put("signup", new ResponseHandler(this::signUp, "username", "email", "password"));
		requestResponse.put("twofactor", new ResponseHandler(this::twoFactor, "username", "code"));
		requestResponse.put("resendtwofactor", new ResponseHandler(this::resendTwoFactor, "username"));
		requestResponse.put("faces", new ResponseHandler(this::faces));
		requestResponse.put("deleteface", new ResponseHandler(this::deleteFace, "id"));
		requestResponse.put("renameface", new ResponseHandler(this::renameFace, "id", "name"));
		requestResponse.put("addface", new ResponseHandler(this::addFace, "personname", "image"));
		requestResponse.put("lastface", new ResponseHandler(this::lastFace));

		noValidTokenRequests = new ArrayList<String>(){{
			add("login");
			add("signup");
			add("twofactor");
			add("resendtwofactor");
		}};
	}

	@Override
	public boolean isRequestValid(String request) {
		if (!super.isRequestValid(request)) {
			return false;
		}
		JSONObject requestObject = new JSONObject(request);
		// All Android requests must include token
		return (requestObject.get("token") != null);
	}

	/**
	 * Checks if user's token is valid
	 * @return if user's token is valid
	 */
	private boolean checkToken() {
		String requestName = request.getString("request");
		String token = request.getString("token");
		if (!noValidTokenRequests.contains(requestName)) {
			userTokenTable.connect();
			user = userTokenTable.getUserByToken(token);
			userTokenTable.disconnect();
			if (user == null) {
				response.put("response", "invalid");
				response.put("message", "Session has expired");
				return false;
			}
		}
		return true;
	}

	/**
	 * Saves username and token to database
	 * @param username - Username of token
	 * @param token - Token of user
	 */
	private void saveToken(String username, String token) {
		userTokenTable.connect();
		userTokenTable.deleteByToken(token);
		userTokenTable.addToken(token, username);
		userTokenTable.disconnect();
	}

	@Override
	public String processInput() {
		if (!checkToken()) {
			return response.toString();
		}
		return super.processInput();
	}

	public void lastFace(){
		String username = user.getUsername();
		JSONObject image = new JSONObject();
		dataTable.connect();
		Data recentImage = dataTable.getRecentImage(username);
		dataTable.disconnect();
		Blob blob = recentImage.getImage();
		byte[] imageBytes = null;
		String encodedImage = null;
		try {
			imageBytes = blob.getBytes(1, (int) blob.length());
			encodedImage = java.util.Base64.getEncoder().encodeToString(imageBytes);
			image.put("image", encodedImage);

			response.put("response", "success");
			response.put("image", image);
			response.put("time", recentImage.getCreatedAt());
		} catch (SQLException e) {
			e.printStackTrace();
			response.put("response", "fail");
			response.put("message", "failed to retrieve recent image");
		}
	}

	public void addFace() {
		try {
			String username = user.getUsername();
			String personName = request.getString("personname");
			byte[] image = Base64.decode(request.getString("image").getBytes());
			accountTable.connect();
			ArrayList<String> deviceID = accountTable.getDeviceID(username);
			accountTable.disconnect();

			dataTable.connect();
			Connection conn = dataTable.getConn();
			Blob blobImage = conn.createBlob();
			blobImage.setBytes(1, image);

			if (deviceID.size() != 0) {
				for (int i = 0; i < deviceID.size(); i++) {
					dataTable.addRecord(new Data(
							deviceID.get(i),
							blobImage,
							personName
					));
				}
				response.put("response", "success");
				dataTable.disconnect();
			} else {
				response.put("response", "fail");
			}

		} catch (SQLException e) {
			response.put("response", "fail");
		}
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
		String username = user.getUsername();
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
		String token = request.getString("token");
		User user = new User(username, email, password, "user");
		accountTable.connect();
		if (accountTable.addRecord(user)) {
			response.put("response", "success");
			response.put("message", "Account created");

			// Add user's token
			saveToken(username, token);
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
		String token = request.getString("token");

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

				// Add user's token
				saveToken(username, token);
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
