package server;

import java.net.*;
import java.io.*;

public class Server {
	private final int PORT = 4444;

	/**
	 * Handle incoming client connections
	 */
	public void run() {
		try {
			ServerSocket serverSocket = new ServerSocket(PORT);

			while (true) {
				// Accept new client connection
				Socket incomingClientSocket = serverSocket.accept();
				System.out.println("New connection accepted...");

				// Create client connection and start thread
				ClientConnection client = new ClientConnection(incomingClientSocket);
				client.start();
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
