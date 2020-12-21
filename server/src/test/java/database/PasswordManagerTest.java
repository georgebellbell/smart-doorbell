package database;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class PasswordManagerTest {
	PasswordManager passwordManager = new PasswordManager();

	@Test
	void testGenerateUniqueSalt() {
		assertNotEquals(passwordManager.generateSalt(), passwordManager.generateSalt());
	}

	@Test
	void hashUniquePassword() {
		String password = "securePassword1";
		assertNotEquals(
				passwordManager.hashPassword(password, passwordManager.generateSalt()),
				passwordManager.hashPassword(password, passwordManager.generateSalt())
		);
	}

	@Test
	void checkIncorrectPassword() {
		String hashedPassword = passwordManager.hashPassword("securePassword1", passwordManager.generateSalt());
		assertNull(passwordManager.checkPasswords(hashedPassword, "wrongPassword"));
	}

	@Test
	void checkCorrectPassword() {
		String hashedPassword = passwordManager.hashPassword("securePassword1", passwordManager.generateSalt());
		assertEquals(hashedPassword, passwordManager.checkPasswords(hashedPassword, "securePassword1"));
	}
}