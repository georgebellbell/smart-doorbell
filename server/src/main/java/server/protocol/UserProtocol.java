package server.protocol;

import authentication.TwoFactorAuthentication;
import communication.Email;
import database.Data;
import database.User;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;

public class UserProtocol extends Protocol {
	private User user;
	private ArrayList<String> noValidTokenRequests;

	@Override
	public void init() {
		requestHashMap.put("login", new RequestHandler(this::login, "username", "password"));
		requestHashMap.put("signup", new RequestHandler(this::signUp, "username", "email", "password"));
		requestHashMap.put("twofactor", new RequestHandler(this::twoFactor, "username", "code"));
		requestHashMap.put("resendtwofactor", new RequestHandler(this::resendTwoFactor, "username"));
		requestHashMap.put("faces", new RequestHandler(this::faces, "doorbellID"));
		requestHashMap.put("deleteface", new RequestHandler(this::deleteFace, "id"));
		requestHashMap.put("renameface", new RequestHandler(this::renameFace, "id", "name"));
		requestHashMap.put("addface", new RequestHandler(this::addFace,
				"personname", "image", "doorbellID"));
		requestHashMap.put("lastface", new RequestHandler(this::lastFace));
		requestHashMap.put("logout", new RequestHandler(this::logout));
		requestHashMap.put("opendoor", new RequestHandler(this::openDoor, "message", "doorbellID"));
		requestHashMap.put("getdoorbells", new RequestHandler(this::getDoorbells));
		requestHashMap.put("connectdoorbell", new RequestHandler(this::connectDoorbell,
				"doorbellID", "doorbellname"));
		requestHashMap.put("changepassword", new RequestHandler(this::changePassword, "password"));
		requestHashMap.put("changeemail", new RequestHandler(this::changeEmail, "email"));
		requestHashMap.put("deleteaccount", new RequestHandler(this::deleteAccount));
		requestHashMap.put("removedoorbell", new RequestHandler(this::removeDoorbell, "doorbellID"));

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

		// All Android requests must include token
		boolean hasToken = false;
		try {
			JSONObject requestObject = new JSONObject(request);
			if (requestObject.get("token") != null) {
				hasToken = true;
			}
		} catch (JSONException ignored) {
		}

		return hasToken;
	}

