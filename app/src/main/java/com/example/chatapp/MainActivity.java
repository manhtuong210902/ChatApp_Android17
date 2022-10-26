package com.example.chatapp;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainActivity extends FragmentActivity {
    FragmentTransaction ft;
    BottomNavigationView bottomNavigation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        replaceFragment(new ChatHomeFragment());
        bottomNavigation = (BottomNavigationView) findViewById(R.id.bottomNavigationView);
        bottomNavigation.setOnItemSelectedListener(item -> {
            switch (item.getItemId()){
                case (R.id.chats):
                    replaceFragment(new ChatHomeFragment());
                    break;
                case (R.id.profile):
                    replaceFragment(new ProfileFragment());
                    break;
                case (R.id.groups):
                    break;
                case (R.id.calls):
                    replaceFragment(new CallHistoryFragment());
                    break;
                default:
                    break;
            }
            return true;
        });
    }

    private void replaceFragment(Fragment fragment){
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.flTabContent, fragment);
        ft.commit();
    }

}