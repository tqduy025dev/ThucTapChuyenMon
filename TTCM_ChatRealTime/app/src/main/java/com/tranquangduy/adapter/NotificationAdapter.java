package com.tranquangduy.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.tranquangduy.model.Notification;
import com.tranquangduy.model.Post;
import com.tranquangduy.model.User;
import com.tranquangduy.ttcm_chatrealtime.R;

import java.util.List;

public class NotificationAdapter extends RecyclerView.Adapter<NotificationAdapter.NotiViewHolder> {
    private Context mContext;
    private List<Notification> mNotifications;

    public NotificationAdapter(Context mContext, List<Notification> mNotifications) {
        this.mContext = mContext;
        this.mNotifications = mNotifications;
    }

    @NonNull
    @Override
    public NotificationAdapter.NotiViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_notification,parent,false);


        return new NotiViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull NotificationAdapter.NotiViewHolder holder, int position) {
        Notification notification = mNotifications.get(position);

        holder.content.setText(notification.getText());
        getUserInfo(holder.imgAvt, holder.userName, notification.getUserid());


        if (notification.getIspost()) {
            holder.imgPost.setVisibility(View.VISIBLE);

            getImgPost(holder.imgPost, notification.getPostid());
        } else {
            holder.imgPost.setVisibility(View.GONE);
        }


    }

    private void getImgPost(final ImageView imgPost, String postID) {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference()
                .child("Posts").child(postID);

        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Post post = dataSnapshot.getValue(Post.class);
                Glide.with(mContext).load(post.getPostimage()).into(imgPost);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void getUserInfo(final ImageView imgUser, final TextView userName, String userID ) {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference()
                .child("Users").child(userID);

        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                User user = dataSnapshot.getValue(User.class);
                Glide.with(mContext).load(user.getImageUrl()).into(imgUser);
                userName.setText(user.getUserName());
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });

    }

    @Override
    public int getItemCount() {
        return mNotifications.size();
    }

    public class NotiViewHolder extends RecyclerView.ViewHolder {
        ImageView imgAvt;
        ImageView imgPost;
        TextView userName;
        TextView content;



        public NotiViewHolder(@NonNull View itemView) {
            super(itemView);

            imgAvt = itemView.findViewById(R.id.imgItem_notification_avatar);
            imgPost = itemView.findViewById(R.id.imgItem_notification_post);
            userName = itemView.findViewById(R.id.txtItem_notification_userName);
            content = itemView.findViewById(R.id.txtItem_notification_content);

        }
    }
}
