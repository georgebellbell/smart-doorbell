package server;

import database.AccountTable;
import database.DatabaseConnection;
import database.User;
import org.json.JSONObject;

import java.net.*;
import java.io.*;

public class Protocol {

	public String processInput(JSONObject request){
		JSONObject response = new JSONObject();
		if (request == null) {
			System.out.println("null");
		}
		if (request.getString("request").equals("login")) {
			AccountTable accountTable = new AccountTable();
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
		}
		return response.toString();
	}


}
