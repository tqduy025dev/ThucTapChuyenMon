package com.tranquangduy.ttcm_chatrealtime;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.messaging.FirebaseMessaging;

import java.util.HashMap;
import java.util.Map;

public class LoginActivity extends AppCompatActivity {
    EditText txtUserName;
    EditText txtPassWord;
    ImageView imgShowPassWord;
    Button btnLogin;
    TextView tvSignUp, tvforgotPass;
    ProgressDialog progressDialog;

    boolean checkShowPass = false;
    private FirebaseAuth mAuth;
    private Intent intent = null;
    DatabaseReference reference;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        linkView();
        addEvent();


    }


    private void addEvent() {
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String str_email = txtUserName.getText().toString().trim();
                String str_password = txtPassWord.getText().toString();
                if (TextUtils.isEmpty(str_email)) {
                    Toast.makeText(LoginActivity.this, "Không được để trống Email!", Toast.LENGTH_SHORT).show();
                } else if (TextUtils.isEmpty(str_password)) {
                    Toast.makeText(LoginActivity.this, "Không được để trống Password!!", Toast.LENGTH_SHORT).show();
                } else {
                    logIn(str_email, str_password);
                }

            }
        });

        imgShowPassWord.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!checkShowPass) {
                    txtPassWord.setTransformationMethod(null);
                    imgShowPassWord.setImageResource(R.drawable.ic_show_password);
                    checkShowPass = true;
                } else {
                    txtPassWord.setTransformationMethod(new PasswordTransformationMethod());
                    imgShowPassWord.setImageResource(R.drawable.ic_reshow_password);
                    checkShowPass = false;
                }

            }
        });


        tvSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                intent = new Intent(LoginActivity.this, RegistrationActivity.class);
                startActivity(intent);
            }
        });


        tvforgotPass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                intent = new Intent(LoginActivity.this, ResetPasswordActivity.class);
                startActivity(intent);
            }
        });


    }

    private void logIn(final String email, final String password) {
        progressDialog = new ProgressDialog(LoginActivity.this);
        progressDialog.setMessage("Xin hãy đợi!...");
        progressDialog.show();

        mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    Toast.makeText(LoginActivity.this, "Đăng nhập thành công!", Toast.LENGTH_SHORT).show();

                    intent = new Intent(LoginActivity.this, MainActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    task.addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                        @Override
                        public void onSuccess(AuthResult authResult) {
                            updateToken(authResult.getUser().getUid());
                        }
                    });
                    startActivity(intent);


                } else {
                    Toast.makeText(LoginActivity.this, "Sai email hoặc mật khẩu!", Toast.LENGTH_SHORT).show();
                    progressDialog.dismiss();
                }
            }
        });
    }

    private void updateToken(String userID) {
        FirebaseMessaging.getInstance().getToken().addOnCompleteListener(new OnCompleteListener<String>() {
            @Override
            public void onComplete(@NonNull Task<String> task) {
                if (!task.isSuccessful()) {
                    Log.w("TAG", "Fetching FCM registration token failed!", task.getException());
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
        reference = FirebaseDatabase.getInstance().getReference("User");

        btnLogin = findViewById(R.id.btn_login);
        tvSignUp = findViewById(R.id.tv_login_dangki);
        txtUserName = findViewById(R.id.txt_login_userName);
        txtPassWord = findViewById(R.id.txt_login_passWord);
        imgShowPassWord = findViewById(R.id.img_login_showPassWord);
        tvforgotPass = findViewById(R.id.tv_login_quenmatkhau);

        mAuth = FirebaseAuth.getInstance();
    }


}