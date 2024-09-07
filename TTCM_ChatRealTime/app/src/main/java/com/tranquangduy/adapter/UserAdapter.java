package com.tranquangduy.adapter;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.tranquangduy.control.APIService;
import com.tranquangduy.control.OnItemClickRecycleView;
import com.tranquangduy.model.Message;
import com.tranquangduy.model.User;
import com.tranquangduy.ttcm_chatrealtime.R;

import java.util.HashMap;
import java.util.List;

public class UserAdapter extends RecyclerView.Adapter<UserAdapter.UserViewHolder> implements APIService {
    private final Context mContext;
    private List<User> mUsers;
    private final boolean isFragment;
    private final boolean isMessage;
    private final boolean isStatus;
    private final OnItemClickRecycleView onItemClickRecycleView;

    private FirebaseUser firebaseUser;
    private String theLastMessage;
    private String chatListID;


    public UserAdapter(Context mContext, List<User> mUsers, boolean isFragment, boolean isStatus, boolean isMessage, OnItemClickRecycleView onItemClickRecycleView) {
        this.mContext = mContext;
        this.mUsers = mUsers;
        this.isFragment = isFragment;
        this.isMessage = isMessage;
        this.isStatus = isStatus;
        this.onItemClickRecycleView = onItemClickRecycleView;
    }

