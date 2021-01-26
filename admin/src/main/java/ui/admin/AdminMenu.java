/**
 * @author Dominykas Makarovas, Jack Reed
 * @version 1.0
 * @since 25/01/2021
 */

package ui.admin;

import connection.Client;
import ui.admin.panels.Accounts;
import ui.admin.panels.Analytics;
import ui.admin.panels.Doorbell;
import ui.admin.panels.Email;

import javax.swing.*;
import java.awt.*;
import java.net.URL;

public class AdminMenu extends JFrame{
	private JButton analyticsButton;
	private JPanel panel;
	private JPanel sidePanel;
	private JButton accountButton;
	private JButton logoutButton;
	private JPanel mainPanel;
	private JButton doorbellButton;
	private JButton emailButton;
	private Analytics analytics;
	private Doorbell doorbell;
	private Email email;
	private Accounts accounts;
	private JLabel logo;

	private Client connection;

	public AdminMenu(Client connection) {
		add(panel);
		setTitle("Quick Solutions: Smart Doorbell Admin Tool");
		setSize(640, 500);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setVisible(true);
		sidePanel.setPreferredSize(new Dimension(150, 600));

		// Connection
		this.connection = connection;
		accounts.setConnection(connection);
		analytics.setConnection(connection);
		doorbell.setConnection(connection);
		email.setConnection(connection);

		// Set actions for navigation buttons
		accountButton.addActionListener(actionEvent -> setMainPanel("accounts"));
		analyticsButton.addActionListener(actionEvent -> {
			setMainPanel("analytics");
			Thread t = new Thread(() -> analytics.getAnalytics());
			t.start();
		});
		doorbellButton.addActionListener(actionEvent -> setMainPanel("doorbell"));
		emailButton.addActionListener(actionEvent -> setMainPanel("email"));
		logoutButton.addActionListener(actionEvent -> dispose());

		// Load logo
		loadLogo();
	}

	private void loadLogo() {
		logo.setText("");
		URL logoResource = getClass().getClassLoader().getResource("logo.jpg");
		if (logoResource != null) {
			ImageIcon image = new ImageIcon(logoResource);
			logo.setIcon(image);
			setIconImage(image.getImage());
		}
	}

	private void setMainPanel(String panelName) {
		CardLayout cardLayout = (CardLayout) mainPanel.getLayout();
		cardLayout.show(mainPanel, panelName);
	}

	@Override
	public void dispose() {
		connection.close();
		super.dispose();
	}
}
