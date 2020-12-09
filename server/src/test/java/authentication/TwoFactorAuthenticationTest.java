package authentication;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.junit.jupiter.api.Assertions.*;

class TwoFactorAuthenticationTest {

	private TwoFactorAuthentication twoFactor;
	private String userEmail;

	@BeforeEach
	void setup() {
		userEmail = "quicksolutions.doorbell@gmail.com";
		twoFactor = new TwoFactorAuthentication(userEmail);
	}

	@Test
	void testEmailSetOnInitialisation() {
		assertEquals(userEmail, twoFactor.getUserEmail());
	}

	@Test
	void testGeneratedCodeIs6DigitsLong() {
		String code = twoFactor.generateCode();

		// Check if generated code is 6 digits long
		Pattern pattern = Pattern.compile("^\\d{6}$");
		Matcher matcher = pattern.matcher(code);

		assertTrue(matcher.find());
	}

	@Test
	void testGeneratedCodeIsRandom() {
		String code1 = twoFactor.generateCode();
		String code2 = twoFactor.generateCode();

		// Codes should be random and be different to each other
		assertNotEquals(code1, code2);
	}

}