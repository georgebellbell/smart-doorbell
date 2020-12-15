package authentication;

import database.User;
import email.Email;

import java.security.SecureRandom;

public class TwoFactorAuthentication {

	private final User user;
	private String generatedCode;

	public TwoFactorAuthentication(User user) {
		this.user = user;
	}

	public User getUser() {
		return user;
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

	/**
	 * Sends generated 2FA to email
	 * @return if email was sent successfully
	 */
	public boolean sendEmail() {
		if (generatedCode == null) {
			// Code needs to be generated before email is sent
			return false;
		}

		// Create and send email
		Email authCodeEmail = new Email();
		authCodeEmail.addRecipient(user.getEmail());
		authCodeEmail.setSubject("2 Factor Verification Code");
		authCodeEmail.setContents(String.format("Hello,<br>Your code is: <b>%s</b>", generatedCode));
		return authCodeEmail.send();
	}

}