    @NonNull
    @Override
    public UserViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_user, parent, false);
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        return new UserViewHolder(view, onItemClickRecycleView);
    }

    @Override
    public void onBindViewHolder(@NonNull UserAdapter.UserViewHolder holder, int position) {
        User user = mUsers.get(position);

        Glide.with(mContext).load(user.getImageUrl()).into(holder.imgViewUser);
        holder.tvUserName.setText(user.getUserName());

        if (isFragment) {
            holder.btnFollow.setVisibility(View.VISIBLE);
            holder.tvFullName.setText(user.getFullName());

        } else {
            holder.btnFollow.setVisibility(View.GONE);
        }

        if (isMessage) {
            checkLastMessage(user.getId(), holder.tvFullName);
            if (user.getId().equals(firebaseUser.getUid())) {
                String t = mContext.getString(R.string.only_me);
                holder.tvUserName.setText(t);
                holder.imgStatus.setVisibility(View.GONE);
            }
        }

        if (isStatus) {
            if (user.getStatus().equals("online")) {
                holder.imgStatus.setImageResource(R.drawable.ic_online);
            } else {
                holder.imgStatus.setImageResource(R.drawable.ic_offline);
            }
        } else {
            holder.imgStatus.setVisibility(View.GONE);
        }

        isFollowing(user.getId(), holder.btnFollow);
        if (user.getId().equals(firebaseUser.getUid())) {
            holder.btnFollow.setVisibility(View.GONE);
        }


        holder.btnFollow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (holder.btnFollow.getText().toString().equals("theo dõi")) {
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

        holder.imgDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                holder.imgDelete.setVisibility(View.GONE);
                String t1 = mContext.getString(R.string.confim_deleto);
                String t2 = mContext.getString(R.string.delete);
                String t3 = mContext.getString(R.string.cancel);
                String t4 = mContext.getString(R.string.deleted);
                AlertDialog alertDialog = new AlertDialog.Builder(mContext).create();
                alertDialog.setTitle(t1);
                alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, t3,
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        });
                alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, t2,
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                deleteChat();

                                dialog.dismiss();
                            }
                        });
                alertDialog.show();
            }
        });


    }


    @Override
    public int getItemCount() {
        return mUsers.size();
    }


    // sử dụng interface
    @Override
    public void UPDATE_USERID(String id) {
        this.chatListID = id;
    }

    // sử dụng interface
    @Override
    public void SEARCH_LISTUSER(List<User> listUser) {
        this.mUsers = listUser;
        notifyDataSetChanged();
    }


    public class UserViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener {
        ImageView imgViewUser;
        TextView tvUserName;
        TextView tvFullName;
        Button btnFollow;
        ImageView imgStatus;
        ImageView imgDelete;
        OnItemClickRecycleView onItemClickRecycleViewHolder;
        boolean check = false;

        public UserViewHolder(@NonNull View itemView, OnItemClickRecycleView onItemClickRecycleViewHolder) {
            super(itemView);
            this.onItemClickRecycleViewHolder = onItemClickRecycleViewHolder;
            itemView.setOnClickListener(this);
            itemView.setOnLongClickListener(this);
            imgStatus = itemView.findViewById(R.id.img_status);
            imgViewUser = itemView.findViewById(R.id.imgItem_user_avatar);
            tvUserName = itemView.findViewById(R.id.txtItem_user_userName);
            tvFullName = itemView.findViewById(R.id.txtItem_user_fullName);
            btnFollow = itemView.findViewById(R.id.btnItem_user_follow);
            imgDelete = itemView.findViewById(R.id.imgItem_user_delete);
        }

        @Override
        public void onClick(View v) {
            onItemClickRecycleViewHolder.onItemClick(getAdapterPosition());
        }

        @Override
        public boolean onLongClick(View v) {
            onItemClickRecycleViewHolder.onItemLongClick(getAdapterPosition());
            if (isMessage) {
                if (!check) {
                    imgDelete.setVisibility(View.VISIBLE);
                    check = true;
                } else {
                    imgDelete.setVisibility(View.GONE);
                    check = false;
                }
            } else {
                imgDelete.setVisibility(View.GONE);
            }


            return true;
        }
    }


    private void deleteChat() {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("ChatList")
                .child(firebaseUser.getUid()).child(chatListID);
        reference.removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    Toast.makeText(mContext, mContext.getString(R.string.deleted), Toast.LENGTH_SHORT).show();
                }
            }
        });

        DatabaseReference reference1 = FirebaseDatabase.getInstance().getReference("Chats").child(firebaseUser.getUid());
        reference1.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    Message msg = dataSnapshot.getValue(Message.class);
                    if (msg.getSender().equals(firebaseUser.getUid()) && msg.getReceiver().equals(chatListID) ||
                            msg.getSender().equals(chatListID) && msg.getReceiver().equals(firebaseUser.getUid())) {
                        dataSnapshot.getRef().removeValue();
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });

    }


    private void addNotification(String userid) {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Notifications").child(userid);
        String t = mContext.getString(R.string.notification_follow); // lấy text ở file string.xml mà đa ngôn ngữ thôi :v
        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("userid", firebaseUser.getUid());
        hashMap.put("text", t);
        hashMap.put("postid", "");
        hashMap.put("ismessage", Boolean.FALSE);
        hashMap.put("ispost", Boolean.FALSE);
        reference.push().setValue(hashMap);
    }

    private void isFollowing(final String userid, final Button button) {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference()
                .child("Follow").child(firebaseUser.getUid()).child("following");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String t;
                if (snapshot.child(userid).exists()) {
                    t = mContext.getString(R.string.following);
                    button.setText(t);
                } else {
                    t = mContext.getString(R.string.follow);
                    button.setText(t);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
    }


    private void checkLastMessage(String userID, TextView txt_lastMessage) {
        theLastMessage = "deFauLt"; // cố tình sai format để tránh trường hợp người dùng gửi tin nhắn đúng chữ default
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Chats").child(firebaseUser.getUid());
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    Message msg = dataSnapshot.getValue(Message.class);
                    if (msg.getReceiver().equals(firebaseUser.getUid()) && msg.getSender().equals(userID) ||
                            msg.getReceiver().equals(userID) && msg.getSender().equals(firebaseUser.getUid())) {
                        if (msg.getType().equals("image")) {
                            theLastMessage = "imAGe";
                        } else {
                            theLastMessage = msg.getMessage();
                        }

                    }
                }

                if (theLastMessage.equals("imAGe")) {
                    String t1 = mContext.getString(R.string.notification_my_send_img);
                    txt_lastMessage.setText(t1);
                } else if (!theLastMessage.equals("deFauLt")) {
                    txt_lastMessage.setText(theLastMessage);
                } else {
                    txt_lastMessage.setText("");
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });

    }


}
