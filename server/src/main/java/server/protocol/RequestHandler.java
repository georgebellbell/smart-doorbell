/**
 * @author Dominykas Makarovas, Jack Reed
 * @version 1.0
 * @since 25/01/2021
 */

package server.protocol;

import org.json.JSONObject;

public class RequestHandler {
	private final Runnable method;
	private final String[] requiredKeys;

	/**
	 * @param method Method that will deal with the request and create the response
	 * @param requiredKeys Required keys needed in request JSON object
	 */
	public RequestHandler(Runnable method, String... requiredKeys) {
		this.method = method;
		this.requiredKeys = requiredKeys;
	}

	public Runnable getMethod() {
		return method;
	}

	/**
	 * Checks if request has correct required keys
	 * @param request Request object to be checked
	 * @return if request has the required keys
	 */
	public boolean requestHasRequiredKeys(JSONObject request) {
		for (String requiredKey : requiredKeys) {
			// Check if each key exists
			if (request.get(requiredKey) == null)
				return false;
		}
		return true;
	}
}
