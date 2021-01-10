package connection;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;

public class Client {
	// Connection details
	private static final String HOST = "localhost";
	private static final int PORT = 4444;

	// Socket variables
	private Socket socket;
	private PrintWriter out;
	private BufferedReader in;

	// Status
	private boolean requestInProgress = false;

	public Client() {
		try {
			connect();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void connect() throws IOException {
		// Socket connection
		socket = new Socket(HOST, PORT);
		out = new PrintWriter(socket.getOutputStream(), true);
		in = new BufferedReader(new InputStreamReader(socket.getInputStream()));

		// Send connection type
		out.println("admin");
	}

	public boolean isRequestInProgress() {
		return requestInProgress;
	}

	/**
	 * Sends request to server and gets the response
	 * @param request to be send to server
	 * @return response from server
	 */
	public JSONObject run(JSONObject request) {
		// Check request is not already in progress
		if (isRequestInProgress()) {
			throw new IllegalStateException("Request is already in progress");
		}

		// Ensure socket is still running
		try {
			if (!socket.getKeepAlive()) {
				connect();
			}
		} catch (IOException e) {
			throw new IllegalStateException("Connection to server failed");
		}

		// Send request
		requestInProgress = true;
		out.println(request.toString());

		// Get response
		JSONObject response = new JSONObject();
		try {
			String responseString = in.readLine();
			if (responseString != null) {
				response = new JSONObject(responseString);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}

		// Request is over
		requestInProgress = false;

		return response;
	}

	public void close() {
		try {
			socket.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
