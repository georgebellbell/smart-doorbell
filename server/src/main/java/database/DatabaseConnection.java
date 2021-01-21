package database;

import com.jcraft.jsch.*;

import java.net.URL;
import java.sql.*;
import java.util.Objects;

public class DatabaseConnection {
	// SSH connection info
	private static final String HOST = "linux.cs.ncl.ac.uk";
	private static final String R_HOST = "cs-db.ncl.ac.uk";
	private static final int PORT = 3306;
	private static final String USER = "b9021925";
	private static final String PASSWORD = "password";



	// Database connection info
	private static final String DB_USERNAME = "t2033t17";
	private static final String DB_PASSWORD = "KnewBut+(Fin";
	private static final String DB_URL = "jdbc:mysql://localhost:3306/t2033t17?autoReconnect=true";
	private static final String DRIVER_NAME = "com.mysql.cj.jdbc.Driver";

	// Session variables
	static JSch jsch = new JSch();
	static Connection conn;
	static Session session;
	static int assignedPort = -1;
	static boolean connected;

	public DatabaseConnection() {
		establishSession();
	}

	/**
	 * Connect via SSH tunnel forwarding local port to remote host and port
	 */
	public void establishSession() {
		if (connected) {
			return;
		}
		try {
			// Not verify the public key of the HOST
			java.util.Properties config = new java.util.Properties();
			config.put("StrictHostKeyChecking", "no");

			// Set up session with the host using login details and default port 22
			session = jsch.getSession(USER, HOST, 22);
			session.setPassword(PASSWORD);
			session.setConfig(config);

			// Connect to SSH
			session.connect();
			assignedPort = session.setPortForwardingL(PORT, R_HOST, PORT);

			// Database connection
			Class.forName(DRIVER_NAME).newInstance();
			conn = DriverManager.getConnection(DB_URL, DB_USERNAME, DB_PASSWORD);

//			jsch.addIdentity(privateKey);
//			session = jsch.getSession(USER, HOST, PORT);
//			session.setConfig("PreferredAuthentications", "publickey,keyboard-interactive,password");
//			java.util.Properties config = new java.util.Properties();
//			config.put("StrictHostKeyChecking", "no");
//			session.setConfig(config);
//			session.connect();
//			assignedPort = session.setPortForwardingL(PORT, R_HOST, PORT);
//
//			Class.forName(DRIVER_NAME).newInstance();
//			conn = DriverManager.getConnection(DB_URL, DB_USERNAME, DB_PASSWORD);

			connected = true;

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Connect to the database via SSH
	 * @return if connection established
	 */
	@Deprecated
	public boolean connect() {
		return true;
	}

	/**
	 * @return if connection closed
	 */
	@Deprecated
	public boolean disconnect() {
		return true;
	}

	/**
	 * Disconnects the current SSH session
	 */
	public static void disconnectSession() {
		try {
			connected = false;
			conn.close();
			session.disconnect();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public Connection getConn() {
		return conn;
	}
}
