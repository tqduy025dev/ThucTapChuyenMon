package com.tranquangduy.ttcm_chatrealtime;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.tranquangduy.adapter.PhotoAdapter;
import com.tranquangduy.fragments.FragmentProfile;
import com.tranquangduy.model.Post;
import com.tranquangduy.model.User;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

public class ProfileActivity extends AppCompatActivity {
    ImageButton btnMyPhoto, btnSavePhoto, btnLogout;
    Button btnFollow , btnEditProfile;
    ImageView imgViewAvt, imgBack;
    TextView txtPost, txtFollowers, txtFollowing;
    TextView txtUserName, txtBio, txtBarProfile, txtWebpage;
    RecyclerView recyclerViewPhoTo;
    RecyclerView recyclerViewSave;
    ProgressBar progressProfile;

    private DatabaseReference reference;
    private FirebaseUser firebaseUser;
    private Intent intent;
    private String userID;
    private User user;

    private List<String> mSave;
    private List<Post> listPost_mPhoto;
    private PhotoAdapter photoAdapter_mPhoto;

    private List<Post> listPost_mSave;
    private PhotoAdapter photoAdapter_mSave;
    private boolean check = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_profile);

        linkView();
        getUser();
        addEvent();
        getFollowPost();
        myFotos();
        mySave();


    }

    private void getUser() {
        intent = getIntent();
        userID = intent.getStringExtra("profileID");

        reference = FirebaseDatabase.getInstance().getReference("Users").child(userID);
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                user = snapshot.getValue(User.class);

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

    private void myFotos(){
        listPost_mPhoto = new ArrayList<>();
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Posts");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                listPost_mPhoto.clear();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()){
                    Post post = dataSnapshot.getValue(Post.class);
                    if (post.getPublisher().equals(userID)){
                        listPost_mPhoto.add(post);
                    }
                }
                Collections.reverse(listPost_mPhoto);
                photoAdapter_mPhoto.notifyDataSetChanged();
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
        photoAdapter_mPhoto = new PhotoAdapter(this, listPost_mPhoto);
        recyclerViewPhoTo.setAdapter(photoAdapter_mPhoto);
    }

    private void mySave(){
        mSave= new ArrayList<>();
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Saves").child(userID);
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot dataSnapshot : snapshot.getChildren()){
                    mSave.add(dataSnapshot.getKey());
                }
                readSave();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void readSave(){
        listPost_mSave = new ArrayList<>();
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Posts");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                listPost_mSave.clear();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()){
                    Post post = dataSnapshot.getValue(Post.class);
                    for (String id : mSave) {
                        if (post.getPostid().equals(id)) {
                            listPost_mSave.add(post);
                        }
                    }
                }
                progressProfile.setVisibility(View.GONE);
                photoAdapter_mSave.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        photoAdapter_mSave = new PhotoAdapter(this, listPost_mSave);
        recyclerViewSave.setAdapter(photoAdapter_mSave);
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


        DatabaseReference reference2 = FirebaseDatabase.getInstance().getReference("Posts");
        reference2.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                int dem = 0;
                for(DataSnapshot dataSnapshot : snapshot.getChildren()){
                    Post post = dataSnapshot.getValue(Post.class);
                    if(post.getPublisher().equals(userID)){
                        dem++;
                    }
                }

                txtPost.setText(String.valueOf(dem));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


    }

    private void addEvent() {
        btnLogout.setVisibility(View.GONE);

        if(userID.equals(firebaseUser.getUid())){
            btnFollow.setVisibility(View.GONE);
            btnSavePhoto.setVisibility(View.VISIBLE);
            btnEditProfile.setVisibility(View.VISIBLE);
        }else {
            btnEditProfile.setVisibility(View.GONE);
            btnFollow.setVisibility(View.VISIBLE);
            btnSavePhoto.setVisibility(View.GONE);
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

        txtFollowers.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ProfileActivity.this, MoreStatusActivity.class);
                intent.putExtra("id", userID);
                intent.putExtra("title", "Người theo dõi");
                startActivity(intent);
            }
        });

        txtFollowing.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ProfileActivity.this, MoreStatusActivity.class);
                intent.putExtra("id", userID);
                intent.putExtra("title", "Đang theo dõi");
                startActivity(intent);
            }
        });

        btnEditProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openDiglogEditProfile(user);
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


        btnSavePhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                recyclerViewPhoTo.setVisibility(View.GONE);
                btnMyPhoto.setImageResource(R.drawable.ic_baseline_cloud);
                btnSavePhoto.setImageResource(R.drawable.ic_baseline_savebackground);
                recyclerViewSave.setVisibility(View.VISIBLE);
            }
        });

        btnMyPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                recyclerViewSave.setVisibility(View.GONE);
                btnMyPhoto.setImageResource(R.drawable.ic_baseline_cloud_background);
                btnSavePhoto.setImageResource(R.drawable.ic_baseline_save);
                recyclerViewPhoTo.setVisibility(View.VISIBLE);
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
        progressProfile = findViewById(R.id.progress_profile);

        recyclerViewPhoTo = findViewById(R.id.recyclerView_photo);
        recyclerViewPhoTo.setHasFixedSize(true);
        recyclerViewPhoTo.setLayoutManager(new GridLayoutManager(this, 4));

        recyclerViewSave = findViewById(R.id.recyclerView_save);
        recyclerViewSave.setHasFixedSize(true);
        recyclerViewSave.setLayoutManager(new GridLayoutManager(this, 4));

        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
    }



    private void openDiglogEditProfile(User user) {
        final Dialog dialog = new Dialog(ProfileActivity.this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.custom_dialog);
        dialog.setCanceledOnTouchOutside(false);
        Window window = dialog.getWindow();
        window.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);


        final ImageButton btnCancel = dialog.findViewById(R.id.btn_editProfile_cancel);
        final ImageButton btnAccept = dialog.findViewById(R.id.btn_editProfile_accept);
        final EditText edtUserName = dialog.findViewById(R.id.edt_editProfile_userName);
        final ImageView imgAvt = dialog.findViewById(R.id.img_editProfile_avatar);
        final EditText edtFullName = dialog.findViewById(R.id.edt_editProfile_fullName);
        final EditText edtBio = dialog.findViewById(R.id.edt_editProfile_bio);
        final EditText edtWebpage = dialog.findViewById(R.id.edt_editProfile_webpage);
        final EditText edtOldPass = dialog.findViewById(R.id.edt_editProfile_oldPass);
        final EditText edtNewPass = dialog.findViewById(R.id.edt_editProfile_newPass);
        final EditText edtConfimPass = dialog.findViewById(R.id.edt_editProfile_confimNewPass);
        final Button btnChangePass = dialog.findViewById(R.id.btn_editProfile_changePass);


        edtFullName.setText(user.getFullName());
        edtUserName.setText(user.getUserName());
        edtBio.setText(user.getBio());
        edtWebpage.setText(user.getWebpage());
        Glide.with(ProfileActivity.this).load(user.getImageUrl()).into(imgAvt);

        btnAccept.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (TextUtils.isEmpty(edtFullName.getText().toString().trim()) || TextUtils.isEmpty(edtUserName.getText().toString().trim())) {
                    Toast.makeText(ProfileActivity.this, "Không được bỏ trống họ tên và tên người dùng! ", Toast.LENGTH_SHORT).show();
                } else {
                    String str_userName = edtUserName.getText().toString();
                    String str_fullName = edtFullName.getText().toString();
                    String str_bio = edtBio.getText().toString();
                    String str_webpage = edtWebpage.getText().toString();

                    reference = FirebaseDatabase.getInstance().getReference("Users").child(firebaseUser.getUid());
                    HashMap<String, Object> map = new HashMap<>();
                    map.put("userName", str_userName.toLowerCase());
                    map.put("fullName", str_fullName);
                    map.put("bio", str_bio);
                    map.put("webpage", str_webpage);
                    reference.updateChildren(map);

                    String str_oldPass = edtOldPass.getText().toString();
                    String str_newPass = edtNewPass.getText().toString();
                    String str_confimPass = edtConfimPass.getText().toString();


                    ProgressDialog progressDialog = new ProgressDialog(ProfileActivity.this);
                    progressDialog.setMessage("Xin hãy đợi!...");

                    if(edtOldPass.getVisibility() == View.VISIBLE){
                        if(!TextUtils.isEmpty(str_oldPass) && !TextUtils.isEmpty(str_newPass) && !TextUtils.isEmpty(str_confimPass)){
                            if(str_newPass.equals(str_confimPass) && str_newPass.length() >= 8){
                                progressDialog.show();
                                AuthCredential credential = EmailAuthProvider.getCredential(firebaseUser.getEmail(), edtOldPass.getText().toString());
                                firebaseUser.reauthenticate(credential).addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if(task.isSuccessful()){
                                            firebaseUser.updatePassword(str_newPass).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> task) {
                                                    if(task.isSuccessful()){
                                                        Toast.makeText(ProfileActivity.this, "Cập nhật thành công!", Toast.LENGTH_SHORT).show();
                                                        progressDialog.dismiss();
                                                        dialog.dismiss();
                                                    }else {
                                                        Toast.makeText(ProfileActivity.this, "Thay đổi thất bại vui lòng thử lại sau!", Toast.LENGTH_SHORT).show();
                                                    }
                                                }
                                            });
                                        }else {
                                            Toast.makeText(ProfileActivity.this, "Mật khẩu cũ không chính xác", Toast.LENGTH_SHORT).show();
                                            progressDialog.dismiss();
                                        }

                                    }
                                });
                            }else {
                                Toast.makeText(ProfileActivity.this, "Xác nhận mật khẩu không đúng hoặc mật khẩu ít hơn 8 kí tự", Toast.LENGTH_SHORT).show();
                            }

                        }else if(!TextUtils.isEmpty(str_oldPass) || !TextUtils.isEmpty(str_newPass) || !TextUtils.isEmpty(str_confimPass)){
                            Toast.makeText(ProfileActivity.this, "Vui lòng điền đầy đủ thông tin mật khẩu", Toast.LENGTH_SHORT).show();
                        }else {
                            dialog.dismiss();
                        }
                    }else {
                        dialog.dismiss();
                    }




                }

            }
        });

        check = false;
        btnChangePass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!check){
                    edtOldPass.setVisibility(View.VISIBLE);
                    edtNewPass.setVisibility(View.VISIBLE);
                    edtConfimPass.setVisibility(View.VISIBLE);
                    check = true;
                }else {
                    edtOldPass.setVisibility(View.GONE);
                    edtNewPass.setVisibility(View.GONE);
                    edtConfimPass.setVisibility(View.GONE);
                    check = false;
                }

            }
        });


        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });


        dialog.show();
    }


    private void addNotification() {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Notifications").child(userID);
        String t = getString(R.string.notification_follow); // lấy text ở file string.xml mà đa ngôn ngữ thôi :v
        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("userid", firebaseUser.getUid());
        hashMap.put("text", t);
        hashMap.put("postid", "");
        hashMap.put("ismessage", Boolean.FALSE);
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

