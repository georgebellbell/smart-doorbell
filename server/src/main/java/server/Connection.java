package server;

import server.protocol.AdminProtocol;
import server.protocol.DoorbellProtocol;
import server.protocol.Protocol;
import server.protocol.UserProtocol;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class Connection extends Thread {
	private Socket socket;

	public Connection(Socket socket) {
		this.socket = socket;
	}

	/**
	 * Handles connection with client
	 */
	public void run() {
		try {
			PrintWriter out =
				 new PrintWriter(socket.getOutputStream(), true);
			BufferedReader in = new BufferedReader(
				 new InputStreamReader(socket.getInputStream()));

			// Initiate protocol for connection type
			String connectionType = in.readLine();
			Protocol protocol;
			switch (connectionType) {
				case "user":
					// Create protocol for user connection type
					protocol = new UserProtocol();
					break;
				case "admin":
					// Create protocol for admin connection type
					protocol = new AdminProtocol();
					break;
				case "doorbell":
					// Create protocol for doorbell connection type
					protocol = new DoorbellProtocol();
					break;
				default:
					// No protocol for connection type
					socket.close();
					return;
			}

			// Communicate with client
			String request, response;
			while ((request = in.readLine()) != null) {
				System.out.println("Request: " + request);
				if (protocol.isRequestValid(request)) {
					// Handle request
					protocol.setRequest(request);
					response = protocol.processRequest();
					out.println(response);
				} else {
					// Invalid request
					System.out.println("Connection closed: Invalid request");
					socket.close();
					break;
				}
			}

		} catch (IOException e) {
			System.out.println(e.getMessage());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
