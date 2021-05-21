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

import com.tranquangduy.adapter.UserAdapter;
import com.tranquangduy.model.User;
import com.tranquangduy.ttcm_chatrealtime.R;

import java.util.ArrayList;
import java.util.List;

public class FragmentSearch extends Fragment {
    RecyclerView recyclerView;
    UserAdapter adapter;
    List<User> listUser;



    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_search, container, false);

        recyclerView = view.findViewById(R.id.recyclerView);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        listUser = new ArrayList<>();
        listUser.add(new User("1","2","3","4","5","6"));
        listUser.add(new User("2","3","4","5","6","7"));
        listUser.add(new User("3","4","5","6","7","8"));
        listUser.add(new User("4","5","6","7","8","9"));



        adapter = new UserAdapter(getContext(), listUser, true);
        recyclerView.setAdapter(adapter);



        return view;
    }
}
