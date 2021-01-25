package server.protocol;

import authentication.PasswordManager;
import database.ImageData;
import database.User;
import communication.Email;
import org.json.JSONArray;
import org.json.JSONObject;
import java.util.ArrayList;

public class AdminProtocol extends Protocol {
	private PasswordManager passwordManager = new PasswordManager();
	private User user;

	@Override
	public void init() {
		requestHashMap.put("login", new RequestHandler(this::login, "username", "password"));
		requestHashMap.put("user", new RequestHandler(this::userInfo, "username"));
		requestHashMap.put("deleteuser", new RequestHandler(this::deleteUser, "username"));
		requestHashMap.put("update", new RequestHandler(this::update, "username", "newusername", "newemail", "devices"));
		requestHashMap.put("searchdoorbell", new RequestHandler(this::searchDoorbell, "id"));
		requestHashMap.put("deletedoorbell", new RequestHandler(this::deleteDoorbell, "id"));
		requestHashMap.put("updatedoorbell", new RequestHandler(this::updateDoorbell,"id", "name", "users"));
		requestHashMap.put("email", new RequestHandler(this::sendEmail, "type", "subject", "contents", "recipient"));
		requestHashMap.put("newpassword", new RequestHandler(this::newPassword, "username"));
		requestHashMap.put("analysis", new RequestHandler(this::performAnalysis));
	}

	@Override
	public String processRequest() {
		if (!request.getString("request").equals("login") && user == null) {
			// Admin must be logged in to perform non-login requests
			response.put("response", "invalid");
			response.put("message", "Must be logged in to perform this action");
			return response.toString();
		}

		return super.processRequest();
	}

	public void performAnalysis() {
		int users = accountTable.getTotalUsers("user");
		int admins = accountTable.getTotalUsers("admin");
		int images = dataTable.getTotalImages();
		int doorbells = doorbellTable.getTotalDoorbells();
		JSONArray jsonArray = doorbellTable.getDoorbellPieData();
		response.put("response", "success");
		response.put("users", users);
		response.put("admins", admins);
		response.put("images", images);
		response.put("doorbells", doorbells);
		response.put("imagegraph", jsonArray);
	}

	public void newPassword() {
		String username = request.getString("username");
		String newPassword = passwordManager.generateString();
		String emailAddress = accountTable.getEmailByUsername(username);
		boolean changedPassword = accountTable.changePassword(username, newPassword);

		if (changedPassword) {
			userTokenTable.deleteToken(username);
			Email email = new Email();
			email.addRecipient(emailAddress);
			email.setSubject("Password reset");
			email.setContents("Dear user: " + username +  " your new password is: " + newPassword);
			if (email.send()) {
				response.put("response", "success");
				response.put("message", "Password change email sent");
			}
			else {
				response.put("response", "fail");
				response.put("message", "Password change email not sent");
			}
		}
		else {
			response.put("response", "fail");
			response.put("message", "Password can't be changed");
		}
	}

	public void sendEmail() {
		int type = request.getInt("type");
		String subject = request.getString("subject");
		String content = request.getString("contents");
		String id = request.getString("recipient");
		Email email = new Email();

		// Send by email type, 0 is by username, 1 is by doorbell id, and 2 is all
		switch (type) {
			case 0:
				String userEmail = accountTable.getEmailByUsername(id);
				if (userEmail == null) {
					response.put("response", "fail");
					response.put("message", "Username does not exist");
					return;
				}
				email.addRecipient(userEmail);
				break;
			case 1:
				ArrayList<String> doorbellEmails = accountTable.getEmailByDoorbell(id);
				if (doorbellEmails == null) {
					response.put("response", "fail");
					response.put("message", "Doorbell ID does not exist");
					return;
				}
				email.addRecipients(doorbellEmails);
				break;
			case 2:
				ArrayList<String> allEmails = accountTable.getAllEmails();
				email.addRecipients(allEmails);
				break;
		}

		email.setSubject(subject);
		email.setContents(content);

		if (email.send()) {
			response.put("response", "success");
			response.put("message", "Email successfully sent");
		}
		else {
			response.put("response", "fail");
			response.put("message", "Email could not be sent");
		}
	}

