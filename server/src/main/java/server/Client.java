package server;

import java.io.*;
import java.net.*;

public class Client {
	private final String HOST = "localhost";
	private final int PORT = 4444;

	public void run() {
		try (
				Socket socket = new Socket(HOST, PORT);
				PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
				BufferedReader in = new BufferedReader(
						new InputStreamReader(socket.getInputStream()));
		) {
			BufferedReader stdIn = new BufferedReader(new InputStreamReader(System.in));
			String fromServer;
			String fromUser;

			while ((fromServer = in.readLine()) != null) {
				System.out.println("Server: " + fromServer);
				if (fromServer.equals("Bye."))
					break;

				fromUser = stdIn.readLine();
				if (fromUser != null) {
					System.out.println("Client: " + fromUser);
					out.println(fromUser);
				}
			}
		} catch (UnknownHostException e) {
			System.err.println("Don't know about host " + HOST);
			System.exit(1);
		} catch (IOException e) {
			System.err.println("Couldn't get I/O for the connection to " +
					HOST);
			System.exit(1);
		}
	}

	public static void main(String[] args) {
		Client client = new Client();
		client.run();
	}
}
