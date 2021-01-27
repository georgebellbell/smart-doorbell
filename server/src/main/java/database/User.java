/**
 * @author Dominykas Makarovas, Jack Reed
 * @version 1.0
 * @since 25/01/2021
 */

package database;

import authentication.PasswordManager;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Objects;

public class User {
	PasswordManager passwordManager = new PasswordManager();
	// Object properties
	private String username;
	private String email;
	private String password;
	private String salt;
	private String role;
	private String created_at;

	// Double constructor, when retrieving user from database and when adding user to database.
	public User(String username, String email, String password, String role, String created_at) {
		this.username = username;
		this.email = email;
		this.password = password;
		this.role = role;
		this.created_at = created_at;
	}
	public User(String username, String email, String password, String role) {
		this(username, email, password, role, LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
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
	public String getCreated_at() { return created_at; }
	public void setCreated_at(String created_at) { this.created_at = created_at; }

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		User user = (User) o;
		return 	Objects.equals(username, user.username) &&
				Objects.equals(email, user.email) &&
				Objects.equals(role, user.role) &&
				Objects.equals(created_at, user.created_at);
	}

	@Override
	public int hashCode() {
		return Objects.hash(passwordManager, username, email, password, salt, role, created_at);
	}
}
