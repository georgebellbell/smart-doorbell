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

			// Initiate protocol
			Protocol protocol = new Protocol();

			// Communicate with client
			while ((request = in.readLine()) != null) {
				System.out.println("Request: " + request);
				try {
					// Handle request
					protocol.setRequest(request);
					response = protocol.processInput();
					out.println(response);
				} catch (IllegalArgumentException e) {
					// Invalid request
					System.out.println("Connection closed: " + e.getMessage());
					clientSocket.close();
					break;
				}

			}

		} catch (IOException e) {
			System.out.println(e.getMessage());
		}
	}
}
