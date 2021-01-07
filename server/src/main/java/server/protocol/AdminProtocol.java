package server.protocol;

import authentication.TwoFactorAuthentication;
import database.User;
import server.ResponseHandler;

public class AdminProtocol extends Protocol {
	private User user;

	public AdminProtocol() {
		requestResponse.put("login", new ResponseHandler(this::login, "username", "password"));
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
