package server.protocol;

import database.*;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.HashMap;

public abstract class Protocol {
	JSONObject request;
	JSONObject response = new JSONObject();
	AccountTable accountTable = new AccountTable();
	UserTokenTable userTokenTable = new UserTokenTable();
	DataTable dataTable = new DataTable();
	DoorbellTable doorbellTable = new DoorbellTable();
	PollingTable pollingTable = new PollingTable();
	HashMap<String, RequestHandler> requestHashMap = new HashMap<>();

	public Protocol() {
		init();
	}

	/**
	 * Initialise request handling hash map
	 */
	public abstract void init();

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
			if (!requestHashMap.containsKey(requestType)) {
				return false;
			}

			// Check if required keys for request are present
			RequestHandler requestHandler = requestHashMap.get(requestType);
			return requestHandler.requestHasRequiredKeys(requestObject);

		} catch (JSONException e) {
			// Request is not a valid JSON object
			return false;
		}
	}

	/**
	 * Processes the input request
	 * @return response
	 */
	public String processRequest(){
		if (request == null) {
			throw new IllegalStateException("Request must be set before processing");
		}

		// Handle response to request
		String requestType = request.getString("request");
		RequestHandler requestHandler = requestHashMap.get(requestType);
		if (requestHandler != null) {
			Runnable responseMethod = requestHandler.getMethod();
			responseMethod.run();
		}
		return response.toString();
	}
}
