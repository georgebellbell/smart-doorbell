package server;

import database.AccountTable;
import database.User;
import org.json.JSONObject;

public class Protocol {

	public String processInput(JSONObject request){
		JSONObject response = new JSONObject();
		AccountTable accountTable = new AccountTable();

		if (request != null) {
			boolean illegalChars = checkIllegalChars(request.toString().toLowerCase());
			if (illegalChars) {
				response.put("response", "fail");
				response.put("message", "illegal expression");
				return response.toString();
			}
		}
		if (request.getString("request").equals("login")) {

			accountTable.connect();
			boolean validLogin = accountTable.getLogin(request.getString("username"), request.getString("password"));
			accountTable.disconnect();
			if (validLogin) {
				response.put("response", "success");
				response.put("message", "Successfully logged in!");
			}
			else {
				response.put("response", "fail");
				response.put("message", "invalid login");
			}
		} else if (request.getString("request").equals("signup")) {
			String username = request.getString("username");
			String email = request.getString("email");
			String password = request.getString("password");
			User user = new User(username, email, password, "User");
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
