package com.tranquangduy.adapter;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.tranquangduy.model.Notification;
import com.tranquangduy.model.Post;
import com.tranquangduy.model.User;
import com.tranquangduy.ttcm_chatrealtime.MessageActivity;
import com.tranquangduy.ttcm_chatrealtime.PostDetailActivity;
import com.tranquangduy.ttcm_chatrealtime.ProfileActivity;
import com.tranquangduy.ttcm_chatrealtime.R;

import java.util.List;

import static android.content.Context.MODE_PRIVATE;

public class NotificationAdapter extends RecyclerView.Adapter<NotificationAdapter.NotiViewHolder> {
    private final Context mContext;
    private final List<Notification> mNotifications;

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

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (notification.getIspost()) {
                    Intent intent = new Intent(mContext, PostDetailActivity.class);
                    intent.putExtra("postID", notification.getPostid());
                    mContext.startActivity(intent);

                } else if(notification.getIsmessage()){
                    Intent intent = new Intent(mContext, MessageActivity.class);
                    intent.putExtra("userid", notification.getUserid());
                    mContext.startActivity(intent);

                }else {
                    Intent intent = new Intent(mContext, ProfileActivity.class);
                    intent.putExtra("profileID", notification.getUserid());
                    mContext.startActivity(intent);
                }
            }
        });



    }

    private void getImgPost(final ImageView imgPost, String postID) {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference()
                .child("Posts").child(postID);

        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Post post = dataSnapshot.getValue(Post.class);
                Glide.with(mContext).load(post.getPostimage()).into(imgPost);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void getUserInfo(final ImageView imgUser, final TextView userName, String userID ) {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference()
                .child("Users").child(userID);

        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                User user = dataSnapshot.getValue(User.class);
                Glide.with(mContext).load(user.getImageUrl()).into(imgUser);
                userName.setText(user.getUserName());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
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
