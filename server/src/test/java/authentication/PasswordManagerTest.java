package authentication;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class PasswordManagerTest {
	private PasswordManager passwordManager;
	private String password;
	private String salt;
	private String hashedPassword;

	@BeforeEach
	void setup() {
		passwordManager = new PasswordManager();
		password = passwordManager.generateString();
		salt = passwordManager.generateSalt();
		hashedPassword = passwordManager.hashPassword(password, salt);
	}

	@Test
	void generateUniqueSalt() {
		String newSalt = passwordManager.generateSalt();
		assertNotEquals(salt, newSalt);
	}

	@Test
	void hashPassword() {
		assertNotEquals(password, hashedPassword);
	}

	@Test
	void checkPasswords() {
		assertEquals(passwordManager.checkPasswords(hashedPassword, password), hashedPassword);
	}

	@Test
	void generateUniqueString() {
		String newPassword = passwordManager.generateString();
		assertNotEquals(password, newPassword);
	}
}