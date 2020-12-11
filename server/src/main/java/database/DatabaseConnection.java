package database;

import com.jcraft.jsch.*;

import java.sql.*;

public class DatabaseConnection {
	// SSH connection info
	private final String HOST = "linux.cs.ncl.ac.uk";
	private final String RHOST = "cs-db.ncl.ac.uk";
	private final int PORT = 3306;
	private final String USER = "b9021925";

	// Database connection info
	private final String DBUSERNAME = "t2033t17";
	private final String DBPASSWORD = "KnewBut+(Fin";
	private final String DBURL = "jdbc:mysql://localhost:3306/t2033t17";
	private final String DRIVERNAME = "com.mysql.cj.jdbc.Driver";

	Connection conn;
	Session session;
	int assigned_port = -1;
	JSch jsch = new JSch();

	public void establishSSH() {
		try {
			java.util.Properties config = new java.util.Properties();
			config.put("StrictHostKeyChecking", "no");
			session = jsch.getSession(USER, HOST, 22);
			session.setPassword("lol");
			session.setConfig(config);
			session.connect();
			assigned_port = session.setPortForwardingL(PORT, RHOST, PORT);
			System.out.println("SSH Connected");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	public boolean connectToDatabase() {
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
	public boolean closeConnection() {
		try {
			conn.close();
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}
}
