package com.tranquangduy.adapter;

import android.content.Context;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

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
        if (viewType == MSG_TYPE_LEFT) {
            View view = LayoutInflater.from(mContext).inflate(R.layout.item_chat_left, parent, false);
            return new MessageAdapter.MessageViewHolder(view);
        } else {
            View view = LayoutInflater.from(mContext).inflate(R.layout.item_chat_right, parent, false);
            return new MessageAdapter.MessageViewHolder(view);
        }

    }

    @Override
    public void onBindViewHolder(@NonNull MessageViewHolder holder, int position) {
        Message msg = mMessage.get(position);

        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(msg.getTime());
        SimpleDateFormat dateFormat = new SimpleDateFormat("hh:mm", Locale.getDefault());



        if(msg.getType().equals("text")){
            holder.txtShowMessage.setVisibility(View.VISIBLE);
            holder.txtShowMessage.setText(msg.getMessage());
            holder.txtTime.setVisibility(View.VISIBLE);
            holder.txtTime.setText(dateFormat.format(calendar.getTime()));

            holder.txtTimeImg.setVisibility(View.GONE);
            holder.imgMessage.setVisibility(View.GONE);
        }else {
            holder.imgMessage.setVisibility(View.VISIBLE);
            Glide.with(mContext).load(msg.getMessage()).into(holder.imgMessage);
            holder.txtTimeImg.setVisibility(View.VISIBLE);
            holder.txtTimeImg.setText(dateFormat.format(calendar.getTime()));

            holder.txtShowMessage.setVisibility(View.GONE);
            holder.txtTime.setVisibility(View.GONE);

        }


        if(getItemViewType(position) == MSG_TYPE_LEFT){
            Glide.with(mContext).load(imgURL).into(holder.imgAvatar);
        }

        if(getItemViewType(position) == MSG_TYPE_RIGHT){
            if(position == mMessage.size() -1){
                if(msg.getIsseen()){
                    holder.txtIsseen.setText("đã xem");
                }else {
                    holder.txtIsseen.setText("đã nhận");
                }
            }else {
                holder.txtIsseen.setVisibility(View.GONE);
            }

        }




    }




    @Override
    public int getItemCount() {
        return mMessage.size();
    }


    public class MessageViewHolder extends RecyclerView.ViewHolder {
        TextView txtShowMessage;
        ImageView imgAvatar, imgMessage;
        TextView txtTime;
        TextView txtTimeImg;
        TextView txtIsseen;

        public MessageViewHolder(@NonNull View itemView) {
            super(itemView);
            imgAvatar =  itemView.findViewById(R.id.imgItem_chat_avatar);
            txtIsseen =  itemView.findViewById(R.id.txtItem_chat_seen);
            txtShowMessage = itemView.findViewById(R.id.txtItem_chat_messageContent);
            imgMessage = itemView.findViewById(R.id.imgItem_chat_message);
            txtTime = itemView.findViewById(R.id.txtItem_chat_time);
            txtTimeImg = itemView.findViewById(R.id.txtItem_chat_timeImg);

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
