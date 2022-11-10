package com.example.chatapp;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.io.File;

public class LoginActivity extends Activity {
    TextView btnSignup;
    EditText editText_emailLogin, editText_passwordLogin;
    CheckBox checkbox_login;
    Button btnSignin;
    private FirebaseAuth mAuth;
    SharedPreferences sharedPreferences,sharedPreferencesLogin;
    SharedPreferences.Editor editor;
    private void initPreferences() {
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        editor = sharedPreferences.edit();
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        initPreferences();
        mAuth = FirebaseAuth.getInstance();
//        AutoLogin();
        editText_emailLogin = findViewById(R.id.editText_emailLogin);
        editText_passwordLogin = findViewById(R.id.editText_passwordLogin);
        checkbox_login = findViewById(R.id.checkbox_login);
        btnSignin = findViewById(R.id.btnSignin);

        String savedData = sharedPreferences.getString("Email", "");
        if(!savedData.isEmpty()){
            editText_emailLogin.setText(savedData);
            checkbox_login.setChecked(true);
        }

        //auto login
        btnSignup = (TextView) findViewById(R.id.btnSignup);
        btnSignup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
                startActivity(intent);
            }
        });

        btnSignin = (Button) findViewById(R.id.btnSignin);
        btnSignin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                boolean check = true;
                String email = editText_emailLogin.getText().toString();
                String password = editText_passwordLogin.getText().toString();
                if(password.isEmpty() && password.length() <6){
                    editText_passwordLogin.setError("Invalid password");
                    check = false;
                }
                if(email.isEmpty() ){
                    editText_emailLogin.setError("Invalid email");
                    check = false;
                }
                if(check)
                    logInWithEmailAndPassword( );
            }
        });
    }

    private void logInWithEmailAndPassword(){
        String email = editText_emailLogin.getText().toString();
        String password = editText_passwordLogin.getText().toString();
        if(checkbox_login.isChecked()){
            editor.putString("Email", email);
            editor.commit();
        }
        mAuth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if (task.isSuccessful()) {
                        editor.putString("EmailLogin", email);
                        editor.putString("PasswordLogin", password);
                        editor.commit();
                        FirebaseUser user = mAuth.getCurrentUser();
                        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                        startActivity(intent);
                        Toast.makeText(LoginActivity.this, "Login seccessful.", Toast.LENGTH_SHORT).show();

                    } else {
                        Toast.makeText(LoginActivity.this, "Authentication failed.", Toast.LENGTH_SHORT).show();
                    }
                }
            });
    }


}

