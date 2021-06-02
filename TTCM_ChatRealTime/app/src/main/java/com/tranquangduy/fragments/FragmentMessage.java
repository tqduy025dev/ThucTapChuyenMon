package com.tranquangduy.fragments;



import android.content.Intent;
import android.os.Bundle;
import android.os.SystemClock;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;


import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.messaging.FirebaseMessaging;
import com.tranquangduy.adapter.UserAdapter;
import com.tranquangduy.model.ChatList;
import com.tranquangduy.model.User;
import com.tranquangduy.notifications.Token;
import com.tranquangduy.ttcm_chatrealtime.MessageActivity;
import com.tranquangduy.ttcm_chatrealtime.NewChatActivity;
import com.tranquangduy.ttcm_chatrealtime.R;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;



public class FragmentMessage extends Fragment implements OnItemClickRecycleView {
    EditText edtSearch;
    ImageView imgAddChat;
    RecyclerView recyclerView;

    List<User> mUser;
    UserAdapter userAdapter;
    List<ChatList> listChat;

    private FirebaseUser firebaseUser;
    private DatabaseReference reference;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_message, container, false);


        linkView(view);
        getData();
        addEvent();
        getToken();


        return view;
    }

    private void getToken(){
        FirebaseMessaging.getInstance().getToken().addOnCompleteListener(new OnCompleteListener<String>() {
            @Override
            public void onComplete(@NonNull Task<String> task) {
                if(!task.isSuccessful()){
                    Log.w("TAG", "Fetching FCM registration token failed!", task.getException());
                    return;
                }
                String token = task.getResult();
                updateToken(token);
            }
        });
    }


    private void updateToken(String token){
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Tokens");
        Token token1 = new Token(token);
        reference.child(firebaseUser.getUid()).setValue(token1);

    }

    private void addEvent() {
        edtSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                searchUsers(s.toString().toLowerCase());
            }
        });

        // bàn phím tự động bật lên vào edit text khi chạy activity
        edtSearch.requestFocus();
        edtSearch.dispatchTouchEvent(MotionEvent.obtain(SystemClock.uptimeMillis(), SystemClock.uptimeMillis(), MotionEvent.ACTION_DOWN, 0, 0, 0));
        edtSearch.dispatchTouchEvent(MotionEvent.obtain(SystemClock.uptimeMillis(), SystemClock.uptimeMillis(), MotionEvent.ACTION_UP, 0, 0, 0));


        imgAddChat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), NewChatActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
            }
        });
    }


    private void getData() {
        reference = FirebaseDatabase.getInstance().getReference("ChatList").child(firebaseUser.getUid());
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                listChat.clear();
                for(DataSnapshot dataSnapshot : snapshot.getChildren()){
                    ChatList chatList = dataSnapshot.getValue(ChatList.class);
                    listChat.add(chatList);
                }

                readMessageList();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    private void readMessageList() {
        reference = FirebaseDatabase.getInstance().getReference("Users");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                mUser.clear();
                for(DataSnapshot dataSnapshot : snapshot.getChildren()){
                    User user = dataSnapshot.getValue(User.class);
                    for(ChatList chatList : listChat){
                        if(user.getId().equals(chatList.getId())){
                            mUser.add(user);
                        }
                    }
                }
                Collections.reverse(mUser);
                userAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        userAdapter = new UserAdapter(getContext(), mUser, false,true,true, this);
        recyclerView.setAdapter(userAdapter);
    }

    private void searchUsers(String s){
        ArrayList<User> filterList = new ArrayList<>();
        for(User user : mUser){
            if(user.getUserName().toLowerCase().contains(s)){
                filterList.add(user);
            }
        }
        userAdapter.filertListUser(filterList);

    }

    private void linkView(View view) {
        edtSearch = view.findViewById(R.id.search_message);
        imgAddChat = view.findViewById(R.id.img_addRoom);
        recyclerView = view.findViewById(R.id.recyclerView_MessageUser);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        mUser = new ArrayList<>();
        listChat = new ArrayList<>();
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

    }

    @Override
    public void onItemClick(int position) {
        Intent intent = new Intent(getContext(), MessageActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        User user = mUser.get(position);
        intent.putExtra("user_message", user);
        startActivity(intent);
    }
}
