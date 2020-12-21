package database;

import org.springframework.security.crypto.bcrypt.BCrypt;

public class PasswordManager {
	/**
	 * @return securely generated salt
	 */
	public String generateSalt() {
		return BCrypt.gensalt();
	}

	/**
	 * @param password user's password to hash
	 * @param salt used to hash the password
	 * @return hashed user password
	 */
	public String hashPassword(String password, String salt) {
		return BCrypt.hashpw(password, salt);
	}

	/**
	 * @param storedPassword that is stored in the database
	 * @param passwordToCheck the string that is typed by the user to check against the stored password
	 * @return user password if correct entered password, else null
	 */
	public String checkPasswords(String storedPassword, String passwordToCheck) {
		if (BCrypt.checkpw(passwordToCheck, storedPassword))
			return storedPassword;
		else
			return null;
	}
}
