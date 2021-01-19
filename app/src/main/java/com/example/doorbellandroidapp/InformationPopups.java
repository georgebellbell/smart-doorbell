package com.example.doorbellandroidapp;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;


public class InformationPopups {

	private static Dialog dialog;

	private static Dialog dialogDelete;

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
						Thread.sleep(40);
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

	public static void deleteConfirmation(final Activity activity, final Context context){

		Button btnConfirmDeletion, btnCancelDeletion;
		dialogDelete = new Dialog(context);
		dialogDelete.setContentView(R.layout.popup_confirmation);

		btnConfirmDeletion = dialogDelete.findViewById(R.id.btnConfirmDeletion);
		btnConfirmDeletion.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Helper.logout(activity);
				deleteAccount(activity);
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

	public static void logoutConfirmation(final Activity activity){

		Button btnConfirmDeletion, btnCancelDeletion;
		dialogLogout = new Dialog(activity);
		dialogLogout.setContentView(R.layout.popup_logout);

		btnConfirmDeletion = dialogLogout.findViewById(R.id.btnConfirmLogout);
		btnConfirmDeletion.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
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

}

