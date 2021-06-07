package com.tranquangduy.ttcm_chatrealtime;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.ContentResolver;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.tranquangduy.adapter.MessageAdapter;
import com.tranquangduy.fragments.APIService;
import com.tranquangduy.model.Message;
import com.tranquangduy.model.User;


import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class MessageActivity extends AppCompatActivity {
    private static final int REQUEST_CODE = 1;
    ImageView imgBack;
    ImageView imgAvt;
    ImageButton btnSendImage;
    TextView txtUserName;
    Button btnSendMessage;
    EditText editContentMessage;
    RecyclerView recyclerViewMessage;

    private MessageAdapter messageAdapter;
    private List<Message> listMessage;

    private Uri imageUri;
    private StorageReference storageReference;
    private UploadTask uploadTask;

    private User user = null;
    private FirebaseUser firebaseUser;
    private DatabaseReference reference;
    private ValueEventListener seenListener;


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

        btnSendImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent, "CHỌN ẢNH"), REQUEST_CODE);
            }
        });


        btnSendMessage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String msg = editContentMessage.getText().toString();
                if (!msg.isEmpty()) {
                    sendMessage(firebaseUser.getUid(), user.getId(), msg);
                    addNotification(user.getId());

                    getToken(user.getId(), msg);

                    editContentMessage.setText("");
                } else {
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
        btnSendImage = findViewById(R.id.btn_sendImage);
        recyclerViewMessage = findViewById(R.id.recycleView_message);
        recyclerViewMessage.setHasFixedSize(true);
        recyclerViewMessage.setLayoutManager(new LinearLayoutManager(MessageActivity.this));

        storageReference = FirebaseStorage.getInstance().getReference("MessageImage");
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

    }

    private void getData() {
        Intent intent = getIntent();
        if (intent != null) {

            if (intent.hasExtra("userid")) {
                String userIDNotification = intent.getStringExtra("userid");

                DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users").child(userIDNotification);
                reference.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        user = snapshot.getValue(User.class);
                        Glide.with(getApplicationContext()).load(user.getImageUrl()).into(imgAvt);
                        txtUserName.setText(user.getUserName());

                        readMessage(firebaseUser.getUid(), userIDNotification, user.getImageUrl());
                        seenMessage(user.getId());
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
            }

            String t = getString(R.string.only_me);
            user = (User) intent.getSerializableExtra("user_newChat");
            if (user != null) {
                if (user.getId().equals(firebaseUser.getUid())) {
                    txtUserName.setText(t);
                } else {
                    txtUserName.setText(user.getUserName());
                }

                Glide.with(getApplicationContext()).load(user.getImageUrl()).into(imgAvt);
                readMessage(firebaseUser.getUid(), user.getId(), user.getImageUrl());
                seenMessage(user.getId());
            } else if (!intent.hasExtra("userid")) {
                user = (User) intent.getSerializableExtra("user_message");
                if (user.getId().equals(firebaseUser.getUid())) {
                    txtUserName.setText(t);
                } else {
                    txtUserName.setText(user.getUserName());
                }
                Glide.with(getApplicationContext()).load(user.getImageUrl()).into(imgAvt);

                readMessage(firebaseUser.getUid(), user.getId(), user.getImageUrl());
                seenMessage(user.getId());
            }


        }

    }


    private void getToken(String userID, String message) {
        SharedPreferences preferences = getSharedPreferences("MyPreferences", MODE_PRIVATE);
        String userId = preferences.getString("mUserID", "");
        String userName = preferences.getString("mUserName", "");

        reference = FirebaseDatabase.getInstance().getReference("Users").child(userID);
        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                String token = snapshot.child("token").getValue().toString();
                if (token.equals("")) {
                    return;
                }
                JSONObject to = new JSONObject();
                JSONObject data = new JSONObject();
                try {
                    data.put("title", userName);
                    data.put("message", message);
                    data.put("id", userId);

                    to.put("to", token);
                    to.put("data", data);

                    sendNotification(to);
                } catch (JSONException e) {
                    e.printStackTrace();
                }


            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }


    private void sendNotification(JSONObject to) {
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, APIService.NOTIFICATIOIN_URL, to, response -> {
            Log.d("Notification", "sendNotification: " + response);
        }, error -> {
            Log.d("Notification", "sendNotification: " + error);
        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {

                Map<String, String> map = new HashMap<>();

                map.put("Authorization", "key=" + APIService.SEVER_KEY);
                map.put("Content-type", "application/json");

                return map;
            }

            @Override
            public String getBodyContentType() {
                return "application/json";
            }
        };

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        request.setRetryPolicy(new DefaultRetryPolicy(3000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        requestQueue.add(request);


    }


    private void seenMessage(String userID) {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Chats").child(userID);

        seenListener =  reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    Message msg = dataSnapshot.getValue(Message.class);
                    if (msg.getReceiver().equals(firebaseUser.getUid()) && msg.getSender().equals(userID)) {
                        HashMap<String, Object> map = new HashMap<>();
                        map.put("isseen", Boolean.TRUE);
                        dataSnapshot.getRef().updateChildren(map);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }


    private void sendMessage(final String sender, final String receiver, String message) {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference();

        Map<String, Object> hashMap = new HashMap<>();
        hashMap.put("sender", sender);
        hashMap.put("receiver", receiver);
        hashMap.put("message", message);
        hashMap.put("type", "text");
        hashMap.put("isseen", Boolean.FALSE);
        hashMap.put("time", ServerValue.TIMESTAMP);
        reference.child("Chats").child(sender).push().setValue(hashMap);
        reference.child("Chats").child(receiver).push().setValue(hashMap);

        reference.child("ChatList").child(firebaseUser.getUid()).child(user.getId()).child("id").setValue(user.getId());
        reference.child("ChatList").child(user.getId()).child(firebaseUser.getUid()).child("id").setValue(firebaseUser.getUid());

    }


    private void readMessage(String myID, String userID, String imgURL) {
        listMessage = new ArrayList<>();
        reference = FirebaseDatabase.getInstance().getReference("Chats").child(myID);
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                listMessage.clear();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    Message msg = dataSnapshot.getValue(Message.class);
                    if (msg.getSender().equals(myID) && msg.getReceiver().equals(userID) ||
                            msg.getSender().equals(userID) && msg.getReceiver().equals(myID)) {
                        listMessage.add(msg);
                    }
                }

                if (messageAdapter.getItemCount() != 0) {
                    recyclerViewMessage.smoothScrollToPosition(listMessage.size() - 1);
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

    private void addNotification(String userid) {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Notifications").child(userid);

        if (!userid.equals(firebaseUser.getUid())) {
            String t = getString(R.string.notification_send_text);
            HashMap<String, Object> hashMap = new HashMap<>();
            hashMap.put("userid", firebaseUser.getUid());
            hashMap.put("text", t);
            hashMap.put("postid", "");
            hashMap.put("ispost", Boolean.FALSE);
            reference.push().setValue(hashMap);
        }
    }

    private void status(String status) {
        reference = FirebaseDatabase.getInstance().getReference("Users").child(firebaseUser.getUid());

        HashMap<String, Object> map = new HashMap<>();
        map.put("status", status);
        reference.updateChildren(map);

    }

    private String getFileExtension(Uri uri) {
        ContentResolver contentResolver = getContentResolver();
        MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();
        return mimeTypeMap.getExtensionFromMimeType(contentResolver.getType(uri));
    }

    private void sendMessageImage(final String sender, final String receiver) {

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

                        DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
                        Map<String, Object> hashMap = new HashMap<>();
                        hashMap.put("sender", sender);
                        hashMap.put("receiver", receiver);
                        hashMap.put("message", mUri);
                        hashMap.put("type", "image");
                        hashMap.put("isseen", Boolean.FALSE);
                        hashMap.put("time", ServerValue.TIMESTAMP);
                        reference.child("Chats").push().setValue(hashMap);

                        reference.child("ChatList").child(firebaseUser.getUid()).child(user.getId()).child("id").setValue(user.getId());
                        reference.child("ChatList").child(user.getId()).child(firebaseUser.getUid()).child("id").setValue(firebaseUser.getUid());
                        Toast.makeText(MessageActivity.this, "Successfully!", Toast.LENGTH_SHORT).show();

                    }
                }
            });
        }


    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_CODE && resultCode == RESULT_OK && data != null && data.getData() != null) {
            imageUri = data.getData();
            sendMessageImage(firebaseUser.getUid(), user.getId());
        }

    }


    @Override
    protected void onResume() {
        super.onResume();
        status("online");

    }

    @Override
    protected void onPause() {
        super.onPause();
        reference.removeEventListener(seenListener);
        status("offline");

    }

}