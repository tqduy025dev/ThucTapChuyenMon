package com.tranquangduy.fragments;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.tranquangduy.adapter.PostAdapter;
import com.tranquangduy.model.Post;
import com.tranquangduy.ttcm_chatrealtime.R;

import java.util.ArrayList;
import java.util.List;

import static android.content.Context.MODE_PRIVATE;

public class FragmentPostDetail extends Fragment {
    RecyclerView recyclerViewPostDetail;

    private PostAdapter postAdapter;
    private List<Post> listPost;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_post_detail, container, false);

        linkView(view);
        readPost();

        return view;
    }

    private void readPost() {
        listPost = new ArrayList<>();
        SharedPreferences preferences = getContext().getSharedPreferences("MyPreferences", MODE_PRIVATE);
        String postID = preferences.getString("postID", "");

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
        postAdapter = new PostAdapter(getContext(), listPost);
        recyclerViewPostDetail.setAdapter(postAdapter);
    }

    private void linkView(View view) {
        recyclerViewPostDetail = view.findViewById(R.id.recyclerView_postDetail);
        recyclerViewPostDetail.setHasFixedSize(true);
        recyclerViewPostDetail.setLayoutManager(new LinearLayoutManager(getContext()));




    }
}
