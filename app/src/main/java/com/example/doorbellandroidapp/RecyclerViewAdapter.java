package com.example.doorbellandroidapp;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.preference.PreferenceManager;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

import static android.content.ContentValues.TAG;

public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.ViewHolder> {

	private ArrayList<String> mImageNames = new ArrayList<>();
	private ArrayList<String> mImages = new ArrayList<>();
	private ArrayList<Integer> mImageIDs = new ArrayList<>();
	private Context mContext;
	private Activity mActivity;
	Dialog dialog;

	/**
	 * initialises variables for view holder
	 * @param mContext current context of the application, should be faces fragment
	 * @param mImageNames name of each image being displayed
	 * @param mImages each image being displayed
	 */
	public RecyclerViewAdapter( Context mContext, Activity mActivity, ArrayList<String> mImageNames, ArrayList<String> mImages, ArrayList<Integer> mImageIDs) {
		this.mImageNames = mImageNames;
		this.mImages = mImages;
		this.mImageIDs = mImageIDs;
		this.mContext = mContext;
		this.mActivity = mActivity;

	}

	/**
	 * Creates new view holder for next image to be displayed
	 * @return the new view holder
	 */
	@NonNull
	@Override
	public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
		View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_listitem,parent, false);
		ViewHolder holder = new ViewHolder(view);
		dialog = new Dialog(mContext);
		return holder;
	}

	/**
	 * adds the image and image name to the new view holder
	 * @param holder view holder created in onCreateViewHolder
	 * @param position current image to be displayed
	 */
	@Override
	public void onBindViewHolder(@NonNull final ViewHolder holder, final int position) {
		Log.d(TAG, "onBindViewHolder: called.");
		byte[] decodedString = Base64.decode(mImages.get(position), Base64.DEFAULT );
		final Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString,0, decodedString.length);

		Glide.with(mContext)
				.asBitmap()
				.load(decodedByte)
				.into(holder.image);
		holder.imageName.setText(mImageNames.get(position));

		holder.imageID = mImageIDs.get(position);

		holder.ivEdit.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Log.d(TAG, "onClick: clicked on: " + mImageNames.get(position));

				showPopup(holder, decodedByte);

				Toast.makeText(mContext, mImageNames.get(position), Toast.LENGTH_SHORT).show();
			}
		});
	}

	@Override
	public int getItemCount() {
		return mImageNames.size();
	}

	public class ViewHolder extends RecyclerView.ViewHolder{

		CircleImageView image;
		TextView imageName;
		RelativeLayout parentLayout;
		ImageView ivEdit;
		Integer imageID;

		public ViewHolder(@NonNull View itemView) {
			super(itemView);
			image = itemView.findViewById(R.id.ivPopupImage);
			imageName = itemView.findViewById(R.id.image_name);
			parentLayout = itemView.findViewById(R.id.parent_layout);
			ivEdit = itemView.findViewById(R.id.ivEdit);
		}
	}
	public void showPopup(final ViewHolder holder, Bitmap img) {
		TextView txtClose;
		final EditText etEditImageName;
		ImageView ivPopupImage, ivDeleteImage;
		Button btnSaveAndClose;
		dialog.setContentView(R.layout.popup_face);

		btnSaveAndClose = (Button) dialog.findViewById(R.id.btnAddNewFace);
		txtClose = (TextView) dialog.findViewById(R.id.txtClose);
		ivPopupImage = (ImageView) dialog.findViewById(R.id.ivPopupImage);
		ivDeleteImage = (ImageView) dialog.findViewById(R.id.ivPopUpDelete);
		etEditImageName = (EditText) dialog.findViewById(R.id.etEditImageName);

		etEditImageName.setHint(holder.imageName.getText().toString());
		ivPopupImage.setImageBitmap(img);

		txtClose.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				dialog.dismiss();
			}
		});
		btnSaveAndClose.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				holder.imageName.setText(etEditImageName.getText().toString());
				changeName(holder.imageID, etEditImageName.getText().toString());
				dialog.dismiss();
			}
		});
		ivDeleteImage.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Toast.makeText(mContext, "face deleted", Toast.LENGTH_SHORT).show();
				deleteFace(holder.imageID);
				dialog.dismiss();
			}
		});
		dialog.show();
	}

	void deleteFace(Integer ID){
		// Client to handle response from server
		Client client = new Client(mActivity) {
			@Override
			public void handleResponse(JSONObject response) throws JSONException {
				switch (response.getString("response")) {
					case "success":
						SharedPreferences preferences= PreferenceManager.getDefaultSharedPreferences(mContext);
						preferences.edit().putString("currentTask","Face Deleted").apply();
						mActivity.finish();
						mActivity.startActivity(mActivity.getIntent());
						// TODO take user back to faces page rather than the main page
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

	void changeName(Integer ID, String name){
		// Client to handle response from server
		Client client = new Client(mActivity) {
			@Override
			public void handleResponse(JSONObject response) throws JSONException {
				switch (response.getString("response")) {
					case "success":
						mActivity.finish();
						mActivity.startActivity(mActivity.getIntent());
						// TODO take user back to faces page rather than the main page
						break;
					case "fail":
						// TODO display toast message
						break;
				}
			}
		};

		// JSON Request object
		JSONObject request = new JSONObject();
		try {
			request.put("request","renameface");
			request.put("id", ID);
			request.put("name", name);
		} catch (JSONException e) {
			e.printStackTrace();
		}

		// Set request and start connection
		client.setRequest(request);
		client.start();
	}
}
