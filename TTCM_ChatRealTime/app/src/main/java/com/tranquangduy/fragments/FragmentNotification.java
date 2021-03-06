package com.tranquangduy.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

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
import com.tranquangduy.adapter.NotificationAdapter;
import com.tranquangduy.model.Notification;
import com.tranquangduy.ttcm_chatrealtime.R;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class FragmentNotification extends Fragment {
    RecyclerView recyclerView;

    private List<Notification> listNotification;
    private NotificationAdapter notificationAdapter;




    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view  = inflater.inflate(R.layout.fragment_notificaion, container, false);

        recyclerView = view.findViewById(R.id.recyclerView_notification);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        listNotification = new ArrayList<>();


        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Notifications").child(firebaseUser.getUid());

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                listNotification.clear();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()){
                    listNotification.add(dataSnapshot.getValue(Notification.class));
                }
                Collections.reverse(listNotification);
                notificationAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


        notificationAdapter = new NotificationAdapter(getContext(),listNotification);
        recyclerView.setAdapter(notificationAdapter);


        return view;
    }
}