	public void updateDoorbell() {
		String id = request.getString("id");
		String name = request.getString("name");
		JSONArray users = request.getJSONArray("users");

		// Check users
		for (int i=0; i < users.length(); i++) {
			String userToBeAddedUsername = users.getString(i);
			User userToBeAdded = accountTable.getRecord(userToBeAddedUsername);
			if (userToBeAdded == null) {
				response.put("response", "fail");
				response.put("message", "Doorbell could not be updated\n" +
						String.format("Username '%s' does not exist", userToBeAddedUsername));
				return;
			}
		}

		// Remove current users from doorbell
		doorbellTable.deleteUsersFromDoorbell(id);

		// Add users
		for (int i=0; i < users.length(); i++)
			doorbellTable.setDoorbell(users.getString(i), id);

		// Update doorbell
		if (doorbellTable.updateDoorbell(id, name)) {
			response.put("response", "success");
			response.put("message", "Doorbell successfully updated");
		}
		else {
			response.put("response", "fail");
			response.put("message", "Doorbell name could not be updated");
		}
	}

	public void deleteDoorbell() {
		String id = request.getString("id");
		if (doorbellTable.deleteDoorbell(id)) {
			response.put("response", "success");
			response.put("message", "Doorbell successfully deleted");
		} else {
			response.put("response", "fail");
			response.put("message", "Doorbell could not be deleted");
		}
	}

	public void searchDoorbell() {
		String id = request.getString("id");
		String doorbellName = doorbellTable.getDoorbellName(id);
		ArrayList<String> users = doorbellTable.getUsers(id);
		faces(id);
		if (doorbellName != null) {
			response.put("response", "success");
			response.put("id", id);
			response.put("name",doorbellName);
			response.put("users", users);
		} else {
			response.put("response", "fail");
			response.put("message", "Doorbell could not be found");
		}
	}

	public void update() {
		String oldUsername = request.getString("username");
		String newUsername = request.getString("newusername");
		String newEmail = request.getString("newemail");
		JSONArray devices = request.getJSONArray("devices");

		// Check email
		if (!Email.isValidEmail(newEmail)) {
			response.put("response", "fail");
			response.put("message", "Email is not valid");
			return;
		}

		// Update account details
		boolean updateAccount = accountTable.changeDetails(oldUsername, newUsername, newEmail);
		if (!updateAccount) {
			response.put("response", "fail");
			response.put("message", "Account username is already taken");
			return;
		}

		// Update doorbells
		doorbellTable.deleteUserDoorbells(newUsername);
		for (int i = 0; i < devices.length(); i++) {
			String doorbellID = devices.getString(i);
			if (!doorbellTable.doorbellExists(doorbellID)) {
				doorbellTable.addNewDoorbell(doorbellID);
			}
			doorbellTable.setDoorbell(newUsername, doorbellID);
		}

		response.put("response", "success");
		response.put("message", "Account successfully updated");
	}

	public void faces(String id) {
		ArrayList<ImageData> allImages = dataTable.getAllImages(id);
		ArrayList<JSONObject> jsonImages = new ArrayList<>();
		if (allImages != null) {
			for (ImageData imageData : allImages) {
				JSONObject jsonData = new JSONObject();
				String encodedImage = java.util.Base64.getEncoder().encodeToString(imageData.getImage());
				jsonData.put("id", imageData.getImageID());
				jsonData.put("person", imageData.getPersonName());
				jsonData.put("image", encodedImage);
				jsonData.put("created", imageData.getLastUsed());
				jsonImages.add(jsonData);
			}
		}
		response.put("images", jsonImages);
	}

	public void deleteUser() {
		String username = request.getString("username");

		// Check user is not deleting itself
		if (username.equals(user.getUsername())) {
			response.put("response", "fail");
			response.put("message", "You cannot delete your own account");
			return;
		}

		// Delete user
		if (accountTable.deleteRecord(username)){
			response.put("response", "success");
			response.put("message", "Account successfully deleted");
		} else {
			response.put("response", "fail");
			response.put("message", "Account not deleted");
		}
	}

	public void userInfo() {
		String username = request.getString("username");
		User user = accountTable.getRecord(username);
		ArrayList<String> deviceIDs = accountTable.getDeviceID(username);
		if (user != null) {
			response.put("response", "success");
			response.put("username", user.getUsername());
			response.put("email", user.getEmail());
			response.put("role", user.getRole());
			response.put("time", user.getCreated_at());
			response.put("devices", deviceIDs);
		} else {
			response.put("response", "fail");
			response.put("message", "User could not be found");
		}
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
		boolean validLogin = accountTable.getLogin(username, password, "admin");
		User currentUser = accountTable.getRecord(username);

		if (validLogin) {
			// Successful login response
			response.put("response", "success");

			// Set current user
			setUser(currentUser);
		}
		else {
			// Failed login response
			response.put("response", "fail");
			response.put("message", "Incorrect username/password for admin account");
		}
	}
}
