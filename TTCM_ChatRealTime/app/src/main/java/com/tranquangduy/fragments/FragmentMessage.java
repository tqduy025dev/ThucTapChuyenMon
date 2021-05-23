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

import com.tranquangduy.adapter.MessageAdapter;
import com.tranquangduy.model.Message;
import com.tranquangduy.ttcm_chatrealtime.R;

import java.util.ArrayList;
import java.util.List;

public class FragmentMessage extends Fragment {
    RecyclerView recyclerView;
    List<Message> listMessage;
    MessageAdapter messageAdapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_message, container, false);

        recyclerView = view.findViewById(R.id.recyclerViewMessage);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        listMessage = new ArrayList<>();
        listMessage.add(new Message("Content",1000,true,"Duy"));
        listMessage.add(new Message("Content",1000,true,"Hai"));
        listMessage.add(new Message("Content",1000,true,"Long"));
        listMessage.add(new Message("Content",1000,true,"Huy"));
        listMessage.add(new Message("Content",1000,true,"Hoang"));
        listMessage.add(new Message("Content",1000,true,"Dương Uyên"));


        messageAdapter = new MessageAdapter(getContext(),listMessage,true);
        recyclerView.setAdapter(messageAdapter);







        return view;

    }





}
