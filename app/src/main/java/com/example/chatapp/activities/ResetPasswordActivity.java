package com.example.chatapp.activities;

import android.app.Activity;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.example.chatapp.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;

public class ResetPasswordActivity extends AppCompatActivity {
    ImageView btn_close;
    EditText editText_emailResetPw;
    Button btn_Done;
    TextView textView_errEmail;
    private FirebaseAuth mAuth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reset_password);
        getSupportActionBar().hide();

        mAuth = FirebaseAuth.getInstance();
        textView_errEmail = findViewById(R.id.textView_errEmail);
        //back screen
        btn_close = findViewById(R.id.btn_close);
        btn_close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        //done
        editText_emailResetPw = findViewById(R.id.editText_emailResetPw);
        btn_Done = findViewById(R.id.btn_Done);
        btn_Done.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String email = editText_emailResetPw.getText().toString();
                if (email.isEmpty()) {
                    Drawable errorIcon = ContextCompat.getDrawable(getApplicationContext(),R.drawable.ic_error_warning_line);
                    errorIcon.setBounds(0, 0, errorIcon.getIntrinsicWidth(), errorIcon.getIntrinsicHeight());
                    editText_emailResetPw.setError("Invalid email", errorIcon);
                    textView_errEmail.setText("Invalid email");
                } else {
                    textView_errEmail.setText("");
                    forgetPass(email);
                }
            }
        });
    }


    private void forgetPass(String email) {
        mAuth.sendPasswordResetEmail(email)
            .addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (task.isSuccessful()) {
                        Toast.makeText(ResetPasswordActivity.this, "Check your Email", Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(ResetPasswordActivity.this, LoginActivity.class));
                        finish();
                    } else {
                        Toast.makeText(ResetPasswordActivity.this, "Error"+task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }
            });
    }
}