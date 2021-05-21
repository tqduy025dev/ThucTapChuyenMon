package com.tranquangduy.ttcm_chatrealtime;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

public class RegistrationActivity extends AppCompatActivity {
    TextView tvDacoTK;

    Intent intent = null;

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
                setResult(Activity.RESULT_OK, intent);
                finish();
            }
        });

    }

    private void linkView() {
        tvDacoTK = findViewById(R.id.tv_signUp_trovedangnhap);
        intent = getIntent();

    }
}