package com.example.chatapp.activities;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import com.example.chatapp.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class LoginActivity extends AppCompatActivity {
    TextView btnSignup, textView_forgotPw;
    EditText editText_emailLogin, editText_passwordLogin;
    CheckBox checkbox_login;
    Button btnSignin;
    private FirebaseAuth mAuth;
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;
    private void initPreferences() {
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        editor = sharedPreferences.edit();
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        getSupportActionBar().hide();

        initPreferences();

        boolean nightMode = sharedPreferences.getBoolean("night", false); //light mode default false
        if(nightMode){
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        }else{
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        }
        //
        mAuth = FirebaseAuth.getInstance();
        editText_emailLogin = findViewById(R.id.editText_emailLogin);
        editText_passwordLogin = findViewById(R.id.editText_passwordLogin);
        checkbox_login = findViewById(R.id.checkbox_login);
        btnSignin = findViewById(R.id.btnSignin);

        String savedData = sharedPreferences.getString("Email", "");
        if(!savedData.isEmpty()){
            editText_emailLogin.setText(savedData);
            checkbox_login.setChecked(true);
        }
        else {
            editText_emailLogin.setText("");
            checkbox_login.setChecked(false);
        }

        //auto login
        btnSignup = (TextView) findViewById(R.id.btnSignup);
        btnSignup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
                startActivity(intent);
                finish();
            }
        });

        //forgot password
        textView_forgotPw = findViewById(R.id.textView_forgotPw);
        textView_forgotPw.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(LoginActivity.this, ResetPasswordActivity.class);
                startActivity(intent);
            }
        });

        //sign in
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
        else {
            editor.putString("Email", "");
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
                        finish();
                        Toast.makeText(LoginActivity.this, "Login seccessful.", Toast.LENGTH_SHORT).show();

                    } else {
                        Toast.makeText(LoginActivity.this, "Authentication failed.", Toast.LENGTH_SHORT).show();
                    }
                }
            });
    }


}

