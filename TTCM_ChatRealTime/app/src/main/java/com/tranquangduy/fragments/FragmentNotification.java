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

import com.tranquangduy.adapter.NotificationAdapter;
import com.tranquangduy.model.Notification;
import com.tranquangduy.ttcm_chatrealtime.R;

import java.util.ArrayList;
import java.util.List;

public class FragmentNotification extends Fragment {
    RecyclerView recyclerView;
    List<Notification> listNotification;
    NotificationAdapter notificationAdapter;




    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view  = inflater.inflate(R.layout.fragment_notificaion, container, false);

        recyclerView = view.findViewById(R.id.recyclerViewNotification);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        listNotification = new ArrayList<>();
        listNotification.add(new Notification("1","oke man","1",true));
        listNotification.add(new Notification("2","oke pro","2",true));
        listNotification.add(new Notification("3","oke you","3",true));
        listNotification.add(new Notification("4","oke mày","4",true));
        listNotification.add(new Notification("5","oke cc","5",true));


        notificationAdapter = new NotificationAdapter(getContext(),listNotification);
        recyclerView.setAdapter(notificationAdapter);


        return view;
    }
}
