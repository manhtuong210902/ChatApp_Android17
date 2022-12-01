package com.example.chatapp.activities;

import android.media.MediaPlayer;
import android.os.Bundle;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentTransaction;

import com.example.chatapp.db.FCMSend;
import com.example.chatapp.fragments.ProfileFragment;
import com.example.chatapp.R;
import com.example.chatapp.fragments.CallHistoryFragment;
import com.example.chatapp.fragments.ChatHomeFragment;
import com.example.chatapp.fragments.GroupFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;

import org.checkerframework.checker.units.qual.C;

public class MainActivity extends FragmentActivity {
    FragmentTransaction ft;
    BottomNavigationView bottomNavigation;

    public ChatHomeFragment chatHomeFragment = new ChatHomeFragment();
    public ProfileFragment profileFragment = new ProfileFragment();
    public GroupFragment groupFragment = new GroupFragment();
    public CallHistoryFragment callHistoryFragment = new CallHistoryFragment();

    Fragment selected = chatHomeFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        createFragment(chatHomeFragment);
        createFragment(profileFragment);
        createFragment(groupFragment);
        createFragment(callHistoryFragment);
        showFragment(selected);
        bottomNavigation = (BottomNavigationView) findViewById(R.id.bottomNavigationView);
        bottomNavigation.setOnItemSelectedListener(item -> {
            switch (item.getItemId()){
                case (R.id.chats):
                    hideFragment(selected);
                    selected = chatHomeFragment;
                    showFragment(selected);
                    break;
                case (R.id.profile):
                    hideFragment(selected);
                    selected = profileFragment;
                    showFragment(selected);
                    break;
                case (R.id.groups):
                    hideFragment(selected);
                    selected = groupFragment;
                    showFragment(selected);
                    break;
                case (R.id.calls):
                    hideFragment(selected);
                    selected = callHistoryFragment;
                    showFragment(selected);
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

    private void createFragment(Fragment fragment){
        getSupportFragmentManager().beginTransaction()
                .add(R.id.flTabContent, fragment)
                .hide(fragment)
                .commit();
    }
    private void showFragment(Fragment fragment){
        getSupportFragmentManager().beginTransaction()
                .show(fragment)
                .commit();
    }
    private void hideFragment(Fragment fragment){
        getSupportFragmentManager().beginTransaction()
                .hide(fragment)
                .commit();
    }

}