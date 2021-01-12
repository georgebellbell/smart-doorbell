package server.protocol;

import authentication.TwoFactorAuthentication;
import database.*;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.security.crypto.codec.Base64;
import server.ResponseHandler;

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
	DoorbellTable doorbellTable = new DoorbellTable();
	HashMap<String, ResponseHandler> requestResponse = new HashMap<>();

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

		// Handle response to request
		String requestType = request.getString("request");
		ResponseHandler responseHandler = requestResponse.get(requestType);
		if (responseHandler != null) {
			Runnable responseMethod = responseHandler.getMethod();
			responseMethod.run();
		}
		return response.toString();
	}
}
