package com.example.doorbellandroidapp;

import android.app.Dialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;


public class InformationDialog {

	private static Dialog dialog;

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
		}

		TextView tvClose = dialog.findViewById(R.id.tvClose);

		tvClose.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				dialog.dismiss();
			}
		});

		dialog.show();
	}
}

