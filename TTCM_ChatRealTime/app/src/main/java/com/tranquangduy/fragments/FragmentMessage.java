package com.tranquangduy.fragments;


import android.app.AlertDialog;
import android.content.DialogInterface;
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
import android.widget.ImageView;
import android.widget.Toast;

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
import com.tranquangduy.adapter.MessageAdapter;
import com.tranquangduy.model.Message;
import com.tranquangduy.ttcm_chatrealtime.MessageActivity;
import com.tranquangduy.ttcm_chatrealtime.NewChatActivity;
import com.tranquangduy.ttcm_chatrealtime.R;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class FragmentMessage extends Fragment implements OnItemClickRecycleView {
    EditText edtSearch;
    ImageView imgAddChat;
    RecyclerView recyclerView;
    List<Message> listMessage;
    MessageAdapter messageAdapter;

    private DatabaseReference reference;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_message, container, false);


        linkView(view);
//        getData();
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
                // xử lý tìm kiếm :V
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        // bàn phím tự động bật lên vào edit text khi chạy activity
        edtSearch.requestFocus();
        edtSearch.dispatchTouchEvent(MotionEvent.obtain(SystemClock.uptimeMillis(), SystemClock.uptimeMillis(), MotionEvent.ACTION_DOWN , 0, 0, 0));
        edtSearch.dispatchTouchEvent(MotionEvent.obtain(SystemClock.uptimeMillis(), SystemClock.uptimeMillis(), MotionEvent.ACTION_UP , 0, 0, 0));


        imgAddChat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), NewChatActivity.class);
                startActivity(intent);
            }
        });
    }

    private void getData() {
        reference = FirebaseDatabase.getInstance().getReference("Chat");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (getContext() == null){
                    return;
                }
                for (DataSnapshot dataSnapshot: snapshot.getChildren()) {
                    listMessage.add(dataSnapshot.getValue(Message.class));
                }

                messageAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
    }

    private void linkView(View view) {
        edtSearch = view.findViewById(R.id.search_message);
        imgAddChat = view.findViewById(R.id.img_addRoom);

        listMessage = new ArrayList<>();
        recyclerView = view.findViewById(R.id.recyclerView_MessageUser);

        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(messageAdapter);

    }




    @Override
    public void onItemClick(int position) {
        Intent intent = new Intent(getContext(), MessageActivity.class);
        Message message = listMessage.get(position);
        intent.putExtra("object_message",  message);

        startActivity(intent);

        Toast.makeText(getContext(), "onClick", Toast.LENGTH_SHORT).show();
    }
}
