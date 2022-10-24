package com.example.chatapp;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Spinner;
import android.widget.TextView;

public class MainActivity extends Activity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Demo login form UI
        Intent intent = new Intent(MainActivity.this, ProfileActivity.class);
        startActivity(intent);
    }
}