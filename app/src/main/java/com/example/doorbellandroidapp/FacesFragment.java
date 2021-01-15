package com.example.doorbellandroidapp;

import android.Manifest;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.media.Image;
import android.os.AsyncTask;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Handler;
import android.os.Message;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.util.Base64;
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

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.List;


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
	private ImageView ivAddFace, ivNewFace;
	private Spinner selectDoorbellFaces, chooseDoorbell;

	private boolean pictureTaken;
	private Bitmap newFaceBitmap;

	private View view;

	private ProgressDialog progressDialog;
	Dialog dialog;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		view =  inflater.inflate(R.layout.fragment_faces, container, false);
		Log.d(TAG, "onCreate: started");

		preferences= PreferenceManager.getDefaultSharedPreferences(getContext());
		currentUser= preferences.getString("currentUser",null);
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

		Log.d(TAG, "onCreateView: loop exited");
		return view;
	}

	void loadingPopUp(){
		progressDialog = new ProgressDialog(getContext());
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
						Thread.sleep(30);
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

	public void showPopup() {
		pictureTaken = false;
		final EditText etEditImageName;
		final ImageView ivAddPicture;
		Button btnAddNewFace, btnCancelAddNewFace;
		dialog.setContentView(R.layout.addfacepopup);


		etEditImageName = (EditText) dialog.findViewById(R.id.etEditImageName);
		ivAddPicture = dialog.findViewById(R.id.ivAddPicture);
		ivNewFace = dialog.findViewById(R.id.ivNewFace);
		pictureTaken = false;
		btnAddNewFace = dialog.findViewById(R.id.btnAddNewFace);
		btnCancelAddNewFace = dialog.findViewById(R.id.btnCancelAddNewFace);


		chooseDoorbell = dialog.findViewById(R.id.spinnerAddID);
		ArrayAdapter<String> adapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_spinner_dropdown_item, doorbells);
		chooseDoorbell.setAdapter(adapter);
		chooseDoorbell.setOnItemSelectedListener(this);

		etEditImageName.setHint("New Face");
		ivAddPicture.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Toast.makeText(getContext(), "Taking Picture", Toast.LENGTH_SHORT).show();
				//Requests for camera runtime permission
				if(ContextCompat.checkSelfPermission(getContext(), Manifest.permission.CAMERA)!= PackageManager.PERMISSION_GRANTED ){
					ActivityCompat.requestPermissions(getActivity(),new String[]{Manifest.permission.CAMERA},100);
				}
				else{
					Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
					startActivityForResult(intent,100);
				}
			}
		});
		btnAddNewFace.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				String newFaceName = etEditImageName.getText().toString();
				if (newFaceName.equals("") || newFaceName==null){
					Toast.makeText(getContext(), "Put a name to the face!", Toast.LENGTH_SHORT).show();
				}
				else{
					if (!pictureTaken){
						Toast.makeText(getContext(), "Make sure to take a picture for the face!", Toast.LENGTH_SHORT).show();
						
					}
					else{
						Toast.makeText(getContext(), "New Face Added", Toast.LENGTH_SHORT).show();
						addFace(newFaceBitmap,newFaceName, doorbellIDs.get(chooseDoorbell.getSelectedItemPosition()));
					}
				}
			}
		});
		btnCancelAddNewFace.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				dialog.dismiss();
			}
		});

		dialog.show();
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (requestCode==100){
			newFaceBitmap = (Bitmap) data.getExtras().get("data");
			ivNewFace.setImageBitmap(newFaceBitmap);
			pictureTaken = true;
		}
	}

	public void addFace(Bitmap newFaceBitmap, String newFaceName, String doorbellID){
		String newFace = bitmapToString(newFaceBitmap);
		// Client to handle login response from server
		Client client = new Client(getActivity()) {
			@Override
			public void handleResponse(JSONObject response) throws JSONException {
				switch (response.getString("response")) {
					case "success":
						SharedPreferences preferences= PreferenceManager.getDefaultSharedPreferences(getContext());
						preferences.edit().putString("currentTask","Face Added").apply();
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
			request.put("request","addface");
			request.put("username", preferences.getString("currentUser",null));
			request.put("personname", newFaceName);
			// TODO make not hardcoded
			request.put("doorbellID",doorbellID);
			request.put("image", newFace);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		// Set request and start connection
		client.setRequest(request);
		client.start();

	}
	public String bitmapToString(Bitmap bitmap){
		ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
		bitmap.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream);
		byte[] byteArray = byteArrayOutputStream .toByteArray();
		return Base64.encodeToString(byteArray,Base64.DEFAULT);
	}

	@Override
	public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
		Log.d(TAG, "onItemSelected: "+parent.getId());
		if (parent.getId()==R.id.spinnerID){
			String currentID = doorbellIDs.get(position);
			Toast.makeText(getContext(), currentID, Toast.LENGTH_SHORT).show();
			loadingPopUp();
			// TODO pass in currentID
			loadImages(currentID);
		}
	}

	@Override
	public void onNothingSelected(AdapterView<?> parent) {

	}

	public void populateSpinner() {
		ArrayAdapter<String> adapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_spinner_dropdown_item,doorbells);
		selectDoorbellFaces.setAdapter(adapter);
		selectDoorbellFaces.setOnItemSelectedListener(this);
	}

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
}
