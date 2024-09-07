package com.tranquangduy.adapter;

import android.Manifest;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Environment;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.ScaleGestureDetector;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.github.chrisbanes.photoview.PhotoView;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.tranquangduy.model.Post;
import com.tranquangduy.model.User;
import com.tranquangduy.ttcm_chatrealtime.CommentActivity;
import com.tranquangduy.ttcm_chatrealtime.MoreStatusActivity;
import com.tranquangduy.ttcm_chatrealtime.ProfileActivity;
import com.tranquangduy.ttcm_chatrealtime.R;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

public class PostAdapter extends RecyclerView.Adapter<PostAdapter.PostViewHolder>{
    private final Context mContext;
    private final List<Post> mPost;

    private FirebaseUser firebaseUser;
    private Thread thread; // đa luồng khi nhấn vào tải ảnh

    public PostAdapter(Context mContext, List<Post> mPost) {
        this.mContext = mContext;
        this.mPost = mPost;
    }

    @NonNull
    @Override
    public PostAdapter.PostViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.item_post, parent, false);
        return new PostAdapter.PostViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PostAdapter.PostViewHolder holder, int position) {


        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        Post post = mPost.get(position);

        if(!post.getPostimage().equals("noImage")){
            holder.imageViewPost.setVisibility(View.VISIBLE);
            Glide.with(mContext).load(post.getPostimage()).into(holder.imageViewPost);
        }else {
            holder.imageViewPost.setVisibility(View.GONE);
            holder.imgSave.setVisibility(View.GONE);
        }

        if (post.getDescription().equals("")){
            holder.txtPostContent.setVisibility(View.GONE);
        } else {
            holder.txtPostContent.setVisibility(View.VISIBLE);
            holder.txtPostContent.setText(post.getDescription());
        }

        userInfo(holder.imgAvt, holder.txtUserName, post.getPublisher());
        isLiked(post.getPostid(), holder.imgLike);
        isSaved(post.getPostid(), holder.imgSave);
        getCommetns(post.getPostid(),holder.txtCommentCount);
        getLikes(post.getPostid(), holder.txtLikeCount);

        holder.imgLike.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (holder.imgLike.getTag().equals("like")) {
                    FirebaseDatabase.getInstance().getReference().child("Likes").child(post.getPostid())
                            .child(firebaseUser.getUid()).setValue(true);
                    if(!post.getPublisher().equals(firebaseUser.getUid())){
                        addNotification(post.getPublisher(), post.getPostid());
                    }
                } else {
                    FirebaseDatabase.getInstance().getReference().child("Likes").child(post.getPostid())
                            .child(firebaseUser.getUid()).removeValue();
                }
            }
        });


        holder.imgSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (holder.imgSave.getTag().equals("save")){
                    FirebaseDatabase.getInstance().getReference().child("Saves").child(firebaseUser.getUid())
                            .child(post.getPostid()).setValue(true);
                } else {
                    FirebaseDatabase.getInstance().getReference().child("Saves").child(firebaseUser.getUid())
                            .child(post.getPostid()).removeValue();
                }
            }
        });


        holder.imgAvt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(mContext, ProfileActivity.class);
                intent.putExtra("profileID", post.getPublisher());
                mContext.startActivity(intent);
            }
        });


        holder.imgComment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, CommentActivity.class);
                intent.putExtra("postID", post.getPostid());
                intent.putExtra("publisherID", post.getPublisher());
                mContext.startActivity(intent);

            }
        });

        holder.txtCommentCount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, CommentActivity.class);
                intent.putExtra("postID", post.getPostid());
                intent.putExtra("publisherID", post.getPublisher());
                mContext.startActivity(intent);
            }
        });

        holder.txtLikeCount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, MoreStatusActivity.class);
                intent.putExtra("id", post.getPostid());
                intent.putExtra("title", "Những người thích bài viết này!");
                mContext.startActivity(intent);
            }
        });


        holder.imgMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                openMenu(post ,holder.imageViewPost ,v);

            }
        });

        holder.imageViewPost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openDiaLog(post.getPostimage());
            }
        });

    }



    @Override
    public int getItemCount() {
        return mPost.size();
    }


    public class PostViewHolder extends RecyclerView.ViewHolder{
        ImageView imgAvt;
        ImageView imageViewPost;
        ImageView imgLike;
        ImageView imgSave;
        ImageView imgComment;
        ImageView imgMenu;
        TextView txtUserName;
        TextView txtPostContent;
        TextView txtLikeCount;
        TextView txtCommentCount;

        public PostViewHolder(@NonNull View itemView) {
            super(itemView);
            imgMenu = itemView.findViewById(R.id.imgItem_post_menu);
            imgAvt = itemView.findViewById(R.id.imgItem_Avt);
            imageViewPost = itemView.findViewById(R.id.imgViewItem_post);
            imgLike = itemView.findViewById(R.id.imgItem_like);
            imgSave = itemView.findViewById(R.id.imgItem_save);
            imgComment = itemView.findViewById(R.id.imgItem_comment);
            txtUserName = itemView.findViewById(R.id.txtItem_post_userName);
            txtPostContent = itemView.findViewById(R.id.txtItem_post_postContent);
            txtLikeCount = itemView.findViewById(R.id.txtItem_post_likeCount);
            txtCommentCount = itemView.findViewById(R.id.txtItem_post_commentCount);
        }


    }

    private  void openDiaLog(String imgURL){
        final Dialog dialog = new Dialog(mContext);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.show_image_view_dialog);
        dialog.setCanceledOnTouchOutside(true);
        Window window = dialog.getWindow();
        window.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);

        final PhotoView imageViewPost = dialog.findViewById(R.id.showImageView);
        final ImageButton imageButtonBack = dialog.findViewById(R.id.showBtnBack);
        Glide.with(mContext).load(imgURL).into(imageViewPost);

        imageButtonBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        dialog.show();

    }


    private void openMenu(Post post,final ImageView imgPost, View v){

        PopupMenu popupMenu = new PopupMenu(mContext, v);

        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()){
                    case R.id.menu_delete :
                        final String id = post.getPostid();
                        FirebaseDatabase.getInstance().getReference("Posts")
                                .child(id).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()){
                                    deleteNotification(id, firebaseUser.getUid());
                                    Toast.makeText(mContext, "Đã xoá!", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                        return true;

                    case R.id.menu_report :
                        Toast.makeText(mContext, mContext.getString(R.string.report_post), Toast.LENGTH_SHORT).show();
                        return true;

                    case R.id.menu_edit_post :
                        editPost(post.getPostid());
                        return true;
                    case R.id.menu_edit_download :
                        downloadImage(imgPost);
                    default: return false;
                }
            }
        });

        popupMenu.inflate(R.menu.menu_edit_post);
        if (!post.getPublisher().equals(firebaseUser.getUid())){
            popupMenu.getMenu().findItem(R.id.menu_delete).setVisible(false);
            popupMenu.getMenu().findItem(R.id.menu_edit_post).setVisible(false);
        }
        if(post.getPostimage().equals("noImage")){
            popupMenu.getMenu().findItem(R.id.menu_edit_download).setVisible(false);
        }
        popupMenu.show();


    }

    private void downloadImage(ImageView imgPost){
        if(ContextCompat.checkSelfPermission(mContext, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
            thread = new Thread(new Runnable() {
                @Override
                public void run() {
                    Bitmap bitmap = ((BitmapDrawable) imgPost.getDrawable()).getBitmap();
                    File filePath = Environment.getExternalStorageDirectory();
                    File dir = new File(filePath + "/Download");
                    dir.mkdir();
                    File file = new File(dir, System.currentTimeMillis() + ".jpg");
                    OutputStream outputStream;
                    try {
                        outputStream = new FileOutputStream(file);
                        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream);
                        outputStream.flush();
                        outputStream.close();

                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            });
            thread.start();
            Toast.makeText(mContext, "Tải xuống thành công!", Toast.LENGTH_SHORT).show();
        }else {
            Toast.makeText(mContext, "Tải xuống thất bại!", Toast.LENGTH_SHORT).show();
        }
    }


    private void editPost(final String postID) {
        AlertDialog alertDialog = new AlertDialog.Builder(mContext).create();
        alertDialog.setTitle(mContext.getString(R.string.update_post));
        final EditText editText = new EditText(mContext);
        getTextPost(postID, editText);

        alertDialog.setView(editText);
        alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, mContext.getString(R.string.update),
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        HashMap<String, Object> map = new HashMap<>();
                        map.put("description", editText.getText().toString());

                        FirebaseDatabase.getInstance().getReference("Posts")
                                .child(postID).updateChildren(map);
                    }
                });
        alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, mContext.getString(R.string.cancel), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                alertDialog.dismiss();
            }
        });

        alertDialog.show();
    }

    private void getTextPost(final String postID, final EditText editText) {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Posts")
                .child(postID);
        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                editText.setText(dataSnapshot.getValue(Post.class).getDescription());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }


    private void deleteNotification(final String postID, String userID){
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Notifications").child(userID);
        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot dataSnapshot : snapshot.getChildren()){
                    if (Objects.equals(dataSnapshot.child("postid").getValue(), postID)){
                        dataSnapshot.getRef().removeValue()
                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        Toast.makeText(mContext, "Đã xoá", Toast.LENGTH_SHORT).show();
                                    }
                                });
                    }
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });


    }



    private void addNotification(String userid, String postid){
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Notifications").child(userid);

        HashMap<String, Object> map = new HashMap<>();
        map.put("userid", firebaseUser.getUid());
        map.put("text", "Đã thích ảnh của bạn");
        map.put("ismessage", Boolean.FALSE);
        map.put("postid", postid);
        map.put("ispost", Boolean.TRUE);

        reference.push().setValue(map);
    }


    private void userInfo(final ImageView imgAvt, final TextView userName, final String userId){
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference()
                .child("Users").child(userId);

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                User user = snapshot.getValue(User.class);
                Glide.with(mContext.getApplicationContext()).load(user.getImageUrl()).into(imgAvt);
                userName.setText(user.getUserName());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void isLiked(final String postid, final ImageView imageLike){

        final FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference()
                .child("Likes").child(postid);
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.child(firebaseUser.getUid()).exists()){
                    imageLike.setImageResource(R.drawable.ic_favorite_red);
                    imageLike.setTag("liked");
                } else{
                    imageLike.setImageResource(R.drawable.ic_favorite);
                    imageLike.setTag("like");
                }
            }

            @Override
            public void onCancelled(DatabaseError error) {

            }
        });
    }

    private void isSaved(final String postid, final ImageView imageView){

        final FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference()
                .child("Saves").child(firebaseUser.getUid());
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.child(postid).exists()){
                    imageView.setImageResource(R.drawable.ic_baseline_savebackground);
                    imageView.setTag("saved");
                } else{
                    imageView.setImageResource(R.drawable.ic_baseline_save);
                    imageView.setTag("save");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void getCommetns(final String postId, final TextView commentCount){
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("Comments").child(postId);
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                commentCount.setText(snapshot.getChildrenCount()+" bình luận");
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void getLikes(final String postId , final TextView likeCount){
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("Likes").child(postId);
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                likeCount.setText(snapshot.getChildrenCount() +" lượt thích");
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }




}
