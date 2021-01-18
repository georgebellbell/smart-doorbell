package com.example.doorbellandroidapp;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.view.View;
import android.widget.TextView;



public class InformationPopups {

	private static Dialog dialog;

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
						Thread.sleep(35);
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

}

