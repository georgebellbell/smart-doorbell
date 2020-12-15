package authentication;

import database.TwoFactorTable;
import database.User;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.junit.jupiter.api.Assertions.*;

class TwoFactorAuthenticationTest {

	private TwoFactorAuthentication twoFactor;
	private User user;

	@BeforeEach
	void setup() {
		user = new User("Test", "quicksolutions.doorbell@gmail.com",
				"Password", "Salt", "User");
		twoFactor = new TwoFactorAuthentication(user);
	}

	@AfterEach
	void cleanUp() {
		// Remove stored record from database
		TwoFactorTable twoFactorTable = new TwoFactorTable();
		twoFactorTable.connectToDatabase();
		twoFactorTable.deleteRecord(user);
		twoFactorTable.closeConnection();
	}

	@Test
	void testUserSetOnInitialisation() {
		assertEquals(user, twoFactor.getUser());
	}

	@Test
	void testGeneratedCodeIs6DigitsLong() {
		twoFactor.generateCode();
		String code = twoFactor.getGeneratedCode();

		// Check if generated code is 6 digits long
		Pattern pattern = Pattern.compile("^\\d{6}$");
		Matcher matcher = pattern.matcher(code);

		assertTrue(matcher.find());
	}

	@Test
	void testGeneratedCodeIsRandom() {
		// 1st code
		twoFactor.generateCode();
		String code1 = twoFactor.getGeneratedCode();

		// 2nd code
		twoFactor.generateCode();
		String code2 = twoFactor.getGeneratedCode();

		// Codes should be random and be different to each other
		assertNotEquals(code1, code2);
	}

	@Test
	void testSendEmail() {
		twoFactor.generateCode();
		boolean sent = twoFactor.sendEmail();
		assertTrue(sent);
	}

	@Test
	void testSendEmailWithoutGeneratingCode() {
		boolean sent = twoFactor.sendEmail();
		assertFalse(sent);
	}

	@Test
	void testCheckSameCode() {
		twoFactor.generateCode();
		String code = twoFactor.getGeneratedCode();
		assertTrue(twoFactor.checkGeneratedCode(code));
	}

	@Test
	void testCheckIncorrectCode() {
		twoFactor.generateCode();
		String code = "000000";
		assertFalse(twoFactor.checkGeneratedCode(code));
	}

	@Test
	void testCheckCodeWithoutGeneratingCode() {
		String code = "000000";
		assertFalse(twoFactor.checkGeneratedCode(code));
	}

}