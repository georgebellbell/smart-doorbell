package authentication;

import java.security.SecureRandom;

public class TwoFactorAuthentication {

	private final String email;
	private String generatedCode;

	public TwoFactorAuthentication(String email) {
		this.email = email;
	}

	public String getUserEmail() {
		return email;
	}

	/**
	 * Generates 6 digit code that is securely random
	 */
	public void generateCode() {
		SecureRandom secureRandom = new SecureRandom();
		StringBuilder generatedCode = new StringBuilder();

		// Generate 6 random numbers and add them to generated code
		for (int i=0; i < 6; i++) {
			generatedCode.append(secureRandom.nextInt(10));
		}

		this.generatedCode = generatedCode.toString();
	}

	public String getGeneratedCode() {
		return generatedCode;
	}

	public boolean sendEmail() {
		return false;
	}

}
