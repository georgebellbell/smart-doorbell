package database;

import com.jcraft.jsch.*;

import java.sql.*;

public class Database {
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
	PreparedStatement statement;
	int assigned_port = -1;
	JSch jsch = new JSch();

	public Database() {
		connectToDatabase();
	}
	public void establishSSH() {
		try {
			java.util.Properties config = new java.util.Properties();
			config.put("StrictHostKeyChecking", "no");
			session = jsch.getSession(USER, HOST, 22);
			session.setPassword("");
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
			System.out.println ("Database connection established");
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}
	public boolean addUser(String username, String email, String password, String salt, String role) {
		try {
			String query = "INSERT INTO accounts (Username, Email, Password, Salt, Role, Created_at)"
					+ " VALUES (?, ?, ?, ?, ?, ?)";
			statement = conn.prepareStatement(query);
			statement.setString(1, username);
			statement.setString(2,email);
			statement.setString(3, password);
			statement.setString(4, salt);
			statement.setString(5, role);
			statement.setTimestamp(6, java.sql.Timestamp.from(java.time.Instant.now()));
			statement.execute();
			conn.close();
			return true;
		} catch (Exception e){
			e.printStackTrace();
			return false;
		}
	}
	public String getUser(String username) {
		String email = null;
		String role = null;
		String created_at = null;
		String password = null;
		String salt = null;
		try {
			String query = "SELECT Username, Email, Password, Salt, Role, Created_at  FROM accounts WHERE Username=?";
			statement = conn.prepareStatement(query);
			statement.setString(1, username);
			ResultSet resultSet = statement.executeQuery();
			while (resultSet.next()) {
				email = resultSet.getString("Email");
				password = resultSet.getString("Password");
				salt = resultSet.getString("Salt");
				role = resultSet.getString("Role");
				created_at = resultSet.getString("Created_at");
			}
			return username + " " + email + " " + password + " " + salt + " " + role + " " + created_at;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
	public static void main(String[] args) {
		Database t17 = new Database();
		System.out.println(t17.addUser(
				"Jeff",
				"John@gmail.com",
				"Password",
				"Salt",
				"admin")
		);
		System.out.println(t17.getUser("Dom"));
	}
}
