package com.example.doorbellandroidapp;

import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.preference.PreferenceManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;


public class FacesFragment extends Fragment {
	private static final String TAG = "FacesFragment";

	//vars
	private ArrayList<String> mNames = new ArrayList<>();
	private ArrayList<String> mImages = new ArrayList<>();

	private SharedPreferences preferences;
	private String currentUser;

	private View view;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		view =  inflater.inflate(R.layout.fragment_faces, container, false);
		Log.d(TAG, "onCreate: started");

		preferences= PreferenceManager.getDefaultSharedPreferences(getContext());
		currentUser= preferences.getString("currentUser",null);

		loadImages();
		Log.d(TAG, "onCreateView: loop exited");
		return view;
	}

	/**
	 * populates the mNames and mImages with names of pictures and  pictures
	 * @param jsonArray jsonArray of faces
	 */
	void populateImages(final JSONArray jsonArray) {
		try {
			initImageBitmaps(jsonArray);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		Log.d(TAG, "handleResponse: images got");
		initRecyclerView(view);
	}

	/**
	 * calls server requesting images from database
	 */
	void loadImages(){
		// Client to handle login response from server
		Client client = new Client(getActivity()) {
			@Override
			public void handleResponse(JSONObject response) throws JSONException {
				switch (response.getString("response")) {
					case "success":
						populateImages(response.getJSONArray("images"));
						break;
					case "fail":
						Toast.makeText(getContext(), "FAILURE TO GET IMAGES", Toast.LENGTH_SHORT).show();
						break;
				}
			}
		};

		// JSON Request object
		JSONObject request = new JSONObject();
		try {
			request.put("request","faces");
			//request.put("username", username);
			request.put("username", "unique ID");

		} catch (JSONException e) {
			e.printStackTrace();
		}
		// Set request and start connection
		client.setRequest(request);
		client.start();
	}

	/**
	 * Adds image URLS and image names to ArrayLists to be added to view holders
	 */
	private void initImageBitmaps(JSONArray images) throws JSONException {
		Log.d(TAG, "initImageBitmaps: preparing bitmaps");
		JSONObject currentImage;
		for (int i = 0; i < images.length() ; i++) {
			currentImage = images.getJSONObject(i);
			Log.d(TAG, "initImageBitmaps: "+currentImage.getString("image"));
			mImages.add(currentImage.getString("image"));
			mNames.add(currentImage.getString("person"));
		}

	}

	/**
	 * Finds the recycler view and initialised it with RecyclerViewAdapter
	 * @param view current view of android app
	 */
	private void initRecyclerView(View view){
		Log.d(TAG, "initRecyclerView: init recyclerview.");
		RecyclerView recyclerView = view.findViewById(R.id.recycler_view);
		RecyclerViewAdapter adapter = new RecyclerViewAdapter(getActivity(), mNames, mImages);
		recyclerView.setAdapter(adapter);
		recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));


	}
}