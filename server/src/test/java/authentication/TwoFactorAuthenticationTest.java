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

}