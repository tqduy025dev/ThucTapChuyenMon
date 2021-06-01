package com.tranquangduy.fragments;


import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;
import com.tranquangduy.model.User;
import com.tranquangduy.ttcm_chatrealtime.LoginActivity;
import com.tranquangduy.ttcm_chatrealtime.R;

import java.util.HashMap;

import static android.app.Activity.RESULT_OK;

public class FragmentProfile extends Fragment {
    private static final int IMAGE_REQUEST = 1;

    ImageButton btnLogout, btnMyPhoto, btnSavePhoto;
    Button btnEditProfile;
    ImageView imgViewAvt;
    TextView txtPost, txtFollowers, txtFollowing;
    TextView txtUserName, txtBio, txtBarProfile, txtWebpage;

    private StorageReference storageReference;
    private Uri imageUri;
    private UploadTask uploadTask;

    private User user;
    private FirebaseAuth mAuth;
    private FirebaseUser firebaseUser;
    private DatabaseReference reference;

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

        storageReference = FirebaseStorage.getInstance().getReference("Uploads");
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
    }

    private void addEvent() {
        btnLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                status("offline");
                mAuth = FirebaseAuth.getInstance();
                mAuth.signOut();
                startActivity(new Intent(getContext(), LoginActivity.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK));
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
                if(uri.isAbsolute()){
                    Intent intent = new Intent();
                    intent.setAction(Intent.ACTION_VIEW);
                    intent.setData(uri);
                    startActivity(intent);
                }else{
                    Toast.makeText(getContext(), "Địa chỉ này không tồn tại", Toast.LENGTH_SHORT).show();
                }


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


    }

    private void openDiglogEditProfile(User user) {
        final Dialog dialog = new Dialog(getContext());
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.custom_dialog);
        dialog.setCanceledOnTouchOutside(false);

        final ImageButton btnCancel = dialog.findViewById(R.id.btn_editProfile_cancel);
        final ImageButton btnAccept = dialog.findViewById(R.id.btn_editProfile_accept);
        final EditText edtUserName = dialog.findViewById(R.id.edt_editProfile_userName);
        final ImageView imgAvt = dialog.findViewById(R.id.img_editProfile_avatar);
        final EditText edtFullName = dialog.findViewById(R.id.edt_editProfile_fullName);
        final EditText edtBio = dialog.findViewById(R.id.edt_editProfile_bio);
        final EditText edtWebpage = dialog.findViewById(R.id.edt_editProfile_webpage);

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
                if (TextUtils.isEmpty(edtFullName.getText().toString()) || TextUtils.isEmpty(edtUserName.getText().toString())) {
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
                    dialog.dismiss();
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



    private void status(String status){
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
