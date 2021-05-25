package com.tranquangduy.ttcm_chatrealtime;

import androidx.annotation.NonNull;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;


import android.os.Bundle;
import android.view.MenuItem;


import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.tranquangduy.fragments.FragmentHome;
import com.tranquangduy.fragments.FragmentProfile;
import com.tranquangduy.fragments.FragmentMessage;
import com.tranquangduy.fragments.FragmentNotification;
import com.tranquangduy.fragments.FragmentSearch;


public class MainActivity extends AppCompatActivity {
    BottomNavigationView bottomNavigationView;
    Fragment selectedfragment = null;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        linkView();


    }

    private void linkView() {
        bottomNavigationView = findViewById(R.id.bottomView);
        bottomNavigationView.setOnNavigationItemSelectedListener(onNavigationItemReselectedListener);

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


}