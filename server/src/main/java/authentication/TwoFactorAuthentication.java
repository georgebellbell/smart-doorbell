package authentication;

public class TwoFactorAuthentication {

	private final String email;

	public TwoFactorAuthentication(String email) {
		this.email = email;
	}

	public String getUserEmail() {
		return email;
	}
}
