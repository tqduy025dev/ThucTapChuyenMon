package com.tranquangduy.ttcm_chatrealtime;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.messaging.FirebaseMessaging;

import java.util.HashMap;
import java.util.Map;

public class RegistrationActivity extends AppCompatActivity {
    TextView tvDacoTK;
    EditText txtUserName, txtFullName, txtPassWord, txtConfimPass, txtEmail;
    Button btnSignUp;
    ProgressDialog progressDialog;

    private FirebaseAuth mAuth;
    private DatabaseReference reference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);


        linkView();
        addEvent();
    }


    private void addEvent() {
        tvDacoTK.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });


        btnSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                String str_userName = txtUserName.getText().toString().trim();
                String str_fullName = txtFullName.getText().toString().trim();
                String str_email = txtEmail.getText().toString().trim();
                String str_passWord = txtPassWord.getText().toString();
                String str_confimPass = txtConfimPass.getText().toString();


                txtUserName.setBackgroundResource(R.drawable.boder_rectangle);
                txtFullName.setBackgroundResource(R.drawable.boder_rectangle);
                txtEmail.setBackgroundResource(R.drawable.boder_rectangle);
                txtPassWord.setBackgroundResource(R.drawable.boder_rectangle);
                txtConfimPass.setBackgroundResource(R.drawable.boder_rectangle);

                if (TextUtils.isEmpty(str_fullName)) {
                    txtFullName.setBackgroundResource(R.drawable.boder_rectangle_red);
                    Toast.makeText(RegistrationActivity.this, "Tên đăng nhập không được trống!", Toast.LENGTH_SHORT).show();
                } else if (TextUtils.isEmpty(str_userName)) {
                    txtFullName.setBackgroundResource(R.drawable.boder_rectangle);
                    txtUserName.setBackgroundResource(R.drawable.boder_rectangle_red);
                    Toast.makeText(RegistrationActivity.this, "Họ tên không được trống!", Toast.LENGTH_SHORT).show();
                } else if (TextUtils.isEmpty(str_email)) {
                    txtFullName.setBackgroundResource(R.drawable.boder_rectangle);
                    txtUserName.setBackgroundResource(R.drawable.boder_rectangle);
                    txtEmail.setBackgroundResource(R.drawable.boder_rectangle_red);
                    Toast.makeText(RegistrationActivity.this, "Email không được trống!", Toast.LENGTH_SHORT).show();
                } else if (TextUtils.isEmpty(str_passWord) || str_passWord.length() < 8) {
                    txtFullName.setBackgroundResource(R.drawable.boder_rectangle);
                    txtUserName.setBackgroundResource(R.drawable.boder_rectangle);
                    txtEmail.setBackgroundResource(R.drawable.boder_rectangle);
                    txtPassWord.setBackgroundResource(R.drawable.boder_rectangle_red);
                    Toast.makeText(RegistrationActivity.this, "Mật khẩu không được trống và có tối thiểu 8 kí tự!", Toast.LENGTH_SHORT).show();
                } else if (TextUtils.isEmpty(str_confimPass) || !str_confimPass.equals(str_passWord)) {
                    txtFullName.setBackgroundResource(R.drawable.boder_rectangle);
                    txtUserName.setBackgroundResource(R.drawable.boder_rectangle);
                    txtEmail.setBackgroundResource(R.drawable.boder_rectangle);
                    txtPassWord.setBackgroundResource(R.drawable.boder_rectangle);
                    txtConfimPass.setBackgroundResource(R.drawable.boder_rectangle_red);
                    Toast.makeText(RegistrationActivity.this, "Xác nhận mật khẩu không khớp!", Toast.LENGTH_SHORT).show();
                } else {
                    addUser(str_userName, str_fullName, str_passWord, str_email);
                }


            }

        });
    }

    private void addUser(final String userName, final String fullName, final String passWord, final String email) {
        progressDialog = new ProgressDialog(RegistrationActivity.this);
        progressDialog.setMessage("Xin hãy đợi!...");
        progressDialog.show();

        mAuth.createUserWithEmailAndPassword(email, passWord).addOnCompleteListener(RegistrationActivity.this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    FirebaseUser firebaseUser = mAuth.getCurrentUser();
                    String userID = firebaseUser.getUid();
                    reference = FirebaseDatabase.getInstance().getReference().child("Users").child(userID);
                    HashMap<String, Object> map = new HashMap<>();
                    map.put("id", userID);
                    map.put("userName", userName.toLowerCase());
                    map.put("fullName", fullName);
                    map.put("email", email);
                    map.put("imageUrl", "https://firebasestorage.googleapis.com/v0/b/instagramtest-fcbef.appspot.com/o/placeholder.png?alt=media&token=b09b809d-a5f8-499b-9563-5252262e9a49");
                    map.put("bio", "");
                    map.put("webpage", "");
                    map.put("status", "offline");

                    reference.setValue(map).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                Intent intent = new Intent(RegistrationActivity.this, MainActivity.class);
                                //xoá hết các task hiện thời
                                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK );
                                startActivity(intent);
                            }
                        }
                    });
                    task.addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                        @Override
                        public void onSuccess(AuthResult authResult) {
                            updateToken(authResult.getUser().getUid());

                        }
                    });
                } else {
                    progressDialog.dismiss();
                    Toast.makeText(RegistrationActivity.this, "Không thể đăng kí!!", Toast.LENGTH_SHORT).show();
                }

            }
        });

    }

    private void updateToken(String userID) {
        FirebaseMessaging.getInstance().getToken().addOnCompleteListener(new OnCompleteListener<String>() {
            @Override
            public void onComplete(@NonNull Task<String> task) {
                if (!task.isSuccessful()) {
                    return;
                }
                String token = task.getResult();

                DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users");
                Map<String, Object> map = new HashMap<>();
                map.put("token", token);
                reference.child(userID).updateChildren(map);
            }
        });
    }

    private void linkView() {
        tvDacoTK = findViewById(R.id.tv_signUp_trovedangnhap);
        txtUserName = findViewById(R.id.txt_signUp_userName);
        txtFullName = findViewById(R.id.txt_signUp_fullName);
        txtPassWord = findViewById(R.id.txt_signUp_passWord);
        txtConfimPass = findViewById(R.id.txt_signUp_confimPass);
        txtEmail = findViewById(R.id.txt_signUp_email);
        btnSignUp = findViewById(R.id.btn_signUp);

        mAuth = FirebaseAuth.getInstance();

    }
}
