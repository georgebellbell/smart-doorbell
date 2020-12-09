package authentication;

import java.security.SecureRandom;

public class TwoFactorAuthentication {

	private final String email;

	public TwoFactorAuthentication(String email) {
		this.email = email;
	}

	public String getUserEmail() {
		return email;
	}

	/**
	 * Generates a 6 digit code that is securely random
	 * @return generated 6 digit code
	 */
	public String generateCode() {
		SecureRandom secureRandom = new SecureRandom();
		StringBuilder generatedCode = new StringBuilder();

		// Generate 6 random numbers and add them to generated code
		for (int i=0; i < 6; i++) {
			generatedCode.append(secureRandom.nextInt(10));
		}

		return generatedCode.toString();
	}
}
