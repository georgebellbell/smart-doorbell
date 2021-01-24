/*
 * @author George Bell
 * @version 1.0
 * @since 24/01/2021
 */

package com.example.doorbellandroidapp;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;


/**
 * This class is the template for all the view holders to be created, in this case the faces of a doorbell
 */
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
				EditFacePopup editFacePopup = new EditFacePopup(mContext,mActivity);
				editFacePopup.showPopup(holder, decodedByte);

				Toast.makeText(mContext, mImageNames.get(position), Toast.LENGTH_SHORT).show();
			}
		});
	}

	/**
	 * @return Number of images being retrieved
	 */
	@Override
	public int getItemCount() {
		return mImageNames.size();
	}

	/**
	 * The item apart of the recycler view
	 */
	public class ViewHolder extends RecyclerView.ViewHolder{

		CircleImageView image;
		TextView imageName;
		RelativeLayout parentLayout;
		ImageView ivEdit;
		Integer imageID;

		/**
		 * Assigns variables of class ViewHolder to given objects in layout file
		 * @param itemView current item
		 */
		public ViewHolder(@NonNull View itemView) {
			super(itemView);
			image = itemView.findViewById(R.id.ivPopupImage);
			imageName = itemView.findViewById(R.id.image_name);
			parentLayout = itemView.findViewById(R.id.parent_layout);
			ivEdit = itemView.findViewById(R.id.ivEdit);
		}
	}


}
