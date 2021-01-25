/**
 * @author Dominykas Makarovas, Jack Reed
 * @version 1.0
 * @since 25/01/2021
 */

package database;

public class Doorbell {
	private String id;
	private String name;

	public Doorbell(String id) {
		this(id, "Name not set");
	}

	public Doorbell(String id, String name) {
		this.id = id;
		this.name = name;
	}

	public String getId() {
		return id;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getName() {
		return name;
	}
}
