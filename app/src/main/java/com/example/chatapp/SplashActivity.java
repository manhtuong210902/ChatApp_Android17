package com.example.chatapp;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class SplashActivity extends Activity {
    Handler handler=new Handler();
    private FirebaseAuth mAuth;
    SharedPreferences sharedPreferences,sharedPreferencesLogin;
    SharedPreferences.Editor editor;
    private void initPreferences() {
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        editor = sharedPreferences.edit();
    }
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.splash_screen);
        initPreferences();
        mAuth = FirebaseAuth.getInstance();
        AutoLogin();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                AutoLogin();
            }},1000);
    }

    private void AutoLogin(){
        String email = sharedPreferences.getString("EmailLogin", "");
        String password = sharedPreferences.getString("PasswordLogin", "");
        if( email.isEmpty() || password.isEmpty()){
            Intent intent = new Intent(SplashActivity.this, LoginActivity.class);
            startActivity(intent);
        }
        else {
            mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if (task.isSuccessful()) {
                        FirebaseUser user = mAuth.getCurrentUser();
                        Intent intent = new Intent(SplashActivity.this, MainActivity.class);
                        startActivity(intent);

                    } else {
                        Intent intent = new Intent(SplashActivity.this, LoginActivity.class);
                        startActivity(intent);
                    }
                }
            });
        }
    }

}
