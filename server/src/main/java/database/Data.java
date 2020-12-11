package database;

import java.sql.Timestamp;
import java.time.Instant;

public class Data {
	private int id;
	private String image;
	private String username;
	private String person_name;
	private Timestamp created_at;

	public Data(int id, String username, String image, String person_name) {
		this.id = id;
		this.username = username;
		this.image = image;
		this.person_name = person_name;
		this.created_at = Timestamp.from(Instant.now());
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
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
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
	public Timestamp getCreated_at() {
		return created_at;
	}
	public void setCreated_at(Timestamp created_at) {
		this.created_at = created_at;
	}
}
