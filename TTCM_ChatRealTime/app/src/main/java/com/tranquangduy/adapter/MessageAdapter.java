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
import com.tranquangduy.fragments.OnItemClickRecycleView;
import com.tranquangduy.model.Message;
import com.tranquangduy.ttcm_chatrealtime.R;


import java.util.List;

public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.MessageViewHolder>{

    private Context mContext;
    private List<Message> mMessage;
    private boolean isFragment;
    private OnItemClickRecycleView onItemClickRecycleView;

    public MessageAdapter(Context mContext, List<Message> mMessage, boolean isFragment, OnItemClickRecycleView onItemClickRecycleView) {
        this.mContext = mContext;
        this.mMessage = mMessage;
        this.isFragment = isFragment;
        this.onItemClickRecycleView = onItemClickRecycleView;
    }

    @NonNull
    @Override
    public MessageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view =  LayoutInflater.from(parent.getContext()).inflate(R.layout.item_message,parent, false);
        return new MessageViewHolder(view, onItemClickRecycleView);
    }

    @Override
    public void onBindViewHolder(@NonNull MessageViewHolder holder, int position) {
        Glide.with(mContext).load(mMessage.get(position).getImgURL()).into(holder.imgAvt);
        holder.userName.setText(mMessage.get(position).getFrom());
//        holder.content.setText(mMessage.get(position).getContent());
        holder.time.setText(mMessage.get(position).getTime() + "");
    }

    @Override
    public int getItemCount() {
        return mMessage.size();
    }


    public class MessageViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        ImageView imgAvt;
        TextView userName;
        TextView content;
        TextView time;
        OnItemClickRecycleView onItemClickRecycleViewHolder;

        public MessageViewHolder(@NonNull View itemView, OnItemClickRecycleView onClickListener) {
            super(itemView);
            this.onItemClickRecycleViewHolder = onClickListener; //set onlick item cho recycle view
            itemView.setOnClickListener(this);

            imgAvt = itemView.findViewById(R.id.imgItem_message_avatar);
            userName = itemView.findViewById(R.id.txtItem_message_userName);
            content = itemView.findViewById(R.id.txtItem_message_content);
            time = itemView.findViewById(R.id.txtItem_message_time);
        }


        @Override
        public void onClick(View v) {
            onItemClickRecycleViewHolder.onItemClick(getAdapterPosition());
        }
    }
}
