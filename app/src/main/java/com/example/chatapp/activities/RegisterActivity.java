package com.example.chatapp.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.chatapp.R;
import com.example.chatapp.db.DbReference;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class RegisterActivity extends AppCompatActivity {
    EditText editText_emailRegister, editText_userNameRegister, editText_passwordRegister;
    TextView btn_login;
    Button btn_signup;

    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        getSupportActionBar().hide();

        editText_emailRegister = findViewById(R.id.editText_emailRegister);
        editText_userNameRegister = findViewById(R.id.editText_userNameRegister);
        editText_passwordRegister = findViewById(R.id.editText_passwordRegister);
        btn_login = findViewById(R.id.btn_login);
        btn_signup = findViewById(R.id.btn_signup);

        mAuth = FirebaseAuth.getInstance();


        btn_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
                startActivity(intent);
                finish();
            }
        });

        btn_signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String email = editText_emailRegister.getText().toString();
                String username = editText_userNameRegister.getText().toString();
                String password = editText_passwordRegister.getText().toString();
                boolean check = true;
                if(password.isEmpty() && password.length() < 6){
                    editText_passwordRegister.setError("Invalid password");
                    check = false;
                }
                if(email.isEmpty() ){
                    editText_emailRegister.setError("Invalid email");
                    check = false;
                }
                if(username.isEmpty()){
                    editText_userNameRegister.setError("Invalid email");
                    check = false;
                }
                if(check)
                    createUserWithEmailPass( );
            }
        });
    }

    protected void createUserWithEmailPass(){
        String email = editText_emailRegister.getText().toString();
        String username = editText_userNameRegister.getText().toString();
        String password = editText_passwordRegister.getText().toString();
        if(email.isEmpty() || username.isEmpty() || password.isEmpty()){
            Toast.makeText(RegisterActivity.this, "Thong tin dang ky khong hop le", Toast.LENGTH_LONG).show();
        }
        else{
            mAuth.createUserWithEmailAndPassword( email, password).addOnCompleteListener(RegisterActivity.this, new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if( task.isSuccessful()){
                        FirebaseUser user = mAuth.getCurrentUser();

//                        Toast.makeText(RegisterActivity.this, "uid: " + mAuth.getCurrentUser().getUid(), Toast.LENGTH_SHORT).show();
                        DbReference.writeNewUser(mAuth.getCurrentUser().getUid(), editText_userNameRegister.getText().toString(), "avtdefault.jpg", true, "");
                        Toast.makeText(RegisterActivity.this, "Register successfully", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
                        startActivity(intent);
                        finish();
                    }else{
                        Toast.makeText(RegisterActivity.this, "Registration Error: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
    }

}