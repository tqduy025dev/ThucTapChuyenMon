package com.tranquangduy.adapter;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
import com.tranquangduy.fragments.OnItemClickRecycleView;
import com.tranquangduy.model.Comment;
import com.tranquangduy.model.User;
import com.tranquangduy.ttcm_chatrealtime.MainActivity;
import com.tranquangduy.ttcm_chatrealtime.R;

import java.util.List;

public class CommentAdapter extends RecyclerView.Adapter<CommentAdapter.CommentViewHolder> {

    private List<Comment> mComment;
    private Context mContext;
    private String mPostID;
    private OnItemClickRecycleView onItemClickRecycle;
    private FirebaseUser firebaseUser;

    boolean check = false;

    public CommentAdapter(List<Comment> mComment, Context mContext, String mPostID) {
        this.mComment = mComment;
        this.mContext = mContext;
        this.mPostID = mPostID;
    }

    @NonNull
    @Override
    public CommentAdapter.CommentViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.item_comment, parent, false);
        mContext = parent.getContext();

        return new CommentAdapter.CommentViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CommentAdapter.CommentViewHolder holder, int position) {
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        Comment comment = mComment.get(position);

        holder.txtComment.setText(comment.getComment());

        getInfo(holder.imgAvt, holder.txtUserName, comment.getPublisher());

        holder.txtUserName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, MainActivity.class);
                intent.putExtra("publisherID", comment.getPublisher());
                mContext.startActivity(intent);
            }
        });

        holder.imgAvt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, MainActivity.class);
                intent.putExtra("publisherID", comment.getPublisher());
                mContext.startActivity(intent);

            }
        });


        holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                if (comment.getPublisher().equals(firebaseUser.getUid()) && !check) {
                    holder.imgDelete.setVisibility(View.VISIBLE);
                    check = true;

                    deleteComment(holder.imgDelete, comment.getCommentid());
                }else {
                    holder.imgDelete.setVisibility(View.GONE);
                    check = false;

                    deleteComment(holder.imgDelete, comment.getCommentid());
                }


                return true;
            }
        });


    }

    private void deleteComment(final ImageView imgDelete, String postID){
        imgDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                v.setVisibility(View.GONE);
                AlertDialog alertDialog = new AlertDialog.Builder(mContext).create();
                alertDialog.setTitle("Bạn chắc chắn muốn xoá bình luận này?");
                alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "Huỷ",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        });
                alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "Xoá",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                FirebaseDatabase.getInstance().getReference("Comments")
                                        .child(mPostID).child(postID)
                                        .removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if (task.isSuccessful()) {
                                            Toast.makeText(mContext, "Đã xoá!", Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                });
                                dialog.dismiss();
                            }
                        });
                alertDialog.show();
            }
        });
    }



    private void getInfo(final ImageView imgView, final TextView userName, String publisherID) {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("Users").child(publisherID);

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                User user = snapshot.getValue(User.class);
                //ko load dc vi activyti detroy -> getAplication
                Glide.with(mContext.getApplicationContext()).load(user.getImageUrl()).into(imgView);
                userName.setText(user.getUserName());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });

    }

    @Override
    public int getItemCount() {
        return mComment.size();
    }

    public class CommentViewHolder extends RecyclerView.ViewHolder{
        ImageView imgAvt;
        ImageView imgDelete;
        TextView txtUserName;
        TextView txtComment;

        public CommentViewHolder(@NonNull View itemView) {
            super(itemView);
            imgAvt = itemView.findViewById(R.id.imgItem_comment_Avt);
            txtComment = itemView.findViewById(R.id.txtItem_comment_commentContent);
            txtUserName = itemView.findViewById(R.id.txtItem_comment_username);
            imgDelete = itemView.findViewById(R.id.imgItem_comment_delete);
        }


    }
}
