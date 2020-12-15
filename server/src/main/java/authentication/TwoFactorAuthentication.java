package authentication;

import database.TwoFactorTable;
import database.User;
import email.Email;

import java.security.SecureRandom;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TwoFactorAuthentication {

	private final User user;

	public TwoFactorAuthentication(User user) {
		this.user = user;
	}

	public User getUser() {
		return user;
	}

	/**
	 * Generates 6 digit code that is securely random and saves it to database
	 */
	public void generateCode() {
		SecureRandom secureRandom = new SecureRandom();
		StringBuilder generatedCode = new StringBuilder();

		// Generate 6 random numbers and add them to generated code
		for (int i=0; i < 6; i++) {
			generatedCode.append(secureRandom.nextInt(10));
		}

		// Save to database
		TwoFactorTable twoFactorTable = new TwoFactorTable();
		twoFactorTable.connect();
		twoFactorTable.deleteRecord(user); // Delete any previous code
		twoFactorTable.addRecord(user, generatedCode.toString()); // Add code
		twoFactorTable.disconnect();
	}

	/**
	 * Gets valid generated 2FA code from database
	 * @return generated 2FA code
	 */
	public String getGeneratedCode() {
		// Connect to database and get code
		TwoFactorTable twoFactorTable = new TwoFactorTable();
		twoFactorTable.connect();
		String generatedCode = twoFactorTable.getCode(user);
		twoFactorTable.disconnect();

		return generatedCode;
	}

	/**
	 * Compares code to generated code stored in database
	 * @param code - Code to compare to database
	 * @return if the code is the same to the database
	 */
	public boolean checkGeneratedCode(String code) {
		// Check if code is 6 digits
		Pattern pattern = Pattern.compile("^\\d{6}$");
		Matcher matcher = pattern.matcher(code);
		if (!matcher.find()) {
			return false;
		}

		// Get code from database
		TwoFactorTable twoFactorTable = new TwoFactorTable();
		twoFactorTable.connect();
		String generatedCode = twoFactorTable.getCode(user);
		twoFactorTable.disconnect();

		if (generatedCode == null) {
			// Code not generated
			return false;
		}

		// Compare codes
		return (generatedCode.equals(code));
	}

	/**
	 * Sends generated 2FA to email
	 * @return if email was sent successfully
	 */
	public boolean sendEmail() {
		String generatedCode = getGeneratedCode();
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
