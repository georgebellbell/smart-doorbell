package server.protocol;

import authentication.PasswordManager;
import database.Data;
import database.User;
import email.Email;
import org.json.JSONArray;
import org.json.JSONObject;
import server.ResponseHandler;

import java.sql.Blob;
import java.sql.SQLException;
import java.util.ArrayList;

public class AdminProtocol extends Protocol {
	private PasswordManager passwordManager = new PasswordManager();
	private User user;

	public AdminProtocol() {
		requestResponse.put("login", new ResponseHandler(this::login, "username", "password"));
		requestResponse.put("user", new ResponseHandler(this::userInfo, "username"));
		requestResponse.put("deleteuser", new ResponseHandler(this::deleteUser, "username"));
		requestResponse.put("update", new ResponseHandler(this::update, "username", "newusername", "newemail"));
		requestResponse.put("searchdoorbell", new ResponseHandler(this::searchDoorbell, "id"));
		requestResponse.put("deletedoorbell", new ResponseHandler(this::deleteDoorbell, "id"));
		requestResponse.put("updatedoorbell", new ResponseHandler(this::updateDoorbell,"id", "name"));
		requestResponse.put("email", new ResponseHandler(this::sendEmail, "type", "subject", "contents", "recipient"));
		requestResponse.put("newpassword", new ResponseHandler(this::newPassword, "username"));
		requestResponse.put("analysis", new ResponseHandler(this::performAnalysis));
	}

	public void performAnalysis() {
		accountTable.connect();
		int users = accountTable.getTotalUsers("user");
		int admins = accountTable.getTotalUsers("admin");
		accountTable.disconnect();
		dataTable.connect();
		int images = dataTable.getTotalImages();
		dataTable.disconnect();
		doorbellTable.connect();
		int doorbells = doorbellTable.getTotalDoorbells();
		JSONArray jsonArray = doorbellTable.getDoorbellPieData();
		doorbellTable.disconnect();
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
		accountTable.connect();
		String emailAddress = accountTable.getEmailByUsername(username);
		boolean changedPassword = accountTable.changePassword(username, newPassword);
		accountTable.disconnect();

		if (changedPassword) {
			accountTable.disconnect();
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

		accountTable.connect();
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

		accountTable.disconnect();
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
		doorbellTable.connect();
		if (doorbellTable.updateDoorbell(id, name)) {
			response.put("response", "success");
			response.put("message", "Doorbell successfully updated");
		}
		else {
			response.put("response", "fail");
			response.put("message", "Doorbell could not be updated");
		}
		doorbellTable.disconnect();
	}

	public void deleteDoorbell() {
		String id = request.getString("id");
		doorbellTable.connect();
		if (doorbellTable.deleteDoorbell(id)) {
			response.put("response", "success");
			response.put("message", "Doorbell successfully deleted");
		} else {
			response.put("response", "fail");
			response.put("message", "Doorbell could not be deleted");
		}
		doorbellTable.disconnect();
	}

	public void searchDoorbell() {
		String id = request.getString("id");
		doorbellTable.connect();
		String doorbellName = doorbellTable.getDoorbellName(id);
		ArrayList<String> users = doorbellTable.getUsers(id);
		doorbellTable.disconnect();
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
		accountTable.connect();
		if (accountTable.changeDetails(oldUsername, newUsername, newEmail)){
			response.put("response", "success");
			response.put("message", "Account successfully updated");
		}
		else {
			response.put("response", "fail");
			response.put("message", "Account username is already taken");
		}
		accountTable.disconnect();
	}

	public void faces(String id) {
		dataTable.connect();
		ArrayList<Data> allImages = dataTable.getAllImages(id);
		dataTable.disconnect();
		ArrayList<JSONObject> jsonImages = new ArrayList<>();
		if (allImages != null) {
			for (Data data: allImages) {
				JSONObject jsonData = new JSONObject();
				Blob blob = data.getImage();
				byte[] image;
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
		accountTable.connect();
		if (accountTable.deleteRecord(username)){
			response.put("response", "success");
			response.put("message", "Account successfully deleted");
		} else {
			response.put("response", "fail");
			response.put("message", "Account not deleted");
		}
		accountTable.disconnect();
	}

	public void userInfo() {
		String username = request.getString("username");
		accountTable.connect();
		User user = accountTable.getRecord(username);
		ArrayList<String> deviceIDs = accountTable.getDeviceID(username);
		accountTable.disconnect();
		if (user != null) {
			response.put("response", "success");
			response.put("username", user.getUsername());
			response.put("email", user.getEmail());
			response.put("role", user.getRole());
			response.put("time", user.getCreated_at());
			response.put("devices", deviceIDs.toString());
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
