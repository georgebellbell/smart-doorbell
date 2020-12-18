package server;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class ClientConnection extends Thread {
	private Socket clientSocket;

	public ClientConnection(Socket socket) {
		clientSocket = socket;
	}

	/**
	 * Handles connection with client
	 */
	public void run() {
		try {
			 PrintWriter out =
					 new PrintWriter(clientSocket.getOutputStream(), true);
			 BufferedReader in = new BufferedReader(
					 new InputStreamReader(clientSocket.getInputStream()));

			String request, response;

			// Initiate protocol with client
			Protocol protocol = new Protocol();

			// Client is connected
			JSONObject responseObj = new JSONObject();
			responseObj.put("response", "connected");
			out.println(responseObj.toString());

			// Communicate with client
			while ((request = in.readLine()) != null) {
				System.out.println(request);
				JSONObject requestObj = new JSONObject(request);
				response = protocol.processInput(requestObj);
				out.println(response);
			}

		} catch (IOException e) {
			System.out.println(e.getMessage());
		}
	}
}
