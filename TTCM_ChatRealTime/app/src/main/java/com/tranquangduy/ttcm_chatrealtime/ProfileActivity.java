package com.tranquangduy.ttcm_chatrealtime;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.tranquangduy.fragments.FragmentProfile;
import com.tranquangduy.model.User;

import java.util.HashMap;

public class ProfileActivity extends AppCompatActivity {
    ImageButton btnMyPhoto, btnSavePhoto, btnLogout;
    Button btnFollow , btnEditProfile;
    ImageView imgViewAvt, imgBack;
    TextView txtPost, txtFollowers, txtFollowing;
    TextView txtUserName, txtBio, txtBarProfile, txtWebpage;

    private DatabaseReference reference;
    private FirebaseUser firebaseUser;
    private Intent intent;
    private String userID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_profile);

        linkView();
        getUser();
        addEvent();
        getFollowPost();





    }

    private void getUser() {
        intent = getIntent();
        userID = intent.getStringExtra("profileID");

        reference = FirebaseDatabase.getInstance().getReference("Users").child(userID);
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                User user = snapshot.getValue(User.class);

                Glide.with(getApplicationContext()).load(user.getImageUrl()).into(imgViewAvt);
                txtUserName.setText(user.getFullName());
                txtBarProfile.setText(user.getUserName());
                txtBio.setText(user.getBio());
                txtWebpage.setText(user.getWebpage());

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });

        isFollowing();
    }

    private void getFollowPost() {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Follow").child(userID).child("following");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String count = String.valueOf(snapshot.getChildrenCount());
                txtFollowing.setText(count);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });

        DatabaseReference reference1 = FirebaseDatabase.getInstance().getReference("Follow").child(userID).child("followers");
        reference1.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String count = String.valueOf(snapshot.getChildrenCount());
                txtFollowers.setText(count);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
    }

    private void addEvent() {
        btnEditProfile.setVisibility(View.GONE);
        btnLogout.setVisibility(View.GONE);

        if(userID.equals(firebaseUser.getUid())){
            btnFollow.setVisibility(View.GONE);
            btnEditProfile.setVisibility(View.VISIBLE);
        }else {
            btnFollow.setVisibility(View.VISIBLE);
        }

        txtWebpage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Uri uri = Uri.parse(txtWebpage.getText().toString());
                if (uri.isAbsolute()) {
                    Intent intent = new Intent();
                    intent.setAction(Intent.ACTION_VIEW);
                    intent.setData(uri);
                    startActivity(intent);
                } else {
                    Toast.makeText(ProfileActivity.this, "Địa chỉ này không tồn tại", Toast.LENGTH_SHORT).show();
                }


            }
        });


        btnFollow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (btnFollow.getText().toString().equals("theo dõi")) {
                    FirebaseDatabase.getInstance().getReference().child("Follow").child(firebaseUser.getUid())
                            .child("following").child(userID).setValue(true);
                    FirebaseDatabase.getInstance().getReference().child("Follow").child(userID)
                            .child("followers").child(firebaseUser.getUid()).setValue(true);

                    if (!userID.equals(firebaseUser.getUid())) {
                        addNotification();
                    }
                } else {
                    FirebaseDatabase.getInstance().getReference().child("Follow").child(firebaseUser.getUid())
                            .child("following").child(userID).removeValue();
                    FirebaseDatabase.getInstance().getReference().child("Follow").child(userID)
                            .child("followers").child(firebaseUser.getUid()).removeValue();
                }
            }
        });

        imgBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });


    }


    private void linkView() {
        btnMyPhoto = findViewById(R.id.btnMyPhoto);
        btnSavePhoto = findViewById(R.id.btnSavePhoto);
        imgViewAvt = findViewById(R.id.img_profile);
        txtPost = findViewById(R.id.txt_posts);
        txtFollowers = findViewById(R.id.txt_followers);
        txtFollowing = findViewById(R.id.txt_following);
        txtUserName = findViewById(R.id.txt_fullName);
        txtBio = findViewById(R.id.txt_bio);
        txtBarProfile = findViewById(R.id.txtBar_profile);
        txtWebpage = findViewById(R.id.txt_profile_webpage);
        btnFollow = findViewById(R.id.btn_profile_follow);
        imgBack = findViewById(R.id.img_profile_back);
        btnEditProfile =  findViewById(R.id.btn_edit_profile);
        btnLogout = findViewById(R.id.imgLogout);

        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
    }



    private void addNotification() {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Notifications").child(userID);
        String t = getString(R.string.notification_follow); // lấy text ở file string.xml mà đa ngôn ngữ thôi :v
        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("userid", firebaseUser.getUid());
        hashMap.put("text", t);
        hashMap.put("postid", "");
        hashMap.put("ispost", Boolean.FALSE);
        reference.push().setValue(hashMap);
    }

    private void isFollowing() {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference()
                .child("Follow").child(firebaseUser.getUid()).child("following");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String t;
                if (snapshot.child(userID).exists()) {
                    t = getString(R.string.following);
                    btnFollow.setText(t);
                } else {
                    t = getString(R.string.follow);
                    btnFollow.setText(t);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
    }


}

