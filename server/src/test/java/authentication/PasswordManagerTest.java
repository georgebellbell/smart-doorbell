/**
 * @author Dominykas Makarovas
 * @version 1.0
 * @since 25/01/2021
 */

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
	void testGenerateUniqueSalt() {
		String newSalt = passwordManager.generateSalt();
		assertNotEquals(salt, newSalt);
	}

	@Test
	void testHashPassword() {
		assertNotEquals(password, hashedPassword);
	}

	@Test
	void testCheckPasswords() {
		assertEquals(passwordManager.checkPasswords(hashedPassword, password), hashedPassword);
	}

	@Test
	void testGenerateUniqueString() {
		String newPassword = passwordManager.generateString();
		assertNotEquals(password, newPassword);
	}
}