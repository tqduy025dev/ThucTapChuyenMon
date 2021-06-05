package com.tranquangduy.adapter;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.tranquangduy.fragments.FragmentProfile;
import com.tranquangduy.model.Post;
import com.tranquangduy.model.User;
import com.tranquangduy.ttcm_chatrealtime.CommentActivity;
import com.tranquangduy.ttcm_chatrealtime.MoreStatusActivity;
import com.tranquangduy.ttcm_chatrealtime.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static android.content.Context.MODE_PRIVATE;

public class PostAdapter extends RecyclerView.Adapter<PostAdapter.PostViewHolder>{
    public static final String PREF = "MyPreferences";
    Context mContext;
    List<Post> mPost;
    ArrayList<String> listIDLiked;
    ArrayList<User> listUserLiked;
    UserAdapter userAdapter;

    private FirebaseUser firebaseUser;

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
                    addNotification(post.getPublisher(), post.getPostid());
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
                SharedPreferences.Editor editor = mContext.getSharedPreferences(PREF, MODE_PRIVATE).edit();
                editor.putString("profileID", post.getPublisher());
                editor.apply();

                ((FragmentActivity)mContext).getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                        new FragmentProfile()).commit();
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





    }

    @Override
    public int getItemCount() {
        return mPost.size();
    }




    public class PostViewHolder extends RecyclerView.ViewHolder {
        ImageView imgAvt;
        ImageView imageViewPost;
        ImageView imgLike;
        ImageView imgSave;
        ImageView imgComment;
        TextView txtUserName;
        TextView txtPostContent;
        TextView txtLikeCount;
        TextView txtCommentCount;

        public PostViewHolder(@NonNull View itemView) {
            super(itemView);

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

    private void addNotification(String userid, String postid){
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Notifications").child(userid);

        HashMap<String, Object> map = new HashMap<>();
        map.put("userid", firebaseUser.getUid());
        map.put("text", "đã thích ảnh của bạn");
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
                Glide.with(mContext).load(user.getImageUrl()).into(imgAvt);
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
