package database;

import com.jcraft.jsch.*;

import java.io.OutputStream;
import java.sql.*;
import java.util.Properties;

public class Database {
	public static void main(String[] args) {
		int lport = 3306;
		String host = "linux.cs.ncl.ac.uk";
		String rhost = "cs-db.ncl.ac.uk";
		int rport = 3306;
		String user = "b9021925";
		String dbuserName = "t2033t17";
		String dbpassword = "KnewBut+(Fin";
		String dburl = "jdbc:mysql://localhost:3306/t2033t17";
		String driverName = "com.mysql.cj.jdbc.Driver";

		Connection conn;
		Session session;
		Statement stmt;
		int assigned_port = -1;

		try {
			JSch jsch = new JSch();
			java.util.Properties config = new java.util.Properties();
			config.put("StrictHostKeyChecking", "no");
			session = jsch.getSession(user, host, 22);
			session.setPassword("unipassword");
			session.setConfig(config);
			session.connect();
			assigned_port = session.setPortForwardingL(lport, rhost, rport);
			System.out.println("Connected");

			Class.forName(driverName).newInstance();
			conn = DriverManager.getConnection(dburl, dbuserName, dbpassword);
			stmt = conn.createStatement();
			System.out.println ("Database connection established");
			ResultSet rs = stmt.executeQuery("SELECT * FROM accounts");
			while (rs.next()) {
				System.out.println(rs.getString("Username"));
			}
			stmt.close();
			conn.close();
			System.out.println("Connection closed");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
