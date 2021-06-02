package com.tranquangduy.ttcm_chatrealtime;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;

public class ResetPasswordActivity extends AppCompatActivity {

    ImageView imgBack;
    EditText txtEmail;
    TextView tvAddInfo;
    Button btnSendRequest;
    FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reset_password);

        linkView();
        addEvent();



    }

    private void addEvent() {
        btnSendRequest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String str_email = txtEmail.getText().toString().trim();
                if(str_email.equals("")){
                    Toast.makeText(ResetPasswordActivity.this, "Vui lòng nhập địa chỉ Email", Toast.LENGTH_SHORT).show();
                }
                firebaseAuth.sendPasswordResetEmail(str_email).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(task.isSuccessful()){
                            Toast.makeText(ResetPasswordActivity.this, "Vui lòng kiểm tra hộp thư của bạn!", Toast.LENGTH_SHORT).show();
                            tvAddInfo.setText("Chúng tôi đã gửi yêu cầu đến tài khoản của bạn. Vui lòng kiểm tra trong hộp thư đến và quay lại đăng nhập");
                            txtEmail.setText("");
                        }else {
                            String error = task.getException().getMessage();
                            Toast.makeText(ResetPasswordActivity.this, error, Toast.LENGTH_SHORT).show();
                        }
                    }
                });

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
        imgBack = findViewById(R.id.img_resetPass_back);
        txtEmail = findViewById(R.id.txt_resetPass_email);
        btnSendRequest = findViewById(R.id.btn_reset_passWord);
        tvAddInfo = findViewById(R.id.tv_resetPass_addInfo);

        firebaseAuth = FirebaseAuth.getInstance();

    }
}