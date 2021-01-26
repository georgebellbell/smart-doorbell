/**
 * @author Dominykas Makarovas, Jack Reed
 * @version 1.0
 * @since 25/01/2021
 */

package ui.admin.panels;

import connection.Client;

import javax.swing.*;

abstract public class AdminPanel extends JPanel {

	Client connection = null;

	public void setConnection(Client connection) {
		this.connection = connection;
	}

	public Client getConnection() {
		return connection;
	}


}
