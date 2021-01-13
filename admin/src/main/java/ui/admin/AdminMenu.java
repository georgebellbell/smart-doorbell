package ui.admin;

import connection.Client;
import ui.admin.panels.Accounts;
import ui.admin.panels.Analytics;
import ui.admin.panels.Doorbell;
import ui.admin.panels.Email;

import javax.swing.*;
import java.awt.*;

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

	private Client connection;

	public AdminMenu(Client connection) {
		add(panel);
		setTitle("Quick Solutions: Smart Doorbell Admin Tool");
		setSize(550, 350);
		setVisible(true);
		sidePanel.setSize(new Dimension(200, 0));

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
