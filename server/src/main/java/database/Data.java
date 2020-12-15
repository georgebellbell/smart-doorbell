package database;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Objects;

public class Data {
	// Object properties
	private int id;
	private String image;
	private String username;
	private String person_name;
	private String created_at;

	// Double constructor for when retrieving data from database and when adding to the database
	public Data(int id, String username, String image, String person_name) {
		this(id, username, image, person_name, LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
	}
	public Data(int id, String username, String image, String person_name, String created_at) {
		this.id = id;
		this.username = username;
		this.image = image;
		this.person_name = person_name;
		this.created_at = created_at;
	}
	@Override
	public String toString() {
		return "Data{" +
				"id=" + id +
				", image='" + image + '\'' +
				", username='" + username + '\'' +
				", person_name='" + person_name + '\'' +
				", created_at=" + created_at +
				'}';
	}
	public int getId() { return id; }
	public void setId(int id) { this.id = id; }
	public String getImage() {
		return image;
	}
	public void setImage(String image) {
		this.image = image;
	}
	public String getUsername() {
		return username;
	}
	public void setUsername(String username) {
		this.username = username;
	}
	public String getPerson_name() {
		return person_name;
	}
	public void setPerson_name(String person_name) {
		this.person_name = person_name;
	}
	public String getCreated_at() {
		return created_at;
	}
	public void setCreated_at(String created_at) {
		this.created_at = created_at;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		Data data = (Data) o;
		return id == data.id &&
				Objects.equals(image, data.image) &&
				Objects.equals(username, data.username) &&
				Objects.equals(person_name, data.person_name) &&
				Objects.equals(created_at, data.created_at);
	}

	@Override
	public int hashCode() {
		return Objects.hash(id, image, username, person_name, created_at);
	}
}
