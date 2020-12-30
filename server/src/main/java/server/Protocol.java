package server;

import authentication.TwoFactorAuthentication;
import database.AccountTable;
import database.User;
import org.json.JSONObject;
import java.util.HashMap;

public class Protocol {
	JSONObject request;
	JSONObject response = new JSONObject();
	AccountTable accountTable = new AccountTable();
	HashMap<String, Runnable> requestResponse = new HashMap<>();

	public Protocol() {
		requestResponse.put("login", this::login);
		requestResponse.put("signup", this::signUp);
	}

	public void login() {
		// Request details
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

	public void setRequest(String request) {
		this.request = new JSONObject(request);
	}

	public String processInput(){
		if (request != null) {
			boolean illegalChars = checkIllegalChars(request.toString().toLowerCase());
			if (illegalChars) {
				response.put("response", "fail");
				response.put("message", "illegal expression");
				return response.toString();
			}
		}
		Runnable responseMethod = requestResponse.get(request.getString("request"));
		if (responseMethod != null)
			responseMethod.run();

		return response.toString();
	}

	/**
	 * @param request - Request received from the client
	 * @return true if illegal char found
	 */
	public boolean checkIllegalChars(String request) {
		boolean illegalCharFound = false;

		String[] badChars = {
				"<", ">", "script", "alert", "truncate", "delete", "insert", "drop", "into", "where", "null", "xp_",
				"<>", "!", "`", "input"
		};

		for (String badChar : badChars) {
			if (request.contains(badChar))
				illegalCharFound = true;
		}

		return illegalCharFound;
	}
}
