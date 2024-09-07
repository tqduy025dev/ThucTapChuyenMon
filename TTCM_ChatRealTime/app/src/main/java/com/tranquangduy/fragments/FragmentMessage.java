package com.tranquangduy.fragments;


import android.content.Intent;
import android.os.Bundle;
import android.os.SystemClock;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;


import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.tranquangduy.adapter.UserAdapter;
import com.tranquangduy.control.OnItemClickRecycleView;
import com.tranquangduy.model.ChatList;
import com.tranquangduy.model.User;
import com.tranquangduy.ttcm_chatrealtime.MessageActivity;
import com.tranquangduy.ttcm_chatrealtime.NewChatActivity;
import com.tranquangduy.ttcm_chatrealtime.R;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Stack;
import java.util.function.UnaryOperator;


public class FragmentMessage extends Fragment implements OnItemClickRecycleView {
    EditText edtSearch;
    ImageButton imgAddChat;
    ImageView imgDelete;
    RecyclerView recyclerView;

    private List<User> listUser;
    private UserAdapter userAdapter;
    private List<ChatList> listChat;

    private FirebaseUser firebaseUser;
    private DatabaseReference reference;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_message, container, false);

        linkView(view);
        getData();
        addEvent();

        return view;
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
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    ChatList chatList = dataSnapshot.getValue(ChatList.class);
                    listChat.add(chatList);
                }
                compareToLastTime();
                readMessageList();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    private void compareToLastTime() {
        listChat.sort(new Comparator<ChatList>() {
            @Override
            public int compare(ChatList o1, ChatList o2) {
                return Long.compare(o1.getLasttime(), o2.getLasttime());
            }
        });
        Collections.reverse(listChat);
    }

    private void readMessageList() {
        reference = FirebaseDatabase.getInstance().getReference("Users");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                listUser.clear();
                for (ChatList chatList : listChat) {
                    for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                        User user = dataSnapshot.getValue(User.class);
                        assert user != null;
                        if (user.getId().equals(chatList.getId())) {
                            listUser.add(user);
                        }

                    }
                }
                userAdapter.notifyDataSetChanged();


            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        recyclerView.setAdapter(userAdapter);
    }


    private void searchUsers(String s) {
        ArrayList<User> filterList = new ArrayList<>();
        for (User user : listUser) {
            if (user.getUserName().toLowerCase().contains(s)) {
                filterList.add(user);
            }
        }
        userAdapter.SEARCH_LISTUSER(filterList); // sử dụng interface

    }

    private void linkView(View view) {
        edtSearch = view.findViewById(R.id.search_message);
        imgAddChat = view.findViewById(R.id.img_addRoom);
        imgDelete = view.findViewById(R.id.imgItem_user_delete);
        recyclerView = view.findViewById(R.id.recyclerView_messageUser);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        listUser = new ArrayList<>();
        listChat = new ArrayList<>();
        userAdapter = new UserAdapter(getContext(), listUser, false, true, true, this);
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

    }

    @Override
    public void onItemClick(int position) {
        Intent intent = new Intent(getContext(), MessageActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        User user = listUser.get(position);
        intent.putExtra("user_message", user);
        startActivity(intent);
    }

    @Override
    public void onItemLongClick(int postition) {
        // sử dụng interface để truyền dữ liệu cho userAdapter
        userAdapter.UPDATE_USERID(listChat.get(postition).getId());
    }


}
