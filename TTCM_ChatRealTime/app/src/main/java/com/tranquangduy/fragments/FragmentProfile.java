package com.tranquangduy.fragments;


import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.tranquangduy.model.User;
import com.tranquangduy.ttcm_chatrealtime.LoginActivity;
import com.tranquangduy.ttcm_chatrealtime.R;

public class FragmentProfile extends Fragment {
    ImageButton btnLogout, btnMyPhoto, btnSavePhoto;
    ImageView imgViewAvt;
    TextView txtPost, txtFollowers, txtFollowing;
    TextView txtUserName, txtBio, txtBarProfile;

    private FirebaseAuth mAuth;
    private FirebaseUser firebaseUser;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        linkView(view);
        addEvent();
        getUser();
        getFollow();

        return view;

    }

    private void getFollow() {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Follow").child(firebaseUser.getUid()).child("following");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (getContext() == null){
                    return;
                }
                txtFollowing.setText(""+ snapshot.getChildrenCount());

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });

        DatabaseReference reference1 = FirebaseDatabase.getInstance().getReference("Follow").child(firebaseUser.getUid()).child("followers");
        reference1.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (getContext() == null){
                    return;
                }
                txtFollowers.setText(""+ snapshot.getChildrenCount());

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });



    }

    private void getUser() {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users").child(firebaseUser.getUid());
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (getContext() == null){
                    return;
                }
                User user = snapshot.getValue(User.class);

                Glide.with(getContext()).load(user.getImageUrl()).into(imgViewAvt);
                txtUserName.setText(user.getFullName());
                txtBarProfile.setText(user.getUserName());
                txtBio.setText(user.getBio());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


    }

    private void linkView(View view) {
        btnLogout = view.findViewById(R.id.imgLogout);
        btnMyPhoto = view.findViewById(R.id.btnMyPhoto);
        btnSavePhoto = view.findViewById(R.id.btnSavePhoto);
        imgViewAvt = view.findViewById(R.id.img_profile);
        txtPost = view.findViewById(R.id.txt_posts);
        txtFollowers = view.findViewById(R.id.txt_followers);
        txtFollowing = view.findViewById(R.id.txt_following);
        txtUserName = view.findViewById(R.id.txt_fullName);
        txtBio = view.findViewById(R.id.txt_bio);
        txtBarProfile = view.findViewById(R.id.txtBar_profile);

        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
    }

    private void addEvent() {
        btnLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mAuth = FirebaseAuth.getInstance();
                mAuth.signOut();
                startActivity(new Intent(getContext(), LoginActivity.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK));
            }
        });
    }


}
