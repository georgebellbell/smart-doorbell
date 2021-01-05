package com.example.doorbellandroidapp;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

import static android.content.ContentValues.TAG;

public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.ViewHolder> {

	private ArrayList<String> mImageNames = new ArrayList<>();
	private ArrayList<String> mImages = new ArrayList<>();
	private Context mContext;

	/**
	 * initialises variables for view holder
	 * @param mContext current context of the application, should be faces fragment
	 * @param mImageNames name of each image being displayed
	 * @param mImages each image being displayed
	 */
	public RecyclerViewAdapter( Context mContext, ArrayList<String> mImageNames, ArrayList<String> mImages) {
		this.mImageNames = mImageNames;
		this.mImages = mImages;
		this.mContext = mContext;
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
		return holder;
	}

	/**
	 * adds the image and image name to the new view holder
	 * @param holder view holder created in onCreateViewHolder
	 * @param position current image to be displayed
	 */
	@Override
	public void onBindViewHolder(@NonNull ViewHolder holder, final int position) {
		Log.d(TAG, "onBindViewHolder: called.");

		Glide.with(mContext)
				.asBitmap()
				.load(mImages.get(position))
				.into(holder.image);
		holder.imageName.setText(mImageNames.get(position));

		holder.parentLayout.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Log.d(TAG, "onClick: clicked on: " + mImageNames.get(position));

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

		public ViewHolder(@NonNull View itemView) {
			super(itemView);
			image = itemView.findViewById(R.id.image);
			imageName = itemView.findViewById(R.id.image_name);
			parentLayout = itemView.findViewById(R.id.parent_layout);
		}
	}
}
