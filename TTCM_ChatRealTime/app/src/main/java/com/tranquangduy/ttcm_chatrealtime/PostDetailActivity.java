package com.tranquangduy.ttcm_chatrealtime;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.tranquangduy.adapter.PostAdapter;
import com.tranquangduy.model.Post;

import java.util.ArrayList;
import java.util.List;

public class PostDetailActivity extends AppCompatActivity {
    RecyclerView recyclerViewPostDetail;
    ImageView imgBack;

    private PostAdapter postAdapter;
    private List<Post> listPost;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_detail);

        linkView();
        addEvent();
        readPost();
    }

    private void addEvent() {
        imgBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }


    private void readPost() {
        listPost = new ArrayList<>();

        Intent intent = getIntent();
        String postID = intent.getStringExtra("postID");

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Posts").child(postID);

        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                listPost.clear();
                Post post = snapshot.getValue(Post.class);
                listPost.add(post);
                postAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
        postAdapter = new PostAdapter(this, listPost);
        recyclerViewPostDetail.setAdapter(postAdapter);
    }

    private void linkView() {
        imgBack = findViewById(R.id.img_postDetail_back);
        recyclerViewPostDetail = findViewById(R.id.recyclerView_postDetail);
        recyclerViewPostDetail.setHasFixedSize(true);
        recyclerViewPostDetail.setLayoutManager(new LinearLayoutManager(this));

    }
}