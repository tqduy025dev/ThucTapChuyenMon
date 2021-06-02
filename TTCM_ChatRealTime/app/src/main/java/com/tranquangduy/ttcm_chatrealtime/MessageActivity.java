package com.tranquangduy.ttcm_chatrealtime;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageSwitcher;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.tranquangduy.adapter.MessageAdapter;
import com.tranquangduy.fragments.APIService;
import com.tranquangduy.model.Message;
import com.tranquangduy.model.User;
import com.tranquangduy.notifications.Client;
import com.tranquangduy.notifications.Data;
import com.tranquangduy.notifications.MyResponse;
import com.tranquangduy.notifications.Sender;
import com.tranquangduy.notifications.Token;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class MessageActivity extends AppCompatActivity {
    private static final int PICTURE_PICK = 1;
    ImageView imgBack;
    ImageView imgAvt;
    ImageButton btnSendImage;
    TextView txtUserName;
    Button btnSendMessage;
    EditText editContentMessage;
    RecyclerView recyclerViewMessage;

    MessageAdapter messageAdapter;
    List<Message> listMessage = new ArrayList<>();

    private Uri imageUri;
    private StorageReference storageReference;
    private UploadTask uploadTask;

    private Intent intent = null;
    private User user = null;
    private FirebaseUser firebaseUser;
    private DatabaseReference reference;
    private ValueEventListener seenListener;

    private APIService apiService;
    boolean notify = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message);


        apiService = Client.getClient("https://fcm.googleapis.com/").create(APIService.class);


        linkView();
        getData();
        addEvent();


    }

    private void addEvent() {
        imgBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
              finish();
              intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            }
        });

        btnSendImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);

                startActivityForResult(Intent.createChooser(intent, "CHỌN ẢNH"), PICTURE_PICK);
            }
        });


        btnSendMessage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                notify = true;
                String msg = editContentMessage.getText().toString();
                if (!msg.equals("")) {

                    sendMessage(firebaseUser.getUid(), user.getId(), msg);
                    addNotification(user.getId());
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
        intent = getIntent();
        if (intent != null) {
            user = (User) intent.getSerializableExtra("user_newChat");
            if (user != null) {
                if(user.getId().equals(firebaseUser.getUid())){
                    txtUserName.setText("Chỉ có bạn");
                }else {
                    txtUserName.setText(user.getUserName());
                }

                Glide.with(getApplicationContext()).load(user.getImageUrl()).into(imgAvt);

                readMessage(firebaseUser.getUid(), user.getId(), user.getImageUrl());
            } else {
                user = (User) intent.getSerializableExtra("user_message");
                if(user.getId().equals(firebaseUser.getUid())){
                    txtUserName.setText("Chỉ có bạn");
                }else {
                    txtUserName.setText(user.getUserName());
                }
                Glide.with(getApplicationContext()).load(user.getImageUrl()).into(imgAvt);

                readMessage(firebaseUser.getUid(), user.getId(), user.getImageUrl());
            }
        }

        seenMessage(user.getId());

    }

    private void seenMessage(String userID){
        reference = FirebaseDatabase.getInstance().getReference("Chats");

        seenListener = reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot dataSnapshot : snapshot.getChildren()){
                    Message msg = dataSnapshot.getValue(Message.class);
                    if(msg.getReceiver().equals(firebaseUser.getUid()) && msg.getSender().equals(userID)){
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



    private void sendMessage(final String sender,final String receiver, String message) {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference();

        Map<String, Object> hashMap = new HashMap<>();
        hashMap.put("sender", sender);
        hashMap.put("receiver", receiver);
        hashMap.put("message", message);
        hashMap.put("type", "text");
        hashMap.put("isseen", Boolean.FALSE);
        hashMap.put("time", ServerValue.TIMESTAMP);
        reference.child("Chats").push().setValue(hashMap);

        reference.child("ChatList").child(firebaseUser.getUid()).child(user.getId()).child("id").setValue(user.getId());
        reference.child("ChatList").child(user.getId()).child(firebaseUser.getUid()).child("id").setValue(firebaseUser.getUid());

        final String msg = message;
        reference = FirebaseDatabase.getInstance().getReference("Users").child(firebaseUser.getUid());
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                User user = snapshot.getValue(User.class);
                if(notify){
                    sendNotification(receiver,user.getUserName(), msg);
                }
                notify = false;
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });

    }

    private void sendNotification(String receiver, String userName, String message){
        DatabaseReference tokens = FirebaseDatabase.getInstance().getReference("Tokens");
        Query query = tokens.orderByKey().equalTo(receiver);

        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot dataSnapshot : snapshot.getChildren()){
                    Token token = snapshot.getValue(Token.class);
                    Data data = new Data(firebaseUser.getUid(),R.mipmap.ic_launcher, userName + ": " + message, "Tin nhắn mới", user.getId());

                    Sender sender = new Sender(data, token.getToken());

                    apiService.sendNotification(sender).enqueue(new Callback<MyResponse>() {
                        @Override
                        public void onResponse(Call<MyResponse> call, Response<MyResponse> response) {
                            if(response.code() == 200 ){
                                if(response.body().success != 1){
                                    Toast.makeText(MessageActivity.this, "Failed!", Toast.LENGTH_SHORT).show();
                                }
                            }
                        }

                        @Override
                        public void onFailure(Call<MyResponse> call, Throwable t) {

                        }
                    });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });



    }



    private void readMessage(String myID, String userID, String imgURL) {
        reference = FirebaseDatabase.getInstance().getReference("Chats");

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

                if(messageAdapter.getItemCount() != 0){
                    recyclerViewMessage.smoothScrollToPosition(listMessage.size() -1);
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

        if(!userid.equals(firebaseUser.getUid())){
            HashMap<String, Object> hashMap = new HashMap<>();
            hashMap.put("userid", firebaseUser.getUid());
            hashMap.put("text", "đã gửi cho bạn 1 tin nhắn");
            hashMap.put("postid", "");
            hashMap.put("ispost", Boolean.FALSE);
            reference.push().setValue(hashMap);
        }
    }

    private void status(String status){
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

    private void sendMessageImage(final String sender,final String receiver){

        if(imageUri != null){
            final StorageReference fileReference = storageReference.child(System.currentTimeMillis() + "." + getFileExtension(imageUri));
            uploadTask = fileReference.putFile(imageUri);

            Task<Uri> uriTask = uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                @Override
                public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                    if(!task.isSuccessful()){
                        throw task.getException();
                    }
                    return fileReference.getDownloadUrl();
                }
            }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                @Override
                public void onComplete(@NonNull Task<Uri> task) {
                    if(task.isSuccessful()){
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

        if(requestCode == PICTURE_PICK &&resultCode == RESULT_OK &&data != null && data.getData() != null){
            imageUri = data.getData();
            sendMessageImage(firebaseUser.getUid(), user.getId());
        }else {
            Toast.makeText(this, "Không có ảnh được chọn!", Toast.LENGTH_SHORT).show();
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
        status("offline");
        reference.removeEventListener(seenListener);
    }

}