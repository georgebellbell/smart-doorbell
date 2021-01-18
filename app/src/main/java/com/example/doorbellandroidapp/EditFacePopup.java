package com.example.doorbellandroidapp;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;


public class EditFacePopup {

	private Dialog dialog;
	private Context mContext;
	private Activity mActivity;

	/**
	 * assigns variables
	 * @param mContext given context
	 * @param mActivity given activity
	 */
	public EditFacePopup(Context mContext, Activity mActivity) {
		this.mContext = mContext;
		this.mActivity = mActivity;
	}

	/**
	 * Creates popup of selected item and you can edit or delete it
	 * @param holder item selected by user from their list of faces
	 * @param img the image associated with that item
	 */
	public void showPopup(final RecyclerViewAdapter.ViewHolder holder, Bitmap img) {
		TextView txtClose;
		final EditText etEditImageName;
		ImageView ivPopupImage, ivDeleteImage;
		Button btnSaveAndClose;
		dialog = new Dialog(mContext);
		dialog.setContentView(R.layout.popup_face);

		etEditImageName = (EditText) dialog.findViewById(R.id.etEditImageName);
		etEditImageName.setHint(holder.imageName.getText().toString());

		ivPopupImage = (ImageView) dialog.findViewById(R.id.ivPopupImage);
		ivPopupImage.setImageBitmap(img);

		// deletes face currently being edited
		ivDeleteImage = (ImageView) dialog.findViewById(R.id.ivPopUpDelete);
		ivDeleteImage.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Toast.makeText(mContext, "face deleted", Toast.LENGTH_SHORT).show();
				deleteFace(holder.imageID);
				dialog.dismiss();
			}
		});

		// closes popup and updates face name to new one, if valid
		btnSaveAndClose = (Button) dialog.findViewById(R.id.btnAddNewFace);
		btnSaveAndClose.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				String newName = etEditImageName.getText().toString();
				if (validateNameChange(newName)){
					holder.imageName.setText(newName);
					changeName(holder.imageID, newName);
					dialog.dismiss();
				}
			}
		});

		// closes popup without saving any of the changes
		txtClose = (TextView) dialog.findViewById(R.id.txtClose);
		txtClose.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				dialog.dismiss();
			}
		});

		dialog.show();
	}

	/**
	 * Checks if the new face name is given and isn't too long
	 * @param newName updated name of face
	 * @return boolean
	 */
	boolean validateNameChange(String newName){
		if (newName.isEmpty() || newName.equals(" ")){
			Toast.makeText(mContext, "Please give valid name", Toast.LENGTH_SHORT).show();
			return false;
		}
		if (newName.length()>10){
			Toast.makeText(mContext, "Name is too long", Toast.LENGTH_SHORT).show();
			return false;
		}
		return true;

	}

	/**
	 * Contacts server and updates that faces name in database
	 * @param ID identifier for given face
	 * @param name new name for given face
	 */
	void changeName(Integer ID, String name) {
		// Client to handle response from server
		Client client = new Client(mActivity) {
			@Override
			public void handleResponse(JSONObject response) throws JSONException {
				switch (response.getString("response")) {
					case "success":
						Toast.makeText(mContext, "Name changed", Toast.LENGTH_SHORT).show();
						Helper.refresh(mActivity,"Faces");
						break;
					case "fail":
						Toast.makeText(mContext, "Failed to change name", Toast.LENGTH_SHORT).show();
						break;
				}
			}
		};

		// JSON Request object
		JSONObject request = new JSONObject();
		try {
			request.put("request", "renameface");
			request.put("id", ID);
			request.put("name", name);
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
	void deleteFace(Integer ID) {
		// Client to handle response from server
		Client client = new Client(mActivity) {
			@Override
			public void handleResponse(JSONObject response) throws JSONException {
				switch (response.getString("response")) {
					case "success":
						Helper.refresh(mActivity, "Faces");
						break;
					case "fail":
						Toast.makeText(mContext, "FAILED TO DELETE FACE", Toast.LENGTH_SHORT).show();
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

