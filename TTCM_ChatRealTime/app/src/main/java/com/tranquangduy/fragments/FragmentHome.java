package com.tranquangduy.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.tranquangduy.adapter.PostAdapter;
import com.tranquangduy.model.Post;
import com.tranquangduy.ttcm_chatrealtime.MessageActivity;
import com.tranquangduy.ttcm_chatrealtime.PostActivity;
import com.tranquangduy.ttcm_chatrealtime.R;

import java.util.ArrayList;
import java.util.List;

public class FragmentHome extends Fragment {
    ImageView imgViewAvt;
    Button btnAddPost;
    TextView txtHome;
    RecyclerView recyclerViewPost;

    private List<Post> listPost;
    private PostAdapter postAdapter;
    private List<String> followingList;

    private FirebaseUser firebaseUser;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        linkView(view);
        checkFollowing();
        addEvent();


        return view;
    }


    private void checkFollowing(){
        followingList = new ArrayList<>();
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Follow")
                .child(firebaseUser.getUid()).child("following");

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                followingList.clear();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()){
                    followingList.add(dataSnapshot.getKey());
                }

                readPosts();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }


    private void readPosts() {
        listPost = new ArrayList<>();
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Posts");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                listPost.clear();
                for(DataSnapshot dataSnapshot: snapshot.getChildren()){
                    Post post = dataSnapshot.getValue(Post.class);
                    for(String id: followingList){
                        if(post.getPublisher().equals(id)){
                            listPost.add(post);
                        }
                    }
                }
                postAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
        postAdapter = new PostAdapter(getContext(), listPost);
        recyclerViewPost.setAdapter(postAdapter);
    }

    private void addEvent() {
        btnAddPost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), PostActivity.class);
                startActivity(intent);
            }
        });
    }


    private void linkView(View view) {
        imgViewAvt = view.findViewById(R.id.imgView_home_Avt);
        btnAddPost = view.findViewById(R.id.btn_home_post);
        txtHome = view.findViewById(R.id.txt_home_main);
        recyclerViewPost = view.findViewById(R.id.recyclerView_post);
        recyclerViewPost.setHasFixedSize(true);
        recyclerViewPost.setLayoutManager(new LinearLayoutManager(getContext()));

        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
    }
}
