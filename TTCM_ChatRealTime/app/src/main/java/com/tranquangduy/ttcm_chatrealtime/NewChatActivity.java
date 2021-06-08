package com.tranquangduy.ttcm_chatrealtime;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.os.SystemClock;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.tranquangduy.adapter.UserAdapter;
import com.tranquangduy.control.OnItemClickRecycleView;
import com.tranquangduy.model.User;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class NewChatActivity extends AppCompatActivity implements OnItemClickRecycleView {
    ImageView imgViewBack;
    EditText edtSearch;
    RecyclerView recyclerViewUser;
    List<User> listUser;
    UserAdapter userAdapter;

    private FirebaseUser firebaseUser;
    private DatabaseReference reference;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_chat);
        linkView();
        getData();
        addEvent();


    }

    private void addEvent() {
        edtSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                searchUser(s.toString().toLowerCase());
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        // bàn phím tự động bật lên vào edit text khi chạy activity
        edtSearch.requestFocus();
        edtSearch.dispatchTouchEvent(MotionEvent.obtain(SystemClock.uptimeMillis(), SystemClock.uptimeMillis(), MotionEvent.ACTION_DOWN , 0, 0, 0));
        edtSearch.dispatchTouchEvent(MotionEvent.obtain(SystemClock.uptimeMillis(), SystemClock.uptimeMillis(), MotionEvent.ACTION_UP , 0, 0, 0));



        imgViewBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private void searchUser(String s) {
        Query query = FirebaseDatabase.getInstance().getReference("Users").orderByChild("userName")
                .startAt(s)
                .endAt(s+"\uf8ff");

        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                listUser.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()){
                    User user = snapshot.getValue(User.class);
                    listUser.add(user);
                }
                userAdapter.notifyDataSetChanged();
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });

    }

    private void getData() {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users");
        listUser = new ArrayList<>();
        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                listUser.clear();
                for (DataSnapshot dataSnapshot: snapshot.getChildren()) {
                    listUser.add(dataSnapshot.getValue(User.class));
                }
                userAdapter.notifyDataSetChanged();
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
        userAdapter = new UserAdapter(NewChatActivity.this, listUser,true, false,false, this);
        recyclerViewUser.setAdapter(userAdapter);

    }

    private void linkView() {
        imgViewBack = findViewById(R.id.img_chat_back);
        edtSearch =  findViewById(R.id.txt_search_user_chat);
        recyclerViewUser  = findViewById(R.id.recycleView_user_chat);

        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        recyclerViewUser.setHasFixedSize(true);
        recyclerViewUser.setLayoutManager(new LinearLayoutManager(NewChatActivity.this));
    }

    @Override
    public void onItemClick(int position) {
        Intent intent = new Intent(NewChatActivity.this, MessageActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        User user = listUser.get(position);
        intent.putExtra("user_message", user);
        startActivity(intent);

    }

    @Override
    public void onItemLongClick(int postition) {

    }

    private void status(String status){
        reference = FirebaseDatabase.getInstance().getReference("Users").child(firebaseUser.getUid());

        HashMap<String, Object> map = new HashMap<>();
        map.put("status", status);
        reference.updateChildren(map);
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
    }
}