package com.example.doorbellandroidapp;

import android.Manifest;
import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.util.Log;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;


public class FacesFragment extends Fragment implements AdapterView.OnItemSelectedListener {
	private static final String TAG = "FacesFragment";

	//vars
	private ArrayList<String> mNames = new ArrayList<>();
	private ArrayList<String> mImages = new ArrayList<>();
	private ArrayList<Integer> mImageIDs = new ArrayList<>();
	private ArrayList<String> doorbells = new ArrayList<>();
	private ArrayList<String> doorbellIDs = new ArrayList<>();

	private SharedPreferences preferences;
	private String currentUser;
	private TextView tvFaces;
	private ImageView ivAddFace, ivNewFace, ivInfo;
	private Spinner selectDoorbellFaces, chooseDoorbell;

	private boolean pictureTaken;
	private Bitmap newFaceBitmap;

	private View view;

	Dialog dialog;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		view =  inflater.inflate(R.layout.fragment_faces, container, false);
		Log.d(TAG, "onCreate: started");

		preferences= PreferenceManager.getDefaultSharedPreferences(getContext());
		currentUser= preferences.getString("currentUser",null);
		ivInfo = view.findViewById(R.id.ivInfo);
		ivAddFace = view.findViewById(R.id.ivAddFace);
		tvFaces = view.findViewById(R.id.tvFaces);
		tvFaces.setText(currentUser+"'s Faces");

		selectDoorbellFaces = view.findViewById(R.id.spinnerID);
		// TODO Get ids for that user
		getIDs();

		dialog = new Dialog(getContext());
		// TODO pass in selected id
		//loadImages(selectDoorbellFaces.getSelectedItem().toString());

