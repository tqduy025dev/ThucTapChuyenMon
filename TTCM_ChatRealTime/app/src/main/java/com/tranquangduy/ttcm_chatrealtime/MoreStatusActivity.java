package com.tranquangduy.ttcm_chatrealtime;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.tranquangduy.adapter.UserAdapter;
import com.tranquangduy.control.OnItemClickRecycleView;
import com.tranquangduy.model.User;

import java.util.ArrayList;
import java.util.List;


public class MoreStatusActivity extends AppCompatActivity implements OnItemClickRecycleView{
    RecyclerView recyclerViewLikePost;
    ImageView imgBack;
    TextView txtTitle;

    private List<User> listUser;
    private List<String> listID;
    private UserAdapter userAdapter;

    private String title;
    private String id;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_more_status);

        linkView();
        getData();
        addEvent();




    }

    private void addEvent() {
        imgBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

    }

    private void linkView() {
        imgBack = findViewById(R.id.img_likePost_back);
        txtTitle = findViewById(R.id.txt_more_title);
        recyclerViewLikePost = findViewById(R.id.recyclerView_moreStatus);
        recyclerViewLikePost.setHasFixedSize(true);
        recyclerViewLikePost.setLayoutManager(new LinearLayoutManager(this));

    }

    private void getData() {
        listID = new ArrayList<>();
        Intent intent = getIntent();
        id = intent.getStringExtra("id");
        title = intent.getStringExtra("title");

        txtTitle.setText(title);

        switch (title){
            case "Những người thích bài viết này!":
                getLike();
                break;
            case "Đang theo dõi":
                getFollowing();
                break;
            case "Người theo dõi":
                getFollower();
                break;
        }



    }

    private void getFollower() {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Follow").child(id).child("followers");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                listID.clear();
                for (DataSnapshot dataSnapshot: snapshot.getChildren()){
                    listID.add(dataSnapshot.getKey());
                }
                showUsers();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });


    }

    private void getFollowing() {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Follow").child(id).child("following");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                listID.clear();
                for (DataSnapshot dataSnapshot: snapshot.getChildren()){
                    listID.add(dataSnapshot.getKey());
                }
                showUsers();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });

    }



    private void getLike() {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Likes")
                .child(id);
        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                listID.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()){
                    listID.add(snapshot.getKey());
                }
                showUsers();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    private void showUsers() {
        listUser = new ArrayList<>();
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users");
        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                listUser.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()){
                    User user = snapshot.getValue(User.class);
                    for (String id : listID){
                        if (user.getId().equals(id)){
                            listUser.add(user);
                        }
                    }
                }
                userAdapter.notifyDataSetChanged();
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        userAdapter = new UserAdapter(MoreStatusActivity.this, listUser,true,false,false,this);
        recyclerViewLikePost.setAdapter(userAdapter);


    }


    @Override
    public void onItemClick(int position) {
        Intent intent = new Intent(this, ProfileActivity.class);
        intent.putExtra("profileID", listUser.get(position).getId());
        startActivity(intent);
    }

    @Override
    public void onItemLongClick(int postition) {

    }
}