package com.tranquangduy.fragments;


import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.IntentSender;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.res.AssetManager;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.database.DatabaseErrorHandler;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.UserHandle;
import android.text.TextUtils;
import android.util.Log;
import android.view.Display;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
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
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;
import com.tranquangduy.adapter.PhotoAdapter;
import com.tranquangduy.adapter.PostAdapter;
import com.tranquangduy.model.Post;
import com.tranquangduy.model.User;
import com.tranquangduy.ttcm_chatrealtime.LoginActivity;
import com.tranquangduy.ttcm_chatrealtime.MainActivity;
import com.tranquangduy.ttcm_chatrealtime.MoreStatusActivity;
import com.tranquangduy.ttcm_chatrealtime.PostActivity;
import com.tranquangduy.ttcm_chatrealtime.R;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static android.app.Activity.RESULT_OK;

public class FragmentProfile extends Fragment {
    private static final int IMAGE_REQUEST = 1;
    ImageButton btnLogout, btnMyPhoto, btnSavePhoto;
    Button btnEditProfile, btnFollow;
    ImageView imgViewAvt, imgBack;
    TextView txtPost, txtFollowers, txtFollowing;
    TextView txtUserName, txtBio, txtBarProfile, txtWebpage;
    RecyclerView recyclerViewPhoTo;
    RecyclerView recyclerViewSave;
    ProgressBar progressProfile;

    private StorageReference storageReference;
    private Uri imageUri;
    private UploadTask uploadTask;

    private List<String> mSave;
    private List<Post> listPost_mPhoto;
    private PhotoAdapter photoAdapter_mPhoto;

    private List<Post> listPost_mSave;
    private PhotoAdapter photoAdapter_mSave;

    private User user;
    private FirebaseAuth mAuth;
    private FirebaseUser firebaseUser;
    private DatabaseReference reference;
    private boolean check = false;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        linkView(view);
        getUser();
        addEvent();
        getFollowPost();
        myFotos();
        mySave();

