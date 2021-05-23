package com.tranquangduy.ttcm_chatrealtime;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class RegistrationActivity extends AppCompatActivity {
    TextView tvDacoTK;
    EditText txtUerName, txtPassWord, txtConfimPass, txtPhone;
    Button btnSignUp;

    Intent intent = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);

        linkView();
        addEvent();



    }

    private void addAccount(final String userName, final String passWord, final String confimPass, final int phone) {




    }

    private void addEvent() {
        tvDacoTK.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(RegistrationActivity.this, LoginActivity.class));
            }
        });
    }

    private void linkView() {
        tvDacoTK = findViewById(R.id.tv_signUp_trovedangnhap);
        txtUerName = findViewById(R.id.txt_signUp_userName);
        txtPassWord = findViewById(R.id.txt_signUp_passWord);
        txtConfimPass = findViewById(R.id.txt_signUp_confimPass);
        txtPhone = findViewById(R.id.txt_signUp_sdt);
        btnSignUp = findViewById(R.id.btn_signUp);
    }
}