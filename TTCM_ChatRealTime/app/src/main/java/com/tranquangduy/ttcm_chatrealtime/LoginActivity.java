package com.tranquangduy.ttcm_chatrealtime;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;


public class LoginActivity extends AppCompatActivity {
    EditText txtUserName;
    EditText txtPassWord;
    Button btnLogin;
    TextView tvSignUp;
    ProgressDialog progressDialog;


    private FirebaseAuth mAuth;
    private Intent intent = null;



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
                txtUserName.setBackgroundResource(R.drawable.boder_while);
                txtPassWord.setBackgroundResource(R.drawable.boder_while);
                if(TextUtils.isEmpty(str_email)){
                    txtUserName.setBackgroundResource(R.drawable.boder_red);
                    Toast.makeText(LoginActivity.this, "Không được để trống Email!", Toast.LENGTH_SHORT).show();
                }else if(TextUtils.isEmpty(str_password)){
                    txtPassWord.setBackgroundResource(R.drawable.boder_red);
                    Toast.makeText(LoginActivity.this, "Không được để trống Password!!", Toast.LENGTH_SHORT).show();
                }else{
                    logIn(str_email, str_password);
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


    }

    private void logIn(final String email, final String password) {
        progressDialog = new ProgressDialog(LoginActivity.this);
        progressDialog.setMessage("Xin hãy đợi!...");
        progressDialog.show();

        mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()){

                    Toast.makeText(LoginActivity.this,"Đăng nhập thành công!", Toast.LENGTH_SHORT).show();

                    intent = new Intent(LoginActivity.this, MainActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                    finish();
                }else {
                    progressDialog.dismiss();
                    Toast.makeText(LoginActivity.this,"Sai email hoặc mật khẩu!" , Toast.LENGTH_SHORT).show();
                }
            }
        });
    }


    private void linkView () {
            btnLogin = findViewById(R.id.btn_login);
            tvSignUp = findViewById(R.id.tv_login_dangki);
            txtUserName = findViewById(R.id.txt_login_userName);
            txtPassWord = findViewById(R.id.txt_login_passWord);

            mAuth = FirebaseAuth.getInstance();
        }


    }