		ivAddFace.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				showPopup();
			}
		});

		ivInfo.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				InformationPopups.showInformation(getContext(),"faces");
			}
		});

		Log.d(TAG, "onCreateView: loop exited");
		return view;
	}

	// RETRIEVING IDs FOR USER

	/**
	 * calls server and retrieves all IDs linked to that user
	 */
	public void getIDs(){
		Client client = new Client(getActivity()) {
			@Override
			public void handleResponse(JSONObject response) throws JSONException {
				switch (response.getString("response")) {
					case "success":
						JSONArray jsonArray = response.getJSONArray("doorbells");
						for (int i = 0; i < jsonArray.length() ; i++) {
							doorbells.add(jsonArray.getJSONObject(i).getString("name"));
							doorbellIDs.add(jsonArray.getJSONObject(i).getString("id"));
						}
						populateSpinner();
						break;
					case "fail":
						Toast.makeText(getContext(), "NO DOORBELL ASSIGNED, PLEASE CONTACT ADMIN", Toast.LENGTH_SHORT).show();
						break;
				}
			}
		};

		// JSON Request object
		JSONObject request = new JSONObject();
		try {
			request.put("request","getdoorbells");
		} catch (JSONException e) {
			e.printStackTrace();
		}
		// Set request and start connection
		client.setRequest(request);
		client.start();

	}

	/**
	 * Adds the retrieved IDs to a dropdown menu the user can navigate between
	 */
	public void populateSpinner() {
		ArrayAdapter<String> adapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_spinner_dropdown_item,doorbells);
		selectDoorbellFaces.setAdapter(adapter);
		selectDoorbellFaces.setOnItemSelectedListener(this);
	}

	/**
	 * Gives functionality to spinner, loads the images for the selected item in spinner
	 * @param parent object being observed, in this case the spinner
	 * @param view current app view
	 * @param position current item in spinner selected
	 * @param id identifier for spinner
	 */
	@Override
	public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

		if (parent.getId()==R.id.spinnerID){
			String currentID = doorbellIDs.get(position);
			Toast.makeText(getContext(), currentID, Toast.LENGTH_SHORT).show();
			InformationPopups informationPopups = new InformationPopups();
			informationPopups.loadingPopUp(getContext());
			loadImages(currentID);
		}
	}

	/**
	 * Required method for when nothing is selected
	 * @param parent object being observed, in this case the spinner
	 */
	@Override
	public void onNothingSelected(AdapterView<?> parent) {

	}

	// LOADING ALL IMAGES FOR SPECIFIC ID

	/**
	 * calls server requesting images from database
	 */
	void loadImages(String doorbell){
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
			request.put("doorbellID", doorbell);


		} catch (JSONException e) {
			e.printStackTrace();
		}
		// Set request and start connection
		client.setRequest(request);
		client.start();
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
	 * Adds image URLS and image names to ArrayLists to be added to view holders
	 */
	private void initImageBitmaps(JSONArray images) throws JSONException {
		mImages = new ArrayList<>();
		mNames = new ArrayList<>();
		mImageIDs = new ArrayList<>();
		Log.d(TAG, "initImageBitmaps: preparing bitmaps");
		JSONObject currentImage;
		for (int i = 0; i < images.length() ; i++) {
			currentImage = images.getJSONObject(i);
			Log.d(TAG, "initImageBitmaps: "+currentImage.getString("image"));
			mImages.add(currentImage.getString("image"));
			mNames.add(currentImage.getString("person"));
			mImageIDs.add(currentImage.getInt("id"));
		}

	}

	/**
	 * Finds the recycler view and initialised it with RecyclerViewAdapter
	 * @param view current view of android app
	 */
	private void initRecyclerView(View view){
		Log.d(TAG, "initRecyclerView: init recyclerview.");
		RecyclerView recyclerView = view.findViewById(R.id.recycler_view);
		RecyclerViewAdapter adapter = new RecyclerViewAdapter(getContext(), getActivity(), mNames, mImages, mImageIDs);
		recyclerView.setAdapter(adapter);
		recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
	}

	// ADD FACE POPUP

	/**
	 * Creates popup in FacesFragment for adding a new face to a given doorbell
	 */
	public void showPopup () {

		dialog.setContentView(R.layout.popup_add_face);

		pictureTaken = false;
		final EditText etEditImageName;
		final ImageView ivAddPicture;
		Button btnAddNewFace, btnCancelAddNewFace;

		etEditImageName = (EditText) dialog.findViewById(R.id.etEditImageName);
		etEditImageName.setHint("New Face");

		ivNewFace = dialog.findViewById(R.id.ivNewFace);

		// Uses phone camera to take picture for new face
		ivAddPicture = dialog.findViewById(R.id.ivAddPicture);
		ivAddPicture.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				takePicture();

			}
		});

		// Adds new face to the doorbell for that user, validating to see if required details are given
		btnAddNewFace = dialog.findViewById(R.id.btnAddNewFace);
		btnAddNewFace.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				String newFaceName = etEditImageName.getText().toString();
				validateAddition(newFaceName);
			}
		});

		// Populates spinner in popup with available doorbells to add new faces to
		chooseDoorbell = dialog.findViewById(R.id.spinnerAddID);
		ArrayAdapter<String> adapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_spinner_dropdown_item, doorbells);
		chooseDoorbell.setAdapter(adapter);
		chooseDoorbell.setOnItemSelectedListener(this);

		// Closes popup
		btnCancelAddNewFace = dialog.findViewById(R.id.btnCancelAddNewFace);
		btnCancelAddNewFace.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				dialog.dismiss();
			}
		});

		dialog.show();
	}

	/**
	 * Requests permission to use camera and if given, take photo
	 */
	public void takePicture () {
		//Requests for camera runtime permission
		if (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
			ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.CAMERA}, 100);
		} else {
			Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
			startActivityForResult(intent, 100);
		}
	}

	/**
	 * In response to taking picture, retrieve image bitmap and set it to popup
	 * @param requestCode code used to send request
	 * @param resultCode The integer result code returned by the child activitu through its setResult().
	 * @param data data from activity, in this case the picture
	 */
	@Override
	public void onActivityResult ( int requestCode, int resultCode, @Nullable Intent data){
		super.onActivityResult(requestCode, resultCode, data);
		if (requestCode == 100) {
			newFaceBitmap = (Bitmap) data.getExtras().get("data");
			ivNewFace.setImageBitmap(newFaceBitmap);
			pictureTaken = true;
		}
	}

	/**
	 * Checks that new picture has been taken and that an appropriate name has been assigned to it
	 * @param newFaceName name of new face being added
	 */
	public void validateAddition (String newFaceName){

		if (newFaceName.equals("") || newFaceName == null) {
			Toast.makeText(getContext(), "Put a name to the face!", Toast.LENGTH_SHORT).show();
		}
		else if (newFaceName.length()>10) {
			Toast.makeText(getContext(), "Name is too long", Toast.LENGTH_SHORT).show();
		}
		else  {
			if (!pictureTaken) {
				Toast.makeText(getContext(), "Make sure to take a picture for the face!", Toast.LENGTH_SHORT).show();
			} else {
				Toast.makeText(getContext(), "New Face Added", Toast.LENGTH_SHORT).show();
				addFace(newFaceBitmap, newFaceName, doorbellIDs.get(chooseDoorbell.getSelectedItemPosition()));
			}
		}
	}

	/**
	 * Contacts server and adds the new face to the specified doorbell
	 * @param newFaceBitmap bitmap for image taken using camera
	 * @param newFaceName name of new face
	 * @param doorbellID doorbell the face is being added to
	 */
	public void addFace (Bitmap newFaceBitmap, String newFaceName, String doorbellID){
		String newFace = Helper.bitmapToString(newFaceBitmap);
		// Client to handle login response from server
		Client client = new Client(getActivity()) {
			@Override
			public void handleResponse(JSONObject response) throws JSONException {
				switch (response.getString("response")) {
					case "success":
						SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getContext());
						preferences.edit().putString("currentTask", "Face Added").apply();
						getActivity().finish();
						getActivity().startActivity(getActivity().getIntent());
						break;
					case "fail":
						Toast.makeText(getContext(), "NO DOORBELL ASSIGNED, PLEASE CONTACT ADMIN", Toast.LENGTH_SHORT).show();
						break;
				}
			}
		};

		// JSON Request object
		JSONObject request = new JSONObject();
		try {
			request.put("request", "addface");
			request.put("username", preferences.getString("currentUser", null));
			request.put("personname", newFaceName);
			request.put("doorbellID", doorbellID);
			request.put("image", newFace);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		// Set request and start connection
		client.setRequest(request);
		client.start();

	}

}
