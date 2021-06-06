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
                    String t = getString(R.string.check_email);
                    Toast.makeText(ResetPasswordActivity.this, t, Toast.LENGTH_SHORT).show();
                }
                firebaseAuth.sendPasswordResetEmail(str_email).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(task.isSuccessful()){
                            String t = getString(R.string.check_mailbox);
                            Toast.makeText(ResetPasswordActivity.this, t, Toast.LENGTH_SHORT).show();
                            tvAddInfo.setVisibility(View.VISIBLE);
                            String t1 = getString(R.string.text_fogot_pass);
                            tvAddInfo.setText(t1);
                            txtEmail.setText("");
                        }else {
                            String error = task.getException().getMessage();
                            tvAddInfo.setVisibility(View.GONE);
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