package com.tranquangduy.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.tranquangduy.model.Message;
import com.tranquangduy.ttcm_chatrealtime.R;


import java.util.List;

public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.MessageViewHolder>{

    private Context mContext;
    private List<Message> mMessage;
    private boolean isFragment;

    public MessageAdapter(Context mContext, List<Message> mMessage, boolean isFragment) {
        this.mContext = mContext;
        this.mMessage = mMessage;
        this.isFragment = isFragment;
    }

    @NonNull
    @Override
    public MessageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view =  LayoutInflater.from(parent.getContext()).inflate(R.layout.item_message,parent, false);
        return new MessageViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MessageViewHolder holder, int position) {
        holder.imgAvt.setImageResource(R.drawable.ic_message);
        holder.userName.setText(mMessage.get(position).getFrom());
        holder.content.setText(mMessage.get(position).getContent());
        holder.time.setText(mMessage.get(position).getTime() + "");
    }

    @Override
    public int getItemCount() {
        return mMessage.size();
    }


    public class MessageViewHolder extends RecyclerView.ViewHolder {
        ImageView imgAvt;
        TextView userName;
        TextView content;
        TextView time;

        public MessageViewHolder(@NonNull View itemView) {
            super(itemView);

            imgAvt = itemView.findViewById(R.id.imgItem_message_avatar);
            userName = itemView.findViewById(R.id.txtItem_message_userName);
            content = itemView.findViewById(R.id.txtItem_message_content);
            time = itemView.findViewById(R.id.txtItem_message_time);
        }
    }
}
