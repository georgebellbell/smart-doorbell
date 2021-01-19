package ui.admin.panels;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.data.general.DefaultPieDataset;
import org.json.JSONArray;
import org.json.JSONObject;

import javax.swing.*;

public class Analytics extends AdminPanel {
	private JPanel analyticsPanel;
	private JPanel analyticsOverviewPanel;
	private JLabel analyticsUsers;
	private JLabel analyticsAdmins;
	private JLabel analyticsImages;
	private JLabel analyticsDoorbells;
	private JPanel analyticsImagePanel;
	private JPanel root;

	/**
	 * Retrieves analytics data from server
	 */
	public void getAnalytics() {
		// Make sure request is not already in progress
		if (connection.isRequestInProgress()) {
			return;
		}

		// Create request
		JSONObject request = new JSONObject();
		request.put("request", "analysis");

		// Run request
		JSONObject response = connection.run(request);
		if (response.getString("response").equals("success")) {
			populateAnalytics(
					response.getInt("users"),
					response.getInt("admins"),
					response.getInt("images"),
					response.getInt("doorbells"),
					response.getJSONArray("imagegraph"));
		}
	}

	/**
	 * Creates pie chart for image distribution in analytics
	 * @param graphData - Data retrieved by the server
	 * @return pie chart
	 */
	private JFreeChart createAnalyticsPieChart(JSONArray graphData) {
		DefaultPieDataset<String> dataset = new DefaultPieDataset<>( );
		for (int i=0; i < graphData.length(); i++) {
			JSONObject doorbellInfo = graphData.getJSONObject(i);
			dataset.setValue(doorbellInfo.getString("id"), doorbellInfo.getInt("count"));
		}

		return ChartFactory.createPieChart(
				"Image Distribution by Doorbell",
				dataset,
				true,
				true,
				false);
	}

	/**
	 * Populates the analytics panel
	 * @param users - Total number of users
	 * @param admins - Total number of admins
	 * @param images - Total number of images
	 * @param doorbells - Total number of doorbells
	 * @param imageGraphData - Image distribution graph data
	 */
	private void populateAnalytics(int users, int admins, int images, int doorbells, JSONArray imageGraphData) {
		// Set overview panel
		analyticsUsers.setText("Total Users: " + users);
		analyticsAdmins.setText("Total Admins: " + admins);
		analyticsImages.setText("Total Images: " + images);
		analyticsDoorbells.setText("Total Doorbells: " + doorbells);

		// Image distribution pie chart
		JFreeChart chart = createAnalyticsPieChart(imageGraphData);
		ChartPanel chartPanel = new ChartPanel(chart);
		chartPanel.setVisible(true);
		analyticsImagePanel.removeAll();
		analyticsImagePanel.add(chartPanel);
		analyticsImagePanel.updateUI();
	}
}
