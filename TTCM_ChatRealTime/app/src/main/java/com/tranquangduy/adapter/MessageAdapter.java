package com.tranquangduy.adapter;

import android.content.Context;

import android.util.Log;
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
import com.tranquangduy.model.Message;
import com.tranquangduy.ttcm_chatrealtime.R;


import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;


public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.MessageViewHolder>{
    public static final int MSG_TYPE_LEFT = 0;
    public static final int MSG_TYPE_RIGHT = 1;

    private Context mContext;
    private List<Message> mMessage;
    private String imgURL;

    FirebaseUser firebaseUser;

    public MessageAdapter(Context mContext, List<Message> mMessage, String imgURL) {
        this.mContext = mContext;
        this.mMessage = mMessage;
        this.imgURL = imgURL;
    }

    @NonNull
    @Override
    public MessageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == MSG_TYPE_RIGHT) {
            View view = LayoutInflater.from(mContext).inflate(R.layout.item_chat_right, parent, false);
            return new MessageAdapter.MessageViewHolder(view);
        } else {
            View view = LayoutInflater.from(mContext).inflate(R.layout.item_chat_left, parent, false);
            return new MessageAdapter.MessageViewHolder(view);
        }

    }

    @Override
    public void onBindViewHolder(@NonNull MessageViewHolder holder, int position) {
        Message msg = mMessage.get(position);
        holder.txtShowMessage.setText(msg.getMessage());
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(msg.getTime());
        SimpleDateFormat dateFormat = new SimpleDateFormat("hh:mm", Locale.getDefault());

        holder.txtTime.setText(dateFormat.format(calendar.getTime()));

//        Glide.with(mContext).load(imgURL).into(holder.imgAvatar); // sự cố từ đây

    }




    @Override
    public int getItemCount() {
        return mMessage.size();
    }


    public class MessageViewHolder extends RecyclerView.ViewHolder {
        TextView txtShowMessage;
        ImageView imgAvatar;
        TextView txtTime;

        public MessageViewHolder(@NonNull View itemView) {
            super(itemView);
            txtShowMessage = (TextView) itemView.findViewById(R.id.txtItem_chat_messageContent);
            imgAvatar =  (ImageView) itemView.findViewById(R.id.imgItem_chat_avatar);
            txtTime = (TextView) itemView.findViewById(R.id.txtItem_chat_time);;
        }
    }

    @Override
    public int getItemViewType(int position) {
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        if(mMessage.get(position).getSender().equals(firebaseUser.getUid())){
            return MSG_TYPE_RIGHT;
        }else {
            return MSG_TYPE_LEFT;
        }
    }
}
