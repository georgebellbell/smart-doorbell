package authentication;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

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

}