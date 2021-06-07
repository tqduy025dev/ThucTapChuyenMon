package com.tranquangduy.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.tranquangduy.model.Post;
import com.tranquangduy.ttcm_chatrealtime.R;

import java.util.List;

public class PhotoAdapter extends RecyclerView.Adapter<PhotoAdapter.PhotoViewHolder> {

    private Context mContext;
    private List<Post> mPost;

    public PhotoAdapter(Context mContext, List<Post> mPosts) {
        this.mContext = mContext;
        this.mPost = mPosts;
    }

    @NonNull
    @Override
    public PhotoAdapter.PhotoViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.item_photo, parent, false);
        return new PhotoAdapter.PhotoViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PhotoAdapter.PhotoViewHolder holder, int position) {
        final Post post = mPost.get(position);

        Glide.with(mContext).load(post.getPostimage()).into(holder.imgPostPhoto);
    }

    @Override
    public int getItemCount() {
        return mPost.size();
    }

    public class PhotoViewHolder extends RecyclerView.ViewHolder {
        ImageView imgPostPhoto;

        public PhotoViewHolder(@NonNull View itemView) {
            super(itemView);

            imgPostPhoto = itemView.findViewById(R.id.imgItem_postPhoto);
        }
    }
}
