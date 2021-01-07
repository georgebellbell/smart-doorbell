package server;

import authentication.TwoFactorAuthentication;
import database.AccountTable;
import database.Data;
import database.DataTable;
import database.User;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.security.crypto.codec.Base64;

import javax.sql.rowset.serial.SerialBlob;
import javax.sql.rowset.serial.SerialClob;
import java.sql.Blob;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;

public class Protocol {
	JSONObject request;
	JSONObject response = new JSONObject();
	AccountTable accountTable = new AccountTable();
	DataTable dataTable = new DataTable();
	HashMap<String, ResponseHandler> requestResponse = new HashMap<>();

	public Protocol() {
		requestResponse.put("login", new ResponseHandler(this::login, "username", "password"));
		requestResponse.put("signup", new ResponseHandler(this::signUp, "username", "email", "password"));
		requestResponse.put("twofactor", new ResponseHandler(this::twoFactor, "username", "code"));
		requestResponse.put("resendtwofactor", new ResponseHandler(this::resendTwoFactor, "username"));
<<<<<<< server/src/main/java/server/Protocol.java
		requestResponse.put("image", new ResponseHandler(this::image, "id", "data"));
=======
		requestResponse.put("image", new ResponseHandler(this::image, "id", "data"));
		requestResponse.put("faces", new ResponseHandler(this::faces, "username"));
>>>>>>> server/src/main/java/server/Protocol.java
	}

	public void image() {
		try {
			dataTable.connect();
			Connection conn = dataTable.getConn();
			byte[] Image = java.util.Base64.getDecoder().decode(request.getString("data").getBytes());
			Blob blobImage = conn.createBlob();
			blobImage.setBytes(1, Image);
			dataTable.addRecord(new Data(request.getString("id"), blobImage, "Jeff"));
			dataTable.disconnect();
		} catch (Exception e){
			System.out.println(e);
		}
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
		boolean validLogin = accountTable.getLogin(username, password);
		User currentUser = accountTable.getRecord(username);
		accountTable.disconnect();

		if (validLogin) {
			// Successful login response
			response.put("response", "success");
			response.put("message", "Successfully logged in!");

			// Create and send 2FA code
			TwoFactorAuthentication twoFactorAuthentication = new TwoFactorAuthentication(currentUser);
			twoFactorAuthentication.generateCode();
			twoFactorAuthentication.sendEmail();
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

	/**
	 * Sets request to be handles
	 * @param request - Request received by server
	 */
	public void setRequest(String request) {
		if (!isRequestValid(request)) {
			throw new IllegalArgumentException("Request is not valid");
		}
		this.request = new JSONObject(request);
	}

	/**
	 * Checks if the request is valid
	 * @param request - Request to be checked
	 * @return if request is valid
	 */
	public boolean isRequestValid(String request) {
		try {
			// Create JSON object
			JSONObject requestObject = new JSONObject(request);

			// Check if request type exists
			String requestType = requestObject.getString("request");
			if (!requestResponse.containsKey(requestType)) {
				return false;
			}

			// Check if required keys for request are present
			ResponseHandler responseHandler = requestResponse.get(requestType);
			return responseHandler.requestHasRequiredKeys(requestObject);

		} catch (JSONException e) {
			// Request is not a valid JSON object
			return false;
		}
	}

	/**
	 * Processes the input request
	 * @return response
	 */
	public String processInput(){
		if (request == null) {
			throw new IllegalStateException("Request must be set before processing");
		}

		// Check for illegal characters
		if (!request.getString("request").equals("image")) {
			if (checkIllegalChars(request.toString().toLowerCase())) {
				response.put("response", "fail");
				response.put("message", "illegal expression");
				return response.toString();
			}
		}



		// Handle response to request
		String requestType = request.getString("request");
		ResponseHandler responseHandler = requestResponse.get(requestType);
		if (responseHandler != null) {
			Runnable responseMethod = responseHandler.getMethod();
			responseMethod.run();
		}
		return response.toString();
	}

	/**
	 * Checks if request contains illegal characters
	 * @param request - Request received from the client
	 * @return if illegal character(s) are found
	 */
	public boolean checkIllegalChars(String request) {
		boolean illegalCharFound = false;

		String[] badChars = {
				"<", ">", "script", "alert", "truncate", "delete", "insert", "drop", "into", "where", "null", "xp_",
				"<>", "!", "`", "input"
		};

		for (String badChar : badChars) {
			if (request.contains(badChar)) {
				illegalCharFound = true;
				break;
			}
		}

		return illegalCharFound;
	}
}
