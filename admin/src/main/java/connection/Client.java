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

	public Client() {
		try {
			connect();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void connect() throws IOException {
		socket = new Socket(HOST, PORT);
		out = new PrintWriter(socket.getOutputStream(), true);
		in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
	}

	/**
	 * Sends request to server and gets the response
	 * @param request to be send to server
	 * @return response from server
	 */
	public JSONObject run(JSONObject request) {
		// Send connection type
		out.println("admin");

		// Send request
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
