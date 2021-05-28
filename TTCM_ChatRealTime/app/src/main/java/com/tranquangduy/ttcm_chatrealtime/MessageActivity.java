package com.tranquangduy.ttcm_chatrealtime;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.tranquangduy.adapter.MessageAdapter;
import com.tranquangduy.model.Message;
import com.tranquangduy.model.User;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MessageActivity extends AppCompatActivity {
    ImageView imgBack;
    ImageView imgAvt;
    TextView txtUserName;
    Button btnSendMessage;
    EditText editContentMessage;
    RecyclerView recyclerViewMessage;

    MessageAdapter messageAdapter;
    List<Message> listMessage = new ArrayList<>();


    Intent intent = null;
    User user = null;
    FirebaseUser firebaseUser;
    DatabaseReference reference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message);

        linkView();
        getData();
        addEvent();

    }

    private void addEvent() {
        imgBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });


        btnSendMessage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String msg = editContentMessage.getText().toString();
                if(!msg.equals("")){
                    sendMessage(firebaseUser.getUid(), user.getId(), msg);
                }else {
                    Toast.makeText(MessageActivity.this, "Vui lòng nhập nội dung tin nhắn!", Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

    private void linkView() {
        imgAvt = findViewById(R.id.img_message_avtUser);
        imgBack = findViewById(R.id.img_message_back);
        txtUserName = findViewById(R.id.txt_message_userName);
        btnSendMessage = findViewById(R.id.btn_sendMessage);
        editContentMessage = findViewById(R.id.edt_contentMessage);
        recyclerViewMessage = findViewById(R.id.recycleView_message);
        recyclerViewMessage.setHasFixedSize(true);
        recyclerViewMessage.setLayoutManager(new LinearLayoutManager(getApplicationContext()));


        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
    }

    private void getData() {
        intent = getIntent();
        user = (User) intent.getSerializableExtra("user_chat");
        txtUserName.setText(user.getUserName());
        Glide.with(this).load(user.getImageUrl()).into(imgAvt);

        readMessage(firebaseUser.getUid(),user.getId(), user.getImageUrl());

    }

    private void sendMessage(String sender, String receiver, String message) {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference();

        Map<String, Object> hasMap = new HashMap<>();
        hasMap.put("sender", sender);
        hasMap.put("receiver", receiver);
        hasMap.put("message", message);
        reference.child("Chats").push().setValue(hasMap);

    }

    private void readMessage(String myID, String userID, String imgURL){
        reference = FirebaseDatabase.getInstance().getReference("Chats");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                listMessage.clear();
                for(DataSnapshot dataSnapshot : snapshot.getChildren()){
                    Message msg = dataSnapshot.getValue(Message.class);
                    if(msg.getSender().equals(myID) && msg.getReceiver().equals(userID) ||
                            msg.getSender().equals(userID) && msg.getReceiver().equals(myID)){
                            listMessage.add(msg);
                    }
                }

                messageAdapter.notifyDataSetChanged();

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });

        messageAdapter = new MessageAdapter(MessageActivity.this, listMessage, imgURL);
        recyclerViewMessage.setAdapter(messageAdapter);

    }

}