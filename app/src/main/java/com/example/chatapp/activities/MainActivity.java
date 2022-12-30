package com.example.chatapp.activities;

import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentTransaction;

import com.example.chatapp.db.DbReference;
import com.example.chatapp.db.FCMSend;
import com.example.chatapp.fragments.ProfileFragment;
import com.example.chatapp.R;
import com.example.chatapp.fragments.CallHistoryFragment;
import com.example.chatapp.fragments.ChatHomeFragment;
import com.example.chatapp.fragments.GroupFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;


public class MainActivity extends AppCompatActivity {
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
        //hiddne
        getSupportActionBar().hide();
        //
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
//        SharedPreferences.Editor editor = sharedPreferences.edit();
        boolean reRender = sharedPreferences.getBoolean("render", false);

        //create fragment full
        createFragment(chatHomeFragment);
        createFragment(profileFragment);
        createFragment(groupFragment);
        createFragment(callHistoryFragment);

        bottomNavigation = (BottomNavigationView) findViewById(R.id.bottomNavigationView);
        //
        if(reRender){
            selected = profileFragment;
            bottomNavigation.getMenu().getItem(3).setChecked(true);
        }
        else{
            bottomNavigation.getMenu().getItem(0).setChecked(true);
        }
        //
        showFragment(selected);
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

    @Override
    public void onResume() {
        super.onResume();
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        boolean status = sharedPreferences.getBoolean("status", true);

        String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        if(status) {
            DbReference.writeIsOnlineUserAndGroup(uid, true);
        }
    }

    @Override
    public void onPause() {
        super.onPause();
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        DbReference.writeIsOnlineUserAndGroup(uid, false);
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

    private void updateNavigationBarState(int actionId){
        MenuItem item = bottomNavigation.getMenu().findItem(actionId);
        item.setChecked(true);
    }

}