package database;

import java.sql.PreparedStatement;
import java.sql.ResultSet;


public class AccountTable {
	DatabaseConnection databaseConnection = new DatabaseConnection();
	PreparedStatement statement;

	/**
	 * Add user record to accounts table
	 */
	public boolean addRecord(User user) {
		try {
			String query = "INSERT INTO accounts (Username, Email, Password, Salt, Role, Created_at)"
					+ " VALUES (?, ?, ?, ?, ?, ?)";
			statement = databaseConnection.conn.prepareStatement(query);
			statement.setString(1, user.getUsername());
			statement.setString(2,user.getEmail());
			statement.setString(3, user.getPassword());
			statement.setString(4, user.getSalt());
			statement.setString(5, user.getRole());
			statement.setString(6, user.getCreated_at());
			statement.execute();
			statement.close();
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}
	/**
	 * Get record by username, returns User object
	 */
	public User getRecord(String username){
		User user = null;
		try {
			String query = "SELECT Username, Email, Password, Salt, Role, Created_at  FROM accounts WHERE Username=?";
			statement = databaseConnection.conn.prepareStatement(query);
			statement.setString(1, username);
			ResultSet resultSet = statement.executeQuery();
			while (resultSet.next()) {
				user = new User(
						resultSet.getString("Username"),
						resultSet.getString("Email"),
						resultSet.getString("Password"),
						resultSet.getString("Salt"),
						resultSet.getString("Role"),
						resultSet.getString("Created_at")
				);
			}
			statement.close();
		} catch (Exception e) {
			e.printStackTrace();
			return user;
		}
		return user;
	}
	/**
	 * Delete record by username if exists
	 */
	public boolean deleteRecord(String username) {
		try {
			String query = "DELETE FROM accounts WHERE Username=?";
			statement = databaseConnection.conn.prepareStatement(query);
			statement.setString(1, username);
			statement.execute();
			statement.close();
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}
	public DatabaseConnection getDatabaseConnection() {
		return databaseConnection;
	}
}
