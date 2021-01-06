package database;

import com.jcraft.jsch.*;

import java.sql.*;

public class DatabaseConnection {
	// SSH connection info
	JSch jsch = new JSch();
	Connection conn;
	Session session;
	private final String HOST = "linux.cs.ncl.ac.uk";
	private final String RHOST = "cs-db.ncl.ac.uk";
	private final int PORT = 3306;
	private final String USER = "b9015109";
	private final String PASSWORD = "Fgdcxaz9015109";
	int assigned_port = -1;

	// Database connection info
	private final String DBUSERNAME = "t2033t17";
	private final String DBPASSWORD = "KnewBut+(Fin";
	private final String DBURL = "jdbc:mysql://localhost:3306/t2033t17";
	private final String DRIVERNAME = "com.mysql.cj.jdbc.Driver";

	/**
	 * Connect via SSH tunnel forwarding local port to remote host and port
	 */
	public void establishSSH() {
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
			assigned_port = session.setPortForwardingL(PORT, RHOST, PORT);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Connect to the database via SSH
	 * @return if connection established
	 */
	public boolean connect() {
		establishSSH();
		try {
			Class.forName(DRIVERNAME).newInstance();
			conn = DriverManager.getConnection(DBURL, DBUSERNAME, DBPASSWORD);
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}
	/**
	 * @return if connection closed
	 */
	public boolean disconnect() {
		try {
			conn.close();
			session.disconnect();
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}

	public Connection getConn() {
		return conn;
	}
}
