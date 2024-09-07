package com.tranquangduy.ttcm_chatrealtime;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.tranquangduy.adapter.CommentAdapter;
import com.tranquangduy.model.Comment;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class CommentActivity extends AppCompatActivity {
    ImageView imgLike;
    ImageView imgSendPost;
    ImageView imgAvt;
    ImageView imgBack;
    EditText edtComment;
    RecyclerView recyclerViewComment;

    private List<Comment> listComment;
    private CommentAdapter commentAdapter;
    private String postID;
    private String publisherID;
    private FirebaseUser firebaseUser;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comment);

        linkView();
        getData();
        addEvent();
        readComment();

    }

    private void getData() {
        Intent intent = getIntent();
        postID = intent.getStringExtra("postID");
        publisherID = intent.getStringExtra("publisherID");

        SharedPreferences preferences = getSharedPreferences("MyPreferences", Context.MODE_PRIVATE);
        String mImgURL = preferences.getString("mImgURL", "");
        Glide.with(this).load(mImgURL).into(imgAvt);

        isLike();

    }

    private void addEvent() {
        imgBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });


        imgLike.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(imgLike.getTag().equals("like")){
                    FirebaseDatabase.getInstance().getReference().child("Likes").child(postID)
                            .child(firebaseUser.getUid()).setValue(true);

                    addNotification();
                }else {
                    FirebaseDatabase.getInstance().getReference().child("Likes").child(postID)
                            .child(firebaseUser.getUid()).removeValue();
                }
            }
        });

        imgSendPost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!TextUtils.isEmpty(edtComment.getText().toString())){
                    addComment();
                }
            }
        });







    }

    private void linkView() {
        imgLike = findViewById(R.id.img_comment_like);
        imgAvt = findViewById(R.id.img_comment_avt);
        imgSendPost = findViewById(R.id.img_comment_send);
        edtComment = findViewById(R.id.txt_comment_addComment);
        imgBack = findViewById(R.id.img_comment_back);
        recyclerViewComment = findViewById(R.id.recyclerView_comment);
        recyclerViewComment.setHasFixedSize(true);
        recyclerViewComment.setLayoutManager(new LinearLayoutManager(this));

        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
    }


    private void isLike(){
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Likes").child(postID);
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.hasChild(firebaseUser.getUid())){
                    imgLike.setImageResource(R.drawable.ic_favorite_red);
                    imgLike.setTag("liked");
                }else {
                    imgLike.setImageResource(R.drawable.ic_favorite);
                    imgLike.setTag("like");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
    }

    private void addComment(){

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Comments").child(postID);

        String commentid = reference.push().getKey();

        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("comment", edtComment.getText().toString());
        hashMap.put("publisher", firebaseUser.getUid());
        hashMap.put("commentid", commentid);
        reference.child(commentid).setValue(hashMap);

        if(!publisherID.equals(firebaseUser.getUid())){
            addNotification(postID);
        }
        edtComment.setText("");

    }


    private void readComment(){
        listComment = new ArrayList<>();
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Comments").child(postID);

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                listComment.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()){
                    Comment comment = snapshot.getValue(Comment.class);
                    listComment.add(comment);
                }

                commentAdapter.notifyDataSetChanged();
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });

        commentAdapter = new CommentAdapter(listComment,CommentActivity.this,postID);
        recyclerViewComment.setAdapter(commentAdapter);

    }


    private void addNotification(){
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Notifications").child(publisherID);

        HashMap<String, Object> map = new HashMap<>();
        map.put("userid", firebaseUser.getUid());
        map.put("text", "Đã thích bài viết của bạn");
        map.put("postid", postID);
        map.put("ismessage",Boolean.FALSE);
        map.put("ispost", Boolean.TRUE);

        reference.push().setValue(map);
    }

    private void addNotification(String postID){
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Notifications").child(publisherID);

        HashMap<String, Object> map = new HashMap<>();
        map.put("userid", firebaseUser.getUid());
        map.put("text", "Đã bình luận vào bài viết của bạn");
        map.put("postid", postID);
        map.put("ismessage", Boolean.FALSE);
        map.put("ispost", Boolean.TRUE);

        reference.push().setValue(map);
    }



}