	/**
	 * Checks if user's token is valid
	 * @return if user's token is valid
	 */
	private boolean checkToken() {
		String requestName = request.getString("request");
		String token = request.getString("token");
		if (!noValidTokenRequests.contains(requestName)) {
			user = userTokenTable.getUserByToken(token);
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
		userTokenTable.deleteByToken(token);
		userTokenTable.addToken(token, username);
	}

	@Override
	public String processRequest() {
		if (!checkToken()) {
			return response.toString();
		}
		return super.processRequest();
	}

	/**
	 * Remove current user from doorbell
	 */
	public void removeDoorbell() {
		String username = user.getUsername();
		String doorbellID = request.getString("doorbellID");
		if (doorbellTable.unassignDoorbell(doorbellID, username))
			response.put("response", "success");
		else
			response.put("response", "fail");
	}

	/**
	 * Assigns current user to doorbell
	 */
	public void connectDoorbell() {
		String username = user.getUsername();
		String doorbellID = request.getString("doorbellID");
		String doorbellName = request.getString("doorbellname");
		if (doorbellTable.isUserAssignedDoorbell(username, doorbellID)) {
			response.put("response", "fail");
			response.put("message", "Doorbell already assigned");
			return;
		}
		if (!doorbellTable.doorbellExists(doorbellID)) {
			doorbellTable.addNewDoorbell(doorbellID);
		}
		doorbellTable.setDoorbell(username, doorbellID);
		doorbellTable.updateDoorbell(doorbellID, doorbellName);
		response.put("response", "success");
	}

	/**
	 * Deletes logged in user's account
	 */
	public void deleteAccount() {
		String username = user.getUsername();
		boolean accountDeleted = accountTable.deleteRecord(username);
		if (accountDeleted)
			response.put("response", "success");
		else
			response.put("response", "fail");
	}

	/**
	 * Sets user's password to new password
	 */
	public void changePassword() {
		String username = user.getUsername();
		String password = request.getString("password");
		boolean passwordChanged = accountTable.changePassword(username, password);
		if (passwordChanged)
			response.put("response", "success");
		else
			response.put("response", "fail");
	}

	/**
	 * Sets the user's email to new email address
	 */
	public void changeEmail() {
		String username = user.getUsername();
		String email = request.getString("email");

		// Check email
		if (!Email.isValidEmail(email)) {
			response.put("response", "fail");
			response.put("message", "Email is not valid");
			return;
		}

		boolean emailChanged = accountTable.changeEmail(username, email);
		if (emailChanged)
			response.put("response", "success");
		else
			response.put("response", "fail");
	}

	/**
	 * Gets the doorbells that the user is assigned to
	 */
	public void getDoorbells() {
		String username = user.getUsername();
		JSONArray doorbells = doorbellTable.getDoorbells(username);
		if (doorbells.length() != 0) {
			response.put("response", "success");
			response.put("doorbells", doorbells);
		} else {
			response.put("response", "fail");
			response.put("message", "You have 0 doorbells assigned");
		}
	}

	/**
	 * Opens the door request by the user
	 */
	public void openDoor() {
		String message = request.getString("message");
		String deviceId = request.getString("doorbellID");

		if (message.equals("open")) {
			response.put("response", "open");
			pollingTable.createPoll(deviceId, message);
		}
		else {
			response.put("response", "close");
		}
	}

	/**
	 * Logs out the current session of the user
	 */
	public void logout() {
		String token = request.getString("token");
		if (userTokenTable.deleteByToken(token))
			response.put("response", "success");
	}

	/**
	 * Gets the most recent face that ring the user's doorbell
	 */
	public void lastFace(){
		try {
			String username = user.getUsername();
			JSONObject image = new JSONObject();
			Data recentImage = dataTable.getRecentImage(username);
			String doorbellName = doorbellTable.getDoorbellName(recentImage.getDeviceID());
			String encodedImage = java.util.Base64.getEncoder().encodeToString(recentImage.getImage());
			image.put("image", encodedImage);
			response.put("response", "success");
			response.put("image", image);
			response.put("time", recentImage.getLastUsed());
			response.put("person", recentImage.getPersonName());
			response.put("imageID", recentImage.getImageID());
			response.put("doorbellname", doorbellName);
			response.put("doorbellID", recentImage.getDeviceID());

		} catch (Exception e) {
			response.put("response", "fail");
			response.put("message", "failed to retrieve recent image");
		}
	}

	/**
	 * Adds a new face to recognise faces of the requested doorbell
	 */
	public void addFace() {
		String doorbellID = request.getString("doorbellID");
		String personName = request.getString("personname");
		byte[] image = java.util.Base64.getDecoder().decode(request.getString("image").getBytes());
		if (dataTable.addRecord(new Data(doorbellID, image, personName))) {
			response.put("response", "success");
		} else {
			response.put("response", "fail");
		}
	}

	/**
	 * Sets a new name for a requested face
	 */
	public void renameFace() {
		int id = request.getInt("id");
		String name = request.getString("name");
		if (dataTable.changeName(id, name))
			response.put("response", "success");
	}

	/**
	 * Removes the recognise face requested
	 */
	public void deleteFace() {
		int id = request.getInt("id");
		if (dataTable.deleteRecordById(id))
			response.put("response", "success");
	}

	/**
	 * Gets all of the recognise faces of the doorbell requested
	 */
	public void faces() {
		String doorbellID = request.getString("doorbellID");
		ArrayList<Data> allImages = new ArrayList<>(dataTable.getAllImages(doorbellID));
		ArrayList<JSONObject> jsonImages = new ArrayList<>();
		if (allImages.size() != 0) {
			for (Data data: allImages) {
				JSONObject jsonData = new JSONObject();
				String encodedImage = java.util.Base64.getEncoder().encodeToString(data.getImage());
				jsonData.put("id", data.getImageID());
				jsonData.put("image", encodedImage);
				jsonData.put("person", data.getPersonName());
				jsonData.put("created", data.getLastUsed());
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

	/**
	 * Attempts to sign in user with account details inputted
	 */
	public void login() {
		String username = request.getString("username");
		String password = request.getString("password");

		// Connect to database
		boolean validLogin = accountTable.getLogin(username, password, "user");
		User currentUser = accountTable.getRecord(username);

		if (validLogin) {
			// Successful login response
			response.put("response", "success");
			response.put("message", "Successfully logged in!");

			// Create and send 2FA code
			TwoFactorAuthentication twoFactorAuthentication = new TwoFactorAuthentication(currentUser);
			if (twoFactorAuthentication.getGeneratedCode() == null) {
				twoFactorAuthentication.generateCode();
				Thread emailThread = new Thread(twoFactorAuthentication::sendEmail);
				emailThread.start();
			}

		}
		else {
			// Failed login response
			response.put("response", "fail");
			response.put("message", "invalid login");
		}

	}

	/**
	 * Creates a new account with details passed from app
	 */
	public void signUp() {
		String username = request.getString("username");
		String email = request.getString("email");
		String password = request.getString("password");
		String token = request.getString("token");

		// Check email
		if (!Email.isValidEmail(email)) {
			response.put("response", "fail");
			response.put("message", "Email is not valid");
			return;
		}

		// Create account
		User user = new User(username, email, password, "user");
		if (accountTable.addRecord(user)) {
			response.put("response", "success");
			response.put("message", "Account created");

			// Add user's token
			saveToken(username, token);
		}
		else {
			response.put("response", "fail");
			response.put("message", "Username already exists, please try again");
		}
	}

	/**
	 * Checks user's inputted 2FA code
	 */
	public void twoFactor() {
		String username = request.getString("username");
		String code = request.getString("code");
		String token = request.getString("token");

		// Get user trying to enter 2FA code
		User user = accountTable.getRecord(username);

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

				// Remove 2FA code
				twoFactorAuthentication.deleteCode();
			} else {
				// Incorrect 2FA code entered
				response.put("response", "fail");
				response.put("message", "2FA code is incorrect");
			}
		}
	}

	/**
	 * Requests a new 2FA code to be sent
	 */
	public void resendTwoFactor() {
		String username = request.getString("username");

		// Get user requesting 2FA email
		User user = accountTable.getRecord(username);

		if (user != null) {
			// Generate code and send email
			TwoFactorAuthentication twoFactorAuthentication = new TwoFactorAuthentication(user);
			twoFactorAuthentication.generateCode();
			twoFactorAuthentication.sendEmail();
			response.put("response", "success");
		}

	}
}
