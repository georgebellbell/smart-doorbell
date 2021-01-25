/*
 * @author George Bell
 * @version 1.0
 * @since 24/01/2021
 */

package com.example.doorbellandroidapp;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.preference.PreferenceManager;
import android.util.Base64;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;

/**
 * A class to keep useful methods that are required in more than one place
 */
public class Helper {
	/**
	 * Converts pictures taken from bitmaps into base 64 strings to be sent and stored via the server
	 * @param bitmap bitmap of image taken
	 * @return string equivalent of the bitmap
	 */
	public static String bitmapToString(Bitmap bitmap){
		ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
		bitmap.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream);
		byte[] byteArray = byteArrayOutputStream .toByteArray();
		return Base64.encodeToString(byteArray,Base64.DEFAULT);
	}

	/**
	 *
	 * @param activity what the user is currently doing
	 * @param location where the user is currently located
	 */
	public static void refresh(Activity activity, String location) {
		SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(activity);
		preferences.edit().putString("currentTask", location).apply();
		activity.finish();
		activity.startActivity(activity.getIntent());
	}

	/**
	 * Clears user data and sends them to the home page
	 * @param activity current location of user
	 */
	public static void logout(final Activity activity){
		// Client to handle login response from server
		Client client = new Client(activity) {
			@Override
			public void handleResponse(JSONObject response) throws JSONException {
				switch (response.getString("response")) {
					case "success":
						SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(activity);
						preferences.edit().clear().apply();
						activity.finish();
						Intent intent = new Intent(activity, LoginActivity.class);
						activity.startActivity(intent);
						break;
					case "fail":
						Toast.makeText(activity, "FATAL LOGOUT ERROR", Toast.LENGTH_SHORT).show();
						break;
				}
			}
		};

		// JSON Request object
		JSONObject request = new JSONObject();
		try {
			request.put("request","logout");
		} catch (JSONException e) {
			e.printStackTrace();
		}
		// Set request and start connection
		client.setRequest(request);
		client.start();
	}

	/**
	 * Contacts server and deletes that face from database before refreshing page
	 * @param ID identifier for given face
	 */
	public static void deleteFace(Integer ID, final Activity mActivity, final String location) {
		// Client to handle response from server
		Client client = new Client(mActivity) {
			@Override
			public void handleResponse(JSONObject response) throws JSONException {
				switch (response.getString("response")) {
					case "success":
						Helper.refresh(mActivity, location);
						break;
					case "fail":
						Toast.makeText(mActivity, "FAILED TO DELETE FACE", Toast.LENGTH_SHORT).show();
						break;
				}
			}
		};

		// JSON Request object
		JSONObject request = new JSONObject();
		try {
			request.put("request","deleteface");
			request.put("id", ID);
		} catch (JSONException e) {
			e.printStackTrace();
		}

		// Set request and start connection
		client.setRequest(request);
		client.start();
	}
}
