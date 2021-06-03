package com.tranquangduy.ttcm_chatrealtime;

import androidx.annotation.NonNull;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;


import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Toast;


import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.tranquangduy.fragments.FragmentHome;
import com.tranquangduy.fragments.FragmentProfile;
import com.tranquangduy.fragments.FragmentMessage;
import com.tranquangduy.fragments.FragmentNotification;
import com.tranquangduy.fragments.FragmentSearch;
import com.tranquangduy.model.User;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;


public class MainActivity extends AppCompatActivity {
    BottomNavigationView bottomNavigationView;
    Fragment selectedfragment = null;
    public static final String PREF = "MyPreferences";


    private DatabaseReference reference;
    private FirebaseUser firebaseUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        linkView();
        getData();

    }

    private void getData(){
        reference = FirebaseDatabase.getInstance().getReference("Users").child(firebaseUser.getUid());
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                User mUser = snapshot.getValue(User.class);

                SharedPreferences myPreferences = getSharedPreferences(PREF, Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = myPreferences.edit();

                editor.putString("mUserID", mUser.getId());
                editor.putString("mUserName", mUser.getUserName());
                editor.putString("mImgURL", mUser.getImageUrl());
                editor.putString("mUserFullName", mUser.getFullName());

                editor.apply();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });




    }


    private void linkView() {
        bottomNavigationView = findViewById(R.id.bottomView);
        bottomNavigationView.setOnNavigationItemSelectedListener(onNavigationItemReselectedListener);

        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new FragmentHome()).commit();
    }

    private BottomNavigationView.OnNavigationItemSelectedListener onNavigationItemReselectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {
        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {

            switch (item.getItemId()) {
                case R.id.menu_home:
                    selectedfragment = new FragmentHome();
                    break;
                case R.id.menu_message:
                    selectedfragment = new FragmentMessage();
                    break;
                case R.id.menu_search:
                    selectedfragment = new FragmentSearch();
                    break;
                case R.id.menu:
                    selectedfragment = new FragmentProfile();
                    break;
                case R.id.menu_notification:
                    selectedfragment = new FragmentNotification();
                    break;
            }

            if (selectedfragment != null) {
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                        selectedfragment).commit();
            }

            return true;
        }
    };

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