        return view;

    }


    private void getFollowPost() {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Follow").child(firebaseUser.getUid()).child("following");
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

        DatabaseReference reference1 = FirebaseDatabase.getInstance().getReference("Follow").child(firebaseUser.getUid()).child("followers");
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
                    if(post.getPublisher().equals(firebaseUser.getUid())){
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

    private void getUser() {
        reference = FirebaseDatabase.getInstance().getReference("Users").child(firebaseUser.getUid());
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                user = snapshot.getValue(User.class);
                if (getContext() == null) {
                    return;
                }
                Glide.with(getContext()).load(user.getImageUrl()).into(imgViewAvt);
                txtUserName.setText(user.getFullName());
                txtBarProfile.setText(user.getUserName());
                txtBio.setText(user.getBio());
                txtWebpage.setText(user.getWebpage());

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
        btnEditProfile = view.findViewById(R.id.btn_edit_profile);
        txtWebpage = view.findViewById(R.id.txt_profile_webpage);
        btnFollow = view.findViewById(R.id.btn_profile_follow);
        imgBack = view.findViewById(R.id.img_profile_back);
        progressProfile = view.findViewById(R.id.progress_profile);


        recyclerViewPhoTo = view.findViewById(R.id.recyclerView_photo);
        recyclerViewPhoTo.setHasFixedSize(true);
        recyclerViewPhoTo.setLayoutManager(new GridLayoutManager(getContext(), 4));

        recyclerViewSave = view.findViewById(R.id.recyclerView_save);
        recyclerViewSave.setHasFixedSize(true);
        recyclerViewSave.setLayoutManager(new GridLayoutManager(getContext(), 4));


        storageReference = FirebaseStorage.getInstance().getReference("Uploads");
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
    }

    private void addEvent() {
        imgBack.setVisibility(View.GONE);

        btnLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateToken(firebaseUser.getUid());
                status("offline");
                mAuth = FirebaseAuth.getInstance();
                mAuth.signOut();
                startActivity(new Intent(getContext(), LoginActivity.class).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK));
            }
        });

        btnEditProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openDiglogEditProfile(user);
            }
        });

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
                    Toast.makeText(getContext(), "Địa chỉ này không tồn tại", Toast.LENGTH_SHORT).show();
                }


            }
        });

        txtFollowers.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), MoreStatusActivity.class);
                intent.putExtra("id", firebaseUser.getUid());
                intent.putExtra("title", "Người theo dõi");
                startActivity(intent);
            }
        });

        txtFollowing.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), MoreStatusActivity.class);
                intent.putExtra("id", firebaseUser.getUid());
                intent.putExtra("title", "Đang theo dõi");
                startActivity(intent);
            }
        });



            imgViewAvt.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent();
                    intent.setType("image/*");
                    intent.setAction(Intent.ACTION_GET_CONTENT);
                    startActivityForResult(intent, IMAGE_REQUEST);
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

    private void myFotos(){
        listPost_mPhoto = new ArrayList<>();
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Posts");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                listPost_mPhoto.clear();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()){
                    Post post = dataSnapshot.getValue(Post.class);
                    if (post.getPublisher().equals(firebaseUser.getUid())){
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
        photoAdapter_mPhoto = new PhotoAdapter(getContext(), listPost_mPhoto);
        recyclerViewPhoTo.setAdapter(photoAdapter_mPhoto);
    }

    private void mySave(){
        mSave= new ArrayList<>();
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Saves").child(firebaseUser.getUid());
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
        photoAdapter_mSave = new PhotoAdapter(getContext(), listPost_mSave);
        recyclerViewSave.setAdapter(photoAdapter_mSave);
    }



    private void updateToken(String userID) {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users");
        Map<String, Object> map = new HashMap<>();
        map.put("token", "");
        reference.child(userID).updateChildren(map);
    }

    private void openDiglogEditProfile(User user) {
        final Dialog dialog = new Dialog(getContext());
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


        if (getContext() == null) {
            return;
        }
        edtFullName.setText(user.getFullName());
        edtUserName.setText(user.getUserName());
        edtBio.setText(user.getBio());
        edtWebpage.setText(user.getWebpage());
        Glide.with(getContext()).load(user.getImageUrl()).into(imgAvt);

        btnAccept.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (TextUtils.isEmpty(edtFullName.getText().toString().trim()) || TextUtils.isEmpty(edtUserName.getText().toString().trim())) {
                    Toast.makeText(getContext(), "Không được bỏ trống họ tên và tên người dùng! ", Toast.LENGTH_SHORT).show();
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


                    ProgressDialog progressDialog = new ProgressDialog(getContext());
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
                                                        Toast.makeText(getContext(), "Cập nhật thành công!", Toast.LENGTH_SHORT).show();
                                                        progressDialog.dismiss();
                                                        dialog.dismiss();
                                                    }else {
                                                        Toast.makeText(getContext(), "Thay đổi thất bại vui lòng thử lại sau!", Toast.LENGTH_SHORT).show();
                                                    }
                                                }
                                            });
                                        }else {
                                            Toast.makeText(getContext(), "Mật khẩu cũ không chính xác", Toast.LENGTH_SHORT).show();
                                            progressDialog.dismiss();
                                        }

                                    }
                                });
                            }else {
                                Toast.makeText(getContext(), "Xác nhận mật khẩu không đúng hoặc mật khẩu ít hơn 8 kí tự", Toast.LENGTH_SHORT).show();
                            }

                        }else if(!TextUtils.isEmpty(str_oldPass) || !TextUtils.isEmpty(str_newPass) || !TextUtils.isEmpty(str_confimPass)){
                            Toast.makeText(getContext(), "Vui lòng điền đầy đủ thông tin mật khẩu", Toast.LENGTH_SHORT).show();
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


    private String getFileExtension(Uri uri) {
        ContentResolver contentResolver = getContext().getContentResolver();
        MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();
        return mimeTypeMap.getExtensionFromMimeType(contentResolver.getType(uri));
    }

    private void uploadImage() {
        final ProgressDialog pd = new ProgressDialog(getContext());
        pd.setMessage("Uploading...");
        pd.show();

        if (imageUri != null) {
            final StorageReference fileReference = storageReference.child(System.currentTimeMillis() + "." + getFileExtension(imageUri));
            uploadTask = fileReference.putFile(imageUri);
            uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                @Override
                public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                    if (!task.isSuccessful()) {
                        throw task.getException();
                    }
                    return fileReference.getDownloadUrl();
                }
            }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                @Override
                public void onComplete(@NonNull Task<Uri> task) {
                    if (task.isSuccessful()) {
                        Uri downloadUri = task.getResult();
                        String mUri = downloadUri.toString();

                        reference = FirebaseDatabase.getInstance().getReference("Users").child(firebaseUser.getUid());
                        HashMap<String, Object> map = new HashMap<>();
                        map.put("imageUrl", mUri);
                        reference.updateChildren(map);
                        pd.dismiss();
                    } else {
                        Toast.makeText(getContext(), "Failed!", Toast.LENGTH_SHORT).show();
                        pd.dismiss();
                    }
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                    pd.dismiss();
                }
            });


        } else {
            Toast.makeText(getContext(), "Chưa chọn hình ảnh", Toast.LENGTH_SHORT).show();
        }
    }


    private void status(String status) {
        reference = FirebaseDatabase.getInstance().getReference("Users").child(firebaseUser.getUid());

        HashMap<String, Object> map = new HashMap<>();
        map.put("status", status);
        reference.updateChildren(map);

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            imageUri = data.getData();
            if (uploadTask != null && uploadTask.isInProgress()) {
                Toast.makeText(getContext(), "Đang tải ảnh !", Toast.LENGTH_SHORT).show();
            } else {
                uploadImage();
            }
        }
    }


}
