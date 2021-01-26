/**
 * @author Dominykas Makarovas, Jack Reed
 * @version 1.0
 * @since 25/01/2021
 */

package server;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;

import java.net.*;
import java.io.*;

public class Server {
	private static final int PORT = 4444;

	/**
	 * Connects to firebase
	 * @return if connection was successful
	 */
	public boolean connectFirebase() {
		boolean connection = false;
		URL firebaseKey = getClass().getClassLoader().getResource("key.json");

		try {
			if (firebaseKey != null) {
				FileInputStream serviceAccount =
						new FileInputStream(firebaseKey.getPath());

				FirebaseOptions options = new FirebaseOptions.Builder()
						.setCredentials(GoogleCredentials.fromStream(serviceAccount))
						.build();

				FirebaseApp.initializeApp(options);

				connection = true;
			}
		} catch (IOException e) {
			e.printStackTrace();
		}

		return connection;
	}

	/**
	 * Handle incoming client connections
	 */
	public void run() {
		try {
			ServerSocket serverSocket = new ServerSocket(PORT);

			// Connect to firebase
			boolean firebaseConnection = connectFirebase();
			if (!firebaseConnection) {
				System.out.println("Failed to connect to Firebase, push notifications will be unable to send");
			}

			// Handle incoming connections
			while (true) {
				// Accept new client connection
				Socket incomingClientSocket = serverSocket.accept();
				System.out.println("New connection accepted...");

				// Create client connection and start thread
				Connection client = new Connection(incomingClientSocket);
				client.start();
			}


		} catch (IOException e) {
			System.out.println(e.getMessage());
		}
	}

	public static void main(String[] args) {
		Server server = new Server();
		server.run();
	}
}
