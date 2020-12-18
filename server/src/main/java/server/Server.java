package server;

import org.json.JSONObject;

import java.net.*;
import java.io.*;

public class Server {
	private final int PORT = 4444;

	public void run() {
		try (ServerSocket serverSocket = new ServerSocket(PORT);
			 Socket clientSocket = serverSocket.accept();
			 PrintWriter out =
					 new PrintWriter(clientSocket.getOutputStream(), true);
			 BufferedReader in = new BufferedReader(
					 new InputStreamReader(clientSocket.getInputStream()))
		) {
			String request, response;

			// Initiate conversation with client
			Protocol protocol = new Protocol();
			out.println("Server listening");

			while ((request = in.readLine()) != null) {
				JSONObject requestObj = new JSONObject(request);
				response = protocol.processInput(requestObj);
				out.println(response);
			}
		} catch (IOException e) {
			System.out.println("Exception caught when trying to listen on port "
					+ PORT + " or listening for a connection");
			System.out.println(e.getMessage());
		}
	}

	public static void main(String[] args) {
		Server server = new Server();
		server.run();
	}
}
