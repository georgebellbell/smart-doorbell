package database;

import authentication.PasswordManager;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
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
			return false;
		}
	}
	
	/**
	 * @param role - role assigned to the registered user
	 * @return number of users with the assigned role
	 */
	public int getTotalUsers(String role) {
		int total = 0;
		try {
			String query = "SELECT COUNT(*) FROM accounts WHERE Role = ?";
			statement = conn.prepareStatement(query);
			statement.setString(1, role);
			ResultSet resultSet = statement.executeQuery();
			resultSet.next();
			total = resultSet.getInt(1);
			statement.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return total;
	}

	/**
	 * @param username - username of the email to retrieve
	 * @return email of the associated username
	 */
	public String getEmailByUsername(String username) {
		String email = null;
		try {
			String query = "SELECT Email FROM accounts WHERE Username = ?";
			statement = conn.prepareStatement(query);
			statement.setString(1, username);
			ResultSet resultSet = statement.executeQuery();
			if (resultSet.next())
				email = resultSet.getString("email");
			statement.close();
		} catch (SQLException e) {
			e.printStackTrace();
			System.out.println("Invalid username");
		}
		return email;
	}

	/**
	 * @param id - doorbell id that links to the associated users
	 * @return all email addresses associated to id
	 */
	public ArrayList<String> getEmailByDoorbell(String id) {
		ArrayList<String> emails = new ArrayList<>();
		try {
			String query = "SELECT Email FROM accounts, doorbelluser WHERE accounts.Username = doorbelluser.Username AND doorbelluser.Pi_id = ?";
			statement = conn.prepareStatement(query);
			statement.setString(1, id);
			ResultSet resultSet = statement.executeQuery();
			while (resultSet.next())
				emails.add(resultSet.getString("email"));

		} catch (SQLException e) {
			System.out.println("Doorbell doesn't exist");
		}
		return emails;
	}

	/**
	 * @return all emails from the account table
	 */
	public ArrayList<String> getAllEmails() {
		ArrayList<String> emails = new ArrayList<>();
		try {
			String query = "SELECT Email FROM accounts";
			statement = conn.prepareStatement(query);
			ResultSet resultSet = statement.executeQuery();
			while (resultSet.next())
				emails.add(resultSet.getString("email"));

		} catch (SQLException e) {
			System.out.println("Can't retrieve all emails");
		}
		return emails;
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
	 * @param username - username of the person to change the password of
	 * @param password - password to change in the database
	 * @return if password successfully updated
	 */
	public boolean changePassword(String username, String password) {
		try {
			String salt = passwordManager.generateSalt();
			String query = "UPDATE accounts Set Password = ?, Salt = ? WHERE Username = ?";
			statement = conn.prepareStatement(query);
			statement.setString(1, passwordManager.hashPassword(password, salt));
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

	public boolean changeEmail(String username, String email) {
		try {
			String query = "UPDATE accounts Set Email = ? WHERE Username = ?";
			statement = conn.prepareStatement(query);
			statement.setString(1, email);
			statement.setString(2, username);
			statement.execute();
			statement.close();
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}

	/**
	 * @param oldUsername - username that the user had previously
	 * @param newUsername - username to be changed to
	 * @param newEmail - email to change to
	 * @return details changed
	 */
	public boolean changeDetails(String oldUsername, String newUsername, String newEmail) {
		try {
			String query = "UPDATE accounts Set Username = ?, Email =? WHERE Username = ?";
			statement = conn.prepareStatement(query);
			statement.setString(1, newUsername);
			statement.setString(2, newEmail);
			statement.setString(3, oldUsername);
			statement.execute();
			statement.close();
			return true;
		} catch (SQLException e) {
			System.out.println("Duplicate username");
			return false;
		}
	}

	public boolean deleteRecord(User user) {
		return deleteRecord(user.getUsername());
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
}
