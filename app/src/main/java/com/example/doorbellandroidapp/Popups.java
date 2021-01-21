package com.example.doorbellandroidapp;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;


public class Popups {

	private static Dialog dialog;

	private static Dialog dialogDelete;

	private static Dialog dialogRemoveDoorbell;

	private static Dialog dialogLogout;

	private ProgressDialog progressDialog;

	/**
	 * Creates information popup for a given page
	 * @param context current location of user
	 * @param location current fragment to display appropriate information
	 */
	public static void showInformation(Context context, String location) {
		dialog = new Dialog(context);

		switch (location) {
			case "home":
				dialog.setContentView(R.layout.popup_info_home);
				break;
			case "faces":
				dialog.setContentView(R.layout.popup_info_faces);
				break;
			case "settings":
				dialog.setContentView(R.layout.popup_info_settings);
				break;
			case "doorbell":
				dialog.setContentView(R.layout.popup_info_add_doorbell);
			case "server":
				dialog.setContentView(R.layout.popup_server_error);

		}

		// closes popup
		TextView tvClose = dialog.findViewById(R.id.tvClose);
		tvClose.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				dialog.dismiss();
			}
		});

		dialog.show();
	}

	/**
	 * creates a progress popup while necessary data is collected
	 * @param context current location of user
	 */
	public void loadingPopUp(Context context){
		progressDialog = new ProgressDialog(context);
		progressDialog.setMax(100);
		progressDialog.setMessage("Please wait...");
		progressDialog.setTitle("Loading Faces");
		progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
		progressDialog.show();

		new Thread(new Runnable() {
			@Override
			public void run() {
				while (progressDialog.getProgress() <= progressDialog.getMax()){
					try {
						Thread.sleep(15);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
					progressDialog.incrementProgressBy(1);
					if (progressDialog.getProgress()==progressDialog.getMax()){
						progressDialog.dismiss();
					}

				}
			}
		}).start();

	}

	/**
	 * Creates popup asking if user wants actually delete account
	 * @param activity current location of user
	 */
	public static void deleteConfirmation(final Activity activity){

		Button btnConfirmDeletion, btnCancelDeletion;
		dialogDelete = new Dialog(activity);
		dialogDelete.setContentView(R.layout.confirmation_delete);

		btnConfirmDeletion = dialogDelete.findViewById(R.id.btnConfirmDeletion);
		btnConfirmDeletion.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				deleteAccount(activity);
				Helper.logout(activity);
			}
		});

		btnCancelDeletion = dialogDelete.findViewById(R.id.btnCancelDeletion);
		btnCancelDeletion.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				dialogDelete.dismiss();
			}
		});

		dialogDelete.show();
	}

	/**
	 * contacts server and removes that user from the database before sending app user back to login page
	 * @param activity current location in app
	 */
	public static void deleteAccount(final Activity activity){
		// Client to handle sign up response from server
		Client client = new Client(activity) {
			@Override
			public void handleResponse(JSONObject response) throws JSONException {
				switch (response.getString("response")) {
					case "success":
						Toast.makeText(activity, "Account Deleted", Toast.LENGTH_SHORT).show();
						break;
					case "fail":
						Toast.makeText(activity, "Account Not Deleted", Toast.LENGTH_SHORT).show();
						break;
				}
			}
		};

		// JSON Request object
		JSONObject request = new JSONObject();
		try {
			request.put("request","deleteaccount");
		} catch (JSONException e) {
			e.printStackTrace();
		}

		// Set request and start connection
		client.setRequest(request);
		client.start();
	}

	/**
	 * creates dialog asking user if they actually want to remove the selected doorbell from their account
	 * @param doorbellID Identifier for current doorbell
	 * @param doorbellName Name of current doorbell
	 * @param activity current loaction in app
	 */
	public static void removeDoorbellConfirmation(final String doorbellID, final String doorbellName, final Activity activity){
		Button btnConfirmDeletion, btnCancelDeletion;
		TextView tvCurrentDoorbell;
		dialogRemoveDoorbell = new Dialog(activity);
		dialogRemoveDoorbell.setContentView(R.layout.confirmation_remove_doorbell);

		tvCurrentDoorbell = dialogRemoveDoorbell.findViewById(R.id.tvCurrentDoorbell);
		tvCurrentDoorbell.setText(doorbellName);

		btnConfirmDeletion = dialogRemoveDoorbell.findViewById(R.id.btnConfirmRemove);
		btnConfirmDeletion.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				removeDoorbell(doorbellID,activity);
				dialogRemoveDoorbell.dismiss();
			}
		});

		btnCancelDeletion = dialogRemoveDoorbell.findViewById(R.id.btnCancelRemove);
		btnCancelDeletion.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				dialogRemoveDoorbell.dismiss();
			}
		});

		dialogRemoveDoorbell.show();
	}

	/**
	 * contacts server and removes selected doorbell from their account
	 * @param doorbellID Identifier for selected doorbell
	 * @param activity current location of user
	 */
	public static void removeDoorbell(String doorbellID, final Activity activity){
		// Client to handle sign up response from server
		Client client = new Client(activity) {
			@Override
			public void handleResponse(JSONObject response) throws JSONException {
				switch (response.getString("response")) {
					case "success":
						Toast.makeText(activity, "Doorbell Removed", Toast.LENGTH_SHORT).show();
						SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(activity);
						preferences.edit().putString("userChoiceSpinner",null).apply();
						Helper.refresh(activity,"settings");
						break;
					case "fail":
						Toast.makeText(activity, "Doorbell could not be removed", Toast.LENGTH_SHORT).show();
						break;
				}
			}
		};

		// JSON Request object
		JSONObject request = new JSONObject();
		try {
			request.put("request","removedoorbell");
			request.put("doorbellID", doorbellID);
		} catch (JSONException e) {
			e.printStackTrace();
		}

		// Set request and start connection
		client.setRequest(request);
		client.start();
	}

	/**
	 * checks if user actually wants to log out via a dialog
	 * @param activity current location of user
	 */
	public static void logoutConfirmation(final Activity activity){

		Button btnConfirmDeletion, btnCancelDeletion;
		dialogLogout = new Dialog(activity);
		dialogLogout.setContentView(R.layout.confirmation_logout);

		btnConfirmDeletion = dialogLogout.findViewById(R.id.btnConfirmLogout);
		btnConfirmDeletion.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				dialogLogout.dismiss();
				Helper.logout(activity);
			}
		});

		btnCancelDeletion = dialogLogout.findViewById(R.id.btnCancelLogout);
		btnCancelDeletion.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				dialogLogout.dismiss();
			}
		});

		dialogLogout.show();
	}
}

