package com.tranquangduy.ttcm_chatrealtime;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.google.firebase.installations.Utils;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;

import java.nio.file.Files;
import java.util.HashMap;
import java.util.Map;

public class PostActivity extends AppCompatActivity{
    private static final int REQUEST_CODE = 1;
    ImageView imgBack;
    ImageView imgAddImg;
    ImageView imgViewPost;
    EditText edtDescription;
    TextView txtPost;

    private UploadTask uploadTask;
    private StorageReference storageReference;
    private Uri imagePostUri = null;

    private FirebaseUser firebaseUser;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post);

        linkView();
        addEvent();

    }

    private void addEvent() {
        imgBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        imgAddImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent, "CHỌN ẢNH"), REQUEST_CODE);
            }
        });



        txtPost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String str_Description = edtDescription.getText().toString().trim();
                if(TextUtils.isEmpty(str_Description) && imagePostUri == null){
                    Toast.makeText(PostActivity.this, "Bạn chưa chọn ảnh hoặc viết trạng thái", Toast.LENGTH_SHORT).show();
                    edtDescription.setText("");

                }else {
                    postImage();
                }

            }
        });


    }

    private void linkView() {
        imgBack = findViewById(R.id.img_post_close);
        imgViewPost = findViewById(R.id.imgView_post);
        imgAddImg = findViewById(R.id.imgAdd_post_image);
        edtDescription = findViewById(R.id.txt_post_description);
        txtPost = findViewById(R.id.txt_post);

        storageReference = FirebaseStorage.getInstance().getReference("PostImage");
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
    }




    private String getFileExtension(Uri uri) {
        ContentResolver contentResolver = getContentResolver();
        MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();
        return mimeTypeMap.getExtensionFromMimeType(contentResolver.getType(uri));
    }

    private void postImage() {
        final ProgressDialog pd = new ProgressDialog(this);
        pd.setMessage("Xin hãy đợi...!");
        pd.show();

        if (imagePostUri != null) {
            final StorageReference fileReference = storageReference.child(System.currentTimeMillis() + "." + getFileExtension(imagePostUri));
            uploadTask = fileReference.putFile(imagePostUri);

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

                        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Posts");
                        String postid = reference.push().getKey();

                        HashMap<String, Object> map = new HashMap<>();
                        map.put("postid", postid);
                        map.put("postimage", mUri);
                        map.put("description", edtDescription.getText().toString());
                        map.put("publisher", firebaseUser.getUid());

                        reference.child(postid).setValue(map);
                        pd.dismiss();
                        startActivity(new Intent(PostActivity.this, MainActivity.class));
                        finish();
                        Toast.makeText(PostActivity.this, "Bài viết của bạn đã được đăng", Toast.LENGTH_SHORT).show();

                    }else {
                        Toast.makeText(PostActivity.this, "Không thể đăng bài lúc này!", Toast.LENGTH_SHORT).show();
                        pd.dismiss();
                    }
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(PostActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                    pd.dismiss();
                }
            });
        }else{
            DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Posts");
            String postid = reference.push().getKey();

            HashMap<String, Object> map = new HashMap<>();
            map.put("postid", postid);
            map.put("postimage", "noImage");
            map.put("description", edtDescription.getText().toString());
            map.put("publisher", firebaseUser.getUid());
            reference.child(postid).setValue(map);

            startActivity(new Intent(PostActivity.this, MainActivity.class));
            finish();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == REQUEST_CODE && resultCode == RESULT_OK && data != null && data.getData() != null){
            imagePostUri = data.getData();

            imgViewPost.setVisibility(View.VISIBLE);
            imgViewPost.setImageURI(imagePostUri);
        }else {
            Toast.makeText(this, "Không có ảnh được chọn!", Toast.LENGTH_SHORT).show();
        }
    }
}