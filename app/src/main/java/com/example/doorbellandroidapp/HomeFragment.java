package com.example.doorbellandroidapp;

import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.preference.PreferenceManager;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;


public class HomeFragment extends Fragment {
	private ImageView ivLastFace, ivInfo;
	private Button btnOpenDoor, btnLeaveClosed;
	private TextView tvDoorInformation, tvLastFaceTime, tvLastFace;

	private SharedPreferences preferences;
	private String currentUser;

	private String id;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
							 Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_home, container, false);
		assign(view);
		preferences= PreferenceManager.getDefaultSharedPreferences(getContext());
		currentUser= preferences.getString("currentUser",null);

		InformationPopups informationPopups = new InformationPopups();
		informationPopups.loadingPopUp(getContext());

		loadImage();


		btnOpenDoor.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				tvDoorInformation.setText("You opened the door!");
				contactDoor("open");
			}
		});
		btnLeaveClosed.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				tvDoorInformation.setText("You chose not to open the door");
				contactDoor("close");
			}
		});
		ivInfo.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				InformationPopups.showInformation(getContext(),"home");
			}
		});
		return view;
	}

	/**
	 * assigns variables to objects in layout
	 * @param view current fragment being viewed
	 */
	public void assign(View view){
		ivLastFace = view.findViewById(R.id.ivLastFace);
		btnOpenDoor = view.findViewById(R.id.btnOpenDoor);
		btnLeaveClosed = view.findViewById(R.id.btnLeaveClosed);
		tvDoorInformation = view.findViewById(R.id.tvDoorInformation);
		tvLastFaceTime = view.findViewById(R.id.tvLastFaceTime);
		tvLastFace = view.findViewById(R.id.tvLastFace);
		ivInfo = view.findViewById(R.id.ivInfo);
	}

	/**
	 * contacts server with user decision to either open door, or leave it closed
	 * @param messageToDoor either "open" or "close"
	 */
	private void contactDoor(String messageToDoor){
		Client client = new Client(getActivity()) {
			@Override
			public void handleResponse(JSONObject response) throws JSONException {
				switch (response.getString("response")) {
					case "success":
						Toast.makeText(getContext(), "Woah, your doorbell message in on route", Toast.LENGTH_SHORT).show();
						break;
					case "fail":
						Toast.makeText(getContext(), "FATAL ERROR", Toast.LENGTH_SHORT).show();
						break;
				}
			}
		};
		// JSON Request object
		JSONObject request = new JSONObject();
		try {
			request.put("request","opendoor");
			request.put("message", messageToDoor);
			request.put("doorbellID", id);

		} catch (JSONException e) {
			e.printStackTrace();
		}
		// Set request and start connection
		client.setRequest(request);
		client.start();
	}

	/**
	 * retrieves the most recent face in the database
	 */
	void loadImage(){
		// Client to handle login response from server
		Client client = new Client(getActivity()) {
			@Override
			public void handleResponse(JSONObject response) throws JSONException {
				switch (response.getString("response")) {
					case "success":
						JSONObject image = response.getJSONObject("image");
						String time = response.getString("time");
						String doorbellName = response.getString("doorbellname");
						String personAtDoor = response.getString("person");
						id = response.getString("doorbellID");
						byte[] decodedString = Base64.decode(image.getString("image"),Base64.DEFAULT);
						final Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString,0, decodedString.length);
						ivLastFace.setImageBitmap(decodedByte);
						tvLastFaceTime.setText(time);
						tvLastFace.setText(personAtDoor + " just used the " + doorbellName +" doorbell");
						break;
					case "fail":
						// TODO display error picture?
						tvLastFace.setText("Unknown person is using doorbell");
						Toast.makeText(getContext(), "FAILURE TO GET IMAGES", Toast.LENGTH_SHORT).show();

						break;
				}
			}
		};

		// JSON Request object
		JSONObject request = new JSONObject();
		try {
			request.put("request","lastface");

		} catch (JSONException e) {
			e.printStackTrace();
		}
		// Set request and start connection
		client.setRequest(request);
		client.start();
	}
}
