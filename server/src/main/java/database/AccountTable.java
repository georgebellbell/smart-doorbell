package database;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;


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
			String query = "SELECT Username, Password, Email, Role, Created_at FROM accounts WHERE Username=?";
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
	 * @param username - username of the account to retrieve the deviceID or ID's from
	 * @return deviceID associated with the user
	 */
	public ArrayList<String> getDeviceID(String username) {
		ArrayList<String> deviceIDs = new ArrayList<>();
		try {
			String query = "SELECT Pi_id FROM doorbelluser WHERE Username=?";
			statement = conn.prepareStatement(query);
			statement.setString(1, username);
			ResultSet resultSet = statement.executeQuery();
			while (resultSet.next()){
				deviceIDs.add(resultSet.getString("Pi_id"));
			}
			statement.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return deviceIDs;
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

	/**
	 * @param username - username of the user's account
	 * @param password - password of the account
	 * @param role - role of the user
	 * @return login retrieved from the database
	 */
	public boolean getLogin(String username, String password, String role) {
		boolean found = false;
		password = passwordManager.checkPasswords(getPassword(username), password);
		try {
			String query = "SELECT Username, Password, Role  FROM accounts " +
					"WHERE Username=? AND Password=? AND Role = ?";
			statement = conn.prepareStatement(query);
			statement.setString(1, username);
			statement.setString(2, password);
			statement.setString(3, role);
			ResultSet resultSet = statement.executeQuery();
			found = resultSet.next();
			statement.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return found;
	}

	/**
	 * @param username - username of the account to retrieve the password of
	 * @return password of the user if found, else null
	 */
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

	/**
	 * @param username - username of the person to change the password of
	 * @param password - password to change in the database
	 * @return if password successfully updated
	 */
	public boolean changePassword(String username, String password) {
		try {
			String salt = password.substring(0, 29);
			String query = "UPDATE accounts Set Password = ?, Salt = ? WHERE Username = ?";
			statement = conn.prepareStatement(query);
			statement.setString(1, password);
			statement.setString(2, salt);
			statement.setString(3, username);
			statement.execute();
			statement.close();
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}
}