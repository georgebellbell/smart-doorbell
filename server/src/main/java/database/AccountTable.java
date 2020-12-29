package database;

import java.sql.PreparedStatement;
import java.sql.ResultSet;


public class AccountTable extends DatabaseConnection {
	PreparedStatement statement;
	PasswordManager passwordManager = new PasswordManager();

	/**
	 * @param user - to add to the database
	 * @return if recorded add to the table
	 */
	public boolean addRecord(User user) {
		try {
			String salt = passwordManager.generateSalt();
			String query = "INSERT INTO accounts (Username, Email, Password, Salt, Role, Created_at)"
					+ " VALUES (?, ?, ?, ?, ?, ?)";
			statement = conn.prepareStatement(query);
			statement.setString(1, user.getUsername());
			statement.setString(2,user.getEmail());
			statement.setString(3, passwordManager.hashPassword(user.getPassword(), salt));
			statement.setString(4, salt);
			statement.setString(5, user.getRole());
			statement.setString(6, user.getCreated_at());
			statement.execute();
			statement.close();
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}

	/**
	 * @param username - record to get from the accounts table
	 * @return User object constructed by data if exists
	 */
	public User getRecord(String username){
		User user = null;
		try {
			String query = "SELECT Username, Email, Password, Role, Created_at  FROM accounts WHERE Username=?";
			statement = conn.prepareStatement(query);
			statement.setString(1, username);
			ResultSet resultSet = statement.executeQuery();
			while (resultSet.next()) {
				user = new User(
						resultSet.getString("Username"),
						resultSet.getString("Email"),
						resultSet.getString("Password"),
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
	 * @param username of record to delete
	 * @return if record deleted
	 */
	public boolean deleteRecord(String username) {
		try {
			String query = "DELETE FROM accounts WHERE Username=?";
			statement = conn.prepareStatement(query);
			statement.setString(1, username);
			statement.execute();
			statement.close();
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}

	public boolean getLogin(String username, String password) {
		boolean found = false;
		password = passwordManager.checkPasswords(getPassword(username), password);
		try {
			String query = "SELECT Username, Password  FROM accounts WHERE Username=? AND Password=?";
			statement = conn.prepareStatement(query);
			statement.setString(1, username);
			statement.setString(2, password);
			ResultSet resultSet = statement.executeQuery();
			found = resultSet.next();
			statement.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return found;
	}

	public String getPassword(String username) {
		String found = null;
		try {
			String query = "SELECT Password FROM accounts WHERE Username=?";
			statement = conn.prepareStatement(query);
			statement.setString(1, username);
			ResultSet resultSet = statement.executeQuery();
			if (resultSet.next()){
				found = resultSet.getString("password");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return found;
	}
}