package com.tranquangduy.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.tranquangduy.model.User;
import com.tranquangduy.ttcm_chatrealtime.R;

import java.util.List;

public class UserAdapter extends RecyclerView.Adapter<UserAdapter.UserViewHolder> {
    private final Context mContext;
    private List<User> mUsers;
    private final boolean isFragment;

    public UserAdapter(Context mContext, List<User> mUsers, boolean isFragment) {
        this.mContext = mContext;
        this.mUsers = mUsers;
        this.isFragment = isFragment;
    }

    @NonNull
    @Override
    public UserViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_user,parent, false);

        return new UserViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull UserAdapter.UserViewHolder holder, int position) {
        holder.imgViewUser.setImageResource(R.drawable.ic_home);
        holder.tvUserName.setText(mUsers.get(position).getUserName());
        holder.tvFullName.setText(mUsers.get(position).getFullName());
    }

    @Override
    public int getItemCount() {
        return mUsers.size();
    }

    public class UserViewHolder extends RecyclerView.ViewHolder {
        ImageView imgViewUser;
        TextView tvUserName;
        TextView tvFullName;
        Button btnFollow;

        public UserViewHolder(@NonNull View itemView) {
            super(itemView);

            imgViewUser = itemView.findViewById(R.id.imgItem_user_avatar);
            tvUserName = itemView.findViewById(R.id.txtItem_user_userName);
            tvFullName = itemView.findViewById(R.id.txtItem_user_fullName);
            btnFollow = itemView.findViewById(R.id.btnItem_follow);


        }
    }
}
