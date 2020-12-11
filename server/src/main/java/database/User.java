package database;

import java.sql.Timestamp;
import java.time.Instant;

public class User {
	private String username;
	private String email;
	private String password;
	private String salt;
	private String role;
	private Timestamp created_at;

	public User(String username, String email, String password, String salt, String role) {
		this.username = username;
		this.email = email;
		this.password = password;
		this.salt = salt;
		this.role = role;
		this.created_at = Timestamp.from(Instant.now());
	}
	@Override
	public String toString() {
		return "User{" +
				"username='" + username + '\'' +
				", email='" + email + '\'' +
				", password='" + password + '\'' +
				", salt='" + salt + '\'' +
				", role='" + role + '\'' +
				", created_at=" + created_at +
				'}';
	}
	public String getUsername() {
		return username;
	}
	public void setUsername(String username) {
		this.username = username;
	}
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	public String getSalt() {
		return salt;
	}
	public void setSalt(String salt) {
		this.salt = salt;
	}
	public String getRole() {
		return role;
	}
	public void setRole(String role) {
		this.role = role;
	}
	public Timestamp getCreated_at() {
		return created_at;
	}
	public void setCreated_at(Timestamp created_at) {
		this.created_at = created_at;
	}
}
