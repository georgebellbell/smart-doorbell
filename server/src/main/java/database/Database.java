package database;

import com.jcraft.jsch.*;

import java.io.OutputStream;
import java.sql.*;
import java.util.Properties;

public class Database {
	public static void main(String[] args) {
		int lport = 22;
		String host = "linux.cs.ncl.ac.uk";
		String rhost = "cs-db.ncl.ac.uk";
		int rport = 3306;
		String user = "b9021925";
		String dbuserName = "t2033t17";
		String dbpassword = "KnewBut+(Fin";
		String dburl = "jdbc:mysql://cs-db.ncl.ac.uk:3306/t2033t17";
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
			session.setPassword("");
			session.setConfig(config);
			session.connect();
			assigned_port = session.setPortForwardingL(lport, rhost, rport);
			System.out.println("Connected");

			Class.forName(driverName).newInstance();
			//String connectionString = "jdbc:mysql://cs-db.ncl.ac.uk:3306/" + dbuserName + "?user=" + dbuserName + "&password=" + dbpassword + "&useUnicode=true&characterEncoding=UTF-8";
			//conn = DriverManager.getConnection(connectionString);
			conn = DriverManager.getConnection(dburl, dbuserName, dbpassword);
			stmt = conn.createStatement();
			ResultSet rs = stmt.executeQuery("SELECT * FROM accounts");
			while (rs.next()) {
				System.out.println(assigned_port);
				System.out.println("Working!");
			}
			System.out.println ("Database connection established");
			System.out.println("DONE");
		} catch (SQLException | ClassNotFoundException | JSchException throwables) {
			throwables.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (InstantiationException e) {
			e.printStackTrace();
		}
	}
}
