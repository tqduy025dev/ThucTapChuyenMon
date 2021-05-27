package com.tranquangduy.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.tranquangduy.model.Notification;
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
        holder.imgAvt.setImageResource(R.color.black);
        holder.imgPost.setImageResource(R.drawable.ic_add);
        holder.userName.setText(mNotifications.get(position).getUserid());
        holder.content.setText(mNotifications.get(position).getText());


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
