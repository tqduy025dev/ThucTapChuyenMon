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

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.tranquangduy.fragments.OnItemClickRecycleView;
import com.tranquangduy.model.User;
import com.tranquangduy.ttcm_chatrealtime.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class UserAdapter extends RecyclerView.Adapter<UserAdapter.UserViewHolder>  {
    private final Context mContext;
    private List<User> mUsers;
    private boolean isFragment;
    private boolean isMessage;
    private OnItemClickRecycleView onItemClickRecycleView;

    private FirebaseUser firebaseUser;

    public UserAdapter(Context mContext, List<User> mUsers, boolean isFragment, boolean isMessage, OnItemClickRecycleView onItemClickRecycleView) {
        this.mContext = mContext;
        this.mUsers = mUsers;
        this.isFragment = isFragment;
        this.isMessage = isMessage;
        this.onItemClickRecycleView = onItemClickRecycleView;
    }

    @NonNull
    @Override
    public UserViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_user,parent, false);
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        return new UserViewHolder(view, onItemClickRecycleView);
    }

    @Override
    public void onBindViewHolder(@NonNull UserAdapter.UserViewHolder holder, int position) {
        User user = mUsers.get(position);

        Glide.with(mContext).load(user.getImageUrl()).into(holder.imgViewUser);
        holder.tvUserName.setText(user.getUserName());
        holder.tvFullName.setText(user.getFullName());

        if(isFragment){
            holder.btnFollow.setVisibility(View.VISIBLE);
        }else{
            holder.btnFollow.setVisibility(View.GONE);
        }

        if(isMessage){
            holder.tvFullName.setText(user.getLastMsg());
            if(user.getId().equals(firebaseUser.getUid())){
                String a = "Chỉ có bạn";
                holder.tvUserName.setText(a);
                holder.tvFullName.setText(user.getFullName());
            }
        }

        isFollowing(user.getId(), holder.btnFollow);
        if (user.getId().equals(firebaseUser.getUid())){
            holder.btnFollow.setVisibility(View.GONE);
        }




        holder.btnFollow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (holder.btnFollow.getText().toString().equals("follow")) {
                    FirebaseDatabase.getInstance().getReference().child("Follow").child(firebaseUser.getUid())
                            .child("following").child(user.getId()).setValue(true);
                    FirebaseDatabase.getInstance().getReference().child("Follow").child(user.getId())
                            .child("followers").child(firebaseUser.getUid()).setValue(true);

                    addNotification(user.getId());
                } else {
                    FirebaseDatabase.getInstance().getReference().child("Follow").child(firebaseUser.getUid())
                            .child("following").child(user.getId()).removeValue();
                    FirebaseDatabase.getInstance().getReference().child("Follow").child(user.getId())
                            .child("followers").child(firebaseUser.getUid()).removeValue();
                }

            }
        });


    }

    @Override
    public int getItemCount() {
        return mUsers.size();
    }

//    public void filterList(ArrayList<User> filterList){
//        mUsers = filterList;
//        notifyDataSetChanged();
//    }



    public class UserViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        ImageView imgViewUser;
        TextView tvUserName;
        TextView tvFullName;
        Button btnFollow;
        OnItemClickRecycleView onItemClickRecycleViewHolder;

        public UserViewHolder(@NonNull View itemView, OnItemClickRecycleView onItemClickRecycleViewHolder) {
            super(itemView);
            this.onItemClickRecycleViewHolder = onItemClickRecycleViewHolder;
            itemView.setOnClickListener(this);

            imgViewUser = itemView.findViewById(R.id.imgItem_user_avatar);
            tvUserName = itemView.findViewById(R.id.txtItem_user_userName);
            tvFullName = itemView.findViewById(R.id.txtItem_user_fullName);
            btnFollow = itemView.findViewById(R.id.btnItem_follow);
        }

        @Override
        public void onClick(View v) {
            onItemClickRecycleViewHolder.onItemClick(getAdapterPosition());
        }
    }

    private void addNotification(String userid){
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Notifications").child(userid);

        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("userid", firebaseUser.getUid());
        hashMap.put("text", "đang bắt đầu theo dõi bạn");
        hashMap.put("postid", "");
        hashMap.put("ispost", Boolean.FALSE);
        reference.push().setValue(hashMap);
    }

    private void isFollowing(final String userid, final Button button){
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference()
                .child("Follow").child(firebaseUser.getUid()).child("following");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.child(userid).exists()){
                    button.setText("following");
                } else{
                    button.setText("follow");
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
    }


}
