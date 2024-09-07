package com.tranquangduy.adapter;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.tranquangduy.model.Post;
import com.tranquangduy.ttcm_chatrealtime.PostDetailActivity;
import com.tranquangduy.ttcm_chatrealtime.R;

import java.util.List;

import static android.content.Context.MODE_PRIVATE;

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

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, PostDetailActivity.class);
                intent.putExtra("postID", post.getPostid());
                mContext.startActivity(intent);
            }
        });


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
